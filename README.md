# XMLComparator
It can parse two xml files(Source and Target) and compare target xml file with source xml file. It will write differences in the output file mentioned as a parameter passed to run the jar file.

1. Get a pull
2. Run mvn clean install command (it will generate the "xmlcomparator-0.0.1-SNAPSHOT.jar" in target folder)
3. Run the jar file by cmd command (java -jar <xmlcomparator-0.0.1-SNAPSHOT.jar path>)
4. Add the required parameters
5. Ex. java -jar <xmlcomparator-0.0.1-SNAPSHOT.jar path> "source xml file path" "target xml file path" "output file path"
