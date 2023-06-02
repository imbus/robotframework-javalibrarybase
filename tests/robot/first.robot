*** Settings ***
Library           JavaLibraryBase    de.imbus.robotframework.example.ExampleLibrary    ${EXECDIR}/target/test-classes
Library           JavaLibraryBase    java.lang.String    ${EXECDIR}/target/test-classes    asdfasdf   AS  aa

*** Test Cases ***
Do something from Java
    Do Something From Java
    JavaLibraryBase.Do Something With Parameters    1    2    3    1
    # # JavaLibraryBase.Do Continuable Failure
    # #Do Skip Execution
    # ${f}    aa.Char At    1
    # Log    ${f}
    # ${k}    aa.Compare To    asdasq
    # Log    ${k}
