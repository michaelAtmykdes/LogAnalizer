echo LOG ANALIZER $* 
mvn -q exec:java -Dexec.args="$*"

