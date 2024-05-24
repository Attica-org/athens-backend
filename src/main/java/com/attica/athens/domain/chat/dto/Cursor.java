package com.attica.athens.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Optional;

public record Cursor(Long key, @JsonIgnore Optional<Integer> size) {

    public static final Long NONE_KEY = -1L;
    public static final int DEFAULT_SIZE = 10;

    public Cursor(Long key) {
        this(key, Optional.of(DEFAULT_SIZE));
    }

    public int getEffectiveSize() {
        return size.orElse(DEFAULT_SIZE);
    }

    public Boolean hasKey() {
        return key != null && !key.equals(NONE_KEY);
    }

    public Cursor next(Long key) {
        return new Cursor(key, size);
    }
}
