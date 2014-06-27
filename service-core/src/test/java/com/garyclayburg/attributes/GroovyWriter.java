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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * Date: 6/23/14
 * Time: 10:00 AM
 *
 * @author Gary Clayburg
 */
public class GroovyWriter {
    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(GroovyWriter.class);
    private final File rootDir;

    public GroovyWriter(File multipleClassDir) {
        this.rootDir = multipleClassDir;

    }

    public File write(String groovyFileContents,String fileName) throws IOException {
        File groovyFile = new File(rootDir,fileName);
        FileWriter fw2 = new FileWriter(groovyFile);
        fw2.write(groovyFileContents);
        fw2.close();
        return groovyFile;
    }

    public File getRootDir() {
        return rootDir;
    }
}
