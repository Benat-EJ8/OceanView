package com.oceanview.resort.patterns.user;

public class ActiveAccountState implements AccountState {
    @Override
    public boolean canLogin() {
        return true;
    }

    @Override
    public String getStateName() {
        return "ACTIVE";
    }
}
