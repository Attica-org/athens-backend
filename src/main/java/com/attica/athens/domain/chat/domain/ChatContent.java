package com.attica.athens.domain.chat.domain;

import com.attica.athens.domain.chat.exception.ContentEmptyException;
import com.attica.athens.domain.chat.exception.ContentExceedException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatContent {

    @Lob
    @Column(nullable = false)
    private String content;

    public ChatContent(String content) {
        validateContent(content);
        this.content = content;
    }

    private void validateContent(String content) {
        Objects.requireNonNull(content, "content must not be null");
        if (content.trim().isEmpty()) {
            throw new ContentEmptyException();
        }
        if (content.length() > 10000) {
            throw new ContentExceedException();
        }
    }

    public String getContent() {
        return content;
    }
}
