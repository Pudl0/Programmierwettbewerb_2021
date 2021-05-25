@echo off
rmdir /s /q build
mkdir build
cd backend
call mvn clean package
cd ..
copy backend\target\backend-1.0-SNAPSHOT.jar build\server.jar
cd frontend
call npm install
call npm run build
cd ..
robocopy frontend\build build\static /E
echo Done!
pause