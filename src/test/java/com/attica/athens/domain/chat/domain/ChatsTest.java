package com.attica.athens.domain.chat.domain;

import static com.attica.athens.support.TestDoubleFactory.createBasicChat;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("채팅 리스트 테스트")
class ChatsTest {

    @ParameterizedTest
    @MethodSource
    @DisplayName("채팅 리스트가 비어있는지 확인한다")
    void givenEmptyChats_whenIsEmptyChats_thenTrue(Chats chats, boolean expected) {
        // given

        // when
        boolean result = chats.isEmpty();

        // then
        then(result).isEqualTo(expected);
    }

    static Stream<Arguments> givenEmptyChats_whenIsEmptyChats_thenTrue() {
        return Stream.of(
                Arguments.of(new Chats(new ArrayList<>()), true),
                Arguments.of(
                        new Chats(List.of(createBasicChat())), false)
        );
    }

    @Test
    @DisplayName("채팅의 최소 ID를 반환한다")
    void givenChats_whenFindMinChatId_thenMinChatId() {
        // given
        Chat chat1 = mock(Chat.class);
        Chat chat2 = mock(Chat.class);
        Chat chat3 = mock(Chat.class);
        Chats chats = new Chats(List.of(chat1, chat2, chat3));

        given(chat1.getId()).willReturn(1L);
        given(chat2.getId()).willReturn(2L);
        given(chat3.getId()).willReturn(3L);

        // when
        final Optional<Long> minChatId = chats.findMinChatId();

        // then
        then(minChatId).isPresent();
        then(minChatId.get()).isEqualTo(1L);
    }
}
