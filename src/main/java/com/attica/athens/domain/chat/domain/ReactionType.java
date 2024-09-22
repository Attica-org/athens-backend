package com.attica.athens.domain.chat.domain;

import com.attica.athens.domain.chat.dto.projection.ReactionCount;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public enum ReactionType {
    LIKE,
    DISLIKE,
    LOVE,
    HAPPY,
    SAD;

    private static final EnumMap<ReactionType, Long> EMPTY_ENUM_MAP = new EnumMap<>(ReactionType.class);

    static {
        for (ReactionType reactionType : ReactionType.values()) {
            EMPTY_ENUM_MAP.put(reactionType, 0L);
        }
    }

    public static Map<ReactionType, Long> getReactionTypeEnumMap(final List<ReactionCount> results) {
        Map<ReactionType, Long> counts = new EnumMap<>(EMPTY_ENUM_MAP);
        for (ReactionCount result : results) {
            counts.put(result.getType(), result.getCount());
        }
        return counts;
    }
}
