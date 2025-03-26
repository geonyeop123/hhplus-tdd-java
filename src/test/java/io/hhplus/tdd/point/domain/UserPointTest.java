package io.hhplus.tdd.point.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserPointTest {

    @DisplayName("charge")
    @Nested
    class charge {

        @DisplayName("정상적인 충전 금액으로 충전하는 경우 오류가 발생하지 않는다.")
        @Test
        void success() {
            // given
            long id = 1L;
            long chargePoint = 10L;
            UserPoint userPoint = UserPoint.empty(id);

            // when // then
            UserPoint charge = userPoint.charge(chargePoint);
            assertThat(charge.point()).isEqualTo(chargePoint);
            assertThat(charge.id()).isEqualTo(id);

        }

        @DisplayName("충전 이후 잔고가 10,000원 이상인 경우 IllegalArgumentException이 발생한다.")
        @Test
        void overCharge() {
            // given
            long id = 1L;
            long chargePoint = 10000L;
            UserPoint userPoint = UserPoint.empty(id);

            // when // then
            assertThatThrownBy(() -> userPoint.charge(chargePoint))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("잔고는 10,000원 이상을 넘을 수 없습니다.");
        }

        @DisplayName("1원 이상부터 충전할 수 있다.")
        @Test
        void zeroCharge() {
            // given
            long id = 1L;
            long chargePoint = 0L;
            UserPoint userPoint = UserPoint.empty(id);

            // when // then
            assertThatThrownBy(() -> userPoint.charge(chargePoint))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("1원 이상부터 충전이 가능합니다.");
        }
    }

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