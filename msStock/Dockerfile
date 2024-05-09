FROM maven:3.8.4 AS builder

WORKDIR /app

COPY pom.xml .
COPY ../pom.xml pom.xml.parent
COPY src ./src

RUN mvn clean package -DskipTests

FROM amazoncorretto:17.0.11-alpine

WORKDIR /app

 # Copie o arquivo JAR do seu projeto para dentro do contêiner
COPY --from=builder /app/target/*.jar /app/msstock.jar

# Comando para executar o projeto quando o contêiner for iniciado
CMD ["java", "-jar", "/app/msstock.jar"]