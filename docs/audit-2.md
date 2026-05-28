# Code review — module User (TerangaLink)

**Périmètre :** `User`, `UserRequestDTO`, `UserResponseDTO`, `UserRepository`, `UserService`, `UserController`, `UserMapper`, `GlobalExceptionHandler`, `ApiErrorResponse`, validation Jakarta, `PasswordConfig`, exceptions métier User.  
**Hors périmètre (demande explicite) :** JWT, Spring Security, permissions, authentification avancée.

---

## 1. Synthèse

Le module User est **nettement au-dessus d'un CRUD Spring Boot basique**. L'architecture en couches est respectée, la validation est sérieuse, la gestion d'erreurs est structurée, et le service contient de vraies règles métier (unicité email, normalisation, hash mot de passe, rôle imposé).

Le principal défaut n'est pas la structure, mais **l'incohérence create/update via un seul DTO**, qui casse la mise à jour de profil sans changement de mot de passe. Quelques redondances et lacunes de robustesse (pagination, concurrence, champs non normalisés) complètent le tableau.

| Critère                        | Note /5     |
|--------------------------------|-------------|
| Architecture & responsabilités | 4,0         |
| Qualité / clean code           | 3,8         |
| Validation Jakarta             | 3,5         |
| Gestion des erreurs            | 4,2         |
| REST API                       | 3,5         |
| Robustesse & cas limites       | 3,0         |
| Maintenabilité                 | 3,8         |
| **Niveau global module User**  | **3,7 / 5** |

**Verdict :** module **junior confirmé / début intermédiaire**, avec des réflexes professionnels visibles. Solide comme base de feature ; pas encore au niveau « module prêt à scaler en production » sans les corrections listées ci-dessous.

---

## 2. Architecture du module User

### Flux actuel

```
HTTP Request
    → UserController      (validation entrée, mapping HTTP ↔ statuts)
    → UserService         (règles métier, transactions, encodage password)
    → UserRepository      (accès persistance)
    → User (entité JPA)
    ← UserResponseDTO     (via UserMapper)
```

### Points forts

- **Séparation nette des responsabilités** : le controller ne touche ni à JPA ni au hash ; le repository ne contient pas de logique métier.
- **DTO distincts entrée/sortie** : le mot de passe n'entre jamais dans `UserResponseDTO`.
- **Mapper dédié** : évite l'exposition directe de l'entité et centralise les conversions.
- **Exceptions métier découplées du HTTP** : `UserNotFoundException`, `EmailAlreadyExistsException` remontent au service et sont traduites par `GlobalExceptionHandler`.
- **Infrastructure transverse bien placée** : `GlobalExceptionHandler` et `ApiErrorResponse` servent tout le module (et au-delà) sans polluer le controller.

### Faiblesses architecturales

| Point | Détail |
|-------|--------|
| DTO unique create/update | Casse la sémantique métier ; la logique service « password optionnel en update » est morte |
| `emailExists()` dans le service | Méthode publique non exposée par le controller — API incomplète ou code mort |
| Pas de couche « use case » | Acceptable à cette taille ; deviendra limitant si la logique User grossit |
| Handler global hors package `user` | Cohérent pour un petit projet ; à documenter si d'autres modules arrivent |

---

## 3. Analyse composant par composant

### 3.1 `User` (entité JPA)

**Points forts**
- Contraintes DB explicites (`nullable`, `length`, `unique` sur email).
- `@Enumerated(STRING)` pour `Role` — lisible et stable en base.
- `@PrePersist` pour `createdAt` — bonne pratique, horodatage fiable à la persistance.
- Colonne `password` en `length = 255` — compatible avec un hash BCrypt (~60 caractères).

**Faiblesses**
- Pas de `updatedAt` — impossible de tracer les modifications.
- Pas de `@Version` — pas de protection contre les écrasements concurrents.
- Pas de normalisation au niveau entité (trim sur `firstName`, `lastName`, etc.) — cohérence des données fragile.
- Commentaires utiles mais le bloc Javadoc en tête de classe est plus « doc métier » que technique — acceptable.

**Cas limites**
- Email stocké en lowercase via le service, mais contrainte `unique` PostgreSQL **sensible à la casse** si des données arrivent autrement (script SQL, autre endpoint futur).
- Suppression User sans gestion des relations futures (`Question`, `HousingPost`, etc.) — hors scope immédiat mais dette annoncée.

---

### 3.2 `UserRequestDTO`

