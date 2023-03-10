package com.github.jvalentino.juliet.rest

import com.github.jvalentino.juliet.dto.ResultDto
import com.github.jvalentino.juliet.entity.AuthUser
import com.github.jvalentino.juliet.rest.UploadRest
import com.github.jvalentino.juliet.service.DocService
import com.github.jvalentino.juliet.util.DateGenerator
import com.github.jvalentino.juliet.util.DateUtil
import org.springframework.web.multipart.MultipartFile
import spock.lang.Specification
import spock.lang.Subject

class UploadRestTest extends Specification {

    @Subject
    UploadRest subject

    def setup() {
        subject = new UploadRest()
        subject.with {
            docService = Mock(DocService)
        }
        GroovyMock(DateGenerator, global:true)
    }

    def "test upload"() {
        given:
        Date date = DateUtil.toDate('2022-10-31T00:00:00.000+0000')
        MultipartFile file = GroovyMock()
        Long userId = 1L

        when:
        ResultDto result = subject.upload(file, userId)

        then:
        1 * DateGenerator.date() >> date
        1 * subject.docService.uploadNewDoc(userId, file, date)

        and:
        result.success
    }
}
