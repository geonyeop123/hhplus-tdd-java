package io.hhplus.tdd.point;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.domain.UserPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static io.hhplus.tdd.point.domain.TransactionType.CHARGE;
import static io.hhplus.tdd.point.domain.TransactionType.USE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PointControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PointHistoryTable pointHistoryTable;

    @Autowired
    private UserPointTable userPointTable;


    @DisplayName("포인트를 조회할 수 있다.")
    @Test
    void findPoint() throws Exception {
        // given
        long id = 1L;

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.get("/point/{id}", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.point").value(0))
                .andExpect(jsonPath("$.updateMillis").isNotEmpty())
        ;
    }

    @DisplayName("포인트 충전/사용 이력을 조회할 수 있다.")
    @Test
    void findPointHistories() throws Exception {
        // given
        long id = 2L;
        long updateMillis = System.currentTimeMillis();
        pointHistoryTable.insert(id, 10, CHARGE, updateMillis);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.get("/point/{id}/histories", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").isNotEmpty())
                .andExpect(jsonPath("$[0].userId").value(2))
                .andExpect(jsonPath("$[0].amount").value(10))
                .andExpect(jsonPath("$[0].type").value("CHARGE"))
                .andExpect(jsonPath("$[0].updateMillis").value(updateMillis))
        ;
    }

    @DisplayName("포인트를 충전할 수 있다.")
    @Test
    void chargePoint() throws Exception {
        // given
        long id = 3L;
        long chargePoint = 10L;

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.patch("/point/{id}/charge", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chargePoint))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.point").value(chargePoint))
        ;

        UserPoint userPoint = userPointTable.selectById(id);
        assertThat(userPoint.point()).isEqualTo(chargePoint);
        assertThat(userPoint.id()).isEqualTo(id);

        List<PointHistory> pointHistories = pointHistoryTable.selectAllByUserId(id);
        assertThat(pointHistories).hasSize(1);

        PointHistory pointHistory = pointHistories.get(0);
        assertThat(pointHistory.type()).isEqualTo(CHARGE);
        assertThat(pointHistory.amount()).isEqualTo(chargePoint);
        assertThat(pointHistory.userId()).isEqualTo(id);

    }

    @DisplayName("1포인트 이하는 충전할 수 없다.")
    @Test
    void chargeZeroPoint() throws Exception {
        // given
        long id = 1L;
        long chargePoint = 0L;

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.patch("/point/{id}/charge", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chargePoint))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("1포인트 이상부터 충전이 가능합니다."))
        ;
    }

    @DisplayName("포인트를 사용할 수 있다.")
    @Test
    void usePoint() throws Exception {
        // given
        long id = 5L;
        long usePoint = 10L;

        userPointTable.insertOrUpdate(5L, 10L);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.patch("/point/{id}/use", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usePoint))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.point").value(0))
        ;

        UserPoint userPoint = userPointTable.selectById(id);
        assertThat(userPoint.point()).isEqualTo(0);
        assertThat(userPoint.id()).isEqualTo(id);

        List<PointHistory> pointHistories = pointHistoryTable.selectAllByUserId(id);
        assertThat(pointHistories).hasSize(1);

        PointHistory pointHistory = pointHistories.get(0);
        assertThat(pointHistory.amount()).isEqualTo(usePoint);
        assertThat(pointHistory.userId()).isEqualTo(id);
        assertThat(pointHistory.type()).isEqualTo(USE);
    }

}