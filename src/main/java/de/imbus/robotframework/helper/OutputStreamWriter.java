/* SPDX-FileCopyrightText: 2023-present imbus AG

SPDX-License-Identifier: Apache-2.0package*/
package de.imbus.robotframework.helper;

import java.io.IOException;
import java.io.OutputStream;

public class OutputStreamWriter extends OutputStream {
    Writer writer;

    public OutputStreamWriter(Writer writer) {
        this.writer = writer;
    }

    @Override
    public void write(int b) throws IOException {
        this.writer.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        writer.writeBytes(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        writer.flush();
    }

}
