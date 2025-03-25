package io.hhplus.tdd.point.domain;

import java.text.NumberFormat;

import static io.hhplus.tdd.point.validation.PointValidationRule.CHARGE;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {

    public UserPoint chargePoint(long amount){
        long balance = this.point + amount;
        if(CHARGE.getMin() > amount) {
            throw new IllegalArgumentException(String.format("%d포인트 이상부터 충전이 가능합니다.", CHARGE.getMin()));
        }else if(CHARGE.getMax() < balance) {
            NumberFormat nf = NumberFormat.getInstance();
            throw new IllegalArgumentException(String.format("잔고는 %s원을 초과할 수 없습니다.", nf.format(CHARGE.getMax())));
        }

        return new UserPoint(this.id, balance, System.currentTimeMillis());
    }

    public UserPoint usePoint(long amount){
        long balance = this.point - amount;

        if(balance < 0) {
            throw new IllegalArgumentException("잔고가 부족합니다.");
        }

        return new UserPoint(this.id, balance, System.currentTimeMillis());
    }

    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }
}
