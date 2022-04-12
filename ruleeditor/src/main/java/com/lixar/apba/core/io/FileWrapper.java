package com.lixar.apba.core.io;

import java.io.IOException;
import java.io.OutputStream;

public interface FileWrapper {
    void init() throws IOException;
    OutputStream getOutputStream(String filename) throws IOException;
}
