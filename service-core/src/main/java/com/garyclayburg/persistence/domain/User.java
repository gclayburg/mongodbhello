/*
 * VisualSync - a tool to visualize user data synchronization
 * Copyright (c) 2014 Gary Clayburg
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.garyclayburg.persistence.domain;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Date: 3/20/14
 * Time: 12:54 PM
 *
 * @author Gary Clayburg
 */
@Document
public class User {
    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(User.class);

    @Id
    protected String id;
    protected String firstname;
    protected String lastname;
    protected String email;
    protected String uid;

    // added by csv import (writing directly to DB, avoiding this User.class)
    protected Date createDate;
    protected Date modifiedDate;

    //spring data auditing
    @CreatedDate
    protected DateTime createdAt;
    @LastModifiedDate
    protected DateTime lastModifiedDate;
    @CreatedBy
    protected String createdBy;
    @LastModifiedBy
    protected String lastModifedBy;

    List<UserAccount> userAccounts;

    protected String characterStatus_id;

    public User() {
    }

    public User(User u) {
        this.firstname = u.getFirstname();
        this.lastname = u.getLastname();
        this.id = u.getId();
        this.email = u.getEmail();
        this.createdAt = u.getCreatedAt();
        this.createdBy = this.getCreatedBy();
        this.lastModifedBy = u.getLastModifedBy();
        this.lastModifiedDate = u.getLastModifiedDate();

        this.createDate = u.getCreateDate();
        this.modifiedDate = u.getModifiedDate();
        this.characterStatus_id = u.getCharacterStatus_id();
    }
    public List<UserAccount> getUserAccounts() {
        return userAccounts;
    }

    public void setUserAccounts(List<UserAccount> userAccounts) {
        this.userAccounts = userAccounts;
    }

    @SuppressWarnings("UnusedDeclaration")
    public DateTime getCreatedAt() {
        return createdAt;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    @SuppressWarnings("UnusedDeclaration")
    public DateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setLastModifiedDate(DateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getCreatedBy() {
        return createdBy;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getLastModifedBy() {
        return lastModifedBy;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setLastModifedBy(String lastModifedBy) {
        this.lastModifedBy = lastModifedBy;
    }

    @SuppressWarnings("UnusedDeclaration")
    public Date getCreateDate() {
        return createDate;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @SuppressWarnings("UnusedDeclaration")
    public Date getModifiedDate() {
        return modifiedDate;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getFirstname() {
        return firstname;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getUid() {
        return uid;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getLastname() {
        return lastname;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setUid(String uid) {
        this.uid = uid;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getId() {
        return id;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setId(String id) {
        this.id = id;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getEmail() {
        return email;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setEmail(String email) {
        this.email = email;
    }

    public String getCharacterStatus_id() {
        return characterStatus_id;
    }

    public void setCharacterStatus_id(String characterStatus_id) {
        this.characterStatus_id = characterStatus_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (id != null ? !id.equals(user.id) : user.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
