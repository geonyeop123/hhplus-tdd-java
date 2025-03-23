package io.hhplus.tdd.database;

import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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

}