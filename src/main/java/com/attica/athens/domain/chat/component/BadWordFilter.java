package com.attica.athens.domain.chat.component;

import com.attica.athens.domain.chat.dao.BadWordRepository;
import com.attica.athens.domain.chat.domain.BadWord;
import com.attica.athens.domain.chat.domain.FilterResult;
import java.util.ArrayList;
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
        Collection<Emit> allEmits = trie.parseText(text);
        Collection<Emit> filteredEmits = removeShorterOverlappingMatches(allEmits);
        return new FilterResult(text, filteredEmits);
    }

    private Collection<Emit> removeShorterOverlappingMatches(Collection<Emit> emits) {
        List<Emit> emitList = new ArrayList<>(emits);
        List<Emit> result = new ArrayList<>();

        emitList.sort((e1, e2) -> Integer.compare(e1.getStart(), e2.getStart()));

        for (int i = 0; i < emitList.size(); i++) {
            Emit current = emitList.get(i);
            boolean isContained = false;

            for (int j = 0; j < emitList.size(); j++) {
                if (i == j) {
                    continue;
                }

                Emit other = emitList.get(j);

                if (current.getStart() >= other.getStart() && current.getEnd() <= other.getEnd()
                        && current.getKeyword().length() < other.getKeyword().length()) {
                    isContained = true;
                    break;
                }
            }

            if (!isContained) {
                result.add(current);
            }
        }

        return result;
    }
}
