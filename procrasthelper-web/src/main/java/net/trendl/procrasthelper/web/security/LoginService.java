package net.trendl.procrasthelper.web.security;

import net.trendl.procrasthelpercore.service.UserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Tomas.Rendl on 6.8.2015.
 */
public class LoginService implements UserDetailsService
{
    @Resource(name="userService")
    private UserService userService;


    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        String password = userService.getValidPassword(username);
        if (password != null) {
            List<SimpleGrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
            return new User(username, password, authorities);
        }

        return null;
    }
}