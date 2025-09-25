package com.ecommerce.service.security;

import com.ecommerce.model.UsersDO;
import com.ecommerce.repository.mapper.ext.UserDOExtMapper;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service   // 👈 cần có annotation này
public class CustomUserDetailsService implements UserDetailsService {

    private final UserDOExtMapper userDOExtMapper;

    public CustomUserDetailsService(UserDOExtMapper userDOExtMapper) {
        this.userDOExtMapper = userDOExtMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UsersDO user = userDOExtMapper.selectByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        // Trả về đối tượng UserDetails cho Spring Security
        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }
}
