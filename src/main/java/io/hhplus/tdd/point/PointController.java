package io.hhplus.tdd.point;

import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.domain.UserPoint;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/point")
@RequiredArgsConstructor
public class PointController {

    private static final Logger log = LoggerFactory.getLogger(PointController.class);
    private final PointService pointService;
    private final PointHistoryService pointHistoryService;


    @GetMapping("{id}")
    public ResponseEntity<UserPoint> point(
            @PathVariable long id
    ) {
        return ResponseEntity.ok(pointService.selectUserPointById(id));
    }

    @GetMapping("{id}/histories")
    public ResponseEntity<List<PointHistory>> history(
            @PathVariable long id
    ) {
        return ResponseEntity.ok(pointHistoryService.selectPointHistoriesById(id));
    }

    @PatchMapping("{id}/charge")
    public ResponseEntity<UserPoint> charge(
            @PathVariable long id,
            @RequestBody long amount
    ) {
        return ResponseEntity.ok(pointService.chargePoint(id, amount));
    }

    @PatchMapping("{id}/use")
    public ResponseEntity<UserPoint> use(
            @PathVariable long id,
            @RequestBody long amount
    ) {
        return ResponseEntity.ok(pointService.usePoint(id, amount));
    }
}
