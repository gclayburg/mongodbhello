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

package com.garyclayburg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * Date: 6/25/14
 * Time: 8:19 AM
 *
 * @author Gary Clayburg
 */
@Component
@ConfigurationProperties(prefix = "policy")
public class ApplicationSettings {
    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(ApplicationSettings.class);

    private boolean forceRecompileEntryPoints =true;  //safest setting, although slower

    private String standardName = "novaluehere";

    /*
    override values via spring boot application.properties, i.e.:
    #######
    policy.standardName=unused
    policy.forceRecompileEntryPoints=true

     */
    public boolean isForceRecompileEntryPoints() {
        return forceRecompileEntryPoints;
    }

    public void setForceRecompileEntryPoints(boolean forceRecompileEntryPoints) {
        this.forceRecompileEntryPoints = forceRecompileEntryPoints;
    }

    public String getStandardName() {
        return standardName;
    }

    public void setStandardName(String standardName) {
        this.standardName = standardName;
    }
}
