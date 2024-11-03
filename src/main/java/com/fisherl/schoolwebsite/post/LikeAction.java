package com.fisherl.schoolwebsite.post;

import java.util.function.Function;

public enum LikeAction {

    ADD(i -> i + 1),
    SUBTRACT(i -> i - 1);

    private final Function<Integer, Integer> action;

    LikeAction(Function<Integer, Integer> action) {
        this.action = action;
    }

    public int apply(int amount) {
        return this.action.apply(amount);
    }
}
