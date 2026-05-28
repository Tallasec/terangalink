# Audit 4 - Module User (Spring Boot)

## Problemes restants (priorises)

- **Majeur - Contrat `PUT` encore partiellement incoherent** : `PUT` utilise `UpdateUserRequestDTO` (donc champs optionnels) et compense via `IllegalArgumentException` dans `UserService`. Cela fonctionne, mais ce n'est pas un vrai contrat "remplacement complet" au niveau validation Jakarta et schema DTO.  
  Fichiers : `UserController`, `UserService`, `UpdateUserRequestDTO`.

- **Majeur - Validation `PUT` hors pipeline Jakarta** : les champs obligatoires de `PUT` sont valides dans la couche metier (`validateReplacePayload`) au lieu d'annotations Bean Validation. Resultat : erreurs moins homogenes (code `INVALID_ARGUMENT` au lieu `VALIDATION_ERROR` + details champ par champ).  
  Fichiers : `UserService`, `GlobalExceptionHandler`.

- **Modere - Semantique `PUT` incomplete sur mot de passe** : aujourd'hui, un `PUT` peut omettre `password` et conserver l'ancien. C'est defendable, mais ce n'est pas strictement un "replace" complet. A clarifier dans le contrat API.  
  Fichiers : `UserService`, `UpdateUserRequestDTO`.

- **Modere - Unicite email toujours sujette a race condition applicative** : `exists...` puis `save` reste non atomique (meme si la contrainte DB + `DataIntegrityViolationException` couvre le dernier rempart). Pas un bug bloquant, mais risque residuel attendu en concurrence forte.  
  Fichiers : `UserService`, `UserRepository`, `GlobalExceptionHandler`.

- **Mineur - Verbosite `@NonNull` + `Objects.requireNonNull`** : robuste, mais un peu bruite et moins lisible pour un module Spring MVC ou `@PathVariable` est deja contractuel.  
  Fichiers : `UserController`, `UserService`.

## Incoherences eventuelles

- **Coherence de format d'erreur** : melange entre erreurs Jakarta structurees (`details`) et erreurs metier `IllegalArgumentException` plus globales.
- **Semantique REST/documentation** : `PUT` est "quasi complet" mais techniquement implemente avec DTO partiel + garde service.

## Ameliorations encore possibles

- Introduire un DTO dedie `ReplaceUserRequestDTO` (ou groupe de validation Jakarta) pour `PUT` strict.
- Garder `UpdateUserRequestDTO` uniquement pour `PATCH`.
- Harmoniser les erreurs de validation `PUT` via Bean Validation pour conserver `details` par champ.
- Si souhaite "replace strict", decider explicitement du comportement `password` sur `PUT` (obligatoire ou non, mais documente).

## Risques residuels

- Concurrence sur creation/mise a jour email (deja mitigee par contrainte unique DB).
- Divergence potentielle entre contrat API percu et comportement reel de `PUT` si non documente.

## Points forts

- Separation `Create` / `Update` bien faite.
- Validation taille DTO <-> JPA globalement coherente.
- Bon durcissement des updates partiels (anti valeurs blanches).
- Normalisation email robuste (service + garde-fou entity).
- Verification d'unicite email en update correctement excluante via `existsByEmailIgnoreCaseAndIdNot`.
- Architecture claire et maintenable (`Controller` / `Service` / `Mapper` / `Repository` + handler global).
- Transactions bien posees, exceptions metier propres.

## Niveau global reel

- **Niveau actuel : bon a tres bon**, proche d'un standard production robuste.
- **Pour atteindre un "tres bon" net sans reserve** : finaliser la semantique `PUT` (DTO/validation dediee) et homogeniser le pipeline d'erreurs de validation.
