package com.attica.athens.domain.agora.application;

import com.attica.athens.domain.agora.dao.AgoraRepository;
import com.attica.athens.domain.agora.dao.CategoryRepository;
import com.attica.athens.domain.agora.domain.Agora;
import com.attica.athens.domain.agora.domain.AgoraStatus;
import com.attica.athens.domain.agora.domain.Category;
import com.attica.athens.domain.agora.dto.AgoraSlice;
import com.attica.athens.domain.agora.dto.SimpleAgoraResult;
import com.attica.athens.domain.agora.dto.request.AgoraCreateRequest;
import com.attica.athens.domain.agora.dto.request.SearchCategoryRequestDto;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AgoraService {

    private final AgoraRepository agoraRepository;
    private final CategoryRepository categoryRepository;

    public AgoraService(AgoraRepository agoraRepository, CategoryRepository categoryRepository) {
        this.agoraRepository = agoraRepository;
        this.categoryRepository = categoryRepository;
    }

    public AgoraSlice<SimpleAgoraResult> findAgoraByCategory(SearchCategoryRequestDto request) {
        AgoraStatus status = AgoraStatus.of(request.status());
        List<String> categories = categoryRepository.findParentCodeByCategory(request.category());
        return agoraRepository.findAgoraByCategory(request.next(), status, categories);
    }

    public Agora create(final AgoraCreateRequest requestDto) {
        Category category = categoryRepository.findCategoryByName(requestDto.code())
            .orElseThrow(() -> new RuntimeException("category not found"));

        return agoraRepository.save(createAgora(requestDto, category));
    }


    private Agora createAgora(AgoraCreateRequest requestDto, Category category) {
        return Agora.createAgora(requestDto.title(),
            requestDto.capacity(),
            requestDto.duration(),
            requestDto.color(),
            category);
    }
}