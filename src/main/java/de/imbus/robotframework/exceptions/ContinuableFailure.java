package de.imbus.robotframework.exceptions;

public class ContinuableFailure extends RobotFrameworkException {
    private static final long serialVersionUID = 1L;

    public ContinuableFailure(String message) {
        super(message);
    }

    public ContinuableFailure(String message, boolean html) {
        super(message, html);
    }

}
