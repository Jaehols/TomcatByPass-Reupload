package com.unimelb.tomcatbypass.model;

import java.sql.Timestamp;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class AppUser implements DomainObject {
    private String username;
    private Timestamp createTimestamp;
    private String email;

    @Setter(AccessLevel.NONE)
    @ToString.Exclude
    private String pwd;

    private String ROLE;
    private String address;

    @Builder
    public AppUser(
            String username,
            Timestamp createTimestamp,
            String email,
            String pwd,
            String ROLE,
            String address) {
        this.username = username;
        this.createTimestamp = createTimestamp;
        this.email = email;
        this.pwd = pwd;
        this.ROLE = ROLE;
        this.address = address;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof AppUser)) {
            return false;
        }

        AppUser otherUser = (AppUser) other;
        return this.getUsername().equals(otherUser.getUsername());
    }
}
