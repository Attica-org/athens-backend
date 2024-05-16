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
        List<String> categories = findParentCodeByCategory(request.category());
        return agoraRepository.findAgoraByCategory(request.next(), request.getStatus(), categories);
    }

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

    public List<String> findParentCodeByCategory(String categoryId) {
        List<String> parentCodes = new ArrayList<>();
        String currentCategory = categoryId;

        while (currentCategory != null) {
            parentCodes.add(currentCategory);
            Category entity = findByCategory(currentCategory);

            if (entity == null || entity.getParentCode() == null) {
                break;
            }

            currentCategory = entity.getParentCode().getCode();
        }

        return parentCodes;
    }

    private Category findByCategory(String categoryId) {
        return categoryRepository.findCategoryByName(categoryId)
            .orElseThrow(() -> new NotFoundCategoryException(categoryId));
    }
}