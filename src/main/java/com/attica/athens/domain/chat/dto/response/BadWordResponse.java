package com.attica.athens.domain.chat.dto.response;

import com.attica.athens.domain.chat.domain.FilterResult;
import java.util.List;
import java.util.stream.Collectors;

public record BadWordResponse(List<BadWordInfo> badword) {
    public BadWordResponse(FilterResult filterResult) {
        this(filterResult.getBadword().stream()
                        .map(emit -> new BadWordInfo(emit.getStart(), emit.getEnd(), emit.getKeyword())).collect(
                                Collectors.toList()));
    }
    public record BadWordInfo(int start, int end, String keyword) {
    }
}
