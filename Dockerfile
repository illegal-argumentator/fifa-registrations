FROM gradle:8.10.2-jdk17 AS build
WORKDIR /build

COPY gradlew .
COPY build.gradle settings.gradle ./
COPY gradle ./gradle

RUN chmod +x gradlew

RUN ./gradlew dependencies --no-daemon

COPY src ./src

RUN ./gradlew clean bootJar -x test --no-daemon

RUN ls -lh build/libs

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /build/build/libs/*.jar app.jar

EXPOSE 8432

CMD ["java", "-jar", "app.jar"]
