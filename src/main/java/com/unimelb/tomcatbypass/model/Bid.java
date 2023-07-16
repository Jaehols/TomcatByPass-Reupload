package com.unimelb.tomcatbypass.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;
import lombok.*;

@Getter
@ToString
public class Bid implements DomainObject {
    private UUID bidId;
    private AppUser user;
    private Listing listing;
    private Timestamp createTimestamp;
    private BigDecimal value;

    @Builder
    public Bid(UUID bidId, AppUser user, Listing listing, Timestamp createTimestamp, BigDecimal value) {
        this.bidId = bidId;
        this.user = user;
        this.listing = listing;
        this.createTimestamp = createTimestamp;
        this.value = value;
    }
}
