package com.linqia.abmat;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestingUtils {

    private static final Logger LOG = LoggerFactory
            .getLogger(TestingUtils.class);

    public static boolean delete(File file) {
        if (!file.exists()) {
            return true;
        }
        if (file.isFile()) {
            return file.delete();
        } else if (file.isDirectory()) {
            for (File innerFile : file.listFiles()) {
                if (!delete(innerFile)) {
                    LOG.debug("Failed to delete {}", innerFile);
                    return false;
                }
            }
            return file.delete();
        } else {
            LOG.debug("Unknown file type {}", file);
            return false;
        }
    }

    public static File prepareTestDir(Class<?> clazz) {
        File dir = new File("target/test-files/" + clazz.getName());
        if (!delete(dir)) {
            throw new RuntimeException("Unable to delete folder: " + dir);
        }
        dir.mkdirs();
        return dir;
    }
}
