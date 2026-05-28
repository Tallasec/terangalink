# Audit 3 - Module User (Spring Boot)

## Findings (problemes restants)

### Critique
- **Conflit email possible en update sur donnees legacy** : dans `UserService`, la verification d'unicite utilise `existsByEmailIgnoreCase(normalizedEmail)` sans exclure l'utilisateur courant. Si la base contient un email du meme user avec une casse historique differente, la mise a jour peut lever a tort `EmailAlreadyExistsException`.
- **`PUT` et `PATCH` ont exactement le meme comportement partiel** dans `UserController` (les deux appellent `updateUser` partiel). Ce n'est pas bloquant techniquement, mais c'est une incoherence REST API importante : `PUT` est normalement remplacement complet, `PATCH` partiel.

### Majeur
- **Validation update trop permissive sur champs texte** (`UpdateUserRequestDTO`) : `university`, `fieldOfStudy`, `city` n'ont qu'un `@Size(max=...)`. Donc `"   "` passe la validation et peut degrader la qualite des donnees (valeur "vide" mais non nulle).
- **Double responsabilite sur `role`** : `UserMapper.toEntity()` et `UserService.createUser()` forcent tous les deux `Role.USER`. Redondance mineure mais dette de maintenabilite (source de verite non unique).
- **Methode `getAllUsers(Pageable)` non exposee** : la preparation pagination est bonne dans `UserService`, mais l'API n'a pas encore de point d'entree compatible (`UserController` reste `List`). C'est OK pour "preparer", mais incomplet si l'objectif est proche production.

### Modere
- **Normalisation email dupliquee** : centralisation faite via `EmailNormalizer` (tres bien), mais `User` normalise aussi en `@PrePersist/@PreUpdate`. C'est defensif, mais ajoute une duplication de logique a surveiller (evolution future des regles).
- **`deleteUser` fait 2 acces DB** (`existsById` puis `deleteById`) avec petite fenetre de course. Preferer une suppression basee sur entite chargee (ou strategie unique) pour robustesse/concision.

## Incoherences eventuelles

- **Messages/accents** : certains messages sont en francais accentue, d'autres non (`deja` vs `deja` attendu `deja`/`déjà` de facon homogene). Fonctionnellement neutre, mais cela nuit a l'homogeneite API.
- **Semantique endpoint** : `PUT` partiel + `PATCH` partiel = duplication contractuelle.

## Ameliorations encore possibles (priorisees)

- Ajouter un check d'unicite email **excluant l'ID courant** (ex: methode repository dediee).
- Clarifier le contrat REST :
  - soit `PUT` = complet et `PATCH` = partiel,
  - soit conserver partiel et documenter explicitement (mais idealement eviter).
- Renforcer `UpdateUserRequestDTO` avec contrainte "si champ fourni, il ne doit pas etre blanc" (pattern ou validateur custom).
- Supprimer la redondance d'affectation `role` (garder une seule couche responsable).
- Exposer une version paginee dans `UserController` sans casser l'existant (ex: nouveaux params optionnels `page/size/sort`).

## Points forts du module

- **Separation DTO create/update reussie** : bon decouplage, plus propre qu'un DTO unique.
- **Alignement tailles DTO/JPA globalement bon** (`100/150/120/255`) : reduction nette du risque SQL.
- **Normalisation email en place** (service + garde-fou entity) : coherence meilleure qu'avant.
- **Architecture claire** (`Controller` / `Service` / `Mapper` / `Repository` / exceptions) et lisible.
- **Gestion d'erreurs structuree** via `GlobalExceptionHandler` + `ApiErrorResponse`.
- **Transactions bien posees** (`readOnly` global + overrides en ecriture).

## Open questions / hypotheses

- Souhait produit exact pour `PUT` : remplacement complet ou mise a jour partielle ?
- Veux-tu interdire explicitement les valeurs "blanches" (`"   "`) en update sur tous les champs texte ?
- Souhaites-tu normaliser aussi `firstName/lastName/city` (trim) au niveau service/entity, ou seulement l'email ?

## Niveau global du backend User

- **Niveau actuel : bon / quasi production**, avec une base solide et propre.
- **Pour passer a "tres bon / production robuste"** : corriger les 2 points principaux (unicite email en update + contrat REST `PUT/PATCH`) et durcir les validations update sur champs texte.
