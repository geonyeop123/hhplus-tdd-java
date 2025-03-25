package io.hhplus.tdd.point.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class UserPointTest {

    @DisplayName("포인트를 충전할 수 있다.")
    @Test
    void chargePoint() {
        // given
        UserPoint userPoint = UserPoint.empty(1);

        // when
        UserPoint chargePoint = userPoint.chargePoint(10);

        // then
        assertThat(chargePoint.point()).isEqualTo(10);

    }

    @DisplayName("1포인트 이상만 충전할 수 있다.")
    @Test
    void ZeroPointNotCharge() {
        // given
        UserPoint userPoint = UserPoint.empty(1);

        // when // then
        assertThatThrownBy(() -> userPoint.chargePoint(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("1포인트 이상부터 충전이 가능합니다.");
    }

    @DisplayName("포인트 잔고는 1억을 넘을 수 없다.")
    @Test
    void maxPointCharge() {
        // given
        UserPoint userPoint = UserPoint.empty(1);

        // when // then
        assertThatThrownBy(() -> userPoint.chargePoint(100_000_001))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("잔고는 100000000원을 초과할 수 없습니다.");
    }

    @DisplayName("포인트를 사용할 수 있다.")
    @Test
    void usePoint() {
        // given
        UserPoint userPoint = new UserPoint(1, 100, System.currentTimeMillis());

        // when
        UserPoint usedPoint = userPoint.usePoint(50);

        // then
        assertThat(usedPoint.point()).isEqualTo(50);
    }

    @DisplayName("사용할 수 있는 포인트보다 많은 포인트는 사용할 수 없다.")
    @Test
    void usePointOverBalance() {
        // given
        UserPoint userPoint = new UserPoint(1, 100, System.currentTimeMillis());

        // when // then
        assertThatThrownBy(() -> userPoint.usePoint(150))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("잔고가 부족합니다.");
    }

}