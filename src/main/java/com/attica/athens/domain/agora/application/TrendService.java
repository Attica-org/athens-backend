package com.attica.athens.domain.agora.application;

import static com.attica.athens.domain.agora.domain.AgoraConstants.CHAT_WEIGHT;
import static com.attica.athens.domain.agora.domain.AgoraConstants.COUNT_MULTIPLIER;
import static com.attica.athens.domain.agora.domain.AgoraConstants.DEFAULT_METRIC_COUNT;
import static com.attica.athens.domain.agora.domain.AgoraConstants.HOUR_INTERVAL;
import static com.attica.athens.domain.agora.domain.AgoraConstants.INVERSE_BASE;
import static com.attica.athens.domain.agora.domain.AgoraConstants.MIN_CHAT_COUNT;
import static com.attica.athens.domain.agora.domain.AgoraConstants.MIN_MEMBER_COUNT;
import static com.attica.athens.domain.agora.domain.AgoraConstants.USER_WEIGHT;
import static com.attica.athens.domain.agora.domain.AgoraConstants.ZERO_VALUE;

import com.attica.athens.domain.agora.dao.AgoraRepository;
import com.attica.athens.domain.agora.dao.PopularRepository;
import com.attica.athens.domain.agora.domain.Agora;
import com.attica.athens.domain.agora.domain.Trend;
import com.attica.athens.domain.agora.dto.AgoraMetrics;
import com.attica.athens.domain.agora.exception.NotFoundAgoraException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrendService {

    private final AgoraRepository agoraRepository;
    private final PopularRepository popularRepository;

    @Scheduled(cron = "0 0 * * * *")
    public void calculatePopularAgoraMetrics() {
        log.info("스케줄링 작업 시작: calculatePopularAgoraMetrics");

        popularRepository.deleteAll();
        LocalDateTime now = LocalDateTime.now();

        try {
            List<AgoraMetrics> agoras = agoraRepository.findAgoraWithMetricsByDateRange(MIN_MEMBER_COUNT, MIN_CHAT_COUNT, now, now.minusHours(HOUR_INTERVAL));
            Map<AgoraMetrics, Double> scores = getAgoraScore(agoras);
            double maxScore = getMaxScore(agoras, scores);
            normalizedScore(scores, maxScore);
            saveTopPopularAgora(scores);

            log.info("스케줄링 작업 완료: calculatePopularAgoraMetrics");
        } catch (Exception e) {
            log.error("스케줄링 작업 중 오류 발생: calculatePopularAgoraMetrics", e);
        }
    }

    private void saveTopPopularAgora(Map<AgoraMetrics, Double> scores) {
        int size = 10;

        popularRepository.saveAll(
                scores.entrySet().stream()
                .sorted(Entry.<AgoraMetrics, Double>comparingByValue().reversed())
                .limit(size)
                .map(entry -> {
                    long agoraId = entry.getKey().agoraId();
                    Agora agora = agoraRepository.findById(agoraId)
                            .orElseThrow(() -> new NotFoundAgoraException(agoraId));

                    return new Trend(entry.getValue(), agora);
                })
                .toList()
        );
    }

    private void normalizedScore(Map<AgoraMetrics, Double> scores, double maxScore) {
        for (Entry<AgoraMetrics, Double> entry : scores.entrySet()) {
            double normalizedScore = (maxScore != ZERO_VALUE) ? entry.getValue() / maxScore : ZERO_VALUE;
            scores.replace(entry.getKey(), normalizedScore);
        }
    }

    private double getMaxScore(List<AgoraMetrics> agoras, Map<AgoraMetrics, Double> scores) {
        final int maxMembersCount = getMaxMemberCount(agoras);
        final int maxChatCount = getMaxChatCount(agoras);

        final double maxMembersCountInverse = (maxMembersCount != ZERO_VALUE) ? INVERSE_BASE / maxMembersCount : ZERO_VALUE;
        final double maxChatCountInverse = (maxChatCount != ZERO_VALUE) ? INVERSE_BASE / maxChatCount : ZERO_VALUE;

        return calculateScoreAndMaxScore(scores, maxMembersCountInverse, maxChatCountInverse);
    }

    private double calculateScoreAndMaxScore(Map<AgoraMetrics, Double> scores, double maxMembersCountInverse, double maxChatCountInverse) {
        return scores.entrySet().stream()
                .mapToDouble(entry -> {
                    double originalValue = entry.getValue();
                    long agoraMembersCount = (long) (originalValue / COUNT_MULTIPLIER);
                    long agoraChatCount = (long) (originalValue % COUNT_MULTIPLIER);

                    double normalizedMembers = agoraMembersCount * maxMembersCountInverse;
                    double normalizedChats = agoraChatCount * maxChatCountInverse;

                    double score = (USER_WEIGHT * normalizedMembers) + (CHAT_WEIGHT * normalizedChats);
                    entry.setValue(score);
                    return score;
                })
                .max()
                .orElse(ZERO_VALUE);
    }

    private Map<AgoraMetrics, Double> getAgoraScore(List<AgoraMetrics> agoras) {
        return agoras.stream()
                .collect(
                    Collectors.toMap(
                        agora -> agora,
                        agora -> (double) (agora.membersCount() * COUNT_MULTIPLIER + agora.chatCount()))
                    );
    }

    private int getMaxMemberCount(List<AgoraMetrics> agoras) {
        return agoras.stream()
                .max(Comparator.comparingLong(AgoraMetrics::membersCount))
                .map(AgoraMetrics::membersCount)
                .orElse(DEFAULT_METRIC_COUNT);
    }

    private int getMaxChatCount(List<AgoraMetrics> agoras) {
        return agoras.stream()
                .max(Comparator.comparingLong(AgoraMetrics::chatCount))
                .map(AgoraMetrics::chatCount)
                .orElse(DEFAULT_METRIC_COUNT);
    }
}
