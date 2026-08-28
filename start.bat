@echo off
chcp 65001 >nul
echo ========================================
echo 平安公寓管理系统 - 后端启动脚本
echo ========================================

:: 设置 JDK 17
set JAVA_HOME=C:\Program Files\Java\jdk-17
set PATH=%JAVA_HOME%\bin;%PATH%

echo.
echo 使用 Java 版本:
java -version

echo.
echo 正在编译项目...
cd /d E:\ProgramWorkSpace\pingan-ams\pingan-ams-server
mvn clean compile -DskipTests -q

if %ERRORLEVEL% NEQ 0 (
    echo 编译失败！
    pause
    exit /b 1
)

echo 编译成功！
echo.
echo 正在启动服务...
echo 访问地址: http://localhost:8080/api/doc.html
echo ========================================
mvn spring-boot:run -pl pingan-ams-admin

pause
