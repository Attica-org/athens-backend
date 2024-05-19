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
import com.attica.athens.domain.agora.exception.NotFoundCategoryException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AgoraService {

    private final AgoraRepository agoraRepository;
    private final CategoryRepository categoryRepository;

    public AgoraService(AgoraRepository agoraRepository, CategoryRepository categoryRepository) {
        this.agoraRepository = agoraRepository;
        this.categoryRepository = categoryRepository;
    }

    public AgoraSlice<SimpleAgoraResult> findAgoraByKeyword(final String agoraName, final SearchKeywordRequest request) {
        return agoraRepository.findAgoraByKeyword(request.next(), request.getStatus(), agoraName);
    }

    public AgoraSlice<SimpleAgoraResult> findAgoraByCategory(final SearchCategoryRequest request) {
        List<Long> categoryIds = findParentCategoryById(request.category());
        return agoraRepository.findAgoraByCategory(request.next(), request.getStatus(), categoryIds);
    }

    @Transactional
    public CreateAgoraResponse create(final AgoraCreateRequest request) {
        Category category = findByCategory(request.categoryId());

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
}