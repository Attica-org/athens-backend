package com.attica.athens.domain.chat.application;

import com.attica.athens.domain.agora.dao.AgoraRepository;
import com.attica.athens.domain.agora.domain.Agora;
import com.attica.athens.domain.agoraUser.dao.AgoraUserRepository;
import com.attica.athens.domain.chat.dto.response.SendMetaResponse;
import com.attica.athens.domain.chat.dto.response.SendMetaResponse.MetaData;
import com.attica.athens.domain.chat.dto.response.SendMetaResponse.ParticipantsInfo;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatQueryService {

    private final AgoraRepository agoraRepository;
    private final AgoraUserRepository agoraUserRepository;

    public SendMetaResponse sendMeta(Long agoraId) {

        Agora agora = findAgoraById(agoraId);

        List<ParticipantsInfo> participantsInfos = agoraUserRepository.countAgoraUsersByType(agoraId);
        MetaData metaData = new MetaData(agora, participantsInfos);

        return new SendMetaResponse(metaData);
    }

    private Agora findAgoraById(Long agoraId) {
        return agoraRepository.findById(agoraId)
                .orElseThrow(() -> new IllegalArgumentException("Agora is not exist."));
    }
}
