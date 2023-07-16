package com.unimelb.tomcatbypass.model;

import com.unimelb.tomcatbypass.mapper.UnitOfWork;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public class FixedPriceOrder extends Order {
    private Integer quantity;
    private BigDecimal total;

    @Builder
    public FixedPriceOrder(
            UUID orderId,
            AppUser user,
            Listing listing,
            Timestamp createTimestamp,
            String address,
            Integer quantity,
            BigDecimal total) {
        super(orderId, user, listing, createTimestamp, address);
        this.quantity = quantity;
        this.total = total;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public void setTotal(BigDecimal total) {
        this.total = total;
        UnitOfWork uow = UnitOfWork.getCurrent();
        if (uow != null) {
            uow.registerDirty(this);
        }
    }
}
