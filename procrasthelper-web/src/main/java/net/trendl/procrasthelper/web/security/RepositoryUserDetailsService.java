package net.trendl.procrasthelper.web.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Created by Tomas.Rendl on 7.8.2015.
 */
public class RepositoryUserDetailsService implements UserDetailsService {



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        User user =
//
//        if (user == null) {
//            throw new UsernameNotFoundException("No user found with username: " + username);
//        }

        SocialUserDetails principal = SocialUserDetails.getBuilder()
                .firstName("Tomas")
                .id("1111")
                .lastName("Rendl")
                .password("password")
                .role(Role.ROLE_USER)
                .socialSignInProvider(SocialMediaService.GOOGLE)
                .username("rendltom@gmail.com")
                .build();

        return principal;
    }
}