**Points forts**
- Validation Jakarta **riche et en français** — bonne UX API.
- Politique mot de passe exigeante (longueur, maj/min, chiffre, caractère spécial, sans espace).
- Email : `@Email` + `@Pattern` anti-espace + `@Size(max=255)` — défense en profondeur.
- Prénom/nom : `@NotBlank` + `@Size(min=2)`.

**Faiblesses majeures**

**Un seul DTO pour POST et PUT** avec `@NotBlank` sur `password` :

```java
@NotBlank(message = "Le mot de passe est obligatoire.")
@Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères.")
@Pattern(
    regexp = "^(?=\\S+$)(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9\\s]).{8,}$",
    ...
)
private String password;
```

Or le service prévoit explicitement un password optionnel en update :

```java
if (request.getPassword() != null && !request.getPassword().isBlank()) {
    user.setPassword(passwordEncoder.encode(request.getPassword()));
}
```

**Incohérence confirmée :** la validation controller bloque avant le service. Un `PUT` pour modifier uniquement `city` ou `university` **échouera systématiquement en 400**.

**Autres lacunes validation**

| Champ | DTO | Entité | Écart |
|-------|-----|--------|-------|
| `firstName` / `lastName` | `min=2`, pas de `max` | `length=100` | Pas de plafond côté DTO |
| `university` | `@NotBlank` seulement | `length=150` | Pas de `@Size(max=150)` |
| `fieldOfStudy` | idem | `length=150` | idem |
| `city` | idem | `length=120` | idem |

Conséquence : un client peut envoyer une chaîne de 500 caractères → validation DTO OK → erreur SQL ou troncature selon config Hibernate.

---

### 3.3 `UserResponseDTO`

**Points forts**
- **Ne contient pas le mot de passe** — indispensable et bien fait.
- Structure miroir claire des champs publics du profil.
- Inclusion de `role` et `createdAt` — pertinent pour affichage profil.

**Faiblesses**
- POJO mutable (`@Setter`) — acceptable ici, mais un record ou un builder immuable serait plus sûr à terme.
- Pas de distinction « profil public » vs « profil complet » — acceptable sans couche auth.
- Pas de `@JsonInclude(NON_NULL)` — tous les champs null seraient sérialisés si entité incomplète.

---

### 3.4 `UserRepository`

**Points forts**
- Interface Spring Data minimaliste et **expressive** : `findByEmailIgnoreCase`, `existsByEmailIgnoreCase`.
- Naming convention Spring Data correct — pas de `@Query` superflue.
- Retour `Optional` pour la recherche — idiomatique.

**Faiblesses**
- `@Repository` est **optionnel** sur une interface JpaRepository (redondant, pas nuisible).
- Ligne vide en tête de fichier — détail de style.
- Pas de méthode paginée (`Page<User> findAll(Pageable)`) — limite future.
- `existsByEmailIgnoreCase` + contrainte unique DB : bon duo, mais le handler générique masque parfois l'intention métier en cas de race condition.

---

### 3.5 `UserService`

**Points forts — parties très professionnelles**

1. **`@Transactional(readOnly = true)` au niveau classe**, écritures sur `@Transactional` méthode — excellent réflexe.
2. **Normalisation email centralisée** (`trim` + `toLowerCase(Locale.ROOT)`).
3. **Vérification unicité email** avant create/update, avec exception métier dédiée.
4. **Hash BCrypt dans le service**, pas dans le mapper — bon placement.
5. **Rôle forcé à `USER` à la création** — règle métier explicite.
6. **Rôle non modifié en update** (mapper + garde-fou `if (role == null)`).
7. **Méthode privée `findUserByIdOrThrow`** — DRY, messages d'erreur cohérents.

**Faiblesses**

| Problème | Impact |
|----------|--------|
| Mutation du DTO entrant (`request.setEmail(...)`) | Effet de bord sur l'objet reçu ; peu grave mais évitable |
| Double assignation du rôle à la création (mapper + service) | Redondance, risque de divergence future |
| `deleteUser` : `existsById` puis `deleteById` | 2 requêtes DB au lieu d'une |
| `getAllUsers()` : `findAll()` sans pagination | Risque perf/mémoire |
| `emailExists()` non utilisée | Code mort ou feature inachevée |
| Race condition email | Deux threads passent le check → `DataIntegrityViolationException` générique au lieu de `EMAIL_ALREADY_EXISTS` |

**Cas limites**

