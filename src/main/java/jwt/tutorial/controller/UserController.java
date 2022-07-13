package jwt.tutorial.controller;

import jwt.tutorial.dto.LoginDto;
import jwt.tutorial.dto.TokenDto;
import jwt.tutorial.dto.UserDto;
import jwt.tutorial.entity.User;
import jwt.tutorial.jwt.JwtFilter;
import jwt.tutorial.jwt.TokenProvider;
import jwt.tutorial.service.UserService;
import jwt.tutorial.util.UserType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @PostMapping("/signup")
    public ResponseEntity<User> signup(@Validated @RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.join(userDto));
    }

    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<User> getMyUserInfo() {
        return ResponseEntity.ok(userService.getMyUserWithAuthorities().get());
    }

    @GetMapping("/user/{username}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<User> getUserInfo(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserWithAuthorities(username).get());
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@Validated @RequestBody LoginDto loginDto) {
        User user = userService.getUserByName(loginDto.getUsername());

        String domain = String.format("%s %s %s", String.valueOf(user.getId()),
                loginDto.getUsername(),
                "franchisee");

        UsernamePasswordAuthenticationToken authToken
                = new UsernamePasswordAuthenticationToken(domain, loginDto.getPassword());

        Authentication auth
                = authenticationManagerBuilder.getObject().authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(auth);

        String jwt = tokenProvider.createToken(auth);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);

        return new ResponseEntity<>(new TokenDto(jwt), httpHeaders, HttpStatus.OK);
    }

}
