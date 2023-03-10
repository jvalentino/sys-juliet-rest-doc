package com.github.jvalentino.juliet.rest

import com.github.jvalentino.juliet.dto.HomeDto
import com.github.jvalentino.juliet.util.BaseIntg
import org.springframework.test.web.servlet.MvcResult

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class HomeRestIntgTest extends BaseIntg {

    def "test index"() {
        when:
        MvcResult response = mvc.perform(
                get("/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()

        then:
        HomeDto model = this.toObject(response, HomeDto)
        model.users == 1
        model.documents == 0
    }
}
