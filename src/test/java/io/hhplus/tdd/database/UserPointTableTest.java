package io.hhplus.tdd.database;

import io.hhplus.tdd.point.domain.UserPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserPointTableTest {


    private UserPointTable userPointTable;

    @BeforeEach
    void setUp() {
        userPointTable = new UserPointTable();
    }

    @DisplayName("UserPoint를 저장할 수 있다.")
    @Test
    void insertUserPoint() {
        // given // when
        userPointTable.insertOrUpdate(1, 40);

        // then
        UserPoint userPoint = userPointTable.selectById(1L);

        assertThat(userPoint.id()).isEqualTo(1);
        assertThat(userPoint.point()).isEqualTo(40);
    }

    @DisplayName("id로 저장된 UserPoint를 조회할 수 있다.")
    @Test
    void selectUserPoint() {
        // given
        long id = 1L;
        userPointTable.insertOrUpdate(id, 30);

        // when
        UserPoint userPoint = userPointTable.selectById(id);

        // then
        assertThat(userPoint.point()).isEqualTo(30);
        assertThat(userPoint.id()).isEqualTo(id);
    }

    @DisplayName("이미 저장된 UserPoint를 수정할 수 있다.")
    @Test
    void updateUserPoint() {
        // given
        long id = 1L;
        userPointTable.insertOrUpdate(id, 10);
        userPointTable.insertOrUpdate(id, 20);

        // when
        UserPoint userPoint = userPointTable.selectById(id);

        // then
        assertThat(userPoint.point()).isEqualTo(20);
    }

    @DisplayName("저장되지 않은 UserPoint도 조회 시 0포인트로 조회된다.")
    @Test
    void selectUserPointIsNotNull() {
        // given // when
        UserPoint userPoint = userPointTable.selectById(1L);

        // then
        assertThat(userPoint).isNotNull();
        assertThat(userPoint.id()).isEqualTo(1);
        assertThat(userPoint.point()).isEqualTo(0);
    }

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