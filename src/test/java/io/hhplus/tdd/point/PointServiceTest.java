package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.domain.UserPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @Mock
    private UserPointTable userPointTable;

    @Mock
    private PointHistoryService pointHistoryService;

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

        verify(pointHistoryService).insert(anyLong(), anyLong(), any(), anyLong());
    }

    @DisplayName("포인트를 사용할 수 있다.")
    @Test
    void usePoint() {
        // given
        long updateMillis = System.currentTimeMillis();
        UserPoint emptyPoint = new UserPoint(1, 10, updateMillis);
        UserPoint result = new UserPoint(1, 0, updateMillis);

        when(userPointTable.selectById(anyLong())).thenReturn(emptyPoint);
        when(userPointTable.insertOrUpdate(anyLong(), anyLong())).thenReturn(result);

        // when
        UserPoint userPoint = pointService.usePoint(1L, 10L);

        // then
        assertThat(userPoint.id()).isEqualTo(1L);
        assertThat(userPoint.point()).isEqualTo(0L);
        assertThat(userPoint.updateMillis()).isEqualTo(updateMillis);

        verify(pointHistoryService).insert(anyLong(), anyLong(), any(), anyLong());
    }


}