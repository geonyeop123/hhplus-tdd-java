package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static io.hhplus.tdd.point.TransactionType.CHARGE;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PointControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PointHistoryTable pointHistoryTable;

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

    @DisplayName("포인트를 조회할 수 있다.")
    @Test
    void findPointHistories() throws Exception {
        // given
        long id = 1L;
        long updateMillis = System.currentTimeMillis();
        pointHistoryTable.insert(id, 10, CHARGE, updateMillis);


        // when // then
        mockMvc.perform(MockMvcRequestBuilders.get("/point/{id}/histories", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[0].amount").value(10))
                .andExpect(jsonPath("$[0].type").value("CHARGE"))
                .andExpect(jsonPath("$[0].updateMillis").value(updateMillis))
        ;
    }
}