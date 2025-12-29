# Exchange Service - Real-time Exchange Sessions Microservice

Un microservice Spring Boot pour gérer les sessions d'échange en temps réel entre apprenants et mentors avec outils collaboratifs intégrés.

## Fonctionnalités

- **Gestion des Sessions**: Création, démarrage, arrêt des sessions
- **Partage de Fichiers**: Upload/téléchargement sécurisé avec AWS S3
- **Tableau Blanc Collaboratif**: Dessin temps réel avec synchronisation multi-utilisateurs via WebSocket
- **Quiz & Validation**: Questionnaires avec correction automatique et badges
- **Cache Redis**: Performance optimisée avec cache distribué

## Technologies

- Spring Boot 3.x + Java 17
- PostgreSQL + Redis
- AWS S3 SDK
- Spring WebSocket + STOMP
- Spring Security + JWT
- SpringDoc OpenAPI 3

## Démarrage Rapide

### Prérequis

- Docker + Docker Compose
- Java 17+
- Maven 3.6+

### Avec Docker Compose

\`\`\`bash
docker-compose -f docker-compose.dev.yml up
\`\`\`

L'application sera accessible à `http://localhost:8080`

### Sans Docker (développement local)

1. Installer PostgreSQL et Redis
2. Configurer les variables d'environnement
3. Lancer l'application:

\`\`\`bash
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
\`\`\`

## Documentation API

Swagger UI est disponible à: `http://localhost:8080/swagger-ui.html`

## Endpoints Principaux

### Sessions
- `POST /api/v1/sessions` - Créer une session
- `GET /api/v1/sessions/{sessionId}` - Détails de la session
- `PUT /api/v1/sessions/{sessionId}/start` - Démarrer
- `PUT /api/v1/sessions/{sessionId}/end` - Terminer

### Fichiers
- `POST /api/v1/files/{sessionId}/upload` - URL upload signé
- `GET /api/v1/files/{sessionId}` - Lister fichiers
- `GET /api/v1/files/{sessionId}/{fileId}/download` - URL téléchargement

### Quiz
- `POST /api/v1/quizzes/{sessionId}` - Créer quiz
- `POST /api/v1/quizzes/{quizId}/attempt` - Soumettre réponses
- `GET /api/v1/quizzes/{quizId}/results` - Résultats

### WebSocket
- `WS /ws/exchange` - Connexion tableau blanc STOMP

## Configuration

Voir `application.yml` pour les configurations par défaut et `application-{profile}.yml` pour les profils spécifiques.

Variables d'environnement requises (production):
- `REDIS_HOST`, `REDIS_PORT`
- `AWS_REGION`, `AWS_S3_BUCKET_NAME`
- `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`

## Tests

\`\`\`bash
./mvnw test
\`\`\`

## Build et Déploiement

\`\`\`bash
./mvnw clean package
docker build -t exchange-service:v1.0.0 .
\`\`\`

## Architecture

\`\`\`
src/main/java/com/exchange/
├── config/           # Configurations Spring
├── controller/       # REST Controllers
├── service/          # Business logic
├── repository/       # Data access
├── model/
│   ├── entity/      # JPA entities
│   └── dto/         # DTOs
└── exception/       # Error handling
\`\`\`

## Supports Intégrations

- **Booking Service**: Récupération informations réservation
- **Auth Service**: Validation tokens JWT
- **Profile Service**: Compétences et badges
- **Notification Service**: Alertes sessions
