@echo off
chcp 65001 > nul
title Quản Lý Thời Khóa Biểu (CNJ56)

echo ========================================================
echo   KHỞI CHẠY ỨNG DỤNG QUẢN LÝ THỜI KHÓA BIỂU (CNJ56)
echo ========================================================
echo.

echo [1/2] Đang biên dịch mã nguồn Java...
if not exist "build\classes" mkdir "build\classes"

javac -encoding UTF-8 -cp "lib/mysql-connector-j-8.3.0.jar;src" -d build/classes src/connection/DBConnection.java src/model/*.java src/dao/*.java src/service/*.java src/util/*.java src/view/LoginForm.java src/view/MainForm.java src/view/dialog/*.java src/view/panel/*.java
if %errorlevel% neq 0 (
    echo.
    echo [X] Lỗi biên dịch mã nguồn! Vui lòng kiểm tra lại.
    pause
    exit /b %errorlevel%
)

echo [2/2] Đang khởi chạy giao diện đăng nhập (LoginForm)...
echo (Hãy chắc chắn rằng XAMPP MySQL đã được BẬT - Start MySQL)
echo.
java -cp "lib/mysql-connector-j-8.3.0.jar;build/classes" view.LoginForm
if %errorlevel% neq 0 (
    echo.
    echo [X] Ứng dụng đã kết thúc với mã lỗi %errorlevel%.
    pause
)