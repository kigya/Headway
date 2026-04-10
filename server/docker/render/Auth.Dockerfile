FROM gradle:8.14.0-jdk17 AS build
WORKDIR /src
COPY . .

RUN --mount=type=cache,target=/home/gradle/.gradle \
  ./gradlew --no-daemon -Dorg.gradle.vfs.watch=false -Dorg.gradle.configuration-cache=false \
  auth:internal:installDist

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /src/auth/internal/build/install/auth/ ./
CMD ["build/auth"]
