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

/**
 * Create new user
 *
 * @param {string} range of new user
 * @customfunction
 */
function createUser(userRange){
    // if (userRange.map){
    var mydata = {
        "id": 57,
        "firstname": "Prince",
        "lastname": "Revolution"
    };
    var payload = JSON.stringify(mydata);
    var params = {
        "method": "post",
        "payload": payload,
        "contentType": "application/hal+json",
        "muteHttpExceptions": true
    };
    var response = UrlFetchApp.fetch("http://bitbucket.asuscomm.com:8089/audited-users/auditedsave",params);
    Logger.log(response);
    var newuser = JSON.parse(response.getContentText());
    return newuser.id;
}

function createUsers(userRange){
    var myData = {
        id: userRange[1][0],
        firstname: userRange[1][1],
        lastname: userRange[1][2]
    };

    var payload = JSON.stringify(myData);
    var returnUserIds = [];
    for (j=0;j<userRange.length -1;j++){  //number of users +1 header row
        var userpayload = "{";
        for (i=0;i<userRange[0].length -1;i++){  //number of attributes
            userpayload += "\"" +userRange[0][i] + "\":\"" + userRange[j+1][i]+"\",";
        }
        userpayload += "\"" +userRange[0][i] + "\":\"" + userRange[j+1][i]+"\"}";
        // "{"id":16,"firstname":"Donald","lastname":"Trump"}"
        var params = {
            "method": "post",
            "payload": userpayload,
            "contentType": "application/hal+json",
            "muteHttpExceptions": true
        };
        var response = UrlFetchApp.fetch("http://bitbucket.asuscomm.com:8089/audited-users/auditedsave",params);
        Logger.log("createusers response: " + response);
        var newuser = JSON.parse(response.getContentText());
        returnUserIds.push(newuser.id);
    }
    return returnUserIds;

}

function createStockuserRange(){
    var userRange= [["id","firstname","lastname"],[2017,"Donald","Trump"],[2008,"Hillary","Clinton"]];
    createUsers(userRange);
}

/**
 * Lookup up user by first name
 *
 * @param {string} firstname of the user to find
 * @return lastname of the stored user
 * @customfunction
 */

function findByFirstname(firstname){
    if (firstname.map){
        return firstname.map(findByFirstname);

    } else{
        var response = UrlFetchApp.fetch("http://bitbucket.asuscomm.com:8089/audited-users/findByFirstname?firstname=" + firstname);
        Logger.log(response.getContentText());
        if (response.getContentText() == ""){
            return "nonehere";
        } else {
            var myArr = JSON.parse(response.getContentText());
            return myFunction_(myArr);
        }
    }
}

/**
 * Lookup many users by first name
 *
 * @param {string} firstname range of cells of firstname of the user to find
 * @return lastname of the stored user
 * @customfunction
 */
function findOnceByFirstname(firstname){
    if (firstname.map){
        var response = UrlFetchApp.fetch("http://bitbucket.asuscomm.com:8089/audited-users/findOnceByFirstname?firstname=" + firstname);
        var myArr = JSON.parse(response.getContentText());
        return myArr.map(myFunction_);
    } else{
        return "errfornow"
    }
}

function myFunction_(user) {
    var retval = "nonesuch"
    if (user != null){
        retval = user.lastname;
    }
    return retval;
}
