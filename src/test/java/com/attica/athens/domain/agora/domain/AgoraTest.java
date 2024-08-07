package com.attica.athens.domain.agora.domain;

import static com.attica.athens.domain.agora.domain.AgoraStatus.CLOSED;
import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import com.attica.athens.domain.agora.exception.InvalidAgoraStatusChangeException;
import java.lang.reflect.Field;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("아고라 테스트")
public class AgoraTest {

    @Test
    @DisplayName("아고라 지속시간 이후 아고라 status가 CLOSED가 아니면 false를 반환한다.")
    void testCheckTimeOutAgoraStatusFalse() {
        //given
        Agora agora = Agora.builder().build();
        //when
        boolean isAgoraClosed = agora.isAgoraClosed(agora);
        //then
        then(isAgoraClosed).isEqualTo(false);
    }

    @Test
    @DisplayName("아고라 지속시간 이후 아고라 status가 CLOSED면 true를 반환한다.")
    void testCheckTimeOutAgoraStatusTrue() {
        //given
        Agora agora = Agora.builder().build();
        agora.endAgora();
        //when
        boolean isAgoraClosed = agora.isAgoraClosed(agora);
        //then
        then(isAgoraClosed).isEqualTo(true);
    }

    @Test
    @DisplayName("아고라를 종료시키면 아고라 상태는 CLOSED로 변경되고 종료시간(endTime)필드를 현재 시간으로 초기화한다.")
    void testEndAgoraStatusAndEndTime() {
        //given
        Agora agora = Agora.builder().build();
        //when;
        agora.endAgora();
        //then
        then(agora.getStatus()).isEqualTo(CLOSED);
        then(agora.getEndTime()).isNotNull();
    }

    @ParameterizedTest
    @MethodSource
    @DisplayName("아고라의 현재 상태에 따라 아고라의 상태를 변경할 수 있다.")
    void testChangeStatus(AgoraStatus thisStatus, AgoraStatus status) throws Exception {
        //given
        Agora agora = Agora.builder().build();
        setStatusViaReflection(agora, thisStatus);
        //when;
        agora.changeStatus(status);
        //then
        then(agora.getStatus()).isEqualTo(status);
    }

    static Stream<Arguments> testChangeStatus() {
        return Stream.of(
                Arguments.of(AgoraStatus.QUEUED, AgoraStatus.RUNNING),
                Arguments.of(AgoraStatus.RUNNING, AgoraStatus.CLOSED),
                Arguments.of(AgoraStatus.QUEUED, AgoraStatus.CLOSED)
        );
    }

    @ParameterizedTest
    @MethodSource
    @DisplayName("아고라의 현재 상태와 변경하려는 아고라의 상태가 알맞지 않으면 예외를 발생시킨다.")
    void testInvalidChangeStatus(AgoraStatus thisStatus, AgoraStatus status) throws Exception {
        //given
        Agora agora = Agora.builder().build();
        setStatusViaReflection(agora, thisStatus);
        //when & then
        thenThrownBy(() -> agora.changeStatus(status))
                .isInstanceOf(InvalidAgoraStatusChangeException.class)
                .hasMessage("Invalid agora status transition.");
    }

    static Stream<Arguments> testInvalidChangeStatus() {
        return Stream.of(
                Arguments.of(AgoraStatus.QUEUED, AgoraStatus.QUEUED),
                Arguments.of(AgoraStatus.RUNNING, AgoraStatus.QUEUED),
                Arguments.of(AgoraStatus.RUNNING, AgoraStatus.RUNNING),
                Arguments.of(AgoraStatus.CLOSED, AgoraStatus.QUEUED),
                Arguments.of(AgoraStatus.CLOSED, AgoraStatus.RUNNING),
                Arguments.of(AgoraStatus.CLOSED, AgoraStatus.CLOSED)
        );
    }

    private void setStatusViaReflection(Agora agora, AgoraStatus status) throws Exception {
        Field statusField = Agora.class.getDeclaredField("status");
        statusField.setAccessible(true);
        statusField.set(agora, status);
    }

}
