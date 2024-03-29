package com.github.jvalentino.juliet.entity

import com.fasterxml.jackson.annotation.JsonFormat
import com.github.jvalentino.juliet.util.DateUtil
import groovy.transform.CompileDynamic

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.Table
import java.sql.Timestamp

/**
 * represents the document
 * @author john.valentino
 */
@CompileDynamic
@Entity
@Table(name = 'doc')
class Doc {

    @Id @GeneratedValue
    @Column(name = 'doc_id')
    Long docId

    String name

    @Column(name = 'mime_type')
    String mimeType

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(name = 'created_by_user_id', referencedColumnName = 'auth_user_id')
    AuthUser createdByUser

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(name = 'updated_by_user_id', referencedColumnName = 'auth_user_id')
    AuthUser updatedByUser

    @Column(name = 'created_datetime')
    @JsonFormat(pattern=DateUtil.ISO)
    Timestamp createdDateTime

    @Column(name = 'updated_datetime')
    @JsonFormat(pattern=DateUtil.ISO)
    Timestamp updatedDateTime

    @OneToMany(mappedBy='doc', fetch = FetchType.LAZY)
    Set<DocVersion> versions

    @OneToMany(mappedBy='doc', fetch = FetchType.LAZY)
    Set<DocTask> tasks

}
