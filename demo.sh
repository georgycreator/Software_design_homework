#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")"
command -v javac >/dev/null 2>&1 || { echo "JDK 17+ is required" >&2; exit 1; }
rm -rf target/classes
mkdir -p target/classes target
find src/main/java -name '*.java' -print > target/main-sources.txt
javac --release 17 -encoding UTF-8 -d target/classes @target/main-sources.txt
exec java -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dvyshkat.echoInput=true -cp target/classes ru.vyshkat.app.Main < examples/demo-input.txt
