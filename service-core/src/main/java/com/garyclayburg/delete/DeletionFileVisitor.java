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

package com.garyclayburg.delete;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Created by IntelliJ IDEA. Date: 4/7/14 Time: 12:05 PM
 *
 * @author Gary Clayburg
 */

public final class DeletionFileVisitor implements FileVisitor<Path> {
  @SuppressWarnings("UnusedDeclaration")
  private static final Logger log = LoggerFactory.getLogger(DeletionFileVisitor.class);

  @Override
  public FileVisitResult preVisitDirectory(final Path dir,final BasicFileAttributes attrs) throws IOException {
    return FileVisitResult.CONTINUE;
  }

  @Override
  public FileVisitResult visitFile(final Path file,final BasicFileAttributes attrs) throws IOException {
    log.debug("delete file : " + file);
    Files.delete(file);
    return FileVisitResult.CONTINUE;
  }

  @Override
  public FileVisitResult visitFileFailed(final Path file,final IOException exc) throws IOException {
    throw exc;
  }

  @Override
  public FileVisitResult postVisitDirectory(final Path dir,final IOException exc) throws IOException {
    if (exc != null) throw exc;
    log.debug("delete dir: " + dir);
    Files.delete(dir);
    return FileVisitResult.CONTINUE;
  }

  public static void deletePath(Path rootDir,String fileGlob) throws IOException {
    DirectoryStream<Path> paths = Files.newDirectoryStream(rootDir,fileGlob);
    for (Path path : paths) {
      log.info("deleting path: " + path.toString() + " name: " + path.getFileName());
      Files.walkFileTree(path,new DeletionFileVisitor());
    }
  }

}
