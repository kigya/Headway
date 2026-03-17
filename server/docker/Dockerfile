FROM gradle:8.14.0-jdk17 AS build
WORKDIR /src
COPY . .

RUN --mount=type=cache,target=/home/gradle/.gradle \
  ./gradlew --no-daemon -Dorg.gradle.vfs.watch=false -Dorg.gradle.configuration-cache=false \
  database:internal:installDist \
  auth:internal:installDist \
  gateway:installDist

FROM eclipse-temurin:17-jre AS database
WORKDIR /app
COPY --from=build /src/database/internal/build/install/database/ ./
CMD ["build/database"]

FROM eclipse-temurin:17-jre AS auth
WORKDIR /app
COPY --from=build /src/auth/internal/build/install/auth/ ./
CMD ["build/auth"]

FROM eclipse-temurin:17-jre AS gateway
WORKDIR /app
COPY --from=build /src/gateway/build/install/gateway/ ./
CMD ["build/gateway"]
