package io.hhplus.tdd.point;

import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.domain.UserPoint;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/point")
public class PointController {

    private static final Logger log = LoggerFactory.getLogger(PointController.class);

    private final PointService pointService;
    private final PointHistoryService pointHistoryService;

    @GetMapping("{id}")
    public UserPoint point(
            @PathVariable long id
    ) {
        if(id <= 0L){
            throw new IllegalArgumentException("id값은 1이상만 요청하실 수 있습니다.");
        }
        return pointService.findPoint(id);
    }

    @GetMapping("{id}/histories")
    public List<PointHistory> history(
            @PathVariable long id
    ) {
        if(id <= 0L){
            throw new IllegalArgumentException("id값은 1이상만 요청하실 수 있습니다.");
        }
        return pointHistoryService.findHistoriesById(id);
    }

    @PatchMapping("{id}/charge")
    public UserPoint charge(
            @PathVariable long id,
            @RequestBody long amount
    ) {
        if(id <= 0L){
            throw new IllegalArgumentException("id값은 1이상만 요청하실 수 있습니다.");
        }

        return pointService.charge(id, amount);
    }

    @PatchMapping("{id}/use")
    public UserPoint use(
            @PathVariable long id,
            @RequestBody long amount
    ) {
        if(id <= 0L){
            throw new IllegalArgumentException("id값은 1이상만 요청하실 수 있습니다.");
        }

        return pointService.use(id, amount);
    }
}
