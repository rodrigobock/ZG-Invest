FROM eclipse-temurin:11-jdk-focal AS build
WORKDIR /app
COPY . .
RUN sed -i 's/\r$//' gradlew
RUN chmod +x gradlew
RUN mkdir -p grails-app/assets
# Executa o build da aplicação, pulando testes para acelerar a subida
RUN ./gradlew assemble -x test --no-daemon

FROM eclipse-temurin:11-jre-focal
WORKDIR /app
# Projetos Grails com 'apply plugin: "war"' geram o executável em .war
COPY --from=build /app/build/libs/*.war app.war
EXPOSE 8080
CMD ["sh", "-c", "java -Dserver.port=${PORT:-8080} -Dserver.address=0.0.0.0 -jar app.war"]
