package com;

import com.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;

public class ResourceManager {

    private static ResourceManager instance;
    private HashMap<String, String> cache;

    private ResourceManager() {
        this.cache = new HashMap<>();
    }

    public void copyResources(File inputDirectory, File outputDirectory) {
        String resourcesRootPath = inputDirectory.getAbsolutePath();
        String targetRootPath = outputDirectory.getAbsolutePath();
        this.cache.entrySet().forEach(path -> {
            try {
                String resourceRelativePath = "\\" + path.getValue().replace("/", "\\");

                File resourceFile = new File(resourcesRootPath + resourceRelativePath);
                Path resourcePath = resourceFile.toPath();

                File targetFile = new File(targetRootPath + resourceRelativePath);
                File parentDirectory = targetFile.getParentFile();
                if (!parentDirectory.exists()) {
                    parentDirectory.mkdirs();
                }
                Path targetPath = targetFile.toPath();
                try {
                    Files.copy(resourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                Logger.error("Could not copy file: " + path);
            }
        });
    }

    public void put(String path) {
        this.cache.put(path, path);
    }

    public String get(String path) {
        return this.cache.get(path);
    }

    public void clear() {
        this.cache = new HashMap<>();
    }

    public HashMap<String, String> getCache() {
        return this.cache;
    }

    public static ResourceManager getInstance() {
        if (instance == null) {
            instance = new ResourceManager();
        }
        return instance;
    }

}
