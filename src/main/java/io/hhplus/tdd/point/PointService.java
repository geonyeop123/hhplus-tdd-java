package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointService {

    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;


    public UserPoint selectUserPointById(Long id) {
        return userPointTable.selectById(id);
    }

    public List<PointHistory> selectPointHistoriesById(Long id) {
        return pointHistoryTable.selectAllByUserId(id);
    }

}
