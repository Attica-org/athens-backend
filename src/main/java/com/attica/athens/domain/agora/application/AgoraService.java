package com.attica.athens.domain.agora.application;

import com.attica.athens.domain.agora.dao.AgoraRepository;
import com.attica.athens.domain.agora.dao.CategoryRepository;
import com.attica.athens.domain.agora.domain.Agora;
import com.attica.athens.domain.agora.dto.request.AgoraCreateRequest;
import org.springframework.stereotype.Service;

@Service
public class AgoraService {

    private final AgoraRepository agoraRepository;
    private final CategoryRepository categoryRepository;

    public AgoraService(AgoraRepository agoraRepository, CategoryRepository categoryRepository) {
        this.agoraRepository = agoraRepository;
        this.categoryRepository = categoryRepository;
    }

    public Agora create(final AgoraCreateRequest requestDto) {
        categoryRepository.findCategoryByName(requestDto.code().getName())
                .orElseThrow(() -> new RuntimeException("category not found"));

        return agoraRepository.save(createAgora(requestDto));
    }

    private Agora createAgora(AgoraCreateRequest requestDto) {
        return Agora.createAgora(requestDto.title(),
                requestDto.capacity(),
                requestDto.duration(),
                requestDto.color(),
                requestDto.code());
    }
}
