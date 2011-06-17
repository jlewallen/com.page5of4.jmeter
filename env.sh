#!/bin/bash

pushd "$(DIRNAME '${0}')" > /dev/null
BASEDIR=$(pwd -L)
popd > /dev/null

echo $BASEDIR
export PATH="/c/Program Files/Java/jdk1.6.0_24/bin:$PATH"
export PATH=$PATH:$BASEDIR/jakarta-jmeter-2.4/bin
