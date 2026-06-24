package com.terangalink.backend.responseDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de sortie après inscription ou connexion réussie.
 * <p>
 * Rôle futur — contrat stable pour le client front/mobile :
 * <ul>
 *   <li>{@code accessToken} — JWT signé émis par {@link com.terangalink.backend.security.JwtService}</li>
 *   <li>{@code tokenType} — toujours {@code "Bearer"}</li>
 *   <li>{@code expiresIn} — durée de validité en secondes (confort client)</li>
 *   <li>{@code user} — profil {@link UserResponseDTO} sans mot de passe,
 *       permettant l'affichage immédiat après register/login</li>
 * </ul>
 */
@Getter
@Setter
@NoArgsConstructor
public class AuthResponseDTO {

    // TODO : peupler ces champs depuis AuthService après génération du token
    private String accessToken;
    private String tokenType;
    private long expiresIn;
    private UserResponseDTO user;

}
