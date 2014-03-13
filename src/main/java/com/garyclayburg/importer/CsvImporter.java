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
                    log.info("out:" + headerRow[i] + " " + dataRow[i]);
                    document.put(headerRow[i],dataRow[i]);
                    if ("uid".equals(headerRow[i])) {
                        uidToMatch = dataRow[i];
                    }
                }
                User existingUser = userService.getUserById(uidToMatch);

                //todo: create hook here to pre-process input via groovy script before saving/updating user
                log.info("writing doc");
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
