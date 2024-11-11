package com.xiuxiu.app.server.room;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface GameInfo {
    int gameType();
    int gameSubType() default -1;
    ERoomType roomType() default ERoomType.NORMAL;
}
