package io.hhplus.tdd.point.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
/*
    기능이 포함된 도메인 객체로 단위 테스트를 작성하게 되었습니다.
 */
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

    /* 충전 메서드의 단위 테스트를 작성하였습니다. */
    @DisplayName("충전")
    @Nested
    class charge {

        @DisplayName("정상적인 충전 금액으로 충전하는 경우 기존 포인트에 충전된 포인트가 더해진 객체를 반환한다.")
        @Test
        void success() {
            // given
            long id = 1L;
            long chargePoint = 10L;
            UserPoint userPoint = new UserPoint(1L, 10L, System.currentTimeMillis());

            // when // then
            UserPoint charge = userPoint.charge(chargePoint);
            assertThat(charge.point()).isEqualTo(chargePoint + userPoint.point());
            assertThat(charge.id()).isEqualTo(id);

        }

        /* 비즈니스 정책에 맞게 실패하는 케이스를 작성하였습니다. */
        @DisplayName("충전 이후 잔고가 10,000원 이상인 경우 IllegalArgumentException이 발생한다.")
        @Test
        void failOverCharge() {
            // given
            long id = 1L;
            long chargePoint = 10000L;
            UserPoint userPoint = UserPoint.empty(id);

            // when // then
            assertThatThrownBy(() -> userPoint.charge(chargePoint))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("잔고는 10,000원 이상을 넘을 수 없습니다.");
        }

        /* 비즈니스 정책에 맞게 실패하는 케이스를 작성하였습니다. */
        @DisplayName("충전하는 포인트가 최소 충전 포인트를 넘지 못하는 경우 IllegalArgumentException이 발생한다.")
        @Test
        void failDidntMinimumCharge() {
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

    @DisplayName("사용")
    @Nested
    class use {

        @DisplayName("정상적인 사용 금액으로 사용하는 경우 사용 이후 남은 포인트를 가진 객체를 반환한다.")
        @Test
        void success() {
            // given
            long id = 1L;
            long usePoint = 10L;
            UserPoint userPoint = new UserPoint(1L, 20L, System.currentTimeMillis());
            long leftPoint =  userPoint.point() - usePoint;

            // when // then
            UserPoint usedPoint = userPoint.use(usePoint);
            assertThat(usedPoint.point()).isEqualTo(leftPoint);
            assertThat(usedPoint.id()).isEqualTo(id);

        }

        /* 비즈니스 정책에 맞게 실패하는 케이스를 작성하였습니다. */
        @DisplayName("사용 이후 잔고가 0원 미만인 경우 IllegalArgumentException이 발생한다.")
        @Test
        void failNotEnoughBalance() {
            // given
            long id = 1L;
            long usePoint = 15L;
            UserPoint userPoint = new UserPoint(1L, 10L, System.currentTimeMillis());

            // when // then
            assertThatThrownBy(() -> userPoint.use(usePoint))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("보유 포인트가 부족합니다.");
        }

        /* 비즈니스 정책에 맞게 실패하는 케이스를 작성하였습니다. */
        @DisplayName("사용하는 포인트가 1원 미만인 경우 IllegalArgumentException이 발생한다.")
        @Test
        void failDidntMinimumPoint() {
            // given
            long id = 1L;
            long usePoint = 0L;
            UserPoint userPoint = UserPoint.empty(id);

            // when // then
            assertThatThrownBy(() -> userPoint.use(usePoint))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("1원 이상부터 사용이 가능합니다.");
        }
    }

}