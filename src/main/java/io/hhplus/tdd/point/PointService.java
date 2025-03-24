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


    public UserPoint selectUserPointById(Long id) {
        return userPointTable.selectById(id);
    }

    public UserPoint chargePoint(Long id, Long amount) {
        long now = System.currentTimeMillis();

        UserPoint userPoint = userPointTable.selectById(id);

        UserPoint chargedUserPoint = userPoint.chargePoint(amount);
        pointHistoryService.insert(id, amount, CHARGE, now);

        return userPointTable.insertOrUpdate(id, chargedUserPoint.point());
    }

    public UserPoint usePoint(Long id, Long amount){
        long now = System.currentTimeMillis();

        UserPoint userPoint = userPointTable.selectById(id);

        UserPoint usedUserPoint = userPoint.usePoint(amount);
        pointHistoryService.insert(id, amount, USE, now);

        return userPointTable.insertOrUpdate(id, usedUserPoint.point());
    }

}
