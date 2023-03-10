package com.github.jvalentino.juliet.rest

import com.github.jvalentino.juliet.dto.HomeDto
import com.github.jvalentino.juliet.service.DocService
import com.github.jvalentino.juliet.service.UserService
import groovy.transform.CompileDynamic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Controller used for the initial landing page
 * @author john.valentino
 */
@Controller
@Slf4j
@RestController
@CompileDynamic
class HomeRest {

    @Autowired
    UserService userService

    @Autowired
    DocService docService

    @GetMapping('/')
    HomeDto index() {
        log.info('Rendering index')
        HomeDto response = new HomeDto()
        response.with {
            users = userService.countCurrentUsers()
            documents = docService.countDocuments()
        }

        response
    }

}
