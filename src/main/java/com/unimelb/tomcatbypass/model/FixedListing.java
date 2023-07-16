package com.unimelb.tomcatbypass.model;

import com.unimelb.tomcatbypass.enums.Condition;
import com.unimelb.tomcatbypass.mapper.FixedListingMapper;
import com.unimelb.tomcatbypass.mapper.UnitOfWork;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.*;

@ToString(callSuper = true)
public class FixedListing extends Listing {
    private static final Logger log = Logger.getLogger(FixedListing.class.getName());
    private BigDecimal price;
    private Integer quantity;

    // For lazy loading.
    public FixedListing(UUID listingId) {
        this.listingId = listingId;
    }

    // For eager loading.
    @Builder
    public FixedListing(
            UUID listingId,
            SellerGroup sellerGroup,
            Timestamp createTimestamp,
            String description,
            Condition condition,
            BigDecimal price,
            Integer quantity) {
        super(listingId, sellerGroup, createTimestamp, description, condition);
        this.price = price;
        this.quantity = quantity;
    }

    @Override
    protected void lazyLoad() {
        if (listingId == null) {
            log.log(Level.SEVERE, "FixedListing was initialised with null listingId");
            return;
        }
        FixedListing loadedListing = new FixedListingMapper().findByListingId(listingId);

        sellerGroup = (sellerGroup == null) ? loadedListing.sellerGroup : sellerGroup;
        createTimestamp = (createTimestamp == null) ? loadedListing.createTimestamp : createTimestamp;
        price = (price == null) ? loadedListing.price : price;
        description = (description == null) ? loadedListing.description : description;
        condition = (condition == null) ? loadedListing.condition : condition;
        quantity = (quantity == null) ? loadedListing.quantity : quantity;
    }

    public BigDecimal getPrice() {
        if (price == null) {
            lazyLoad();
        }
        return price;
    }

    public Integer getQuantity() {
        if (quantity == null) {
            lazyLoad();
        }
        return quantity;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
        UnitOfWork uow = UnitOfWork.getCurrent();
        if (uow != null) {
            uow.registerDirty(this);
        }
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        UnitOfWork uow = UnitOfWork.getCurrent();
        if (uow != null) {
            uow.registerDirty(this);
        }
    }
}
