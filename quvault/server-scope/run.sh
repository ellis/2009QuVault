#!/bin/sh
java -cp target/classes/:\
$HOME/.m2/repository/dom4j/dom4j/1.6.1/dom4j-1.6.1.jar:\
$HOME/.m2/repository/org/scala-lang/scala-library/2.7.4/scala-library-2.7.4.jar \
net.ellisw.quvault.server.scope.TestMain
