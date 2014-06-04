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

package embeddedgroovy.com.embedded
import com.garyclayburg.attributes.AttributesClass
import com.garyclayburg.attributes.TargetAttribute
import com.garyclayburg.persistence.domain.User

/**
 * Created by IntelliJ IDEA.
 * Date: 3/27/14
 * Time: 12:42 PM
 *
 * @author Gary Clayburg
 */
@AttributesClass
class DefaultAttributes {

    @TargetAttribute(target = "myAD",attributeName = "cn")
    static String cn(User user){
        return user.firstname +" "+ user.lastname
    }

    @TargetAttribute(target = "internalLDAP") //default attributeName is the name of the method - "objectclass" in this case
    static String objectclass(User user){
        return "top person inetorgperson"
    }

    @TargetAttribute(target = "myAD")
    static String buildDisplayName(User user){
        return user.lastname + ", " + user.firstname
    }

    @TargetAttribute(target = "myAD")
    static String contractor(User user){
        return "embedded Inc"
    }
}
