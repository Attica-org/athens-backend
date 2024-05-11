//package com.attica.athens.agora.service.v1;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//import com.attica.athens.agora.domain.Agora;
//import com.attica.athens.agora.domain.Category;
//import com.attica.athens.agora.domain.CategoryName;
//import com.attica.athens.agora.dto.request.CreateAgoraRequestDto;
//import com.attica.athens.agora.dao.CategoryRepository;
//import java.time.LocalTime;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//@SpringBootTest
//class AgoraServiceV1Test {
//
//    @Autowired
//    private CategoryRepository categoryRepository;
//
//    @Autowired
//    private AgoraServiceV1 agoraServiceV1;
//
//    private Category category;
//
//    @BeforeEach
//    void setup() {
//        category = Category.builder()
//            .categoryName(CategoryName.CT_0001)
//            .category(null)
//            .level(1)
//            .name(CategoryName.CT_0001.code())
//            .build();
//        categoryRepository.save(category);
//    }
//
//    @Test
//    @DisplayName("아고라를 생성한다.")
//    void createAgora() {
//        // given
//        CreateAgoraRequestDto dto = new CreateAgoraRequestDto(
//            "test title", 5, 120, null, category);
//
//        // when
//        Agora createdAgora = agoraServiceV1.create(dto);
//
//        // then
//        assertThat(createdAgora.getId()).isNotNegative();
//        assertThat(createdAgora.getTitle()).isEqualTo("test title");
//        assertThat(createdAgora.getCapacity()).isEqualTo(5);
//        assertThat(createdAgora.getDuration()).isEqualTo(LocalTime.of(2, 0));
//        assertThat(createdAgora.getCode()).isEqualTo(category);
//    }
//
//}
