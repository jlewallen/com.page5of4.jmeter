#!/bin/bash

mvn clean install
cp target/*.jar jakarta-jmeter-2.4/lib/ext
cp target/lib/*.jar jakarta-jmeter-2.4/lib
jakarta-jmeter-2.4/bin/jmeter.sh -t plan.jmx

