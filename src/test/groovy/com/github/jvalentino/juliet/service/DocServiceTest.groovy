package com.github.jvalentino.juliet.service

import com.github.jvalentino.juliet.dto.DocDto
import com.github.jvalentino.juliet.entity.AuthUser
import com.github.jvalentino.juliet.entity.Doc
import com.github.jvalentino.juliet.entity.DocVersion
import com.github.jvalentino.juliet.repo.AuthUserRepo
import com.github.jvalentino.juliet.repo.DocRepo
import com.github.jvalentino.juliet.repo.DocVersionRepo
import com.github.jvalentino.juliet.service.DocService
import com.github.jvalentino.juliet.util.DateUtil
import org.springframework.web.multipart.MultipartFile
import spock.lang.Specification
import spock.lang.Subject

class DocServiceTest extends Specification {

    @Subject
    DocService subject

    def setup() {
        subject = new DocService()
        subject.with {
            docRepo = Mock(DocRepo)
            docVersionRepo = Mock(DocVersionRepo)
            authUserRepo = Mock(AuthUserRepo)
        }
    }

    def "test countDocuments"() {
        when:
        Long result = subject.countDocuments()

        then:
        1 * subject.docRepo.count() >> 5L

        and:
        result == 5L
    }

    def "test uploadNewDoc"() {
        given:
        AuthUser user = new AuthUser(authUserId:123L)
        DocDto file = new DocDto()
        file.fileName = 'alpha.pdf'
        file.base64 = 'bravo'.bytes.encodeBase64()
        Date date = DateUtil.toDate('2022-10-31T00:00:00.000+0000')

        //println new File("./sample.pdf").bytes

        and:
        Optional<AuthUser> optional = GroovyMock()

        when:
        DocVersion result = subject.uploadNewDoc(user.authUserId, file, date)

        then:
        1 * subject.authUserRepo.findById(user.authUserId) >> optional
        1 * optional.get() >> user

        and:
        1 * subject.docRepo.save(_) >> { Doc doc ->
            assert doc.name == 'alpha.pdf'
            assert doc.mimeType == 'application/pdf'
            assert doc.createdByUser.authUserId == 123L
            assert doc.updatedByUser.authUserId == 123L
            assert doc.createdDateTime.time == date.time
            assert doc.updatedDateTime.time == date.time
            doc

        }
        1 * subject.docVersionRepo.save(_) >> { DocVersion version ->
            assert version.doc.name == 'alpha.pdf'
            assert new String(version.data) == 'bravo'
            assert version.createdDateTime.time == date.time
            assert version.createdByUser.authUserId == 123L
            assert version.versionNum == 1L
            version
        }

        and:
        result.versionNum == 1L
    }

    def "Test allDocs"() {
        given:
        List<Doc> docs = [new Doc()]

        when:
        List<Doc> results = subject.allDocs()

        then:
        1 * subject.docRepo.allDocs() >> docs

        and:
        results == docs
    }

    def "test uploadNewVersion"() {
        given:
        AuthUser user = new AuthUser(authUserId:123L)
        DocDto file = new DocDto()
        file.fileName = 'alpha.pdf'
        file.base64 = 'bravo'.bytes.encodeBase64()
        Date date = DateUtil.toDate('2022-10-31T00:00:00.000+0000')
        Long docId = 1L

        and:
        Optional optional = GroovyMock()
        Doc parentDoc = new Doc()

        and:
        Optional<AuthUser> optionalUser = GroovyMock()

        when:
        DocVersion result = subject.uploadNewVersion(user.authUserId, file, date, docId)

        then:
        1 * subject.authUserRepo.findById(user.authUserId) >> optionalUser
        1 * optionalUser.get() >> user

        and:
        1 * subject.docVersionRepo.countForDoc(docId) >> 2
        1 * subject.docRepo.findById(docId) >> optional
        1 * optional.get() >> parentDoc

        and:
        1 * subject.docRepo.save(_) >> { Doc doc ->
            assert doc.name == 'alpha.pdf'
            assert doc.updatedDateTime.time == date.time
            assert doc.updatedByUser.authUserId == user.authUserId
            doc
        }

        and:
        1 * subject.docVersionRepo.save(_) >> { DocVersion version ->
            assert version.doc.name == 'alpha.pdf'
            assert new String(version.data) == 'bravo'
            assert version.createdDateTime.time == date.time
            assert version.createdByUser.authUserId == user.authUserId
            assert version.versionNum == 3L
            version
        }

        and:
        result.doc.name == 'alpha.pdf'
    }
}
