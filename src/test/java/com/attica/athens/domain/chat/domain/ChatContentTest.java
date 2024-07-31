package com.attica.athens.domain.chat.domain;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import com.attica.athens.domain.chat.exception.ContentEmptyException;
import com.attica.athens.domain.chat.exception.ContentExceedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("채팅 내용 테스트")
class ChatContentTest {

    public static final int MAX_LENGTH = 10000;

    @Nested
    @DisplayName("채팅 내용 생성 테스트")
    public class ChatContentCreationTest {

        @Test
        @DisplayName("채팅 내용을 생성한다")
        public void givenContent_whenCreateChatContent_thenChatContentIsCreated() {
            // Given
            String content = "안녕";

            // When
            ChatContent chatContent = new ChatContent(content);

            // Then
            then(chatContent).extracting("content").isEqualTo(content);
        }

        @Test
        @DisplayName("채팅 내용이 없으면 예외를 발생한다")
        public void givenNullContent_whenCreateChatContent_thenThrowException() {
            // When & Then
            thenThrownBy(() -> new ChatContent(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("content must not be null");
        }

        @Test
        @DisplayName("채팅 내용이 빈 문자열이면 예외를 발생한다")
        public void givenEmptyContent_whenCreateChatContent_thenThrowException() {
            // When & Then
            thenThrownBy(() -> new ChatContent(""))
                    .isInstanceOf(ContentEmptyException.class)
                    .hasMessage("Content must not be empty");
        }

        @Test
        @DisplayName("채팅 내용이 최대길이를 초과하면 예외를 발생한다")
        public void givenLongContent_whenCreateChatContent_thenThrowException() {
            // When & Then
            thenThrownBy(() -> new ChatContent("a".repeat(MAX_LENGTH + 1)))
                    .isInstanceOf(ContentExceedException.class)
                    .hasMessage(String.format("Content length exceeds maximum limit of %d characters", MAX_LENGTH));
        }
    }
}
