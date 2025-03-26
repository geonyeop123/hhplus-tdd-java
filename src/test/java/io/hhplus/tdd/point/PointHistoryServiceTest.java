package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.point.domain.PointHistory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static io.hhplus.tdd.point.domain.TransactionType.CHARGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PointHistoryServiceTest {

    @Mock
    private static PointHistoryTable pointHistoryTable;

    @InjectMocks
    private static PointHistoryService pointHistoryService;

    @DisplayName("포인트 사용/충전 내역 조회")
    @Nested
    class findHistories{

        @DisplayName("정상적인 userId로 포인트 이력 조회를 요청하면 포인트 사용/충전 목록이 반환된다.")
        @Test
        void success() {
            // given
            Long userId = 1L;

            when(pointHistoryTable.selectAllByUserId(userId))
                    .thenReturn(List.of(new PointHistory(1L, 1L, 10L, CHARGE, 1L)));

            // when
            List<PointHistory> histories = pointHistoryService.findHistoriesById(userId);

            // then
            assertThat(histories).hasSize(1);
        }
    }
  
}