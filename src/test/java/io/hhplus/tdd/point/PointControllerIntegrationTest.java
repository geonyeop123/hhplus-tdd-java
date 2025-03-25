package io.hhplus.tdd.point;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.domain.UserPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static io.hhplus.tdd.point.domain.TransactionType.CHARGE;
import static io.hhplus.tdd.point.domain.TransactionType.USE;
import static org.mockito.Mockito.*;
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

    @InjectMocks
    private PointService pointService;

    @MockBean
    private UserPointTable userPointTable;

    @InjectMocks
    private PointHistoryService pointHistoryService;

    @MockBean
    private PointHistoryTable pointHistoryTable;

    @BeforeEach
    void setUp() {
        pointHistoryService = new PointHistoryService(pointHistoryTable);
        pointService = new PointService(userPointTable, pointHistoryService);
    }

    @DisplayName("포인트를 조회할 수 있다.")
    @Test
    void findPoint() throws Exception {
        // given
        long id = 1L;
        when(userPointTable.selectById(id)).thenReturn(UserPoint.empty(id));

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.get("/point/{id}", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.point").value(0))
                .andExpect(jsonPath("$.updateMillis").isNotEmpty())
        ;

        verify(userPointTable, times(1)).selectById(id);
    }

    @DisplayName("포인트 충전/사용 이력을 조회할 수 있다.")
    @Test
    void findPointHistories() throws Exception {
        // given
        long id = 1L;
        long updateMillis = System.currentTimeMillis();
        when(pointHistoryTable.selectAllByUserId(id))
                .thenReturn(List.of(new PointHistory(1L, id, 10, CHARGE, updateMillis)));

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.get("/point/{id}/histories", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].userId").value(id))
                .andExpect(jsonPath("$[0].amount").value(10))
                .andExpect(jsonPath("$[0].type").value("CHARGE"))
                .andExpect(jsonPath("$[0].updateMillis").value(updateMillis))
        ;

        verify(pointHistoryTable, times(1)).selectAllByUserId(id);
    }

    @DisplayName("포인트를 충전할 수 있다.")
    @Test
    void chargePoint() throws Exception {
        // given
        long id = 1L;
        long chargePoint = 10L;
        long updateMillis = System.currentTimeMillis();

        when(userPointTable.selectById(id)).thenReturn(UserPoint.empty(id));
        when(userPointTable.insertOrUpdate(id, chargePoint)).thenReturn(new UserPoint(id, chargePoint, updateMillis));
        when(pointHistoryTable.insert(anyLong(), anyLong(), any(), anyLong()))
                .thenReturn(new PointHistory(1L, id, 10, CHARGE, updateMillis));

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

        verify(userPointTable, times(1)).insertOrUpdate(anyLong(), anyLong());
        verify(pointHistoryTable, times(1)).insert(anyLong(), anyLong(), any(), anyLong());
    }

    @DisplayName("포인트를 사용할 수 있다.")
    @Test
    void usePoint() throws Exception {
        // given
        long id = 1L;
        long usePoint = 10L;
        when(userPointTable.selectById(id)).thenReturn(new UserPoint(id, usePoint, 1L));
        when(userPointTable.insertOrUpdate(id, 0L)).thenReturn(new UserPoint(id, 0L, 1L));
        when(pointHistoryTable.insert(anyLong(), anyLong(), any(), anyLong()))
                .thenReturn(new PointHistory(1L, id, 10, USE, 1L));

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

        verify(userPointTable, times(1)).selectById(anyLong());
        verify(userPointTable, times(1)).insertOrUpdate(anyLong(), anyLong());
        verify(pointHistoryTable, times(1)).insert(anyLong(), anyLong(), any(), anyLong());
    }

}