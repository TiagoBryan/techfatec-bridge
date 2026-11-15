@echo off
chcp 65001 > nul
echo Compilando...
if exist out rmdir /s /q out
dir /s /b src\*.java > fontes.txt
javac -encoding UTF-8 -d out @fontes.txt || exit /b 1
del fontes.txt
echo Executando...
echo.
java -Dstdout.encoding=UTF-8 -cp out cliente.Main
