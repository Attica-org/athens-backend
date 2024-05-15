package com.attica.athens.domain.agora.application;

import com.attica.athens.domain.agora.dao.AgoraRepository;
import com.attica.athens.domain.agora.dao.CategoryRepository;
import com.attica.athens.domain.agora.domain.Agora;
import com.attica.athens.domain.agora.domain.Category;
import com.attica.athens.domain.agora.dto.SimpleAgoraResult;
import com.attica.athens.domain.agora.dto.request.AgoraCreateRequest;
import com.attica.athens.domain.agora.dto.request.SearchCategoryRequest;
import com.attica.athens.domain.agora.dto.request.SearchKeywordRequest;
import com.attica.athens.domain.agora.dto.response.AgoraSlice;
import com.attica.athens.domain.agora.dto.response.CreateAgoraResponse;
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
        return agoraRepository.findAgoraByKeyword(request.next(), request.status(), request.agoraName());
    }

    public AgoraSlice<SimpleAgoraResult> findAgoraByCategory(final SearchCategoryRequest request) {
        List<String> categories = categoryRepository.findParentCodeByCategory(request.category());
        return agoraRepository.findAgoraByCategory(request.next(), request.status(), categories);
    }

    public CreateAgoraResponse create(final AgoraCreateRequest request) {
        Category category = categoryRepository.findCategoryByName(request.code())
            .orElseThrow(() -> new RuntimeException());

        Agora created = agoraRepository.save(createAgora(request, category));
        return new CreateAgoraResponse(created.getId());
    }

    private Agora createAgora(final AgoraCreateRequest request, final Category category) {
        return Agora.createAgora(request.title(),
            request.capacity(),
            request.duration(),
            request.color(),
            category);
    }
}