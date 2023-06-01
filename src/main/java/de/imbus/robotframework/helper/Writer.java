package de.imbus.robotframework.helper;

import java.io.IOException;

public interface Writer {
    public void write(int b) throws IOException;

    void writeBytes(byte[] b, int off, int len) throws IOException;

    void flush();
}
