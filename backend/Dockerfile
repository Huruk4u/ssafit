# ----------- 1단계: 빌드 전용 스테이지 ------------
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app

# 의존성 캐싱용 pom.xml 먼저 복사
COPY pom.xml .
RUN mvn dependency:go-offline

# 전체 소스 복사 후 빌드
COPY . .
RUN mvn clean package -DskipTests

# ----------- 2단계: 실행용 스테이지 -------------
FROM openjdk:17-jdk-slim
WORKDIR /app

# 빌드된 JAR만 복사
COPY --from=builder /app/target/*.jar app.jar

# 필요 시 환경변수 기본값 설정 가능
ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
