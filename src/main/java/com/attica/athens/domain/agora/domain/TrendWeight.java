package com.attica.athens.domain.agora.domain;

public enum TrendWeight {
    MEMBER_WEIGHT(0.5),
    CHAT_WEIGHT(0.5),
    COUNT_MULTIPLIER(1000000),
    DEFAULT_METRIC_COUNT(0),
    HOUR_INTERVAL(1L),
    MIN_MEMBER_COUNT(2),
    MIN_CHAT_COUNT(1),
    INVERSE_BASE(1.0),
    ZERO_VALUE(0.0);

    private final double value;

    TrendWeight(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }
}
