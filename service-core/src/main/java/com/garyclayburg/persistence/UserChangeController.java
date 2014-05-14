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

package com.garyclayburg.persistence;

import com.garyclayburg.persistence.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * Date: 5/12/14
 * Time: 4:33 PM
 *
 * @author Gary Clayburg
 */
public class UserChangeController {
    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(UserChangeController.class);
    private Map<User,UserChangeListener> changeListenerMap;

    public UserChangeController() {
        changeListenerMap = new HashMap<User,UserChangeListener>();
    }

    public void addChangeListener(User user, UserChangeListener listener) {
        changeListenerMap.put(user,listener);
    }
    public void removeChangeListener(UserChangeListener listener){

    }

    public void fireUserChangedEvent(User savedUser) {
        UserChangeListener userChangeListener = changeListenerMap.get(savedUser);
        if (userChangeListener !=null) {
            log.debug("firing user change: " + savedUser.getFirstname());
            userChangeListener.userChanged(savedUser);
        }
    }
}
