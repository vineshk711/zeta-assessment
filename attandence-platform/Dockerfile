FROM amazoncorretto:17-alpine as build
WORKDIR /workspace/app

# Install Maven
RUN apk add --no-cache maven

# Copy pom.xml and source code
COPY pom.xml .
COPY src src

# Build the application
RUN mvn package -DskipTests

FROM amazoncorretto:17-alpine
VOLUME /tmp
COPY --from=build /workspace/app/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"] 