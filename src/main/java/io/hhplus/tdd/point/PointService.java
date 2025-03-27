package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.domain.UserPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static io.hhplus.tdd.point.domain.TransactionType.CHARGE;
import static io.hhplus.tdd.point.domain.TransactionType.USE;

@Service
@RequiredArgsConstructor
public class PointService {

    private final UserPointTable userPointTable;
    private final PointHistoryService pointHistoryService;

    public UserPoint findPoint(Long id) {
        return userPointTable.selectById(id);
    }

    public UserPoint charge(Long id, Long amount) {
        long updateMillis = System.currentTimeMillis();

        UserPoint userPoint = userPointTable.selectById(id);
        UserPoint chargedPoint = userPoint.charge(amount);
        pointHistoryService.insert(id, amount, CHARGE, updateMillis);

        return userPointTable.insertOrUpdate(id, chargedPoint.point());
    }

    public UserPoint use(Long id, Long amount) {
        long updateMillis = System.currentTimeMillis();

        UserPoint userPoint = userPointTable.selectById(id);
        UserPoint chargedPoint = userPoint.use(amount);
        pointHistoryService.insert(id, amount, USE, updateMillis);

        return userPointTable.insertOrUpdate(id, chargedPoint.point());
    }
}
