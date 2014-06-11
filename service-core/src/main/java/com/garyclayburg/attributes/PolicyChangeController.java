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

package com.garyclayburg.attributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;

/**
 * Created by IntelliJ IDEA.
 * Date: 6/6/14
 * Time: 2:12 PM
 *
 * @author Gary Clayburg
 */
public class PolicyChangeController {
    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(PolicyChangeController.class);
    private final HashSet<PolicyChangeListener> policyChangeListeners;

    public PolicyChangeController() {
        policyChangeListeners = new HashSet<>();
    }

    public void addChangeListener(PolicyChangeListener listener){
        policyChangeListeners.add(listener);
    }
    public void firePolicyChangedEvent() {
        log.debug("firing policy change to consoles: " + policyChangeListeners.size());
        for (PolicyChangeListener policyChangeListener : policyChangeListeners) {
            policyChangeListener.policyChanged();
        }
    }

    public void firePolicyException(Throwable e) {
        log.debug("firing policy exception to consoles: " + policyChangeListeners.size());
        for (PolicyChangeListener policyChangeListener : policyChangeListeners) {
            policyChangeListener.policyException(e);
        }

    }
}
