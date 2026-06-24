package com.terangalink.backend.security;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests unitaires de {@link JwtAuthenticationFilter}.
 * <p>
 * Rôle futur — valider le comportement du filtre servlet :
 * <ul>
 *   <li>Header {@code Authorization: Bearer &lt;token valide&gt;} —
 *       {@code SecurityContext} peuplé avec le bon {@link UserPrincipal}</li>
 *   <li>Header absent — contexte vide, requête transmise</li>
 *   <li>Token invalide ou expiré — contexte vide, pas d'exception levée dans le filtre</li>
 *   <li>Format Bearer incorrect — contexte vide</li>
 * </ul>
 * Mocks : {@link JwtService}, {@link CustomUserDetailsService}.
 */
@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    // TODO : tester doFilterInternal avec MockHttpServletRequest/Response

}
