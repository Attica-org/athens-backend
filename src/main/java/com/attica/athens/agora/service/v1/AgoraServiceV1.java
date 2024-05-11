package com.attica.athens.agora.service.v1;

import com.attica.athens.agora.domain.Agora;
import com.attica.athens.agora.dto.CreateAgoraRequestDto;
import com.attica.athens.agora.repository.AgoraRepository;
import com.attica.athens.agora.repository.CategoryRepository;
import org.springframework.stereotype.Service;

@Service
public class AgoraServiceV1 {

    private final AgoraRepository agoraRepository;
    private final CategoryRepository categoryRepository;

    public AgoraServiceV1(AgoraRepository agoraRepository, CategoryRepository categoryRepository) {
        this.agoraRepository = agoraRepository;
        this.categoryRepository = categoryRepository;
    }

    public Agora create(final CreateAgoraRequestDto requestDto) {
        categoryRepository.findCategoryByName(requestDto.code().getName())
            .orElseThrow(() -> new RuntimeException("category not found"));

        Agora createdAgora = agoraRepository.save(createAgora(requestDto));
        return createdAgora;
    }

    private Agora createAgora(CreateAgoraRequestDto requestDto) {
        return Agora.builder()
            .title(requestDto.title())
            .capacity(requestDto.capacity())
            .duration(requestDto.getDuration())
            .code(requestDto.code())
            .build();
    }
}