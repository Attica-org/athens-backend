package com.attica.athens.domain.agora.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.attica.athens.domain.agora.dao.AgoraRepository;
import com.attica.athens.domain.agora.dao.PopularRepository;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PopularServiceTest {

    AgoraRepository mockAgoraRepository;
    PopularRepository mockPopularRepository;
    PopularService popularService;

    @BeforeEach
    void setup() {
        mockAgoraRepository = mock(AgoraRepository.class);
        mockPopularRepository = mock(PopularRepository.class);
        popularService = new PopularService(mockAgoraRepository, mockPopularRepository);
    }

    @Test
    void 성공_스케줄링실행_내부메소드호출() {
        // given & when
        popularService.calculatePopularAgoraMetrics();

        // then
        verify(mockPopularRepository, times(1)).deleteAll();
        verify(mockAgoraRepository, times(1)).findAgoraWithMetricsByDateRange(anyInt(), anyInt(), any(), any());
        verify(mockPopularRepository, times(1)).saveAll(anyList());
    }

    @Test
    void 성공_스케줄링실행_로그출력() {
        // given
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // when
        popularService.calculatePopularAgoraMetrics();
        String output = outContent.toString();

        // then
        assertTrue(output.contains("스케줄링 작업 시작: calculatePopularAgoraMetrics"));
        assertTrue(output.contains("스케줄링 작업 완료: calculatePopularAgoraMetrics"));
    }
}
