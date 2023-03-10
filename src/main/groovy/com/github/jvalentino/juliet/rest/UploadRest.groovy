package com.github.jvalentino.juliet.rest

import com.github.jvalentino.juliet.dto.ResultDto
import com.github.jvalentino.juliet.util.DateGenerator
import com.github.jvalentino.juliet.service.DocService
import groovy.transform.CompileDynamic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

/**
 * Used for file upload
 */
@Controller
@Slf4j
@RestController
@CompileDynamic
class UploadRest {

    @Autowired
    DocService docService

    @PostMapping('/upload-file/{userId}')
    ResultDto upload(@RequestParam('file') MultipartFile file, @PathVariable(value='userId') Long userId) {
        docService.uploadNewDoc(userId, file, DateGenerator.date())

        new ResultDto()
    }

}
