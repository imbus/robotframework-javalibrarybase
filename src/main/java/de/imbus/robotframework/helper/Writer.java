/* SPDX-FileCopyrightText: 2023-present imbus AG

SPDX-License-Identifier: Apache-2.0package*/
package de.imbus.robotframework.helper;

import java.io.IOException;

public interface Writer {
    public void write(int b) throws IOException;

    void writeBytes(byte[] b, int off, int len) throws IOException;

    void flush();
}
