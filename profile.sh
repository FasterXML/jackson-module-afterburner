#!/bin/sh

java -Xmx64m -server \
 -cp lib/\*:target/classes \
 -Xrunhprof:cpu=samples,depth=10,verbose=n,interval=2 \
  com.fasterxml.jackson.module.afterburner.ManualTest \
$*
