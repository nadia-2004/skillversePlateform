# Utiliser une image JDK complète (non-Alpine) pour éviter les problèmes réseau
FROM eclipse-temurin:17-jdk

# Répertoire de travail dans le conteneur
WORKDIR /app

# Copier le JAR construit
COPY target/profilservice-0.0.1-SNAPSHOT.jar app.jar

# Créer un utilisateur non-root pour exécuter l'application
RUN groupadd -r spring && useradd -r -g spring spring

# Changer la propriété des fichiers vers l'utilisateur spring
RUN chown -R spring:spring /app

# Basculer vers l'utilisateur non-root
USER spring

# Exposer le port du service
EXPOSE 8081

# Lancer le microservice
ENTRYPOINT ["java", "-jar", "app.jar"]
