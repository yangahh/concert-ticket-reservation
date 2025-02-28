FROM gradle:7.6.0-jdk17 AS builder

WORKDIR /app

RUN apt-get update && apt-get install -y git

# Git 신뢰 디렉토리 설정 (dubious ownership 문제 해결)
RUN git config --global --add safe.directory /app

COPY --chown=gradle:gradle . .

RUN ./gradlew clean bootJar --no-daemon

# 애플리케이션 실행도 동일한 이미지 사용 (일관성 유지)
FROM gradle:7.6.0-jdk17

WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=builder /app/build/libs/*.jar app.jar

#USER gradle

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