- `normalizeEmail(null)` retourne `null` — si appelé sans validation en amont, NPE ou violation contrainte DB possible (actuellement protégé par validation controller/DTO).
- Update avec **même email** (normalisé) : la condition `!Objects.equals(user.getEmail(), normalizedEmail)` évite un faux positif — **bien géré**.
- Update avec password vide `""` : `@NotBlank` bloque en amont ; si un jour le DTO change, `isBlank()` ignorerait le re-hash — logique service OK mais inaccessible aujourd'hui.
- `id` null ou négatif en path : pas de validation explicite sur `@PathVariable Long id`.

---

### 3.6 `UserController`

**Points forts**
- Injection par constructeur.
- `@Valid` sur body, `@Validated` sur la classe pour les query params.
- Statuts HTTP corrects : **201 Created**, **204 No Content**, **200 OK**.
- Recherche par email via **`@RequestParam`** sur `/search` — meilleur design qu'un email en path variable.
- Validation query param email alignée sur le DTO (`@Email`, `@Pattern`, `@Size`).

**Faiblesses REST / structure**

| Endpoint | Observation |
|----------|-------------|
| `POST /api/users` | Pas de header `Location: /api/users/{id}` |
| `GET /api/users` | Pas de pagination, tri, filtre |
| `PUT /api/users/{id}` | Sémantique PUT « remplacement complet » avec DTO create — incohérent ; PATCH serait plus honnête pour update partiel |
| `GET /api/users/{id}` | Déclaré **avant** `GET /search` — Spring résout généralement `/search` en priorité (segment littéral), mais **aucun test ne le garantit** |
| `GET /api/users/search?email=` | Nom `/search` générique ; `/by-email` serait plus explicite |

**Détails clean code**
- Double espace dans `@Valid  UserRequestDTO` — cosmetic.
- Imports non triés (`Valid` après `Size`) — mineur.

**Cas limites**
- `GET /api/users/abc` → `MethodArgumentTypeMismatchException` → 400 — **bien géré** par le handler global.
- `GET /api/users/search` sans param `email` → probable 400 `ConstraintViolationException` — OK.
- Body JSON vide ou mal formé → `HttpMessageNotReadableException` — OK.

---

### 3.7 `UserMapper`

**Points forts**
- **Ne mappe jamais le mot de passe** depuis le DTO — correct.
- `updateEntityFromDto` ne touche ni password ni role — bonne frontière.
- Composant Spring `@Component` simple, testable.

**Faiblesses**
- **Duplication rôle** : `toEntity` fixe `Role.USER`, puis le service refait `user.setRole(Role.USER)`.
- Mapping manuel répétitif — acceptable pour 7 champs ; MapStruct réduirait le boilerplate si le module grandit.
- Pas de trim/normalisation des champs texte (firstName, city, etc.) — tout repose sur le client.
- Pas de méthode `toResponseDtoList` — le stream dans le service suffit.

---

### 3.8 `GlobalExceptionHandler` & `ApiErrorResponse`

**Points forts — l'un des meilleurs morceaux du module**

- Format d'erreur **uniforme** : `timestamp`, `status`, `error` (code), `message`, `path`, `details`.
- **Toutes les erreurs de validation** retournées (map champ → message), pas seulement la première.
- Handlers couvrant les cas User réels :
  - validation body (`MethodArgumentNotValidException`)
  - validation param (`ConstraintViolationException`)
  - métier (`EMAIL_ALREADY_EXISTS`, `USER_NOT_FOUND`)
  - intégrité DB (`DataIntegrityViolationException`)
  - JSON mal formé, type mismatch path param
- **Logging serveur** sur le fallback 500 sans fuite de stack trace au client.
- Codes d'erreur string stables (`VALIDATION_ERROR`, etc.) — utile pour le frontend.

**Faiblesses**

| Point | Détail |
|-------|--------|
| `putIfAbsent` sur field errors | Si deux validateurs échouent sur le même champ, seul le premier message est gardé |
| `ConstraintViolationException` | Clés du type `getUserByEmail.email` — peu lisibles pour le client |
| `DataIntegrityViolationException` | Message générique ; ne distingue pas email dupliqué vs autre contrainte FK future |
| Pas de `@Order` / priorité explicite | Le catch-all `Exception` est en dernier — OK en l'état |
| `ApiErrorResponse` mutable (`@Setter`) | Permet modification post-construction — inutile |
| Champ `error` vs `message` | `error` = code machine, `message` = texte humain — bon, mais naming `error` peut prêter à confusion avec le message |

