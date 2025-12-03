FROM gradle:8.10.2-jdk17 AS build
WORKDIR /build

COPY build.gradle settings.gradle ./

COPY gradle ./gradle

RUN gradle dependencies

COPY src ./src

RUN gradle clean bootJar -x test

RUN ls -lh build/libs

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /build/build/libs/*.jar app.jar

EXPOSE 8432

CMD ["java", "-jar", "app.jar"]
