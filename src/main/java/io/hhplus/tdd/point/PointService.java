package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static io.hhplus.tdd.point.TransactionType.CHARGE;

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

    public UserPoint chargePoint(Long id, Long amount) {
        long now = System.currentTimeMillis();

        UserPoint userPoint = userPointTable.selectById(id);

        UserPoint chargeUserPoint = userPoint.chargePoint(amount);
        pointHistoryTable.insert(id, amount, CHARGE, now);

        return userPointTable.insertOrUpdate(id, chargeUserPoint.point());
    }

}
