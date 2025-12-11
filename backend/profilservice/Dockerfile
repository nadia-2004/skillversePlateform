# Utiliser une image JDK complète (non-Alpine) pour éviter les problèmes réseau
FROM eclipse-temurin:17-jdk

# Répertoire de travail dans le conteneur
WORKDIR /app

# Copier le JAR construit
COPY target/profilservice-0.0.1-SNAPSHOT.jar app.jar

# Exposer le port du service
EXPOSE 8081

# Lancer le microservice
ENTRYPOINT ["java", "-jar", "app.jar"]
