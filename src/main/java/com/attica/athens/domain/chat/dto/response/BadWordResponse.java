package com.attica.athens.domain.chat.dto.response;

import com.attica.athens.domain.chat.domain.FilterResult;
import java.util.List;

public record BadWordResponse(boolean hasBadWord, List<BadWordInfo> badword) {
    public BadWordResponse(FilterResult filterResult) {
        this(!filterResult.getBadword().isEmpty(),
                filterResult.getBadword()
                        .stream()
                        .map(emit -> new BadWordInfo(emit.getStart(), emit.getEnd(), emit.getKeyword()))
                        .toList());
    }

    private record BadWordInfo(int start, int end, String keyword) {
    }
}
