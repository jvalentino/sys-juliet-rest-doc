package com.github.jvalentino.juliet.rest

import com.github.jvalentino.juliet.dto.DocListDto
import com.github.jvalentino.juliet.dto.ResultDto
import com.github.jvalentino.juliet.dto.ViewVersionDto
import com.github.jvalentino.juliet.entity.DocVersion
import com.github.jvalentino.juliet.service.DocService
import com.github.jvalentino.juliet.util.DateGenerator
import groovy.transform.CompileDynamic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

import javax.servlet.http.HttpServletResponse

/**
 * The general rest endpoint for accessing all document related things.
 * @author john.valentino
 */
@CompileDynamic
@Slf4j
@RestController
@SuppressWarnings(['UnnecessarySetter', 'UnnecessaryGetter'])
class DocRest {

    @Autowired
    DocService docService

    @GetMapping('/doc/all')
    DocListDto dashboard() {
        DocListDto dashboard = new DocListDto()
        dashboard.with {
            documents = docService.allDocs()
        }

        dashboard
    }

    @PostMapping('/doc/upload/user/{userId}')
    ResultDto upload(@RequestParam('file') MultipartFile file, @PathVariable(value='userId') Long userId) {
        docService.uploadNewDoc(userId, file, DateGenerator.date())
        new ResultDto()
    }

    @GetMapping('/doc/versions/{docId}')
    ViewVersionDto versions(@PathVariable(value='docId') Long docId) {
        ViewVersionDto result = new ViewVersionDto()
        result.with {
            doc = docService.retrieveDocVersions(docId)
        }

        log.info("Doc ${docId} has ${result.doc.versions.size()} versions")

        result
    }

    // https://www.baeldung.com/servlet-download-file
    @GetMapping('/doc/version/download/{docVersionId}')
    void downloadVersion(@PathVariable(value='docVersionId') Long docVersionId, HttpServletResponse response) {
        DocVersion version = docService.retrieveVersion(docVersionId)

        response.setContentType(version.doc.mimeType)
        response.setHeader('Content-disposition',
                "attachment; filename=${version.doc.name.replaceAll(' ', '')}")

        InputStream is = new ByteArrayInputStream(version.data)
        OutputStream out = response.getOutputStream()

        byte[] buffer = new byte[1048]

        int numBytesRead
        while ((numBytesRead = is.read(buffer)) > 0) {
            out.write(buffer, 0, numBytesRead)
        }
    }

    @PostMapping('/doc/version/new/{docId}/user/{userId}')
    ResultDto upload(@RequestParam('file') MultipartFile file, @PathVariable(value='docId') Long docId,
                     @PathVariable(value='userId') Long userId) {
        docService.uploadNewVersion(userId, file, DateGenerator.date(), docId)

        new ResultDto()
    }

}
