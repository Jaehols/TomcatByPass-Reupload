package com.unimelb.tomcatbypass.model;

import com.unimelb.tomcatbypass.enums.Condition;
import com.unimelb.tomcatbypass.mapper.UnitOfWork;
import java.sql.Timestamp;
import java.util.UUID;
import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public abstract class Listing implements DomainObject {
    protected UUID listingId;
    protected SellerGroup sellerGroup;
    protected Timestamp createTimestamp;
    protected String description;
    protected Condition condition;

    protected abstract void lazyLoad();

    public UUID getListingId() {
        if (listingId == null) {
            lazyLoad();
        }
        return listingId;
    }

    public SellerGroup getSellerGroup() {
        if (sellerGroup == null) {
            lazyLoad();
        }
        return sellerGroup;
    }

    public Timestamp getCreateTimestamp() {
        if (createTimestamp == null) {
            lazyLoad();
        }
        return createTimestamp;
    }

    public String getDescription() {
        if (description == null) {
            lazyLoad();
        }
        return description;
    }

    public Condition getCondition() {
        if (condition == null) {
            lazyLoad();
        }
        return condition;
    }

    public void setListingId(UUID listingId) {
        this.listingId = listingId;
        UnitOfWork uow = UnitOfWork.getCurrent();
        if (uow != null) {
            uow.registerDirty(this);
        }
    }

    public void setSellerGroup(SellerGroup sellerGroup) {
        this.sellerGroup = sellerGroup;
        UnitOfWork uow = UnitOfWork.getCurrent();
        if (uow != null) {
            uow.registerDirty(this);
        }
    }

    public void setCreateTimestamp(Timestamp createTimestamp) {
        this.createTimestamp = createTimestamp;
        UnitOfWork uow = UnitOfWork.getCurrent();
        if (uow != null) {
            uow.registerDirty(this);
        }
    }

    public void setDescription(String description) {
        this.description = description;
        UnitOfWork uow = UnitOfWork.getCurrent();
        if (uow != null) {
            uow.registerDirty(this);
        }
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
        UnitOfWork uow = UnitOfWork.getCurrent();
        if (uow != null) {
            uow.registerDirty(this);
        }
    }
}
