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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * Date: 3/20/14
 * Time: 12:54 PM
 *
 * @author Gary Clayburg
 */

public class User {
    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(User.class);

    @Id
    private String userId;
    private String firstname;
    private String lastname;
    private String email;
    private String uid;

    private Date createDate;
    private Date modifiedDate;

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
    public String getUserId() {
        return userId;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getEmail() {
        return email;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setEmail(String email) {
        this.email = email;
    }
}
