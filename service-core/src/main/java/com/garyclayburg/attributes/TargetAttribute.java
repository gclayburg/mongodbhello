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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by IntelliJ IDEA.
 * Date: 4/4/14
 * Time: 2:23 PM
 * Indicates that this method generates an attribute value suitable for a target
 *
 * @author Gary Clayburg
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface TargetAttribute {
    /**
     * name of target system that this attribute is generated for
     *
     * @return name of target system
     */
    String target() default "";

    /**
     * Nameof the attribute generated for the user
     * @return attribute name, displayName, for example
     */
    String attributeName() default "";

}





