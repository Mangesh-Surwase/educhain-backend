# 1. Build Stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# 2. Run Stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# 🔥🔥🔥 FIX FOR RENDER PORT & MEMORY 🔥🔥🔥
ENV PORT=8080
EXPOSE 8080

# Java ला मेमरी लिमिट देणे (Render Free Tier साठी महत्वाचे)
ENV JAVA_TOOL_OPTIONS="-Xmx400m"

ENTRYPOINT ["java","-jar","app.jar"]