package vn.iotstar.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.iotstar.entity.User;
import vn.iotstar.models.LoginUserModel;
import vn.iotstar.models.RegisterUserModel;
import vn.iotstar.repository.UserRepository;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User signup(RegisterUserModel input) {
        User user = new User();
        user.setFullName(input.getFullName());
        user.setEmail(input.getEmail());
        user.setUsername(input.getUsername() != null && !input.getUsername().isBlank() ? input.getUsername() : input.getEmail());
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        user.setImages("https://cdn-icons-png.flaticon.com/512/847/847969.png");

        return userRepository.save(user);
    }

    public User authenticate(LoginUserModel input) {
        // Hỗ trợ đăng nhập bằng cả username hoặc email
        String identifier = input.getUsername() != null && !input.getUsername().isBlank() ? input.getUsername() : input.getEmail();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        identifier,
                        input.getPassword()
                )
        );

        return userRepository.findByEmailOrUsername(identifier)
                .orElseThrow();
    }
}
