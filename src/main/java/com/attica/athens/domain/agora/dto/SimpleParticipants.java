package com.attica.athens.domain.agora.dto;

public record SimpleParticipants(
        int pros,
        int cons,
        int observer
) {

    public SimpleParticipants(Long pros, Long cons, Long observer) {
        this(pros.intValue(), cons.intValue(), observer.intValue());
    }
}
