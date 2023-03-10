package com.github.jvalentino.juliet.rest

import com.github.jvalentino.juliet.dto.DashboardDto
import com.github.jvalentino.juliet.util.BaseIntg
import org.springframework.test.web.servlet.MvcResult

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class DashboardRestIntgTest extends BaseIntg {

    def "test dashboard"() {
        given:
        this.mockAdminLoggedIn()

        when:
        MvcResult response = mvc.perform(
                get("/dashboard"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()

        then:
        DashboardDto result = this.toObject(response, DashboardDto)
        result.documents.size() == 0
    }

    def "test dashboard no auth"() {
        when:
        MvcResult response = mvc.perform(
                get("/dashboard"))
                .andDo(print())
                .andReturn()

        then:
        response.response.status == 403
    }

}
