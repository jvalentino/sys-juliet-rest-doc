package com.github.jvalentino.juliet.rest

import com.github.jvalentino.juliet.dto.CountDto
import com.github.jvalentino.juliet.dto.DocDto
import com.github.jvalentino.juliet.dto.DocListDto
import com.github.jvalentino.juliet.dto.ViewVersionDto
import com.github.jvalentino.juliet.entity.AuthUser
import com.github.jvalentino.juliet.entity.Doc
import com.github.jvalentino.juliet.entity.DocVersion
import com.github.jvalentino.juliet.util.BaseIntg
import org.springframework.test.web.servlet.MvcResult

import java.sql.Timestamp

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class DocRestIntgTest extends BaseIntg {

    def "test dashboard"() {
        when:
        MvcResult response = mvc.perform(
                get("/doc/all").header('X-Auth-Token', '123'))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()

        then:
        DocListDto result = this.toObject(response, DocListDto)
        result.documents.size() == 0
    }

    def "test unauthorized"() {
        when:
        MvcResult response = mvc.perform(
                get("/doc/all"))
                .andDo(print())
                .andExpect(status().is(401))
                .andReturn()

        then:
        true
    }

    def "test count"() {
        when:
        MvcResult response = mvc.perform(
                get("/doc/count").header('X-Auth-Token', '123'))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()

        then:
        CountDto result = this.toObject(response, CountDto)
        result.value == 0
    }

    def "test versions"() {
        given:
        AuthUser user = new AuthUser()
        user.with {
            email = 'admin'
            password = 'admin'
            salt = 'admin'
            firstName = 'admin'
            lastName = 'admin'
        }
        this.entityManager.persist(user)

        Doc doc = new Doc()
        doc.with {
            name = 'alpha.pdf'
            createdByUser = user
            updatedByUser = user
            mimeType = 'application/json'
            createdDateTime = new Timestamp(new Date().time)
            updatedDateTime = new Timestamp(new Date().time)
        }
        this.entityManager.persist(doc)

        DocVersion version = new DocVersion(doc:doc)
        version.with {
            versionNum = 1L
            createdDateTime = new Timestamp(new Date().time)
            createdByUser = user
        }
        this.entityManager.persist(version)

        when:
        MvcResult response = mvc.perform(
                get("/doc/versions/${doc.docId}").header('X-Auth-Token', '123'))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()

        then:
        ViewVersionDto model = this.toObject(response, ViewVersionDto)
        model.doc.name == 'alpha.pdf'
        model.doc.versions.size() == 1
        model.doc.versions.first().versionNum == 1
    }

    def "test download version"() {
        given:
        AuthUser user = new AuthUser()
        user.with {
            email = 'admin'
            password = 'admin'
            salt = 'admin'
            firstName = 'admin'
            lastName = 'admin'
        }
        this.entityManager.persist(user)

        Doc doc = new Doc()
        doc.with {
            name = 'alpha.txt'
            createdByUser = user
            updatedByUser = user
            mimeType = 'text/plain'
            createdDateTime = new Timestamp(new Date().time)
            updatedDateTime = new Timestamp(new Date().time)
        }
        this.entityManager.persist(doc)

        DocVersion version = new DocVersion(doc:doc)
        version.with {
            versionNum = 1L
            createdDateTime = new Timestamp(new Date().time)
            createdByUser = user
            data = "this is a test".bytes
        }
        this.entityManager.persist(version)

        when:
        MvcResult response = mvc.perform(
                get("/doc/version/download/${version.docVersionId}").header('X-Auth-Token', '123'))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()

        then:
        DocDto result = this.toObject(response, DocDto)
        new String(result.base64.decodeBase64()) == 'this is a test'

    }

}
