package com.attica.athens.domain.chat.domain;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import com.attica.athens.domain.agoraUser.domain.AgoraUser;
import com.attica.athens.domain.agoraUser.domain.AgoraUserType;
import com.attica.athens.global.auth.exception.NullFieldException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("채팅 테스트")
class ChatTest {

    @Nested
    @DisplayName("채팅 생성 테스트")
    public class ChatCreationTest {

        private static AgoraUser createTestAgoraUser() {
            return AgoraUser.builder()
                    .type(AgoraUserType.PROS)
                    .build();
        }

        @Test
        @DisplayName("유효한 파라미터가 주어지면 채팅이 올바르게 생성된다")
        void givenValidParameters_whenCreateChat_thenChatIsCreatedCorrectly() {
            // Given
            final ChatType chatType = ChatType.CHAT;
            final ChatContent content = new ChatContent("안녕");
            final AgoraUser agoraUser = createTestAgoraUser();

            // When
            final Chat chat = new Chat(chatType, content, agoraUser);

            // Then
            then(chat.getType()).isEqualTo(chatType);
            then(chat.getContent()).isEqualTo(content);
            then(chat.getAgoraUser()).isEqualTo(agoraUser);
        }

        @Test
        @DisplayName("채팅의 타입이 주어지지 않으면 예외가 발생한다")
        void givenNoChatType_whenCreateChat_thenThrowException() {
            // Given
            final ChatContent content = new ChatContent("안녕");
            final AgoraUser agoraUser = createTestAgoraUser();

            // When, Then
            thenThrownBy(() -> new Chat(null, content, agoraUser))
                    .isInstanceOf(NullFieldException.class)
                    .hasMessage("The field type must not be null");
        }

        @Test
        @DisplayName("채팅의 내용이 주어지지 않으면 예외가 발생한다")
        void givenNoContent_whenCreateChat_thenThrowException() {
            // Given
            final ChatType chatType = ChatType.CHAT;
            final AgoraUser agoraUser = createTestAgoraUser();

            // When, Then
            thenThrownBy(() -> new Chat(chatType, null, agoraUser))
                    .isInstanceOf(NullFieldException.class)
                    .hasMessage("The field content must not be null");
        }

        @Test
        @DisplayName("채팅의 사용자가 주어지지 않으면 예외가 발생한다")
        void givenNoAgoraUser_whenCreateChat_thenThrowException() {
            // Given
            final ChatType chatType = ChatType.CHAT;
            final ChatContent content = new ChatContent("안녕");

            // When, Then
            thenThrownBy(() -> new Chat(chatType, content, null))
                    .isInstanceOf(NullFieldException.class)
                    .hasMessage("The field agoraUser must not be null");
        }
    }
}
