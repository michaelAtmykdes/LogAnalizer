# LogAnalizer
Simple log analyzer


You will need Java8  and maven installed to build / run the code.

Here are the steps:

Unzip the attached file and navigate into LogAnalizer directory.

When there:

To build:

mvn clean install

To test:

mvn test


To run: 

mvn -q exec:java -Dexec.args="-d [YOUR log directory] -m [Time offset in minutes]"

Alternatively you can run it with: 

On Windows
analizer.bat -d [YOUR log directory] -m [Time offset in minutes]

on Mac / Linux:
./analizer.sh -d [YOUR log directory] -m [Time offset in minutes]

