#!/bin/sh

java -Xmx64m -server -cp lib/\*:target/classes:target/test-classes $*

