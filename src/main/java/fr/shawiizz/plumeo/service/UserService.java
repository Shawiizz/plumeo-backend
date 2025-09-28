package fr.shawiizz.plumeo.service;

import fr.shawiizz.plumeo.dto.request.LoginRequest;
import fr.shawiizz.plumeo.entity.User;
import fr.shawiizz.plumeo.repository.UserRepository;
import fr.shawiizz.plumeo.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public void registerUser(String username, String email, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setCreatedAt(Instant.now());

        userRepository.save(user);
    }

    public String loginUser(LoginRequest request) {
        User user = findByEmail(request.email());
        
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getId().toString(), request.password())
        );

        return jwtUtil.generateTokenWithUserId(user.getId());
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("error.auth.badcredentials"));
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
}