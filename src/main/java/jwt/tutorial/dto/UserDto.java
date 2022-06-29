package jwt.tutorial.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class UserDto {

    @NotNull
    private String username;

    @NotNull
    private String password;

    @NotNull
    private String nickname;
}
