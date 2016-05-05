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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.garyclayburg.persistence.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @JsonIgnore
    List<GeneratedAttributesBean> attributes;

    Map<String, String> dynamicAttributes;

    public DynamicUser() {
    }

    public DynamicUser(User u) {
        super(u);
        dynamicAttributes = new HashMap<>();
    }

    public List<GeneratedAttributesBean> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<GeneratedAttributesBean> attributes) {
        this.attributes = attributes;
        for (GeneratedAttributesBean attribute : attributes) {
            dynamicAttributes.put(attribute.getAttributeName(),attribute.getAttributeValue());
        }
    }

    //    @JsonAnyGetter
    public Map<String, String> getDynamicAttributes() {
        return dynamicAttributes;
    }
}
