package de.imbus.robotframework.exceptions;

public class SkipExecution extends RobotFrameworkException {
    private static final long serialVersionUID = 1L;

    public SkipExecution(String message, boolean html) {
        super(message, html);
    }

    public SkipExecution(String message) {
        super(message);
    }

}
