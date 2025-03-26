package io.hhplus.tdd.point.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserPointTest {

    @DisplayName("empty 메서드를 호출하면 요청받은 id와 0 포인트를 가진 객체를 생성한다.")
    @Test
    void empty() {
        // given
        Long id = 1L;

        // when
        UserPoint userPoint = UserPoint.empty(id);

        // then
        assertThat(userPoint.id()).isEqualTo(id);
        assertThat(userPoint.point()).isEqualTo(0L);
    }

}