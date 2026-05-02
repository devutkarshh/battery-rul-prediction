@echo off
call c:\Users\User\CODING\job\backend\apache-maven-3.9.6\bin\mvn.cmd clean package -DskipTests
echo.
echo Build complete. Starting application...
java -jar c:\Users\User\CODING\job\backend\target\job-portal-backend-1.0.0.jar
