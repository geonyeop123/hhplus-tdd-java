package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @Mock
    private UserPointTable userPointTable;

    @InjectMocks
    private PointService pointService;

    @DisplayName("userId로 포인트를 조회할 수 있다.")
    @Test
    void selectUserPointById() {
        // given
        UserPoint result = new UserPoint(1, 10, System.currentTimeMillis());
        when(userPointTable.selectById(any())).thenReturn(result);

        // when
        UserPoint userPoint = pointService.selectUserPointById(1L);

        // then
        assertThat(userPoint.id()).isEqualTo(1L);
        assertThat(userPoint.point()).isEqualTo(10);
    }
}