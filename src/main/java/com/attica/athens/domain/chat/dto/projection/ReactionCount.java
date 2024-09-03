package com.attica.athens.domain.chat.dto.projection;

import com.attica.athens.domain.chat.domain.ReactionType;

public interface ReactionCount {
    ReactionType getType();

    Long getCount();
}
