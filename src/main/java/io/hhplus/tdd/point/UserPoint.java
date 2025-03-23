package io.hhplus.tdd.point;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {

    public UserPoint chargePoint(long amount){
        if(amount <= 0) {
            throw new IllegalArgumentException("1포인트 이상부터 충전이 가능합니다.");
        };

        return new UserPoint(this.id, this.point + amount, System.currentTimeMillis());
    }

    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }
}
