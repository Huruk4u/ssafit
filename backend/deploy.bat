@echo off
echo [배포 시작 : SSAFIT]

set GCP_CREDENTIALS_PATH=C:\Temp\api-key\cloud-vision-api.json
set JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
set openapi_keys=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

REM 환경변수 확인
echo JWT_SECRET=%JWT_SECRET%
echo openapi_keys=%openapi_keys%
echo GCP_CREDENTIALS_PATH=%GCP_CREDENTIALS_PATH%

REM jar 파일 경로 확인
IF NOT EXIST target\SSAFIT-0.0.1-SNAPSHOT.jar (
    echo [ERROR] JAR 파일이 존재하지 않습니다.
    echo mvnw clean package -DskipTests 를 먼저 실행하세요.
    pause
    exit \b
)

REM 실행

java ^
 -DJWT_SECRET=%JWT_SECRET% ^
 -Dopenapi_keys=%openapi_keys% ^
 -DGCP_CREDENTIALS_PATH="%GCP_CREDENTIALS_PATH%" ^
 -jar target/SSAFIT-0.0.1-SNAPSHOT.jar ^
 --spring.profiles.active=prod

echo [배포 완료]
pause
