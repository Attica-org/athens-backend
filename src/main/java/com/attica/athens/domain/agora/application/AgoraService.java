package com.attica.athens.domain.agora.application;

import com.attica.athens.domain.agora.dao.AgoraRepository;
import com.attica.athens.domain.agora.dao.CategoryRepository;
import com.attica.athens.domain.agora.domain.Agora;
import com.attica.athens.domain.agora.domain.Category;
import com.attica.athens.domain.agora.dto.SimpleAgoraResult;
import com.attica.athens.domain.agora.dto.request.AgoraCreateRequest;
import com.attica.athens.domain.agora.dto.request.AgoraParticipateRequest;
import com.attica.athens.domain.agora.dto.request.SearchCategoryRequest;
import com.attica.athens.domain.agora.dto.request.SearchKeywordRequest;
import com.attica.athens.domain.agora.dto.response.AgoraIdResponse;
import com.attica.athens.domain.agora.dto.response.AgoraParticipateResponse;
import com.attica.athens.domain.agora.dto.response.AgoraSlice;
import com.attica.athens.domain.agora.dto.response.AgoraTitleResponse;
import com.attica.athens.domain.agora.dto.response.CreateAgoraResponse;
import com.attica.athens.domain.agora.dto.response.EndNotificationResponse;
import com.attica.athens.domain.agora.dto.response.EndVoteAgoraResponse;
import com.attica.athens.domain.agora.dto.response.StartAgoraResponse;
import com.attica.athens.domain.agora.dto.response.StartNotificationResponse;
import com.attica.athens.domain.agora.exception.AlreadyParticipateException;
import com.attica.athens.domain.agora.exception.DuplicatedNicknameException;
import com.attica.athens.domain.agora.exception.FullAgoraCapacityException;
import com.attica.athens.domain.agora.exception.NotFoundAgoraException;
import com.attica.athens.domain.agora.exception.NotFoundCategoryException;
import com.attica.athens.domain.agoraUser.dao.AgoraUserRepository;
import com.attica.athens.domain.agoraUser.domain.AgoraUser;
import com.attica.athens.domain.agoraUser.domain.AgoraUserType;
import com.attica.athens.domain.agoraUser.exception.AlreadyEndVotedException;
import com.attica.athens.domain.agoraUser.exception.NotFoundAgoraUserException;
import com.attica.athens.domain.chat.domain.ChatType;
import com.attica.athens.domain.user.dao.BaseUserRepository;
import com.attica.athens.domain.user.domain.BaseUser;
import com.attica.athens.domain.user.exception.NotFoundUserException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AgoraService {

    private static final Integer PROS_COUNT = 0;
    private static final Integer CONS_COUNT = 0;

    private final AgoraRepository agoraRepository;
    private final CategoryRepository categoryRepository;
    private final BaseUserRepository baseUserRepository;
    private final AgoraUserRepository agoraUserRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public CreateAgoraResponse create(final AgoraCreateRequest request) {
        Category category = findByCategory(request.categoryId());
        Agora created = agoraRepository.save(createAgora(request, category));

        return new CreateAgoraResponse(created.getId());
    }

    public AgoraSlice<SimpleAgoraResult> findAgoraByKeyword(final String agoraName,
                                                            final SearchKeywordRequest request) {
        return agoraRepository.findAgoraByKeyword(request.next(), request.getStatus(), agoraName);
    }

    public AgoraSlice<SimpleAgoraResult> findAgoraByCategory(final SearchCategoryRequest request) {
        if (request.category() == 1) {
            return agoraRepository.findAgoraByAllCategory(request.next(), request.getStatus());
        }

        List<Long> categoryIds = findParentCategoryById(request.category());

        return agoraRepository.findAgoraByCategory(request.next(), request.getStatus(), categoryIds);
    }

    @Transactional
    public AgoraParticipateResponse participate(final Long userId, final Long agoraId,
                                                final AgoraParticipateRequest request) {
        Agora agora = findAgoraById(agoraId);

        if (!Objects.equals(AgoraUserType.OBSERVER, request.type())) {
            int typeCount = agoraUserRepository.countCapacityByAgoraUserType(agora.getId(), request.type());
            if (typeCount >= agora.getCapacity()) {
                throw new FullAgoraCapacityException(agora.getId(), request.type());
            }
        }

        boolean existsNickname = agoraUserRepository.existsNickname(agoraId, request.nickname());
        if (existsNickname) {
            throw new DuplicatedNicknameException(request.nickname());
        }

        agoraUserRepository.findByAgoraIdAndUserId(agora.getId(), userId)
                .ifPresent(agoraUser -> {
                            throw new AlreadyParticipateException(agora.getId(), userId);
                        }
                );

        AgoraUser created = createAgoraUser(userId, agoraId, request);
        AgoraUser agoraUser = agoraUserRepository.save(created);
        agora.addUser(agoraUser);

        return new AgoraParticipateResponse(created.getAgora().getId(), userId, created.getType());
    }

    private Agora createAgora(final AgoraCreateRequest request, final Category category) {
        return new Agora(request.title(),
                request.capacity(),
                request.duration(),
                request.color(),
                PROS_COUNT, CONS_COUNT,
                category);
    }

    private AgoraUser createAgoraUser(final Long userId, final Long agoraId, final AgoraParticipateRequest request) {
        return new AgoraUser(
                request.type(),
                request.nickname(),
                request.photoNum(),
                findAgoraById(agoraId),
                findUserById(userId)
        );
    }

    private BaseUser findUserById(final Long userId) {
        return baseUserRepository.findById(userId).orElseThrow(() -> new NotFoundUserException(userId));
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

    public AgoraTitleResponse getAgoraTitle(final Long agoraId) {

        Agora agora = findAgoraById(agoraId);

        return new AgoraTitleResponse(agora);
    }

    public AgoraIdResponse getAgoraIdList() {

        List<Long> agoraIdList = agoraRepository.getAgoraIdList();

        return new AgoraIdResponse(agoraIdList);
    }

    @Transactional
    public StartAgoraResponse startAgora(Long agoraId, Long userId) {

        Agora agora = findAgoraById(agoraId);

        boolean isExists = existsByAgoraIdAndUserId(agoraId, userId);
        if (!isExists) {
            throw new NotFoundAgoraUserException(agoraId, userId);
        }

        agora.startAgora();

        sendAgoraStartMessage(agora);

        return new StartAgoraResponse(agora);
    }

    private boolean existsByAgoraIdAndUserId(Long agoraId, Long userId) {
        return agoraUserRepository.existsByAgoraIdAndUserId(agoraId, userId);
    }

    private Agora findAgoraById(Long agoraId) {
        return agoraRepository.findById(agoraId)
                .orElseThrow(() -> new NotFoundAgoraException(agoraId));
    }

    private void sendAgoraStartMessage(Agora agora) {
        StartNotificationResponse notification = new StartNotificationResponse(ChatType.DISCUSSION_START,
                new StartNotificationResponse.StartAgoraData(agora));

        messagingTemplate.convertAndSend("/topic/agoras/" + agora.getId(), notification);
    }

    @Transactional
    public EndVoteAgoraResponse endVoteAgora(Long agoraId, Long userId) {

        Agora agora = findAgoraById(agoraId);

        findAgoraUserAndMarkEndVoted(agoraId, userId);

        int participantCount = agoraUserRepository.countByAgoraId(agoraId);
        agora.endVoteAgora(participantCount);

        sendAgoraEndMessage(agora);

        return new EndVoteAgoraResponse(agora);
    }

    private void findAgoraUserAndMarkEndVoted(Long agoraId, Long userId) {
        AgoraUser agoraUser = findAgoraUserByAgoraIdAndUserId(agoraId, userId);
        if (agoraUser.isEndVoted()) {
            throw new AlreadyEndVotedException();
        }
        agoraUser.markEndVoted();
    }

    private AgoraUser findAgoraUserByAgoraIdAndUserId(Long agoraId, Long userId) {
        return agoraUserRepository.findByAgoraIdAndUserId(agoraId, userId)
                .orElseThrow(() -> new NotFoundAgoraUserException(agoraId, userId));
    }

    private void sendAgoraEndMessage(Agora agora) {
        EndNotificationResponse notification = new EndNotificationResponse(ChatType.DISCUSSION_END,
                new EndNotificationResponse.EndAgoraData(agora));

        messagingTemplate.convertAndSend("/topic/agoras/" + agora.getId(), notification);
    }
}
