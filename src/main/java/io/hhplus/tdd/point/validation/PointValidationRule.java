package io.hhplus.tdd.point.validation;

import lombok.Getter;

@Getter
public enum PointValidationRule {

    CHARGE(1L, 1_000_000L);

    private final long min;
    private final long max;

    PointValidationRule(long min, long max) {
        this.min = min;
        this.max = max;
    }
}