**Cohérence messages métier**

- Recherche par id : `"Utilisateur introuvable avec l'id : {id}"` — explicite.
- Recherche par email : `"Utilisateur introuvable avec l'email fourni."` — **volontairement vague** (évite l'énumération), mais **incohérent** avec le message par id.

---

## 4. Validation Jakarta — bilan transversal

| Couche | Couverture | Qualité |
|--------|------------|---------|
| DTO body | Forte | Bonne, sauf create/update |
| Query params (`/search`) | Oui | Alignée sur email DTO |
| Path variables (`id`) | Non | Manque `@Positive` / `@Min(1)` |
| Entité JPA | Contraintes colonnes | Complémentaire, pas substitut DTO |
| Service | Unicité email, normalisation | Bonne règle métier |

**Défense en profondeur sur User :** DTO → service (normalisation + unicité) → contrainte DB unique. **Solide**, avec le filet `DataIntegrityViolationException` en dernier recours.

---

## 5. Transactions

| Opération | Annotation | Pertinence |
|-----------|------------|------------|
| Lectures (`get*`, `getAllUsers`) | Hérite `readOnly=true` | ✅ Optimisation connexion |
| `createUser` | `@Transactional` | ✅ |
| `updateUser` | `@Transactional` | ✅ |
| `deleteUser` | `@Transactional` | ✅ |

**Point d'attention :** en update, `findById` + `save` dans la même transaction — correct. Pas de propagation personnalisée nécessaire à ce stade.

**Manque :** pas de gestion explicite de rollback sur exceptions checked (aucune levée ici — OK).

---

## 6. Structure REST API — tableau récapitulatif

| Méthode | URI | Statut succès | Validation | Remarque |
|---------|-----|---------------|------------|----------|
| POST | `/api/users` | 201 | `@Valid` DTO | OK, pas de `Location` |
| GET | `/api/users` | 200 | — | Pas de pagination |
| GET | `/api/users/{id}` | 200 | — | id non validé |
| PUT | `/api/users/{id}` | 200 | `@Valid` DTO | **Bug DTO password** |
| DELETE | `/api/users/{id}` | 204 | — | OK |
| GET | `/api/users/search?email=` | 200 | `@Email` param | OK |

**Cohérence globale API :** bonne base REST, nomenclature claire, préfixe `/api/users` cohérent. Manque versioning (`/api/v1/users`) pour évolutivité.

---

## 7. Clean code & lisibilité

### Ce qui est propre
- Classes courtes, responsabilité unique.
- Noms explicites (`findUserByIdOrThrow`, `normalizeEmail`, `updateEntityFromDto`).
- Messages métier en français, homogènes.
- Peu de sur-abstraction — code lisible pour un reviewer.

### Ce qui pourrait être amélioré
- Redondances rôle (mapper + service).
- DTO muté dans le service.
- Méthode `emailExists` orpheline.
- Quelques incohérences de validation DTO ↔ entité (max length).
- Commentaire service sur le rôle (« endpoints publics user ») — légèrement anticipé sur de la couche auth non implémentée ; acceptable comme intention documentée.

---

## 8. Maintenabilité

**Facile à faire évoluer :**
- Ajouter un champ profil = entité + DTO + mapper + éventuellement validation.
- Ajouter une exception métier = handler dédié + code stable.
- Tester unitairement `UserService` (dépendances injectables, logique isolée).

**Freins à l'évolution :**
- DTO unique create/update — chaque évolution de validation impactera les deux flux.
- Mapper manuel — risque d'oubli de champ à l'ajout.
- Absence de tests sur le module User (hors scope demandé mais impact maintenabilité réelle).

---

## 9. Risques potentiels & cas limites

| Cas | Comportement actuel | Sévérité |
|-----|---------------------|----------|
| PUT profil sans password | 400 validation | **Bug fonctionnel** |
| Deux inscriptions simultanées même email | 409 générique `DATA_INTEGRITY_VIOLATION` possible | Moyenne |
| `GET /api/users` avec 10k+ users | Charge mémoire/temps | Moyenne (future) |
| Champs texte avec espaces en début/fin | Persistés tels quels | Faible |
| Email `User@Mail.COM` vs `user@mail.com` | Normalisé en service | OK |
| Suppression user inexistant | 404 `USER_NOT_FOUND` | OK |
| Suppression user existant | 204 | OK |
| JSON `{}` en POST | 400 multi-champs validation | OK |
| Password avec espaces en milieu | Rejeté par `@Pattern` | OK |
| `firstName` = 1 caractère | Rejeté (`min=2`) | OK |
| `university` = 200 caractères | Accepté DTO, rejet possible en DB | Faible/moyenne |

---

## 10. Points forts (synthèse)

1. **Architecture layered propre** — controller mince, service riche, repository fin.
2. **Gestion d'erreurs API mature** — format structuré, codes stables, multi-erreurs validation.
3. **Validation Jakarta sérieuse** — surtout email et mot de passe.
4. **Règles métier dans le service** — unicité, normalisation, hash, rôle.
5. **Transactions bien configurées** — read-only par défaut.
6. **Mapper avec frontières claires** — password et role exclus de l'update mapping.
7. **Repository expressif** — requêtes dérivées adaptées au métier email.
8. **Statuts HTTP REST corrects** — 201, 204, 404, 409 bien exploités via le handler.

---

## 11. Faiblesses (synthèse)

1. **DTO unique create/update** — incohérence majeure avec la logique service.
2. **Validation DTO incomplète vs contraintes entité** — pas de `@Size(max)` sur plusieurs champs.
3. **Pas de pagination** sur listing.
4. **Redondances** — rôle assigné deux fois à la création ; double requête en delete.
5. **Code mort / incomplet** — `emailExists()` non branchée.
6. **Messages 404 incohérents** — id explicite vs email vague.
7. **Race condition email** — gérée en DB mais message client parfois générique.
8. **Pas de `updatedAt`** — traçabilité limitée.
9. **Détails REST manquants** — `Location` header, validation path `id`.

---

## 12. Améliorations recommandées (sans toucher à la sécurité avancée)

### Priorité haute
1. **Séparer `CreateUserRequestDTO` et `UpdateUserRequestDTO`** (ou Validation Groups) — corrige le bug PUT sans password.
2. **Aligner `@Size(max=...)` DTO** sur les `length` de l'entité User.
3. **Supprimer la redondance rôle** — le fixer soit dans le mapper, soit dans le service, pas les deux.

### Priorité moyenne
4. **Pagination** : `Page<UserResponseDTO> getAllUsers(Pageable pageable)`.
5. **Normaliser les champs texte** (trim) dans le service ou mapper — au minimum `firstName`, `lastName`, `city`.
6. **Unifier les messages 404** ou documenter le choix volontaire pour l'email.
7. **Exposer ou supprimer `emailExists()`** — ex. `HEAD /api/users/search?email=` ou endpoint dédié.
8. **Header `Location`** sur `POST 201`.
9. **`@Positive` sur `@PathVariable Long id`**.

### Priorité basse
10. Ajouter `updatedAt` + `@PreUpdate`.
11. Affiner le handler `DataIntegrityViolationException` pour détecter violation unique email (parsing message ou contrainte nommée).
12. Envisager MapStruct si le mapping grossit.
13. Versionner l'API (`/api/v1/users`).

---

## 13. Incohérences transversales

| # | Incohérence | Où |
|---|-------------|-----|
| 1 | Password optionnel en service vs obligatoire en DTO | `UserService` ↔ `UserRequestDTO` |
| 2 | Rôle USER assigné deux fois à la création | `UserMapper` ↔ `UserService` |
| 3 | Contraintes max entité vs absence max DTO | `User` ↔ `UserRequestDTO` |
| 4 | Message 404 id vs email | `UserService` |
| 5 | Commentaire « endpoints publics » sans couche auth | `UserService` (intention vs réalité) |
| 6 | Logique update partielle vs verbe PUT + DTO complet | REST design |

---

## 14. Niveau global du module User

| Référentiel | Évaluation |
|-------------|------------|
| CRUD étudiant basique | ✅ Largement dépassé |
| Junior | ✅ Oui, globalement |
| Junior confirmé | ⚠️ Oui sur architecture/erreurs ; non sur DTO create/update |
| Mid-level | ❌ Pagination, tests, DTOs séparés, edge cases manquants |
| Module « feature-complete » hors auth | ⚠️ ~75 % — un fix DTO update + pagination + alignement validation |

**En une phrase :** le module User montre une **vraie compréhension des bonnes pratiques Spring Boot** (couches, validation, transactions, erreurs structurées, règles métier), gâchée par **une incohérence create/update** qui est le point bloquant principal avant de considérer ce module comme terminé fonctionnellement.

---

*Review basée sur le code source actuel du dépôt. Aucune modification de code effectuée.*
