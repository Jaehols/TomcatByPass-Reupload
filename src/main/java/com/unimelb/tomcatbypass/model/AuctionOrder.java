package com.unimelb.tomcatbypass.model;

import com.unimelb.tomcatbypass.mapper.UnitOfWork;
import java.sql.Timestamp;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public class AuctionOrder extends Order {
    private Bid bid;

    @Builder
    public AuctionOrder(
            UUID orderId, AppUser user, Listing listing, Timestamp createTimestamp, String address, Bid bid) {
        super(orderId, user, listing, createTimestamp, address);
        this.bid = bid;
    }

    public void setAddress(String address) {
        this.address = address;
        UnitOfWork uow = UnitOfWork.getCurrent();
        if (uow != null) {
            uow.registerDirty(this);
        }
    }
}
