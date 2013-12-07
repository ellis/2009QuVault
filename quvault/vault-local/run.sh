#!/bin/sh
java -cp target/classes/:../vault-core-java/target/classes:../network/target/classes:$HOME/.m2/repository/dom4j/dom4j/1.6.1/dom4j-1.6.1.jar net.ellisw.quvault.vault.ProbServer $*
