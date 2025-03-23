package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static io.hhplus.tdd.point.TransactionType.CHARGE;
import static io.hhplus.tdd.point.TransactionType.USE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @Mock
    private UserPointTable userPointTable;

    @Mock
    private PointHistoryTable pointHistoryTable;

    @InjectMocks
    private PointService pointService;

    @DisplayName("userId로 포인트를 조회할 수 있다.")
    @Test
    void findUserPointById() {
        // given
        UserPoint result = new UserPoint(1, 10, System.currentTimeMillis());
        when(userPointTable.selectById(any())).thenReturn(result);

        // when
        UserPoint userPoint = pointService.selectUserPointById(1L);

        // then
        assertThat(userPoint.id()).isEqualTo(1L);
        assertThat(userPoint.point()).isEqualTo(10);
    }

    @DisplayName("userId로 포인트 충전/이용 내역을 조회할 수 있다.")
    @Test
    void findPointHistoriesById() {
        // given
        long updateMillis = System.currentTimeMillis();
        List<PointHistory> result = List.of(
                new PointHistory(1L, 1L, 10, CHARGE, updateMillis)
                , new PointHistory(2L, 1L, 10, CHARGE, updateMillis)
                , new PointHistory(3L, 1L, 20, USE, updateMillis));
        when(pointHistoryTable.selectAllByUserId(any(Long.class))).thenReturn(result);
        // when
        List<PointHistory> pointHistories = pointService.selectPointHistoriesById(1L);

        // then
        assertThat(pointHistories).hasSize(3);
        assertThat(pointHistories)
                .extracting("id", "userId", "amount", "type", "updateMillis")
                .containsExactlyInAnyOrder(
                        tuple(1L, 1L, 10L, CHARGE, updateMillis)
                        , tuple(2L, 1L, 10L, CHARGE, updateMillis)
                        , tuple(3L, 1L, 20L, USE, updateMillis));

    }

    @DisplayName("userId로 포인트 충전/이용 내역을 조회할 때 내역이 없는 경우 빈 객체를 반환한다.")
    @Test
    void findEmptyPointHistoriesById() {
        // given
        List<PointHistory> result = List.of();
        when(pointHistoryTable.selectAllByUserId(any(Long.class))).thenReturn(result);

        // when
        List<PointHistory> pointHistories = pointService.selectPointHistoriesById(1L);

        // then
        assertThat(pointHistories).isEmpty();

    }

    @DisplayName("포인트를 충전할 수 있다.")
    @Test
    void chargePoint() {
        // given
        long updateMillis = System.currentTimeMillis();
        UserPoint emptyPoint = UserPoint.empty(1);
        UserPoint result = new UserPoint(1, 10, updateMillis);

        when(userPointTable.selectById(anyLong())).thenReturn(emptyPoint);
        when(userPointTable.insertOrUpdate(anyLong(), anyLong())).thenReturn(result);

        // when
        UserPoint userPoint = pointService.chargePoint(1L, 10L);

        // then
        assertThat(userPoint.id()).isEqualTo(1L);
        assertThat(userPoint.point()).isEqualTo(10L);
        assertThat(userPoint.updateMillis()).isEqualTo(updateMillis);
    }
}