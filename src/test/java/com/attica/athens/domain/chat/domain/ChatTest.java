package com.attica.athens.domain.chat.domain;

import static com.attica.athens.support.TestDoubleFactory.createBasicAgoraMember;
import static com.attica.athens.support.TestDoubleFactory.createBasicChatContent;
import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import com.attica.athens.domain.agoraMember.domain.AgoraMember;
import com.attica.athens.global.auth.exception.NullFieldException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("채팅 테스트")
class ChatTest {

    @Nested
    @DisplayName("채팅 생성 테스트")
<<<<<<< HEAD
    public class ChatCreationTest {
=======
    public class ChatCreationTest extends TestDoubleFactory {
>>>>>>> 7d1681a (test: Chats 도메인 테스트를 작성한다.)

        @Test
        @DisplayName("유효한 파라미터가 주어지면 채팅이 올바르게 생성된다")
        void givenValidParameters_whenCreateChat_thenChatIsCreatedCorrectly() {
            // Given
            final ChatType chatType = ChatType.CHAT;
            final ChatContent content = createBasicChatContent();
<<<<<<< HEAD
            final AgoraMember agoraMember = createBasicAgoraMember();
=======
            final AgoraUser agoraUser = createBasicAgoraUser();
>>>>>>> 7d1681a (test: Chats 도메인 테스트를 작성한다.)

            // When
            final Chat chat = new Chat(chatType, content, agoraMember);

            // Then
            then(chat.getType()).isEqualTo(chatType);
            then(chat.getContent()).isEqualTo(content);
            then(chat.getAgoraMember()).isEqualTo(agoraMember);
        }

        @Test
        @DisplayName("채팅의 타입이 주어지지 않으면 예외가 발생한다")
        void givenNoChatType_whenCreateChat_thenThrowException() {
            // Given
            final ChatContent content = createBasicChatContent();
<<<<<<< HEAD
            final AgoraMember agoraMember = createBasicAgoraMember();
=======
            final AgoraUser agoraUser = createBasicAgoraUser();
>>>>>>> 7d1681a (test: Chats 도메인 테스트를 작성한다.)

            // When, Then
            thenThrownBy(() -> new Chat(null, content, agoraMember))
                    .isInstanceOf(NullFieldException.class)
                    .hasMessage("The field type must not be null");
        }

        @Test
        @DisplayName("채팅의 내용이 주어지지 않으면 예외가 발생한다")
        void givenNoContent_whenCreateChat_thenThrowException() {
            // Given
            final ChatType chatType = ChatType.CHAT;
<<<<<<< HEAD
            final AgoraMember agoraMember = createBasicAgoraMember();
=======
            final AgoraUser agoraUser = createBasicAgoraUser();
>>>>>>> 7d1681a (test: Chats 도메인 테스트를 작성한다.)

            // When, Then
            thenThrownBy(() -> new Chat(chatType, null, agoraMember))
                    .isInstanceOf(NullFieldException.class)
                    .hasMessage("The field content must not be null");
        }

        @Test
        @DisplayName("채팅의 회원이 주어지지 않으면 예외가 발생한다")
        void givenNoAgoraMember_whenCreateChat_thenThrowException() {
            // Given
            final ChatType chatType = ChatType.CHAT;
            final ChatContent content = createBasicChatContent();

            // When, Then
            thenThrownBy(() -> new Chat(chatType, content, null))
                    .isInstanceOf(NullFieldException.class)
                    .hasMessage("The field agoraMember must not be null");
        }
    }
}
