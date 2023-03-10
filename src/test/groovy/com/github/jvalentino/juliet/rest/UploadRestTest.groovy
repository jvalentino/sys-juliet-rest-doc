package com.github.jvalentino.juliet.rest

import com.github.jvalentino.juliet.dto.ResultDto
import com.github.jvalentino.juliet.entity.AuthUser
import com.github.jvalentino.juliet.rest.UploadRest
import com.github.jvalentino.juliet.service.DocService
import com.github.jvalentino.juliet.service.UserService
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
            userService = Mock(UserService)
            docService = Mock(DocService)
        }
        GroovyMock(DateGenerator, global:true)
    }

    def "test upload"() {
        given:
        Date date = DateUtil.toDate('2022-10-31T00:00:00.000+0000')
        MultipartFile file = GroovyMock()
        AuthUser user = Mock()

        when:
        ResultDto result = subject.upload(file)

        then:
        1 * DateGenerator.date() >> date
        1 * subject.userService.currentLoggedInUser() >> user
        1 * subject.docService.uploadNewDoc(user, file, date)

        and:
        result.success
    }
}
