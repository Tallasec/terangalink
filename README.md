# 🎓 TerangaLink

> **La plateforme qui connecte les étudiants grâce à l'esprit de la Teranga.**
>
> *Parce qu'aucun étudiant ne devrait avancer seul.*

---

# 🚀 À propos

TerangaLink est une plateforme communautaire destinée aux étudiants internationaux, avec un premier focus sur les étudiants sénégalais souhaitant poursuivre leurs études en France.

L'objectif est de centraliser les informations essentielles concernant les études, le logement, les démarches administratives, les emplois étudiants et la vie associative afin de faciliter l'intégration et la réussite universitaire.

Aujourd'hui, de nombreux étudiants utilisent principalement WhatsApp, Facebook ou d'autres réseaux sociaux pour rechercher des informations.

Ces informations sont souvent :

- dispersées
- répétitives
- difficiles à retrouver
- peu fiables

TerangaLink propose une plateforme collaborative, moderne et centralisée permettant aux étudiants de partager leurs expériences et de s'entraider efficacement.

---

# ✨ Fonctionnalités

## 🔐 Authentification

- Inscription
- Connexion
- Authentification JWT
- Gestion des rôles
- Sécurisation des endpoints

---

## 💬 Forum communautaire

- Création de sujets
- Réponses aux discussions
- Consultation des échanges
- Catégories :

  - Études
  - Logement
  - Administratif
  - Alternance
  - Emploi
  - Vie étudiante
  - Événements

---

## 🏠 Logements

- Publication d'annonces
- Consultation des annonces
- Recherche dynamique
- Pagination
- Tri
- Modification
- Suppression logique

---

## 💼 Offres d'emploi

- Publication d'offres
- Recherche avancée
- Pagination
- Tri
- Gestion des offres

---

## 👥 Groupes de révision

- Création de groupes d'étude
- Organisation de séances
- Recherche par matière
- Recherche par ville
- Gestion des groupes

---

## 🤝 Associations étudiantes

- Découverte des associations
- Dahiras
- Associations étudiantes
- Associations sportives
- Associations culturelles
- Informations de contact

---

# 🛠️ Stack technique

## Backend

- Java 21
- Spring Boot
- Spring Security
- JWT
- Spring Data JPA
- Hibernate
- PostgreSQL
- Maven

---

## Frontend

- React
- Vite
- Tailwind CSS

---

## Outils

- Git
- GitHub
- Postman
- Figma
- IntelliJ IDEA

---

# 🏗️ Architecture Backend

Le backend suit une architecture REST inspirée des bonnes pratiques de Spring Boot.

```
Controller
    ↓
Service
    ↓
Repository
    ↓
PostgreSQL
```

Chaque module est organisé selon la même structure :

```
Entity

DTO

Mapper

Repository

Specification

Service

SecurityService

Controller

Tests
```

Cette architecture facilite la maintenance, les évolutions et les tests.

---

# 🗄️ Modèle de données

Les principales entités sont :

- User
- ForumTopic
- Answer
- HousingPost
- JobPost
- StudyGroup
- Association

---

# 🔐 Sécurité

Le projet utilise :

- Spring Security
- JWT Authentication
- Gestion des rôles
- Protection des endpoints
- Contrôle d'accès par propriétaire
- Soft Delete

---

# 📱 Responsive Design

Le frontend est développé selon une approche **Mobile First** afin d'offrir une expérience optimale sur smartphone, tablette et ordinateur.

---

# 🚀 Roadmap

## ✅ MVP

- Authentification
- Forum communautaire
- Réponses
- Logements
- Emplois étudiants
- Groupes de révision
- Associations étudiantes

---

## 🔄 V2

- Notifications
- Favoris
- Upload d'images
- Messagerie privée

---

## 🔮 V3

- Application mobile
- Intelligence artificielle
- Recommandation personnalisée
- Traduction automatique

---

# 📸 Captures d'écran

*À venir*

---

# ⚙️ Installation

## Backend

```bash
git clone https://github.com/ton-repo/terangalink.git

cd backend

mvn clean install

mvn spring-boot:run
```

---

## Frontend

```bash
cd frontend

npm install

npm run dev
```

---

# 👨‍💻 Auteur

**Talla Seck**

Étudiant en Informatique

Projet personnel développé dans le cadre de TerangaLink.

---

# 📄 Licence

Ce projet est distribué sous licence MIT.
