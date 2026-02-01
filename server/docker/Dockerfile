FROM gradle:8.14.0-jdk21 AS build
WORKDIR /src
COPY . .

RUN --mount=type=cache,target=/home/gradle/.gradle \
    ./gradlew --no-daemon -Dorg.gradle.vfs.watch=false -Dorg.gradle.configuration-cache=false \
      :server:database:installDist \
      :server:auth:installDist \
      :server:gateway:installDist

FROM eclipse-temurin:21-jre AS database
WORKDIR /app
COPY --from=build /src/server/database/build/install/database/ ./
CMD ["bin/database"]

FROM eclipse-temurin:21-jre AS auth
WORKDIR /app
COPY --from=build /src/server/auth/build/install/auth/ ./
CMD ["bin/auth"]

FROM eclipse-temurin:21-jre AS gateway
WORKDIR /app
COPY --from=build /src/server/gateway/build/install/gateway/ ./
CMD ["bin/gateway"]
