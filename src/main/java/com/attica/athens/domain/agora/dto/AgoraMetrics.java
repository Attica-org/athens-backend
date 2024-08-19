package com.attica.athens.domain.agora.dto;

public record AgoraMetrics(long agoraId, int membersCount, int chatCount) {

    public AgoraMetrics(long agoraId, long memberCount, long chatCount) {
        this(agoraId, safeLongToInt(memberCount), safeLongToInt(chatCount));
    }

    private static int safeLongToInt(long value) {
        if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Value out of range for int: " + value);
        }

        return (int) value;
    }
}


