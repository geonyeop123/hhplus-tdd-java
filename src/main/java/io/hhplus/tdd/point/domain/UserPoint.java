package io.hhplus.tdd.point.domain;

import static io.hhplus.tdd.point.domain.validationRule.PointValidationRule.CHARGE;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {

    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }

    public UserPoint charge(Long amount) {

        if(CHARGE.getMin() > amount){
            throw new IllegalArgumentException("1원 이상부터 충전이 가능합니다.");
        }

        long balance = this.point + amount;

        if(CHARGE.getMaxBalance() <= balance){
            throw new IllegalArgumentException("잔고는 10,000원 이상을 넘을 수 없습니다.");
        }

        return new UserPoint(id, amount, System.currentTimeMillis());
    }
}
