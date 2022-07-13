package jwt.tutorial.service;

import jwt.tutorial.dto.UserDto;
import jwt.tutorial.entity.Authority;
import jwt.tutorial.entity.User;
import jwt.tutorial.repository.UserRepository;
import jwt.tutorial.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User join(UserDto userDto) {
        if (userRepository.findOneWithAuthoritiesByUsername(userDto.getUsername())
                .orElse(null) != null) {
            throw new RuntimeException("이미 존재하는 유저");
        }

        Authority authority = new Authority("ROLE_USER");
        User user = User.builder()
                .username(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .nickname(userDto.getNickname())
                .authorities(Collections.singleton(authority))
                .activated(true)
                .build();

        return userRepository.save(user);
    }

    public Optional<User> getUserWithAuthorities(String username) {
        return userRepository.findOneWithAuthoritiesByUsername(username);
    }

    public Optional<User> getMyUserWithAuthorities() {
        return SecurityUtil.getCurrentUsername().flatMap(userRepository::findOneWithAuthoritiesByUsername);
    }

    public User getUserByName(String username) {
        return userRepository.findByUsername(username);
    }
}
