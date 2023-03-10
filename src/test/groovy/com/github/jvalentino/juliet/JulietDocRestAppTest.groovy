package com.github.jvalentino.juliet

import com.github.jvalentino.juliet.JulietDocRestApp
import org.springframework.boot.SpringApplication
import spock.lang.Specification

class JulietDocRestAppTest extends Specification {

    def setup() {
        GroovyMock(SpringApplication, global:true)
    }

    def "test main"() {
        when:
        JulietDocRestApp.main(null)

        then:
        1 * SpringApplication.run(JulietDocRestApp, null)
    }

}
