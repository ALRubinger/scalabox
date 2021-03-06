package org.scalabox.util

import collection.JavaConversions._
import java.io._
import org.scalabox.util.Closeable._
import java.net.URISyntaxException
import org.scalabox.logging.Log
import java.util.jar.JarFile

/**
 * Filesystem related utility methods
 *
 * @author Galder Zamarreño
 * @since 1.0
 */
object FileSystem extends Log {

   def mkDirs(parent: File, child: String): File = mkDirs(parent, child, false)

   def mkDirs(parent: File, child: String, deleteIfPresent: Boolean): File = {
      val f = new File(parent, child)
      if (deleteIfPresent & f.exists()) deleteDirectory(f)
      f.mkdirs()
      f
   }

   /**
    * Returns the target directory
    */
   def getTarget(clazz: Class[_]): File = {
      try {
         new File(new File(clazz.getProtectionDomain
               .getCodeSource.getLocation.toURI).getParent)
      } catch {
         case e: URISyntaxException => {
            throw new RuntimeException("Could not obtain the target URI", e)
         }
      }
   }

   def copy(srcPath: String, destPath: String) {
      copy(new File(srcPath), new File(destPath))
   }

   def copy(src: File, dest: File) {
      if (src.isDirectory) {
         // if directory not exists, create it
         if (!dest.exists()) {
            dest.mkdir();
            debug("Directory copied from %s to %s",
               src.getCanonicalPath, dest.getCanonicalPath)
         }
         // List all the directory contents
         asScalaIterator(src.list().iterator).foreach { file =>
            // Recursive copy
            copy(new File(src, file), new File(dest, file))
         }
      } else {
         copy(new FileInputStream(src), new FileOutputStream(dest))
         debug("File copied from %s to %s",
            src.getCanonicalPath, dest.getCanonicalPath)
      }
   }

   def copy(in: InputStream, out: OutputStream) {
      use(in) { in =>
         use(out) { out =>
            val buffer = new Array[Byte](1024)
            Iterator.continually(in.read(buffer))
               .takeWhile(_ != -1)
               .foreach {
                  out.write(buffer, 0, _)
               }
         }
      }
   }

   /**
    * Recursively deletes a directory and all its contents
    *
    * @param directory
    */
   def deleteDirectory(directory: File) {
      if (directory.isDirectory && directory.exists) {
         for (file <- directory.listFiles) {
            if (file.isDirectory) {
               deleteDirectory(file)
            } else {
               if (!file.delete) {
                  throw new RuntimeException(
                     "Failed to delete file: %s".format(file))
               }
            }
         }
         if (!directory.delete) {
            throw new RuntimeException(
               "Failed to delete directory: %s".format(directory))
         }
      }
      else {
         throw new RuntimeException(("Unable to delete directory: %s.  " +
               "It is either not a directory or does not exist.")
               .format(directory))
      }
   }

   def unzip(file: File, target: File) {
      val zip = new JarFile(file)
      enumerationAsScalaIterator(zip.entries).foreach { entry =>
         val entryPath = entry.getName
         println("Extracting to " + target.getCanonicalPath + "/" + entryPath)
         if (entry.isDirectory) {
            new File(target, entryPath).mkdirs
         } else {
            copy(zip.getInputStream(entry),
               new FileOutputStream(new File(target, entryPath)))
         }
      }
   }

}