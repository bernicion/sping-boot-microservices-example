package com.bernic.msscbeerorderservice.domain;

public enum BeerOrderStatusEnum {
    NEW,
    VALIDATED,
    VALIDATION_PENDING,
    VALIDATION_EXCEPTION,
    ALLOCATION_PENDING,
    CANCELED,
    ALLOCATED,
    ALLOCATION_EXCEPTION,
    PENDING_INVENTORY,
    PICKED_UP,
    DELIVERED,
    DELIVERED_EXCEPTION
}
