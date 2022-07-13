package jwt.tutorial.service;

import jwt.tutorial.entity.User;
import jwt.tutorial.repository.UserRepository;
import jwt.tutorial.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component("userDetailsService")
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository repository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String username) {
        List<String> domains = SecurityUtil.getDomains(username);
        return repository.findById(Long.valueOf(domains.get(0)))
                .map(user -> createUser(username, user))
                .orElseThrow(() -> new UsernameNotFoundException("user name not found in DB"));
//        return repository.findOneWithAuthoritiesByUsername(username)
//                .map(user -> createUser(username, user))
//                .orElseThrow(() -> new UsernameNotFoundException("user name not found in DB"));
    }

    private org.springframework.security.core.userdetails.User createUser(String username, User user) {
        if (!user.isActivated()) {
            throw new RuntimeException("username is not activate");
        }
        List<SimpleGrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(username,
                user.getPassword(),
                grantedAuthorities);
    }



}
