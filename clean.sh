#!/bin/bash

# Remove .class files
find . -name "*.class" -type f -delete

# Remove .jar files
find . -name "*.jar" -type f -delete

# Optional: Remove other temporary files like logs, etc.
find . -name "*.log" -type f -delete
find . -name "*~" -type f -delete

# Print a success message
echo "Cleanup complete!"
