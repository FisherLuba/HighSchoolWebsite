package com.fisherl.schoolwebsite.post.question;

import com.fisherl.schoolwebsite.post.LikeAction;
import org.springframework.core.convert.converter.Converter;

public class LikeActionStringConverter implements Converter<String, LikeAction> {

    @Override
    public LikeAction convert(String source) {
        return LikeAction.valueOf(source.toUpperCase());
    }

}