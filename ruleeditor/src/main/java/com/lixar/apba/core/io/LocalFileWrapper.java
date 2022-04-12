package com.lixar.apba.core.io;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class LocalFileWrapper implements FileWrapper {

    @SuppressWarnings("unused")
	private final Logger log = LoggerFactory.getLogger(LocalFileWrapper.class);

    private String backupDirectory;
    private boolean initialized = false;

    public LocalFileWrapper(String backupDirectory) {
        this.backupDirectory = backupDirectory;
    }

    @Override
    public void init() throws IOException {
        initialized = true;
    }

    @Override
    public OutputStream getOutputStream(String filename) throws IOException {
        if (!initialized) {
            init();
        }

        File backupFile = new File(backupDirectory, filename);
        File backupDir = backupFile.getParentFile();

        FileUtils.forceMkdir(backupDir);

        return new FileOutputStream(backupFile);
    }
}
