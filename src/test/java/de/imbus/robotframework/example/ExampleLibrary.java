/* SPDX-FileCopyrightText: 2023-present imbus AG

SPDX-License-Identifier: Apache-2.0package*/
package de.imbus.robotframework.example;

import de.imbus.robotframework.exceptions.Failure;
import de.imbus.robotframework.exceptions.FatalError;
import de.imbus.robotframework.exceptions.SkipExecution;
import de.imbus.robotframework.RobotLibrary;
import de.imbus.robotframework.exceptions.ContinuableFailure;
import de.imbus.robotframework.exceptions.Error;

/**
 * Example library for Robot Framework
 */
@RobotLibrary(scope = "SUITE")
public class ExampleLibrary {

    public void doSomethingFromJava() {
        System.out.println("Hello from java");
    }

    public void doSkipExecution() {
        throw new SkipExecution("Skip execution");
    }

    public void doSkipExecutionWithHtml() {
        throw new SkipExecution("Skip execution <b>Fett</b>", true);
    }

    public void doFailure() {
        throw new Failure("Failure");
    }

    public void doFatalError() {
        throw new FatalError("Fatal <i>error</i>", true);
    }

    public void doError() {
        throw new Error("Fatal <b>error</b>", true);
    }

    public void doContinuableFailure() {
        throw new ContinuableFailure("Continuable <b>Failure</b>", true);
    }

    public void doJavaError() {
        throw new IndexOutOfBoundsException("nö geht nicht");
    }

    public void doSomethingWithParameters(String str, boolean bool, int i, double d) {
        System.out.println("Hello from java with parameters öäüß°@ÈÊ😂😊❤️😍👍🙌💕" + str);
    }
}
