package de.imbus.robotframework.example;

public class ExampleLibrary1 {

    public int calculate(int a, int b) {

        return a + b;
    }

    public double calculateDouble(double a, double b) {

        return a + b;
    }

    public float calculateFloat(float a, float b) {

        return a + b;
    }

    public String calculateString(String a, String b) {

        return a + b;
    }

    public void doSomethingInJava() {
        System.out.println("Hello from java");
    }

    public static void doSomethingDifferentInJava() {
        System.out.println("Hello from java");
    }

    public void doSomethingWithABool(boolean b) {
        if (b) {
            System.out.println("Hello from java");
        } else {
            System.out.println("Nonono Hello from java");
        }
    }
}
