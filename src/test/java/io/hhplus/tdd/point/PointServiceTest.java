package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.domain.UserPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @Mock
    private UserPointTable userPointTable;

    @Mock
    private PointHistoryService pointHistoryService;

    @InjectMocks
    private PointService pointService;

    @DisplayName("포인트 조회")
    @Nested
    class findPoint {

        @DisplayName("정상적인 id로 포인트를 조회하면 해당하는 UserPoint 객체를 반환한다.")
        @Test
        void success() {
            // given
            long id = 1L;
            UserPoint result = new UserPoint(1, 10, System.currentTimeMillis());
            when(userPointTable.selectById(id)).thenReturn(result);

            // when
            UserPoint point = pointService.findPoint(id);

            // then
            assertThat(point.id()).isEqualTo(id);
            assertThat(point.point()).isEqualTo(10L);

            verify(userPointTable, times(1)).selectById(id);
        }

    }

    @DisplayName("포인트 충전")
    @Nested
    class charge {

        @DisplayName("정상적인 id와 값으로 포인트를 충전하면 충전 완료된 UserPoint 객체를 반환한다.")
        @Test
        void success() {
            // given
            Long id = 1L;
            Long amount = 10L;
            Long updateMillis = 1000L;
            UserPoint userPoint = new UserPoint(id, amount, updateMillis);

            when(userPointTable.selectById(id)).thenReturn(UserPoint.empty(id));
            when(userPointTable.insertOrUpdate(anyLong(), anyLong())).thenReturn(userPoint);

            // when
            UserPoint point = pointService.charge(id, amount);

            // then
            assertThat(point.id()).isEqualTo(id);
            assertThat(point.point()).isEqualTo(amount);
            assertThat(point.updateMillis()).isEqualTo(updateMillis);

            verify(userPointTable, times(1)).selectById(id);
            verify(userPointTable, times(1)).insertOrUpdate(anyLong(), anyLong());
            verify(pointHistoryService, times(1))
                        .insert(anyLong(), anyLong(), any(), anyLong());
        }

        @DisplayName("포인트 충전에 실패하면, 충전 이력 저장을 하지 않는다.")
        @Test
        void failNotWriteHistory() {
            // given
            Long id = 1L;
            Long amount = 0L;
            when(userPointTable.selectById(id)).thenReturn(UserPoint.empty(id));

            // when // then
            assertThatThrownBy(() -> pointService.charge(id, amount)).isInstanceOf(IllegalArgumentException.class);

            verify(userPointTable, times(1)).selectById(id);
            verify(pointHistoryService, never()).insert(anyLong(), anyLong(), any(), anyLong());
        }
    }

}