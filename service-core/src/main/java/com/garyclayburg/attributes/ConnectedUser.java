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

import com.garyclayburg.persistence.domain.CharacterStatus;
import com.garyclayburg.persistence.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * This User has access to see user attributes from connected systems, but not to any generated attributes
 * Date: 9/24/16
 * Time: 9:29 PM
 *
 * @author Gary Clayburg
 */
public class ConnectedUser extends User {
    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(ConnectedUser.class);
    protected CharacterStatus CStatus;
    Map<String, String> characterStatusSheet;

    public ConnectedUser() {
    }

    public ConnectedUser(User u) {
        super(u);
    }
    public void setCStatus(CharacterStatus CStatus) {
        this.CStatus = CStatus;
    }

    public CharacterStatus getCStatus() {
        return CStatus;
    }

}
