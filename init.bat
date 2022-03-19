@echo off
SET bat_path=%~dp0
SET bat_dir=%bat_path:~0,-1%


@REM git submodule init
xcopy "%bat_dir%\.mvn/" "%bat_dir%\reactive-lock\.mvn\" /s/h/e/k/f/c/y
copy "%bat_dir%\mvnw" "%bat_dir%\reactive-lock\mvnw"
copy "%bat_dir%\mvnw.cmd" "%bat_dir%\reactive-lock\mvnw.cmd" /y

cd "%bat_dir%\reactive-lock"
mvnw.cmd install -DskipTests -Dspring-boot.repackage.skip=true