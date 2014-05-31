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

package com.garyclayburg.filesystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * Date: 5/28/14
 * Time: 8:26 AM
 *
 * @author Gary Clayburg
 */
public class DirectoryWalker {
    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(DirectoryWalker.class);
    /**
     * Pretty print the directory tree and its file names.
     *
     * @param folder
     *            must be a folder.
     * @return String representation of Directory tree
     */
    public static String printDirectoryTree(File folder) {
        if (!folder.isDirectory()) {
            throw new IllegalArgumentException("folder is not a Directory");
        }
        int indent = 0;
        StringBuilder sb = new StringBuilder();
        printDirectoryTree(folder, indent, sb);
        return sb.toString();
    }

    private static void printDirectoryTree(File folder, int indent,
                                           StringBuilder sb) {
        if (!folder.isDirectory()) {
            throw new IllegalArgumentException("folder is not a Directory");
        }
        sb.append(getIndentString(indent));
        sb.append("+--");
        sb.append(folder.getName());
        sb.append("/");
        sb.append("\n");
        File[] files = folder.listFiles();
        if (files !=null && files.length >0) {
            for (File file : files) {
                if (file.isDirectory()) {
                    printDirectoryTree(file,indent + 1,sb);
                } else {
                    printFile(file,indent + 1,sb);
                }
            }
        }
    }

    private static void printFile(File file, int indent, StringBuilder sb) {
        sb.append(getIndentString(indent));
        sb.append("+--");
        sb.append(file.getName());
        sb.append("\n");
    }

    private static String getIndentString(int indent) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            sb.append("|  ");
        }
        return sb.toString();
    }
}
