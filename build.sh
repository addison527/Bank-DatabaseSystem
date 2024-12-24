#!/bin/bash

# Clean up
rm -rf bin
rm -f abm527.jar

# Create bin directory if it doesn't exist
mkdir -p bin

# Compile Java files from the correct directory
echo "Compiling Java files..."
cd abm527
javac -d ../bin *.java

if [ $? -eq 0 ]; then
    echo "Compilation successful!"
    
    cd ..
    
    # Create manifest file
    echo "Main-Class: Database" > Manifest.txt
    echo "Class-Path: ojdbc11.jar" >> Manifest.txt
    
    # Create JAR file
    echo "Creating JAR file..."
    jar cvfm abm527.jar Manifest.txt -C bin .
    
    if [ $? -eq 0 ]; then
        echo "JAR file created successfully!"
        echo ""
        echo "To run the application:"
        echo "java -jar abm527.jar"
        echo ""
        echo "Note: Make sure ojdbc11.jar is in the same directory"
    else
        echo "Error creating JAR file"
    fi
else
    echo "Compilation failed"
fi
