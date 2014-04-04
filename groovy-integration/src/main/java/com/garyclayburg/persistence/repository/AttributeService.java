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

package com.garyclayburg.persistence.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * Date: 4/4/14
 * Time: 9:32 AM
 *
 * @author Gary Clayburg
 */
public class AttributeService {
    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(AttributeService.class);
    private String scanPackage;

    public void setScanPackage(String scanPackage) {
        this.scanPackage = scanPackage;
    }

    public String getScanPackage() {
        return scanPackage;
    }

//    public Map<String, String> generateAttributes(User barney) {
//        HashMap<String, String> generatedAttributes = new HashMap<String, String>();
//        generatedAttributes.put("cn","Barney Rubble");
//        return generatedAttributes;
//    }
}
