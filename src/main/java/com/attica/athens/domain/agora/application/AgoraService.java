package com.attica.athens.domain.agora.application;

import com.attica.athens.domain.agora.dao.AgoraRepository;
import com.attica.athens.domain.agora.dao.CategoryRepository;
import com.attica.athens.domain.agora.domain.Agora;
import com.attica.athens.domain.agora.domain.AgoraStatus;
import com.attica.athens.domain.agora.domain.Category;
import com.attica.athens.domain.agora.dto.AgoraSlice;
import com.attica.athens.domain.agora.dto.SimpleAgoraResult;
import com.attica.athens.domain.agora.dto.request.AgoraCreateRequest;
import com.attica.athens.domain.agora.dto.request.SearchCategoryRequest;
import com.attica.athens.domain.agora.dto.request.SearchKeywordRequest;
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

    public AgoraSlice<SimpleAgoraResult> findAgoraByKeyword(final SearchKeywordRequest request) {
        AgoraStatus status = AgoraStatus.of(request.status());

        return agoraRepository.findAgoraByKeyword(request.next(), status, request.agoraName());
    }

    public AgoraSlice<SimpleAgoraResult> findAgoraByCategory(final SearchCategoryRequest request) {
        AgoraStatus status = AgoraStatus.of(request.status());
        List<String> categories = categoryRepository.findParentCodeByCategory(request.category());

        return agoraRepository.findAgoraByCategory(request.next(), status, categories);
    }

    public Agora create(final AgoraCreateRequest request) {
        Category category = categoryRepository.findCategoryByName(request.code())
            .orElseThrow(() -> new RuntimeException("category not found"));

        return agoraRepository.save(createAgora(request, category));
    }

    private Agora createAgora(final AgoraCreateRequest request, final Category category) {
        return Agora.createAgora(request.title(),
            request.capacity(),
            request.duration(),
            request.color(),
            category);
    }
}