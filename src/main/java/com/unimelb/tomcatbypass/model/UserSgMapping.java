package com.unimelb.tomcatbypass.model;

import com.unimelb.tomcatbypass.mapper.UnitOfWork;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class UserSgMapping implements DomainObject {
    private final AppUser appUser;
    private final SellerGroup sellerGroup;

    @Builder
    public UserSgMapping(AppUser appUser, SellerGroup sellerGroup) {
        this.appUser = appUser;
        this.sellerGroup = sellerGroup;
    }
}
