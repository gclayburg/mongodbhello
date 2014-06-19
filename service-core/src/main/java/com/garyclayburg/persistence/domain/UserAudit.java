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

/**
 * Created by IntelliJ IDEA.
 * Date: 4/1/14
 * Time: 1:25 PM
 *
 * @author Gary Clayburg
 */
@Document
public class UserAudit {
    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(UserAudit.class);

    @Id
    private String id;
    private User user;

    //spring data auditing
    @CreatedDate
    private DateTime createdAt;
    @LastModifiedDate
    private DateTime lastModifiedDate;
    @CreatedBy
    private String createdBy;
    @LastModifiedBy
    private String lastModifedBy;

    //todo - should this be read only ouside package?
    public UserAudit() {
    }

    public UserAudit(User user) {
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    public DateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(DateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getLastModifedBy() {
        return lastModifedBy;
    }

    public void setLastModifedBy(String lastModifedBy) {
        this.lastModifedBy = lastModifedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserAudit userAudit = (UserAudit) o;

        if (createdAt != null ? !createdAt.equals(userAudit.createdAt) : userAudit.createdAt != null) return false;
        if (createdBy != null ? !createdBy.equals(userAudit.createdBy) : userAudit.createdBy != null) return false;
        if (id != null ? !id.equals(userAudit.id) : userAudit.id != null) return false;
        if (lastModifedBy != null ? !lastModifedBy.equals(userAudit.lastModifedBy) : userAudit.lastModifedBy != null)
            return false;
        if (lastModifiedDate != null ? !lastModifiedDate.equals(userAudit.lastModifiedDate) :
            userAudit.lastModifiedDate != null) return false;
        if (user != null ? !user.equals(userAudit.user) : userAudit.user != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (lastModifiedDate != null ? lastModifiedDate.hashCode() : 0);
        result = 31 * result + (createdBy != null ? createdBy.hashCode() : 0);
        result = 31 * result + (lastModifedBy != null ? lastModifedBy.hashCode() : 0);
        return result;
    }

}
