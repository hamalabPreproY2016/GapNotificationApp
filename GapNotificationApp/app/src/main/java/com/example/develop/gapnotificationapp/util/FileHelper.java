package com.example.develop.gapnotificationapp.util;

import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by develop on 2017/04/25.
 */

public class FileHelper {
    public static String getRelativePath(File file, File baseFile) {
        File currentDir = baseFile;

        String retPath = ".";

        try {
            for(; !file.getAbsolutePath().startsWith(currentDir.getCanonicalPath()); currentDir = currentDir.getParentFile()) {
                retPath = retPath + "/..";
            }

            retPath = retPath + StringUtils.difference(currentDir.getCanonicalPath(), file.getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();

            return null;
        }

        return retPath;
    }

    public static File getFileFromRelativePath(String filePath, File baseFile) {
        return new File(baseFile, filePath);
    }
}
