package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.domain.TransactionType;
import io.hhplus.tdd.point.domain.UserPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static io.hhplus.tdd.point.domain.validationRule.PointValidationRule.CHARGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;


@SpringBootTest
class PointServiceIntegrationTest {
// DB 대용으로 사용하고 있는 table 객체들의 삭제 메서드가 없어 각 테스트마다 id를 별도로 세팅하였습니다.

    @Autowired
    private PointService pointService;

    @Autowired
    private PointHistoryTable pointHistoryTable;

    @Autowired
    private UserPointTable userPointTable;

    @DisplayName("포인트 조회")
    @Nested
    class findPoint {

        @DisplayName("정상적인 id로 포인트를 조회하면 해당하는 UserPoint 객체를 반환한다.")
        @Test
        void success() {
            // given
            Long id = 1L;

            // when
            UserPoint point = pointService.findPoint(id);

            // then
            assertThat(point.id()).isEqualTo(id);
            assertThat(point.point()).isEqualTo(0L);
        }
    }

    @DisplayName("포인트 충전")
    @Nested
    class charge {

        @DisplayName("정상적인 id와 값으로 포인트를 충전하면 충전 완료된 UserPoint 객체를 반환하며, 충전 이력도 저장된다.")
        @Test
        void success() {
            // given
            Long id = 2L;
            Long amount = 10L;

            // when
            UserPoint point = pointService.charge(id, amount);

            // then
            assertThat(point.id()).isEqualTo(id);
            assertThat(point.point()).isEqualTo(amount);

            List<PointHistory> pointHistories = pointHistoryTable.selectAllByUserId(id);

            assertThat(pointHistories).hasSize(1);

            PointHistory pointHistory = pointHistories.get(0);
            assertThat(pointHistory.userId()).isEqualTo(id);
            assertThat(pointHistory.amount()).isEqualTo(amount);
            assertThat(pointHistory.type()).isEqualTo(TransactionType.CHARGE);
        }

        @DisplayName("포인트 충전에 실패하면, 충전 이력 저장을 하지 않는다.")
        @Test
        void failNotWriteHistory() {
            // given
            Long id = 3L;
            Long amount = 0L;

            // when // then
            assertThatThrownBy(() -> pointService.charge(id, amount)).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("포인트 충전을 하였을 때 최대 잔고가 넘어가면 IllegalArgumentException이 발생한다.")
        @Test
        void failOverCharge() {
            // given
            Long id = 4L;
            Long amount = CHARGE.getMaxBalance();

            // when // then
            assertThatThrownBy(() -> pointService.charge(id, amount)).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("잔고는 10,000원 이상을 넘을 수 없습니다.");
        }

        @DisplayName("포인트 충전을 하였을 때 최소 충전금액을 넘지 못한 경우 IllegalArgumentException이 발생한다.")
        @Test
        void failDidntMinimumCharge() {
            // given
            Long id = 5L;
            Long amount = CHARGE.getMin() - 1;

            // when // then
            assertThatThrownBy(() -> pointService.charge(id, amount)).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("1원 이상부터 충전이 가능합니다.");
        }
    }

    @DisplayName("포인트 사용")
    @Nested
    class use {

        @DisplayName("정상적인 id와 값으로 포인트를 사용하면 사용 이후의 UserPoint 객체를 반환하며, 사용 이력도 저장된다.")
        @Test
        void success() {
            // given
            Long id = 6L;
            Long amount = 10L;

            userPointTable.insertOrUpdate(6L, 15L);

            // when
            UserPoint point = pointService.use(id, amount);

            // then
            assertThat(point.id()).isEqualTo(id);
            assertThat(point.point()).isEqualTo(5L);

            List<PointHistory> pointHistories = pointHistoryTable.selectAllByUserId(id);

            assertThat(pointHistories).hasSize(1);

            PointHistory pointHistory = pointHistories.get(0);
            assertThat(pointHistory.userId()).isEqualTo(id);
            assertThat(pointHistory.amount()).isEqualTo(amount);
            assertThat(pointHistory.type()).isEqualTo(TransactionType.USE);
        }

        @DisplayName("포인트 사용에 실패하면, 충전 이력 저장을 하지 않는다.")
        @Test
        void failNotWriteHistory() {
            // given
            Long id = 7L;
            Long amount = 10L;

            // when // then
            assertThatThrownBy(() -> pointService.use(id, amount)).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("포인트 사용을 하였을 때 사용하는 포인트보다 보유 포인트가 적을 경우 IllegalArgumentException이 발생한다.")
        @Test
        void failNotEnoughBalance() {
            // given
            Long id = 8L;
            Long amount = 10L;

            // when // then
            assertThatThrownBy(() -> pointService.use(id, amount)).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("보유 포인트가 부족합니다.");
        }

        @DisplayName("포인트 사용을 하였을 때 사용하는 포인트가 0원 이하인 경우 IllegalArgumentException이 발생한다.")
        @Test
        void failDidntMinimumPoint() {
            // given
            Long id = 9L;
            Long amount = 0L;

            // when // then
            assertThatThrownBy(() -> pointService.use(id, amount)).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("1원 이상부터 사용이 가능합니다.");
        }
    }

    @DisplayName("충전 및 사용")
    @Nested
    class chargeAndUse {

        @DisplayName("총 충전을 60원을 하고, 45원을 사용하면 15원이 남으며 모든 이력이 저장된다.")
        @Test
        void chargeAndUse() {
            // given
            Long id = 10L;

            // when
            pointService.charge(id, 10L);
            pointService.charge(id, 20L);
            pointService.charge(id, 30L);
            pointService.use(id, 45L);
            // then
            UserPoint userPoint = userPointTable.selectById(10L);
            assertThat(userPoint.id()).isEqualTo(id);
            assertThat(userPoint.point()).isEqualTo(15L);

            List<PointHistory> pointHistories = pointHistoryTable.selectAllByUserId(id);
            assertThat(pointHistories).hasSize(4);
            assertThat(pointHistories).extracting("userId", "amount", "type")
                                .containsExactly(
                                        tuple(id, 10L, TransactionType.CHARGE)
                                        , tuple(id, 20L, TransactionType.CHARGE)
                                        , tuple(id, 30L, TransactionType.CHARGE)
                                        , tuple(id, 45L, TransactionType.USE)
                                );


        }

    }

}