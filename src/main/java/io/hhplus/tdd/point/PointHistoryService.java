package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.point.domain.PointHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointHistoryService {

    private final PointHistoryTable pointHistoryTable;

    public List<PointHistory> findHistoriesById(long id) {
        return pointHistoryTable.selectAllByUserId(id);
    }

}
