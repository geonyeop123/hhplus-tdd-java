package io.hhplus.tdd.database;

import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.domain.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PointHistoryTableTest {

    private PointHistoryTable pointHistoryTable;

    @BeforeEach
    void setUp() {
        pointHistoryTable = new PointHistoryTable();
    }

    @DisplayName("이력을 저장할 수 있다.")
    @Test
    void insertHistory() {
        // given // when
        long currentTime = System.currentTimeMillis();
        pointHistoryTable.insert(1L, 10, TransactionType.CHARGE, currentTime);
        List<PointHistory> pointHistories = pointHistoryTable.selectAllByUserId(1L);

        // then
        assertThat(pointHistories).hasSize(1);

        PointHistory pointHistory = pointHistories.get(0);
        assertThat(pointHistory.userId()).isEqualTo(1L);
        assertThat(pointHistory.amount()).isEqualTo(10);
        assertThat(pointHistory.type()).isEqualTo(TransactionType.CHARGE);
        assertThat(pointHistory.updateMillis()).isEqualTo(currentTime);
    }

    @DisplayName("userId에 해당하는 모든 이력을 조회할 수 있다.")
    @Test
    void selectAllByUserId() {
        // given
        pointHistoryTable.insert(1L, 10, TransactionType.CHARGE, System.currentTimeMillis());
        pointHistoryTable.insert(1L, 20, TransactionType.CHARGE, System.currentTimeMillis());
        pointHistoryTable.insert(1L, 30, TransactionType.USE, System.currentTimeMillis());

        // when
        List<PointHistory> pointHistories = pointHistoryTable.selectAllByUserId(1L);

        // then
        assertThat(pointHistories).hasSize(3);
        PointHistory pointHistory = pointHistories.get(0);
        assertThat(pointHistory.userId()).isEqualTo(1L);
        assertThat(pointHistory.amount()).isEqualTo(10);
    }

    @DisplayName("userId에 해당하는 모든 이력을 조회했을 때 데이터가 없는 경우 빈 목록이 조회된다.")
    @Test
    void selectAllByUserIdIsEmpty() {
        // given // when
        List<PointHistory> pointHistories = pointHistoryTable.selectAllByUserId(1L);

        // then
        assertThat(pointHistories).isEmpty();
    }

}