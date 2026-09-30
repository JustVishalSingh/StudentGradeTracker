@echo off
echo Cleaning old project files...

:: Delete old src/com directory
if exist "src\com" (
    rmdir /s /q "src\com"
    echo Deleted src\com
)

:: Delete old src/main/java/com/codealpha directory
if exist "src\main\java\com\codealpha" (
    rmdir /s /q "src\main\java\com\codealpha"
    echo Deleted src\main\java\com\codealpha
)

:: Delete out directory
if exist "out" (
    rmdir /s /q "out"
    echo Deleted out
)

:: Delete target directory
if exist "target" (
    rmdir /s /q "target"
    echo Deleted target
)

:: Delete any batch files (except this one)
for %%i in (*.bat) do (
    if not "%%i"=="clean.bat" (
        del /q "%%i"
        echo Deleted %%i
    )
)

echo.
echo Cleanup complete! You can now safely delete clean.bat as well.
pause
