# Code Review Spring Boot - TerangaLink

## Portee de la review
Cette review couvre principalement :
- `UserController`
- `UserService`
- `UserRepository`
- `UserMapper`
- DTO (`UserRequestDTO`, `UserResponseDTO`)
- `GlobalExceptionHandler` et exceptions metier
- validation Jakarta, gestion des roles, transactions, gestion des erreurs

## Points positifs
- Separation des couches globalement propre (`controller` -> `service` -> `repository`).
- Usage de DTO et d'un mapper dedie.
- Validation Jakarta activee avec `@Valid` dans le controller.
- Gestion centralisee des erreurs via `@RestControllerAdvice`.
- Transactionnalite correctement posee (read-only par defaut + ecriture annotee).

## Problemes critiques (priorite haute)

### 1) Mot de passe stocke en clair
**Observation**  
Le mot de passe est mappe tel quel (`dto.getPassword()`) puis persiste.

**Pourquoi c'est un probleme**  
En cas de fuite DB, tous les comptes sont compromises. Ce n'est pas conforme aux standards OWASP.

**Amelioration recommandee**  
Hasher le mot de passe cote service avant `save` avec `PasswordEncoder` (BCrypt/Argon2), ne jamais stocker la valeur brute.

### 2) Role pilotable par le client
**Observation**  
`UserRequestDTO` expose `role`, et le mapper applique cette valeur en creation/mise a jour.

**Pourquoi c'est un probleme**  
Risque d'escalade de privileges (auto-attribution ADMIN).

**Amelioration recommandee**  
Retirer `role` des DTO publics (ou ignorer cote mapper), forcer `Role.USER` cote serveur. Gerer la promotion via endpoint admin securise.

### 3) Absence de securite d'acces visible
**Observation**  
Le CRUD utilisateur est exposé sans regles d'autorisation visibles.

**Pourquoi c'est un probleme**  
Lecture/modification/suppression potentiellement ouvertes a tout client.

**Amelioration recommandee**  
Ajouter Spring Security, controle par role et regles d'ownership.

## Validation / DTO

### 4) Meme DTO pour create et update
**Observation**  
`UserRequestDTO` impose `@NotBlank` sur `password`.

**Pourquoi c'est un probleme**  
Une mise a jour partielle sans changement de mot de passe peut echouer.

**Amelioration recommandee**  
Creer deux DTO (`CreateUserRequestDTO`, `UpdateUserRequestDTO`) ou utiliser les Validation Groups.

### 5) Validation email via path variable
**Observation**  
`GET /api/users/email/{email}`.

**Pourquoi c'est un probleme**  
Encodage plus fragile, traces/logs URL, validation moins naturelle.

**Amelioration recommandee**  
Preferer `GET /api/users?email=...` avec validation sur parametre.

## Exceptions / gestion des erreurs

### 6) Une seule erreur de validation retournee
**Observation**  
Le handler de `MethodArgumentNotValidException` retourne uniquement le premier `FieldError`.

**Pourquoi c'est un probleme**  
Le client corrige les erreurs une par une, mauvaise ergonomie API.

**Amelioration recommandee**  
Retourner toutes les erreurs de validation (liste ou map `champ -> message`).

### 7) Handlers incomplets pour erreurs frequentes
**Observation**  
Pas de handlers explicites pour `ConstraintViolationException`, `DataIntegrityViolationException`, `HttpMessageNotReadableException`, etc.

**Pourquoi c'est un probleme**  
Des erreurs client peuvent tomber en 500 generique.

**Amelioration recommandee**  
Ajouter des handlers dedies (400/409) avec messages coherents.

### 8) Logging serveur des exceptions absent
**Observation**  
Le fallback 500 renvoie un message neutre (bien), mais sans log explicite.

**Pourquoi c'est un probleme**  
Difficulte de diagnostic en production.

**Amelioration recommandee**  
Logger l'exception cote serveur (eventuellement avec correlation id).

## Transactions / concurrence / robustesse

### 9) Verification d'unicite email sujette aux courses
**Observation**  
Pattern `existsByEmail(...)` puis `save(...)`.

**Pourquoi c'est un probleme**  
Deux requetes concurrentes peuvent passer le check applicatif.

**Amelioration recommandee**  
Conserver la contrainte DB unique comme source de verite et gerer `DataIntegrityViolationException` en `409 CONFLICT`.

## Performance

### 10) `findAll()` sans pagination
**Observation**  
`getAllUsers()` lit toute la table.

**Pourquoi c'est un probleme**  
Risque de degradation memoire/temps de reponse sur gros volume.

**Amelioration recommandee**  
Ajouter pagination (`Pageable`) et tri.

## Maintenabilite / lisibilite / coherence

### 11) Messages d'erreur metier incoherents
**Observation**  
Messages differents et parfois incomplets (`id` absent ou formatage perfectible).

**Pourquoi c'est un probleme**  
API moins previsible, debugging plus difficile.

**Amelioration recommandee**  
Standardiser les messages et codes d'erreur.

### 12) Contraintes DB a completer
**Observation**  
Seul `email` a des contraintes explicites DB.

**Pourquoi c'est un probleme**  
La robustesse repose trop sur la validation DTO.

**Amelioration recommandee**  
Ajouter `nullable = false`/`length` sur les champs critiques en entite.

## Plan d'amelioration professionnel (minimal et pragmatique)
1. Hasher le password avec `PasswordEncoder` dans le service (priorite immediate).
2. Bloquer la gestion de `role` cote endpoints publics.
3. Separer DTO create/update (ou Validation Groups).
4. Enrichir `GlobalExceptionHandler` (multi-erreurs validation + erreurs techniques usuelles).
5. Ajouter pagination sur la lecture de masse.
6. Normaliser l'email (`trim`, lowercase) avant comparaison/persistance.

## Niveau de risque actuel
- **Eleve** sur la securite (password clair, role modifiable).
- **Moyen** sur robustesse API (gestion d'erreurs partielle, concurrence unicite).
- **Moyen** sur scalabilite (absence de pagination).

