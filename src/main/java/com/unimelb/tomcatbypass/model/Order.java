package com.unimelb.tomcatbypass.model;

import java.sql.Timestamp;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public abstract class Order implements DomainObject {
    protected UUID orderId;
    protected AppUser user;
    protected Listing listing;
    protected Timestamp createTimestamp;
    protected String address;
}
