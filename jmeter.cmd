CALL mvn clean install
COPY target\*.jar jakarta-jmeter-2.4\lib\ext
COPY target\lib\*.jar jakarta-jmeter-2.4\lib
CALL jakarta-jmeter-2.4/bin/jmeter.bat -t plan.jmx

