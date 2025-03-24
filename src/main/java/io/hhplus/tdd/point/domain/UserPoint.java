package io.hhplus.tdd.point.domain;

import static io.hhplus.tdd.point.validation.PointValidationRule.CHARGE;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {

    public UserPoint chargePoint(long amount){

        if(CHARGE.getMin() > amount) {
            throw new IllegalArgumentException(String.format("%d포인트 이상부터 충전이 가능합니다.", CHARGE.getMin()));
        }else if(CHARGE.getMax() < this.point + amount) {
            throw new IllegalArgumentException(String.format("잔고는 %d원을 초과할 수 없습니다.", CHARGE.getMax()));
        }

        return new UserPoint(this.id, this.point + amount, System.currentTimeMillis());
    }

    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }
}
