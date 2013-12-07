#!/bin/bash
java -classpath bin:../core/bin/:../server/war/WEB-INF/lib/dom4j-1.6.1.jar net.ellisw.quvault.vault.ProbServer $*
