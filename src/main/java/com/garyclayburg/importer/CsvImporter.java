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

package com.garyclayburg.importer;

import au.com.bytecode.opencsv.CSVReader;
import com.garyclayburg.data.User;
import com.garyclayburg.data.UserService;
import com.mongodb.BasicDBObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: gclaybur
 * Date: 3/6/14
 * Time: 12:33 PM
 */
public class CsvImporter {
    private static final Logger log = LoggerFactory.getLogger(CsvImporter.class);

    public int importFile(File csvinput) {
        assert (csvinput.exists());
        int processedRecords = 0;
        try {
            UserService userService = new UserService();
            BasicDBObject document;

            CSVReader csvReader = new CSVReader(new FileReader(csvinput));
            String[] headerRow = csvReader.readNext();
            String[] dataRow = csvReader.readNext();
            while (dataRow != null) {
                processedRecords++;
                document = new BasicDBObject();
                int i;
                String uidToMatch = null;
                for (i = 0; i < dataRow.length; i++) {
                    document.put(headerRow[i],dataRow[i]);
                    if ("uid".equals(headerRow[i])) {
                        uidToMatch = dataRow[i];
                    }
                }
                User existingUser = userService.getUserById(uidToMatch);
                //todo: create hook here to pre-process input via groovy script before saving/updating user

                userService.saveUser(document,uidToMatch);
                dataRow = csvReader.readNext();
            }
        } catch (FileNotFoundException e) {
            log.warn("kaboom",e);
        } catch (IOException e) {
            log.warn("kaboom",e);
        }
        return processedRecords;
    }
}
