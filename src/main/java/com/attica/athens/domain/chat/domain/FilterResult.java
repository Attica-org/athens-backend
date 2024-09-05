package com.attica.athens.domain.chat.domain;

import java.util.Collection;
import org.ahocorasick.trie.Emit;

public class FilterResult {
    private final String originalText;
    private final Collection<Emit> badword;

    public FilterResult(String originalText, Collection<Emit> badword) {
        this.originalText = originalText;
        this.badword = badword;
    }

    public String getOriginalText() {
        return originalText;
    }

    public Collection<Emit> getBadword() {
        return badword;
    }
}
