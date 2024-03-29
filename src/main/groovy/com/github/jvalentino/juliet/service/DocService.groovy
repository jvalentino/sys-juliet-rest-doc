package com.github.jvalentino.juliet.service

import com.github.jvalentino.juliet.dto.DocDto
import com.github.jvalentino.juliet.entity.AuthUser
import com.github.jvalentino.juliet.entity.Doc
import com.github.jvalentino.juliet.entity.DocVersion
import com.github.jvalentino.juliet.repo.AuthUserRepo
import com.github.jvalentino.juliet.repo.DocRepo
import com.github.jvalentino.juliet.repo.DocVersionRepo
import groovy.transform.CompileDynamic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.sql.Timestamp

/**
 * General service for interacting with documents
 * @author john.valentino
 */
@CompileDynamic
@Service
@Slf4j
class DocService {

    @Autowired
    DocRepo docRepo

    @Autowired
    DocVersionRepo docVersionRepo

    @Autowired
    AuthUserRepo authUserRepo

    Long countDocuments() {
        docRepo.count()
    }

    DocVersion uploadNewDoc(Long userId, DocDto file, Date date) {
        AuthUser user = authUserRepo.findById(userId).get()
        String ext = file.fileName.split('\\.').last()

        Doc parentDoc = new Doc()
        parentDoc.with {
            name = file.fileName
            mimeType = "application/${ext}"
            createdByUser = user
            updatedByUser = user
            createdDateTime = new Timestamp(date.time)
            updatedDateTime = new Timestamp(date.time)
        }
        docRepo.save(parentDoc)

        DocVersion version = new DocVersion()
        version.with {
            doc = parentDoc
            data = file.base64.decodeBase64()
            createdDateTime = new Timestamp(date.time)
            createdByUser = user
            versionNum = 1
        }
        docVersionRepo.save(version)
    }

    List<Doc> allDocs() {
        docRepo.allDocs()
    }

    Doc retrieveDocVersions(Long docId) {
        Doc doc = docRepo.findById(docId).get()
        doc.versions = docVersionRepo.getVersionsWithoutData(docId)
        doc
    }

    DocDto retrieveVersion(Long docVersionId) {
        DocVersion version = docVersionRepo.getWithParent(docVersionId).first()
        DocDto result = new DocDto()
        result.with {
            fileName = version.doc.name
            mimeType = version.doc.mimeType
            base64 = version.data.encodeBase64()
        }
        result
    }

    DocVersion uploadNewVersion(Long userId, DocDto file, Date date, Long docId) {
        AuthUser user = authUserRepo.findById(userId).get()
        long currentCount = docVersionRepo.countForDoc(docId)

        Doc parentDoc = docRepo.findById(docId).get()
        parentDoc.with {
            name = file.fileName
            updatedByUser = user
            updatedDateTime = new Timestamp(date.time)
        }

        docRepo.save(parentDoc)

        DocVersion version = new DocVersion()
        version.with {
            doc = parentDoc
            data = file.base64.decodeBase64()
            createdDateTime = new Timestamp(date.time)
            createdByUser = user
            versionNum = currentCount + 1
        }
        docVersionRepo.save(version)
    }

}
