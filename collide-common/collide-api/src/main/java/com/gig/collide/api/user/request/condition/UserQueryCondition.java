package com.gig.collide.api.user.request.condition;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

/**
 * @author GIG
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = UserIdQueryCondition.class, name = "UserIdQueryCondition"),
        @JsonSubTypes.Type(value = UserPhoneQueryCondition.class, name = "UserPhoneQueryCondition"),
        @JsonSubTypes.Type(value = UserPhoneAndPasswordQueryCondition.class, name = "UserPhoneAndPasswordQueryCondition"),
        @JsonSubTypes.Type(value = UserUserNameQueryCondition.class, name = "UserUserNameQueryCondition"),
        @JsonSubTypes.Type(value = UserUserNameAndPasswordQueryCondition.class, name = "UserUserNameAndPasswordQueryCondition")
})
public interface UserQueryCondition extends Serializable {
}

