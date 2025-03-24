package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.point.domain.PointHistory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static io.hhplus.tdd.point.domain.TransactionType.CHARGE;
import static io.hhplus.tdd.point.domain.TransactionType.USE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PointHistoryServiceTest {

    @Mock
    private PointHistoryTable pointHistoryTable;

    @InjectMocks
    private PointHistoryService pointHistoryService;

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
        List<PointHistory> pointHistories = pointHistoryService.selectPointHistoriesById(1L);

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
        List<PointHistory> pointHistories = pointHistoryService.selectPointHistoriesById(1L);

        // then
        assertThat(pointHistories).isEmpty();
    }

    @DisplayName("충전 이력을 저장할 수 있다.")
    @Test
    void insertChargePointHistory() {
        // given
        long updateMillis = System.currentTimeMillis();
        long userId = 1L;
        PointHistory result = new PointHistory(1L, 1L, 10, CHARGE, updateMillis);

        when(pointHistoryTable.insert(anyLong(), anyLong(), any(), anyLong())).thenReturn(result);

        // when
        PointHistory pointHistory = pointHistoryService.insert(userId, 10L, CHARGE, updateMillis);

        // then
        assertThat(pointHistory.userId()).isEqualTo(userId);
        assertThat(pointHistory.amount()).isEqualTo(10L);
        assertThat(pointHistory.type()).isEqualTo(CHARGE);
        assertThat(pointHistory.updateMillis()).isEqualTo(updateMillis);
    }

    @DisplayName("사용 이력을 저장할 수 있다.")
    @Test
    void insertUsePointHistory() {
        // given
        long updateMillis = System.currentTimeMillis();
        long userId = 1L;
        PointHistory result = new PointHistory(1L, 1L, 10, USE, updateMillis);

        when(pointHistoryTable.insert(anyLong(), anyLong(), any(), anyLong())).thenReturn(result);

        // when
        PointHistory pointHistory = pointHistoryService.insert(userId, 10L, USE, updateMillis);

        // then
        assertThat(pointHistory.userId()).isEqualTo(userId);
        assertThat(pointHistory.amount()).isEqualTo(10L);
        assertThat(pointHistory.type()).isEqualTo(USE);
        assertThat(pointHistory.updateMillis()).isEqualTo(updateMillis);
    }

}