package com.unimelb.tomcatbypass.model;

import com.unimelb.tomcatbypass.enums.Condition;
import com.unimelb.tomcatbypass.mapper.AuctionListingMapper;
import com.unimelb.tomcatbypass.mapper.UnitOfWork;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.*;

@ToString(callSuper = true)
public class AuctionListing extends Listing implements DomainObject {
    private static final Logger log = Logger.getLogger(AuctionListing.class.getName());
    private BigDecimal startPrice;
    private Timestamp endTimestamp;

    // For lazy loading.
    public AuctionListing(UUID listingId) {
        this.listingId = listingId;
    }

    // For eager loading.
    @Builder
    public AuctionListing(
            UUID listingId,
            SellerGroup sellerGroup,
            Timestamp createTimestamp,
            String description,
            Condition condition,
            BigDecimal startPrice,
            Timestamp endTimestamp) {
        super(listingId, sellerGroup, createTimestamp, description, condition);
        this.startPrice = startPrice;
        this.endTimestamp = endTimestamp;
    }

    @Override
    protected void lazyLoad() {
        if (listingId == null) {
            log.log(Level.SEVERE, "AuctionListing was initialised with null listingId");
            return;
        }
        AuctionListing loadedListing = new AuctionListingMapper().findByListingId(listingId);

        sellerGroup = (sellerGroup == null) ? loadedListing.sellerGroup : sellerGroup;
        createTimestamp = (createTimestamp == null) ? loadedListing.createTimestamp : createTimestamp;
        startPrice = (startPrice == null) ? loadedListing.startPrice : startPrice;
        description = (description == null) ? loadedListing.description : description;
        endTimestamp = (endTimestamp == null) ? loadedListing.endTimestamp : endTimestamp;
        condition = (condition == null) ? loadedListing.condition : condition;
    }

    public BigDecimal getStartPrice() {
        if (startPrice == null) {
            lazyLoad();
        }
        return startPrice;
    }

    public Timestamp getEndTimestamp() {
        if (endTimestamp == null) {
            lazyLoad();
        }
        return endTimestamp;
    }

    public void setStartPrice(BigDecimal startPrice) {
        this.startPrice = startPrice;
        UnitOfWork uow = UnitOfWork.getCurrent();
        if (uow != null) {
            uow.registerDirty(this);
        }
    }

    public void setEndTimestamp(Timestamp endTimestamp) {
        this.endTimestamp = endTimestamp;
        UnitOfWork uow = UnitOfWork.getCurrent();
        if (uow != null) {
            uow.registerDirty(this);
        }
    }
}
