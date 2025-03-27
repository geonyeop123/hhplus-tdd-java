package io.hhplus.tdd.point.domain.validationRule;

import lombok.Getter;

@Getter
public enum PointValidationRule {
    CHARGE(1L, 10_000L);

    private final long min;
    private final long maxBalance;

    PointValidationRule(long min, long maxBalance) {
        this.min = min;
        this.maxBalance = maxBalance;
    }
}
