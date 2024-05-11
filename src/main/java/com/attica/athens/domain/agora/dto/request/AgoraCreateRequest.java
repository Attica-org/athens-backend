package com.attica.athens.domain.agora.dto.request;

import com.attica.athens.domain.agora.domain.Category;
import java.time.LocalTime;

public record AgoraCreateRequest(
        String title,
        Integer capacity,
        Integer duration,
        String color,
        Category code
) {

    static final int DIVISION_MINUTE = 60;

    public LocalTime getDuration() {
        if (duration >= 60) {
            int hour = duration / DIVISION_MINUTE;
            int minute = duration - hour * DIVISION_MINUTE;
            return LocalTime.of(hour, minute);
        } else {
            return LocalTime.of(0, duration);
        }
    }
}
