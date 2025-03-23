package io.hhplus.tdd.point;

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
        return ResponseEntity.ok(pointService.selectPointHistoriesById(id));
    }

    @PatchMapping("{id}/charge")
    public ResponseEntity<UserPoint> charge(
            @PathVariable long id,
            @RequestBody long amount
    ) {
        return ResponseEntity.ok(pointService.chargePoint(id, amount));
    }

    /**
     * TODO - 특정 유저의 포인트를 사용하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/use")
    public UserPoint use(
            @PathVariable long id,
            @RequestBody long amount
    ) {
        return new UserPoint(0, 0, 0);
    }
}
