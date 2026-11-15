#!/usr/bin/env sh
set -e
cd "$(dirname "$0")"
echo "Compilando..."
rm -rf out
javac -encoding UTF-8 -d out $(find src -name "*.java")
echo "Executando..."
echo
java -Dstdout.encoding=UTF-8 -cp out cliente.Main
