package com.gig.collide.chain.domain.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author GIG
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WenChangCreateBody extends WenChangRequestBody{

    @JSONField(name = "name")
    private String name;
}
