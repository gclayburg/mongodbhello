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

package com.garyclayburg.persistence.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;

/**
 * Created by IntelliJ IDEA.
 * Date: 9/22/16
 * Time: 10:08 AM
 *
 * @author Gary Clayburg
 */
public class CharacterStatus {
    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(CharacterStatus.class);

    @Id
    public String id;
    public String lastname;
    public String genDisplayname;
    public String deadnow;

    public CharacterStatus(String id,String lastname,String genDisplayname,String deadnow) {
        this.id = id;
        this.lastname = lastname;
        this.genDisplayname = genDisplayname;
        this.deadnow = deadnow;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getGenDisplayname() {
        return genDisplayname;
    }

    public void setGenDisplayname(String genDisplayname) {
        this.genDisplayname = genDisplayname;
    }

    public String getDeadnow() {
        return deadnow;
    }

    public void setDeadnow(String deadnow) {
        this.deadnow = deadnow;
    }
}
