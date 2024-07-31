package com.attica.athens.domain.chat.dto;

import com.attica.athens.domain.chat.domain.Chats;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Optional;

public record Cursor(Long key, @JsonIgnore Optional<Integer> size) {

    public static final Long NONE_KEY = -1L;
    public static final int DEFAULT_SIZE = 10;

    public Boolean hasKey() {
        return key != null && !key.equals(NONE_KEY);
    }

    public Cursor next(Long key) {
        return new Cursor(key, size);
    }

    public Cursor calculateNext(final Chats chats) {
        if (chats.isEmpty() || chats.isLastChat()) {
            return this.next(Cursor.NONE_KEY);
        }
        return chats.findMinChatId()
                .map(this::next)
                .orElse(this.next(NONE_KEY));
    }

    public int getEffectiveSize() {
        return size.orElse(DEFAULT_SIZE);
    }
}
