package com.github.jvalentino.juliet.dto

import com.github.jvalentino.juliet.entity.Doc
import groovy.transform.CompileDynamic

/**
 * Represents the content for the dashboard
 * @author john.valentino
 */
@CompileDynamic
class DocListDto {

    List<Doc> documents = []

}
