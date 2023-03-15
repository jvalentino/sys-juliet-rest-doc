package com.github.jvalentino.juliet.dto

import groovy.transform.CompileDynamic

/**
 * Represents an uploaded document
 */
@CompileDynamic
class DocDto {

    String fileName
    String base64
    String mimeType

}
