package net.trendl.procrasthelper.web.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Tomas.Rendl on 6.8.2015.
 */
public class LoginService implements UserDetailsService
{

    public UserDetails loadUserByUsername( String username ) throws UsernameNotFoundException
    {

        List<SimpleGrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));

        return new User( "trendl", "password", authorities );
    }
}