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
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @Mock
    private static UserPointTable userPointTable;

    @InjectMocks
    private static PointService pointService;

    @DisplayName("정상적인 id로 포인트를 조회하면 해당하는 UserPoint 객체를 반환한다.")
    @Test
    void point() {
        // given
        Long id = 1L;
        when(userPointTable.selectById(id)).thenReturn(UserPoint.empty(id));

        // when
        UserPoint point = pointService.findPoint(1L);

        // then
        assertThat(point.id()).isEqualTo(id);
        assertThat(point.point()).isEqualTo(0L);
    }



}