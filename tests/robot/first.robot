*** Settings ***
Library    JavaLibraryBase  de.imbus.robotframework.example.ExampleLibrary    ${JAVA_PATH}


*** Variables ***
@{JAVA_PATH}    ${EXECDIR}/target/classes    ${EXECDIR}/target/test-classes


*** Test Cases ***
Do something from Java
    Do Something From Java