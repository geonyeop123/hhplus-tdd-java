package io.hhplus.tdd.point;

import io.hhplus.tdd.point.domain.UserPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(PointController.class)
class PointControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PointService pointService;

    @DisplayName("포인트 조회")
    @Nested
    class findPoint{

        @DisplayName("정상적인 id로 포인트를 조회하면 해당하는 user의 포인트가 조회된다.")
        @Test
        void success() throws Exception {
            // given
            Long id = 1L;
            when(pointService.findPoint(id)).thenReturn(UserPoint.empty(id));

            // when // then
            mockMvc.perform(MockMvcRequestBuilders.get("/point/{id}", id))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(id))
                    .andExpect(jsonPath("$.point").value(0))
                    .andExpect(jsonPath("$.updateMillis").isNotEmpty());
        }

        @DisplayName("요청한 id가 0이하인 경우 400에러가 반환된다.")
        @Test
        void fail() throws Exception {
            // given
            Long id = 0L;
            when(pointService.findPoint(id)).thenReturn(UserPoint.empty(id));

            // when // then
            mockMvc.perform(MockMvcRequestBuilders.get("/point/{id}", id))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(BAD_REQUEST.value()))
                    .andExpect(jsonPath("$.message").value("id값은 1이상만 요청하실 수 있습니다."))
                    ;
        }
    }

}