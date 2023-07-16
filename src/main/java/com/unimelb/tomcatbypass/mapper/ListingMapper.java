package com.unimelb.tomcatbypass.mapper;

import java.util.List;
import java.util.UUID;

public interface ListingMapper<T> {
    List<T> findAllLazy();

    List<T> findAllEager();

    T findByListingId(UUID id);

    List<T> findInLimitOffset(Integer limit, Integer offset);

    List<T> findByUsernameInLimitOffset(String username, Integer limit, Integer offset);

    List<T> findByDescriptionInLimitOffset(String description, Integer limit, Integer offset);
}
