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


    /* 조회에서 실패 케이스는 id가 0 이하인 경우 발생하지만,
    controller에서 해당 검증을 진행하므로 성공 케이스만 작성하였습니다. */
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

    /* 충전에 실패하는 경우는 UserPoint 도메인 내부에 로직이 담겨있어,
       통합테스트에만 해당 실패 케이스를 작성하였습니다. */
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

    /* 사용에 실패하는 경우는 UserPoint 도메인 내부에 로직이 담겨있어,
        통합테스트에만 해당 실패 케이스를 작성하였습니다. */
    @DisplayName("포인트 사용")
    @Nested
    class use {

        @DisplayName("정상적인 id와 값으로 포인트를 사용하면 사용 이후 상태인 UserPoint 객체를 반환한다.")
        @Test
        void success() {
            // given
            Long id = 1L;
            Long amount = 10L;
            Long updateMillis = 1000L;
            UserPoint beforeUseUserPoint = new UserPoint(id, amount, updateMillis);
            UserPoint afterUseUserPoint = new UserPoint(id, 0L, updateMillis);


            when(userPointTable.selectById(id)).thenReturn(beforeUseUserPoint);
            when(userPointTable.insertOrUpdate(anyLong(), anyLong())).thenReturn(afterUseUserPoint);

            // when
            UserPoint point = pointService.use(id, amount);

            // then
            assertThat(point.id()).isEqualTo(id);
            assertThat(point.point()).isEqualTo(0L);
            assertThat(point.updateMillis()).isEqualTo(updateMillis);

            verify(userPointTable, times(1)).selectById(id);
            verify(userPointTable, times(1)).insertOrUpdate(anyLong(), anyLong());
            verify(pointHistoryService, times(1))
                    .insert(anyLong(), anyLong(), any(), anyLong());
        }

        @DisplayName("포인트 사용에 실패하면, 사용 이력 저장을 하지 않는다.")
        @Test
        void failNotWriteHistory() {
            // given
            Long id = 1L;
            Long amount = 0L;
            when(userPointTable.selectById(id)).thenReturn(UserPoint.empty(id));

            // when // then
            assertThatThrownBy(() -> pointService.use(id, amount)).isInstanceOf(IllegalArgumentException.class);

            verify(userPointTable, times(1)).selectById(id);
            verify(pointHistoryService, never()).insert(anyLong(), anyLong(), any(), anyLong());
        }
    }

}