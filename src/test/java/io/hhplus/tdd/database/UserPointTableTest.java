package io.hhplus.tdd.database;

import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UserPointTableTest {


    private UserPointTable userPointTable;

    @BeforeEach
    void setUp() {
        userPointTable = new UserPointTable();
    }

    @DisplayName("UserPoint를 저장할 수 있다.")
    @Test
    void insertUserPoint() {
        // given // when
        userPointTable.insertOrUpdate(1L, 40);

        // then
        UserPoint userPoint = userPointTable.selectById(1L);

        assertThat(userPoint.id()).isEqualTo(1L);
        assertThat(userPoint.point()).isEqualTo(40);
    }

    @DisplayName("id로 저장된 UserPoint를 조회할 수 있다.")
    @Test
    void selectUserPoint() {
        // given
        long id = 1L;
        userPointTable.insertOrUpdate(id, 30);

        // when
        UserPoint userPoint = userPointTable.selectById(id);

        // then
        assertThat(userPoint.point()).isEqualTo(30);
        assertThat(userPoint.id()).isEqualTo(id);
    }

    @DisplayName("이미 저장된 UserPoint를 수정할 수 있다.")
    @Test
    void updateUserPoint() {
        // given
        long id = 1L;
        userPointTable.insertOrUpdate(id, 10);
        userPointTable.insertOrUpdate(id, 20);

        // when
        UserPoint userPoint = userPointTable.selectById(id);

        // then
        assertThat(userPoint.point()).isEqualTo(20);
    }

    @DisplayName("저장되지 않은 UserPoint도 조회 시 0포인트로 조회된다.")
    @Test
    void selectUserPointIsNotNull() {
        // given // when
        UserPoint userPoint = userPointTable.selectById(1L);

        // then
        assertThat(userPoint).isNotNull();
        assertThat(userPoint.id()).isEqualTo(1L);
        assertThat(userPoint.point()).isEqualTo(0);
    }

}