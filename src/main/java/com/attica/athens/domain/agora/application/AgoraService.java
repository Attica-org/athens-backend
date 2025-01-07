package com.attica.athens.domain.agora.application;

import static com.attica.athens.domain.agora.domain.AgoraStatus.CLOSED;
import static com.attica.athens.domain.agoraMember.domain.AgoraMemberType.OBSERVER;

import com.attica.athens.domain.agora.dao.AgoraRepository;
import com.attica.athens.domain.agora.dao.CategoryRepository;
import com.attica.athens.domain.agora.dao.PopularRepository;
import com.attica.athens.domain.agora.domain.Agora;
import com.attica.athens.domain.agora.domain.AgoraStatus;
import com.attica.athens.domain.agora.domain.AgoraThumbnail;
import com.attica.athens.domain.agora.domain.Category;
import com.attica.athens.domain.agora.dto.SimpleAgoraResult;
import com.attica.athens.domain.agora.dto.request.AgoraCreateRequest;
import com.attica.athens.domain.agora.dto.request.AgoraParticipateRequest;
import com.attica.athens.domain.agora.dto.request.AgoraRequest;
import com.attica.athens.domain.agora.dto.request.SearchKeywordRequest;
import com.attica.athens.domain.agora.dto.response.AgoraExitResponse;
import com.attica.athens.domain.agora.dto.response.AgoraIdResponse;
import com.attica.athens.domain.agora.dto.response.AgoraParticipateResponse;
import com.attica.athens.domain.agora.dto.response.AgoraSlice;
import com.attica.athens.domain.agora.dto.response.AgoraTitleResponse;
import com.attica.athens.domain.agora.dto.response.ClosedAgoraParticipateResponse;
import com.attica.athens.domain.agora.dto.response.CreateAgoraResponse;
import com.attica.athens.domain.agora.dto.response.EndAgoraResponse;
import com.attica.athens.domain.agora.dto.response.EndNotificationResponse;
import com.attica.athens.domain.agora.dto.response.EndVoteAgoraResponse;
import com.attica.athens.domain.agora.dto.response.StartAgoraResponse;
import com.attica.athens.domain.agora.dto.response.StartNotificationResponse;
import com.attica.athens.domain.agora.dto.response.UpdateThumbnailResponse;
import com.attica.athens.domain.agora.exception.ActiveAgoraException;
import com.attica.athens.domain.agora.exception.AlreadyParticipateException;
import com.attica.athens.domain.agora.exception.ClosedAgoraException;
import com.attica.athens.domain.agora.exception.DuplicatedNicknameException;
import com.attica.athens.domain.agora.exception.FullAgoraCapacityException;
import com.attica.athens.domain.agora.exception.ImageUpdateAccessDeniedException;
import com.attica.athens.domain.agora.exception.InvalidAgoraStatusException;
import com.attica.athens.domain.agora.exception.NotFoundAgoraException;
import com.attica.athens.domain.agora.exception.NotFoundCategoryException;
import com.attica.athens.domain.agora.exception.NotParticipateException;
import com.attica.athens.domain.agora.vote.application.AgoraVoteService;
import com.attica.athens.domain.agoraMember.dao.AgoraMemberRepository;
import com.attica.athens.domain.agoraMember.domain.AgoraMember;
import com.attica.athens.domain.agoraMember.exception.AlreadyEndVotedException;
import com.attica.athens.domain.chat.domain.ChatType;
import com.attica.athens.domain.member.dao.BaseMemberRepository;
import com.attica.athens.domain.member.domain.BaseMember;
import com.attica.athens.domain.member.exception.NotFoundMemberException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AgoraService {

    private static final Integer PROS_COUNT = 0;
    private static final Integer CONS_COUNT = 0;
    public static final String AGORA_TOPIC = "/topic/agoras/";

    private final AgoraRepository agoraRepository;
    private final CategoryRepository categoryRepository;
    private final BaseMemberRepository baseMemberRepository;
    private final AgoraMemberRepository agoraMemberRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final PopularRepository popularRepository;
    private final S3ThumbnailService s3ThumbnailService;
    private final AgoraVoteService agoraVoteService;

    @Transactional
    public CreateAgoraResponse create(final AgoraCreateRequest request, final MultipartFile file) {
        Category category = findByCategory(request.categoryId());
        AgoraThumbnail thumbnail = s3ThumbnailService.getAgoraThumbnail(file);
        Agora created = agoraRepository.save(createAgora(request, category, thumbnail));

        return new CreateAgoraResponse(created.getId());
    }

    @Transactional
    public UpdateThumbnailResponse updateAgoraImage(Long agoraId, Long memberId, MultipartFile file) {
        Agora agora = agoraRepository.findById(agoraId)
                .orElseThrow(() -> new NotFoundAgoraException(agoraId));

        long createMemberId = Long.parseLong(agora.getCreatedBy());
        if (!isCreateMember(createMemberId, memberId)) {
            throw new ImageUpdateAccessDeniedException();
        }

        AgoraThumbnail updateThumbnail = s3ThumbnailService.getAgoraThumbnail(file);
        agora.updateThumbnail(updateThumbnail);

        return new UpdateThumbnailResponse(agora.getAgoraThumbnail().getImageUrl());
    }

    public AgoraSlice<?> findAgoraByKeyword(final String agoraName,
                                            final SearchKeywordRequest request) {
        boolean isClosed = CLOSED.getType().equals(request.status());

        return isClosed ? agoraRepository.findClosedAgoraByKeyword(request.next(), request.getStatus(), agoraName)
                : agoraRepository.findActiveAgoraByKeyword(request.next(), request.getStatus(), agoraName);
    }

    public AgoraSlice<?> findAgoraByCategory(AgoraRequest request) {

        boolean isClosed = CLOSED.getType().equals(request.status());

        if (request.category() == 1) {
            return isClosed
                    ? agoraRepository.findClosedAgoraVoteResultsByAllCategory(request.next(),
                    request.getStatus())
                    : agoraRepository.findActiveAgoraByAllCategory(request.next(), request.getStatus());
        } else {
            List<Long> categoryIds = findParentCategoryById(request.category());
            return isClosed
                    ? agoraRepository.findClosedAgoraVoteResultsByCategory(request.next(), categoryIds,
                    request.getStatus())
                    : agoraRepository.findActiveAgoraByCategory(request.next(), request.getStatus(), categoryIds);
        }
    }

    @Transactional
    public AgoraParticipateResponse participate(final Long memberId, final Long agoraId,
                                                final AgoraParticipateRequest request) {
        Agora agora = agoraRepository.findAgoraById(agoraId)
                .orElseThrow(() -> new NotFoundAgoraException(agoraId));

        validateParticipate(memberId, agoraId, request, agora);

        AgoraMember created = createAgoraMember(memberId, agoraId, request);
        AgoraMember agoraMember = agoraMemberRepository.save(created);
        agora.addMember(agoraMember);

        long createMemberId = Long.parseLong(agora.getCreatedBy());
        boolean isCreator = isCreateMember(createMemberId, memberId);

        return new AgoraParticipateResponse(created.getAgora().getId(), memberId, created.getType(), isCreator);
    }

    @Transactional
    public ClosedAgoraParticipateResponse closedAgoraParticipate(final Long agoraId, final Long memberId) {
        Agora agora = agoraRepository.findAgoraById(agoraId)
                .orElseThrow(() -> new NotFoundAgoraException(agoraId));

        validateClosedAgoraParticipate(agora);

        return new ClosedAgoraParticipateResponse(agoraId, memberId);
    }

    @Transactional
    public AgoraExitResponse exit(final Long memberId, final Long agoraId) {

        AgoraMember agoraMember = getAgoraMember(memberId, agoraId);
        LocalDateTime socketDisconnectTime = LocalDateTime.now();

        agoraMember.clearNickname();
        agoraMember.updateDisconnectType(true);
        agoraMember.updateSocketDisconnectTime(socketDisconnectTime);

        return new AgoraExitResponse(memberId, agoraMember.getType(), socketDisconnectTime);
    }

    private AgoraMember getAgoraMember(Long userId, Long agoraId) {
        return agoraMemberRepository.findByMemberIdAndAgoraIdAndSocketDisconnectTimeIsNull(userId, agoraId)
                .orElseThrow(() -> new NotFoundMemberException(userId));
    }

    public List<SimpleAgoraResult> findTrendAgora() {
        List<Long> agoraIds = popularRepository.findAllIdsByPopular();
        List<SimpleAgoraResult> agoras = agoraRepository.findAgoraByIdsWithRunning(agoraIds);

        Map<Long, SimpleAgoraResult> agoraMap = agoras.stream()
                .collect(
                        Collectors.toMap(
                                SimpleAgoraResult::id,
                                element -> element));

        return agoraIds.stream()
                .map(agoraMap::get)
                .toList();
    }

    private Agora createAgora(final AgoraCreateRequest request, final Category category,
                              final AgoraThumbnail agoraThumbnail) {
        return new Agora(request.title(),
                request.capacity(),
                request.duration(),
                request.color(),
                PROS_COUNT, CONS_COUNT,
                category,
                agoraThumbnail
        );
    }

    private AgoraMember createAgoraMember(final Long memberId, final Long agoraId,
                                          final AgoraParticipateRequest request) {
        return new AgoraMember(
                request.type(),
                request.nickname(),
                request.photoNum(),
                findAgoraById(agoraId),
                findMemberById(memberId)
        );
    }

    private BaseMember findMemberById(final Long memberId) {
        return baseMemberRepository.findById(memberId).orElseThrow(() -> new NotFoundMemberException(memberId));
    }

    private List<Long> findParentCategoryById(final Long categoryId) {
        Category category = findByCategory(categoryId);
        List<Long> parentCodes = new ArrayList<>();
        Long currentCategory = category.getId();

        while (currentCategory != null) {
            parentCodes.add(currentCategory);
            currentCategory = categoryRepository.findById(currentCategory)
                    .map(Category::getParent)
                    .map(Category::getId)
                    .orElse(null);
        }
        return parentCodes;
    }

    private Category findByCategory(final Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundCategoryException(categoryId));
    }

    public AgoraTitleResponse getAgoraTitleAndImageUrl(final Long agoraId) {

        Agora agora = findAgoraById(agoraId);

        return new AgoraTitleResponse(agora);
    }

    public AgoraIdResponse getAgoraIdList() {

        List<Long> agoraIdList = agoraRepository.getAgoraIdList();

        return new AgoraIdResponse(agoraIdList);
    }

    @Transactional
    public StartAgoraResponse startAgora(final Long agoraId, final Long memberId) {
        Agora agora = findAgoraById(agoraId);
        findValidAgoraMember(agoraId, memberId);
        agora.startAgora();
        sendAgoraStartMessage(agora);

        return new StartAgoraResponse(agora);
    }

    private AgoraMember findValidAgoraMember(final Long agoraId, final Long memberId) {
        return agoraMemberRepository.findLatestByAgoraIdAndMemberId(agoraId, memberId)
                .orElseThrow(NotParticipateException::new)
                .validateSendMessage();
    }

    private Agora findAgoraById(final Long agoraId) {
        return agoraRepository.findById(agoraId)
                .orElseThrow(() -> new NotFoundAgoraException(agoraId));
    }

    private boolean isCreateMember(Long createdMemberId, Long memberId) {
        return Objects.equals(createdMemberId, memberId);
    }

    private void sendAgoraStartMessage(final Agora agora) {
        StartNotificationResponse notification = new StartNotificationResponse(ChatType.DISCUSSION_START,
                new StartNotificationResponse.StartAgoraData(agora));
        messagingTemplate.convertAndSend(AGORA_TOPIC + agora.getId(), notification);
    }

    @Transactional
    public EndVoteAgoraResponse endVoteAgora(final Long agoraId, final Long memberId) {
        Agora agora = findAgoraById(agoraId);
        markEndVoted(agoraId, memberId);
        agora.endVoteAgora(getParticipantCount(agoraId));
        if (agora.isClosed()) {
            sendAgoraEndMessage(agora);
        }

        return new EndVoteAgoraResponse(agora);
    }

    private void markEndVoted(final Long agoraId, final Long memberId) {
        AgoraMember agoraMember = findValidAgoraMember(agoraId, memberId);
        if (agoraMember.getEndVoted()) {
            throw new AlreadyEndVotedException();
        }
        agoraMember.markEndVoted();
    }

    private int getParticipantCount(final Long agoraId) {
        return agoraMemberRepository.countByAgoraIdAndSessionIdIsNotNullAndTypeIsNot(agoraId, OBSERVER);
    }

    private void validateParticipate(Long memberId, Long agoraId, AgoraParticipateRequest request, Agora agora) {
        if (agora.getStatus().equals(CLOSED)) {
            throw new ClosedAgoraException();
        }

        if (!Objects.equals(OBSERVER, request.type())) {
            int typeCount = agoraMemberRepository.countCapacityByAgoraMemberType(agora.getId(), request.type());
            if (agora.isTypeCapacityExceeded(typeCount)) {
                throw new FullAgoraCapacityException();
            }

            boolean existsNickname = agoraMemberRepository.existsNickname(agoraId, request.nickname());
            if (existsNickname) {
                throw new DuplicatedNicknameException(request.nickname());
            }
        }

        agoraMemberRepository.findLatestByAgoraIdAndMemberId(agora.getId(), memberId)
                .ifPresent(agoraMember -> {
                            if (isUnActiveParticipant(agoraMember)) {
                                agoraMember.updateAgoraMember(request.nickname(), request.type());
                            } else {
                                throw new AlreadyParticipateException(agora.getId(), memberId);
                            }
                        }
                );
    }

    private boolean isUnActiveParticipant(AgoraMember agoraMember) {
        return agoraMember.getDisconnectType() || agoraMember.getSocketDisconnectTime() != null;
    }

    private boolean isActiveParticipant(AgoraMember agoraMember) {
        return !agoraMember.getDisconnectType() && agoraMember.getSocketDisconnectTime() == null;
    }

    private void validateClosedAgoraParticipate(Agora agora) {
        if (!agora.getStatus().equals(CLOSED)) {
            throw new ActiveAgoraException();
        }
    }

    @Transactional
    public EndAgoraResponse timeOutAgora(Long agoraId) {
        Agora agora = findAgoraById(agoraId);
        boolean isAgoraClosed = agora.isAgoraClosed(agora);

        if (isAgoraClosed) {
            throw new InvalidAgoraStatusException(AgoraStatus.RUNNING);
        } else {
            agora.endAgora();
            agoraVoteService.removeVotes(agoraId);
            sendAgoraEndMessage(agora);

            return new EndAgoraResponse(agora);
        }
    }

    private void sendAgoraEndMessage(Agora agora) {
        EndNotificationResponse notification = new EndNotificationResponse(ChatType.DISCUSSION_END,
                new EndNotificationResponse.EndAgoraData(agora));

        messagingTemplate.convertAndSend(AGORA_TOPIC + agora.getId(), notification);
    }
}
