package com.lixar.apba.core.io;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import java.io.IOException;
import java.io.OutputStream;

public class AzureFileWrapper implements FileWrapper {
    private String connectionString;
    private String container;
    CloudBlobContainer blobContainer;
    private boolean initialized = false;

    public AzureFileWrapper(String connectionString, String container) {
        this.connectionString = connectionString;
        this.container = container.toLowerCase();
    }

    @Override
    public void init() throws IOException {
        try {
            CloudStorageAccount account = CloudStorageAccount.parse(connectionString);
            CloudBlobClient serviceClient = account.createCloudBlobClient();

            // Container name must be lower case.
            blobContainer = serviceClient.getContainerReference(container);
            blobContainer.createIfNotExists();

            initialized = true;
        } catch (Exception e) {
            throw new IOException("Error connecting to Azure", e);
        }
    }

    @Override
    public OutputStream getOutputStream(String filename) throws IOException {
        OutputStream out;

        if (!initialized) {
            init();
        }

        try {
            filename = filename.replace("\\", "/");

            CloudBlockBlob blob = blobContainer.getBlockBlobReference(filename);

            out = blob.openOutputStream();
        } catch (Exception e) {
            throw new IOException("Error opening outputstream to Azure", e);
        }

        return out;
    }
}
