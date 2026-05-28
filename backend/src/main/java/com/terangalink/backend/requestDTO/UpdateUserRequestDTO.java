package com.terangalink.backend.requestDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = false)
public class UpdateUserRequestDTO {

    @Size(min = 2, max = 100, message = "Le prenom doit contenir entre 2 et 100 caracteres.")
    @Pattern(regexp = "^(?!\\s*$).*$", message = "Le prenom ne peut pas etre vide ou blanc.")
    private String firstName;

    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caracteres.")
    @Pattern(regexp = "^(?!\\s*$).*$", message = "Le nom ne peut pas etre vide ou blanc.")
    private String lastName;

    @Email(message = "Le format de l'email est invalide.")
    @Size(max = 255, message = "L'adresse email ne doit pas depasser 255 caracteres.")
    @Pattern(regexp = "^\\S+$", message = "L'adresse email ne doit contenir aucun espace.")
    private String email;

    @Size(min = 8, max = 255, message = "Le mot de passe doit contenir entre 8 et 255 caracteres.")
    @Pattern(
            regexp = "^(?=\\S+$)(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9\\s]).{8,255}$",
            message = "Le mot de passe doit contenir au moins une majuscule, une minuscule, un chiffre et un caractere special, sans espace."
    )
    private String password;

    @Size(max = 150, message = "L'universite ne doit pas depasser 150 caracteres.")
    @Pattern(regexp = "^(?!\\s*$).*$", message = "L'universite ne peut pas etre vide ou blanche.")
    private String university;

    @Size(max = 150, message = "Le domaine d'etudes ne doit pas depasser 150 caracteres.")
    @Pattern(regexp = "^(?!\\s*$).*$", message = "Le domaine d'etudes ne peut pas etre vide ou blanc.")
    private String fieldOfStudy;

    @Size(max = 120, message = "La ville ne doit pas depasser 120 caracteres.")
    @Pattern(regexp = "^(?!\\s*$).*$", message = "La ville ne peut pas etre vide ou blanche.")
    private String city;
}
