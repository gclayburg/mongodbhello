/*
 * VisualSync - a tool to visualize user data synchronization
 * Copyright (c) 2016 Gary Clayburg
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

package com.garyclayburg.attributes;

import com.garyclayburg.persistence.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Date: 5/3/16
 * Time: 5:36 PM
 *
 * @author Gary Clayburg
 */
public class DynamicUser extends User {
    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(DynamicUser.class);

    List<GeneratedAttributesBean> attributes;

    public DynamicUser(User u) {
        //todo - move this to super()
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
    }

    public List<GeneratedAttributesBean> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<GeneratedAttributesBean> attributes) {
        this.attributes = attributes;
    }
}
