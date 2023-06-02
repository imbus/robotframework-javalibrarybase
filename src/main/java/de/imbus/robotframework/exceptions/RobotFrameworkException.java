/* SPDX-FileCopyrightText: 2023-present imbus AG

SPDX-License-Identifier: Apache-2.0package*/
package de.imbus.robotframework.exceptions;

public class RobotFrameworkException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final boolean html;

    public RobotFrameworkException(String message) {
        super(message);
        this.html = false;
    }

    public RobotFrameworkException(String message, boolean html) {
        super(message);

        this.html = html;
    }

    public boolean isHtml() {
        return html;
    }

}
