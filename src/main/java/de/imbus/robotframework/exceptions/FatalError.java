/* SPDX-FileCopyrightText: 2023-present imbus AG

SPDX-License-Identifier: Apache-2.0package*/
package de.imbus.robotframework.exceptions;

public class FatalError extends RobotFrameworkException {
    private static final long serialVersionUID = 1L;

    public FatalError(String message) {
        super(message);
    }

    public FatalError(String message, boolean html) {
        super(message, html);
    }
}
