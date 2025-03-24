package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.domain.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointHistoryService {

    private final PointHistoryTable pointHistoryTable;

    public List<PointHistory> selectPointHistoriesById(Long id) {
        return pointHistoryTable.selectAllByUserId(id);
    }

    public PointHistory insert(long id, long amount, TransactionType type, long updateMillis) {
        return pointHistoryTable.insert(id, amount, type, updateMillis);
    }
}
