package com.gig.collide.box.domain.listener.event;

import org.springframework.context.ApplicationEvent;

/**
 * 创建HeldCollection事件
 *
 * @author Hollis
 */
public class BlindBoxOpenEvent extends ApplicationEvent {

    public BlindBoxOpenEvent(Long blindBoxItemId) {
        super(blindBoxItemId);
    }
}
