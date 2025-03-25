package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.domain.UserPoint;
import io.hhplus.tdd.point.validation.PointValidationRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static io.hhplus.tdd.point.domain.TransactionType.CHARGE;
import static io.hhplus.tdd.point.domain.TransactionType.USE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class PointServiceIntegrationTest {

    @Autowired
    PointService pointService;

    @Autowired
    UserPointTable userPointTable;

    @Autowired
    PointHistoryTable pointHistoryTable;

    @DisplayName("userId로 해당하는 유저의 포인트를 조회할 수 있다.")
    @Test
    void selectUserPointById() {
        // given
        Long id = 1L;

        // when
        UserPoint userPoint = pointService.selectUserPointById(id);

        // then
        assertThat(userPoint.id()).isEqualTo(id);
        assertThat(userPoint.point()).isEqualTo(0L);
    }

    @DisplayName("포인트 충전에 성공하는 경우, 이력이 저장된다.")
    @Test
    void chargePoint() {
        // given
        Long id = 2L;
        Long amount = 10L;

        // when
        UserPoint userPoint = pointService.chargePoint(id, amount);

        // then
        assertThat(userPoint.id()).isEqualTo(id);
        assertThat(userPoint.point()).isEqualTo(amount);

        UserPoint savedUserPoint = userPointTable.selectById(id);
        assertThat(savedUserPoint.id()).isEqualTo(id);
        assertThat(savedUserPoint.point()).isEqualTo(amount);

        List<PointHistory> pointHistories = pointHistoryTable.selectAllByUserId(id);
        assertThat(pointHistories).hasSize(1);

        PointHistory pointHistory = pointHistories.get(0);
        assertThat(pointHistory.userId()).isEqualTo(id);
        assertThat(pointHistory.type()).isEqualTo(CHARGE);
        assertThat(pointHistory.amount()).isEqualTo(amount);
    }

    @DisplayName("충전에 실패하면, 이력이 남지 않는다.")
    @Test
    void failChargeNonHistory() {
        // given
        Long id = 3L;
        Long chargePoint = PointValidationRule.CHARGE.getMax() + 1;

        // when
        assertThatThrownBy(() -> pointService.chargePoint(id, chargePoint));

        // then
        List<PointHistory> pointHistories = pointHistoryTable.selectAllByUserId(id);
        assertThat(pointHistories).isEmpty();
    }

    @DisplayName("포인트를 사용하는 경우, 이력이 저장된다.")
    @Test
    void usePoint() {
        // given
        Long id = 4L;
        Long amount = 10L;

        userPointTable.insertOrUpdate(id, amount);

        // when
        UserPoint userPoint = pointService.usePoint(id, amount);

        // then
        assertThat(userPoint.id()).isEqualTo(id);
        assertThat(userPoint.point()).isEqualTo(0L);

        UserPoint savedUserPoint = userPointTable.selectById(id);
        assertThat(savedUserPoint.id()).isEqualTo(id);
        assertThat(savedUserPoint.point()).isEqualTo(0L);

        List<PointHistory> pointHistories = pointHistoryTable.selectAllByUserId(id);
        assertThat(pointHistories).hasSize(1);

        PointHistory pointHistory = pointHistories.get(0);
        assertThat(pointHistory.userId()).isEqualTo(id);
        assertThat(pointHistory.type()).isEqualTo(USE);
        assertThat(pointHistory.amount()).isEqualTo(amount);
    }

    @DisplayName("사용에 실패하면, 이력이 남지 않는다.")
    @Test
    void failUseNonHistory() {
        // given
        Long id = 5L;
        Long usePoint = 10L;

        // when
        assertThatThrownBy(() -> pointService.usePoint(id, usePoint));

        // then
        List<PointHistory> pointHistories = pointHistoryTable.selectAllByUserId(id);
        assertThat(pointHistories).isEmpty();
    }
}