/* SPDX-FileCopyrightText: 2023-present imbus AG

SPDX-License-Identifier: Apache-2.0package*/
package de.imbus.robotframework.exceptions;

public class Error extends RobotFrameworkException {
    private static final long serialVersionUID = 1L;

    public Error(String message) {
        super(message);
    }

    public Error(String message, boolean html) {
        super(message, html);
    }
}
