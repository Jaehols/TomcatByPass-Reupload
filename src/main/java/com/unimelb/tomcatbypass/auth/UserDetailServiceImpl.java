package com.unimelb.tomcatbypass.auth;

import com.unimelb.tomcatbypass.model.AppUser;
import com.unimelb.tomcatbypass.service.AppUserService;
import com.unimelb.tomcatbypass.utils.Session;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailServiceImpl implements UserDetailsService {
    private static final Logger log = Logger.getLogger(UserDetailServiceImpl.class.getName());

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Load User By Name started");
        Session.startSession();
        AppUser appuser = AppUserService.findByUsername(username);
        Session.closeSession();
        if (Objects.isNull(appuser)) {
            log.info("Username not found throwing exception");
            throw new UsernameNotFoundException(username);
        }

        log.info("User found " + appuser);

        UserDetails userDetails = null;
        try {
            UserBuilder builder = User.withUsername(appuser.getUsername());
            builder.password(appuser.getPwd());
            builder.roles(appuser.getROLE());
            userDetails = builder.build();
        } catch (Exception e) {
            log.log(Level.INFO, "User builder Error: " + e.getMessage());
        }
        return userDetails;
    }
}
