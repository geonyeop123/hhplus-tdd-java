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

        return new UserPoint(id, balance, System.currentTimeMillis());
    }

    public UserPoint use(Long amount) {

        if(amount < 1){
            throw new IllegalArgumentException("1원 이상부터 사용이 가능합니다.");
        }

        long balance = this.point - amount;

        if(balance < 0){
            throw new IllegalArgumentException("보유 포인트가 부족합니다.");
        }

        return new UserPoint(id, balance, System.currentTimeMillis());
    }
}
