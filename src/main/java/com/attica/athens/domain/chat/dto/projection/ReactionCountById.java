package com.attica.athens.domain.chat.dto.projection;

import com.attica.athens.domain.chat.domain.ReactionType;

public interface ReactionCountById {
    Long getChatId();

    ReactionType getType();

    Long getCount();
}
