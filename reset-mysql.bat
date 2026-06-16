@echo off
REM Stop MySQL service
net stop MySQL80
REM Wait for service to stop
timeout /t 5 /nobreak
REM Start MySQL with skip-grant-tables
"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysqld.exe" --defaults-file="C:\ProgramData\MySQL\MySQL Server 8.0\my.ini" --skip-grant-tables --console