# Docker Compose - Base de données PostgreSQL

## Démarrage rapide

### 1. Démarrer les services
```bash
docker-compose up -d
```

### 2. Vérifier que les services fonctionnent
```bash
docker-compose ps
```

### 3. Arrêter les services
```bash
docker-compose down
```

## Services disponibles

### PostgreSQL
- **URL de connexion** : `localhost:5432`
- **Base de données** : `plumeo`
- **Utilisateur** : `plumeo_user`
- **Mot de passe** : `plumeo_password`

### PgAdmin (Interface web)
- **URL** : http://localhost:8080
- **Email** : admin@plumeo.com
- **Mot de passe** : admin

## Configuration de l'application Spring Boot

Votre `application.properties` a été configuré automatiquement avec :
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/plumeo
spring.datasource.username=plumeo_user
spring.datasource.password=plumeo_password
```

## Commandes utiles

### Voir les logs
```bash
docker-compose logs postgres
docker-compose logs pgadmin
```

### Se connecter à la base de données
```bash
docker-compose exec postgres psql -U plumeo_user -d plumeo
```

### Sauvegarder la base de données
```bash
docker-compose exec postgres pg_dump -U plumeo_user plumeo > backup.sql
```

### Restaurer la base de données
```bash
docker-compose exec -T postgres psql -U plumeo_user plumeo < backup.sql
```

### Réinitialiser les données
```bash
docker-compose down -v  # Supprime les volumes
docker-compose up -d    # Recrée avec des données fraîches
```

## Fichiers créés

- `docker-compose.yml` : Configuration principale
- `docker-compose.override.yml` : Configuration pour le développement
- `init.sql` : Script d'initialisation de la base (optionnel)
- `.env.example` : Exemple de variables d'environnement

## Variables d'environnement

Vous pouvez créer un fichier `.env` pour personnaliser la configuration :
```bash
cp .env.example .env
# Puis modifier les valeurs dans .env
```