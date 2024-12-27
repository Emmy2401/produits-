# Utiliser l'image OpenJDK
FROM openjdk:23-jdk-slim

# Définir le répertoire de travail dans le conteneur
WORKDIR /app

# Copier le fichier JAR généré par Maven dans le conteneur
COPY target/produits-.jar app.jar

# Commande par défaut pour exécuter l'application
ENTRYPOINT ["java", "-jar", "/app.jar"]