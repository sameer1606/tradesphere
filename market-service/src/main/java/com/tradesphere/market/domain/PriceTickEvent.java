package com.tradesphere.market.domain;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PriceTickEvent extends ApplicationEvent {
    private final PriceTick priceTick;

    public PriceTickEvent(Object source, PriceTick priceTick) {
        super(source);
        this.priceTick = priceTick;
    }
}
