package com.terangalink.backend.service;
import  com.terangalink.backend.entity.User;
import com.terangalink.backend.config.JwtProperties;
import com.terangalink.backend.exception.business.InvalidCredentialsException;
import com.terangalink.backend.exception.business.UserNotFoundException;
import com.terangalink.backend.mapper.UserMapper;
import com.terangalink.backend.repository.UserRepository;
import com.terangalink.backend.requestDTO.CreateUserRequestDTO;
import com.terangalink.backend.requestDTO.LoginRequestDTO;
import com.terangalink.backend.responseDTO.AuthResponseDTO;
import com.terangalink.backend.responseDTO.UserResponseDTO;
import com.terangalink.backend.security.JwtService;
import com.terangalink.backend.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final String INVALID_CREDENTIALS_MESSAGE = "Identifiants invalides.";

    private final UserService userService;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailNormalizer emailNormalizer;

    public AuthService(
            UserService userService,
            JwtService jwtService,
            JwtProperties jwtProperties,
            UserRepository userRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder,
            EmailNormalizer emailNormalizer) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.emailNormalizer = emailNormalizer;
    }


    // Inscription d'un nouvel utilisateur
    public AuthResponseDTO register(CreateUserRequestDTO request) {
        UserResponseDTO createdUser = userService.createUser(request);
        return buildAuthResponse(loadUserById(createdUser.getId()));
    }

    // Connexion utilisateur
    public AuthResponseDTO login(LoginRequestDTO request) {
        String normalizedEmail = emailNormalizer.normalize(request.getEmail());
        var user = userRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new InvalidCredentialsException(INVALID_CREDENTIALS_MESSAGE));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException(INVALID_CREDENTIALS_MESSAGE);
        }

        return buildAuthResponse(user);
    }

    // Methode pour récupérer le User courant
    public UserResponseDTO getCurrentUser() {
        UserPrincipal principal = getCurrentPrincipal();
        return userMapper.toResponseDto(loadUserById(principal.getId()));
    }

    // Methode pour récupérer l'utilisateur actuellement connecté
    private UserPrincipal getCurrentPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new InvalidCredentialsException("Utilisateur non authentifie.");
        }

        return principal;
    }
    // Methode pour chercher un utilisateur par son id
    private User loadUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur introuvable avec l'id : " + id));
    }

    private AuthResponseDTO buildAuthResponse(User user) {
        UserPrincipal principal = UserPrincipal.from(user); //Création du principal
        AuthResponseDTO response = new AuthResponseDTO();
        response.setAccessToken(jwtService.generateToken(principal));//Création du JWT
        response.setTokenType("Bearer");
        response.setExpiresIn(jwtProperties.getExpirationMs() / 1000);
        response.setUser(userMapper.toResponseDto(user));
        return response;
    }
}
