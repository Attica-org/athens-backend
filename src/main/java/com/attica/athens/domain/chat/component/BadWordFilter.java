package com.attica.athens.domain.chat.component;

import com.attica.athens.domain.chat.dao.BadWordRepository;
import com.attica.athens.domain.chat.domain.BadWord;
import com.attica.athens.domain.chat.domain.FilterResult;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;
import org.springframework.stereotype.Component;

@Component
public class BadWordFilter {
    private Trie trie;
    private final BadWordRepository badWordRepository;

    public BadWordFilter(BadWordRepository badWordRepository) {
        this.badWordRepository = badWordRepository;
        init();
    }

    public void init() {
        this.trie = buildTrie();
    }

    private Trie buildTrie() {
        List<String> badwords = badWordRepository.findAll()
                .stream()
                .map(BadWord::getWord)
                .collect(Collectors.toList());
        return Trie.builder().addKeywords(badwords).build();
    }

    public FilterResult filter(String text) {
        Collection<Emit> emits = trie.parseText(text);
        return new FilterResult(text, emits);
    }
}
