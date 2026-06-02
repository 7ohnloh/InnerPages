#!/usr/bin/env sh
# ----------------------------------------------------------------------------
# Maven wrapper script
# ----------------------------------------------------------------------------
MAVEN_PROJECTBASEDIR="$(cd "$(dirname "$0")" && pwd)"
MAVEN_WRAPPER_JAR="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.jar"
if [ ! -f "$MAVEN_WRAPPER_JAR" ]; then
  echo "Maven wrapper jar not found: $MAVEN_WRAPPER_JAR"
  exit 1
fi
exec java -Dmaven.multiModuleProjectDirectory="$MAVEN_PROJECTBASEDIR" -jar "$MAVEN_WRAPPER_JAR" "$@"
