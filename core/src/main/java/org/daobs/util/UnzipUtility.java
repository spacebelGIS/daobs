package org.daobs.util;

import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.utils.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * Utility class to extract files and directories of a standard
 * zip file to a destination directory.
 *
 * @author Jose García
 */
public class UnzipUtility {

  /**
   * Extracts a zip file specified by the file to a directory specified by
   * uncompressDir.
   *
   * @param file          Zip file.
   * @param uncompressDir Destination directory.
   * @throws Exception  Exception.
   */
  public void unzip(File file, File uncompressDir) throws Exception {
    InputStream is = null;
    OutputStream out = null;
    ArchiveInputStream in = null;

    try {
      is = new FileInputStream(file);
      in = new ArchiveStreamFactory().createArchiveInputStream(ArchiveStreamFactory.ZIP, is);
      ZipArchiveEntry entry = (ZipArchiveEntry) in.getNextEntry();

      while (entry != null) {
        try {

          String name = entry.getName();
          name = name.replace('\\', '/');
          File destinationFile = new File(uncompressDir, name);
          if (name.endsWith("/")) {
            if (!destinationFile.isDirectory()) {
              destinationFile.mkdirs();
            }
            entry = (ZipArchiveEntry) in.getNextEntry();
            continue;
          } else if (name.indexOf('/') != -1) {
            // Create the the parent directory if it doesn't exist
            File parentFolder = destinationFile.getParentFile();
            if (!parentFolder.isDirectory()) {
              parentFolder.mkdirs();
            }
          }

          out = new FileOutputStream(new File(uncompressDir.getAbsolutePath(), entry.getName()));
          IOUtils.copy(in, out);
        } finally {
          IOUtils.closeQuietly(out);
        }

        entry = (ZipArchiveEntry) in.getNextEntry();
      }

    } finally {
      IOUtils.closeQuietly(out);
      IOUtils.closeQuietly(is);
      IOUtils.closeQuietly(in);

    }
  }
}
