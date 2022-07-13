package jwt.tutorial.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
public class SecurityUtil {

    public static Optional<String> getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (null == authentication) {
            log.info("Security context에 인증 정보 없음");
            return Optional.empty();
        }

        String username = null;
        if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails user = (UserDetails) authentication.getPrincipal();
            List<String> domains = SecurityUtil.getDomains(user.getUsername());
            username = domains.get(1);
        } else if (authentication.getPrincipal() instanceof String) {
            username = (String) authentication.getPrincipal();
        }

        return Optional.ofNullable(username);
    }

    public static List<String> getDomains(String principal) {
        List<String> domains = new ArrayList<>();
        String[] split = principal.split(" ");
        if(split != null) {
            domains.addAll(Arrays.asList(split));
        }
        return domains;
    }
}
