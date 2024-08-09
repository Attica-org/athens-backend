package com.attica.athens.domain.agora.domain;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class AgoraConstants {

    public static final double END_VOTE_RATIO = 2.0 / 3.0;
    public static final double USER_WEIGHT = 0.5;
    public static final double CHAT_WEIGHT = 0.5;
    public static final int COUNT_MULTIPLIER = 1000000;
    public static final long DEFAULT_METRIC_COUNT = 0L;
    public static final long HOUR_INTERVAL = 1L;
    public static final int MIN_MEMBER_COUNT = 2;
    public static final int MIN_CHAT_COUNT = 1;
    public static final double INVERSE_BASE = 1.0;
    public static final double ZERO_VALUE = 0.0;
}
