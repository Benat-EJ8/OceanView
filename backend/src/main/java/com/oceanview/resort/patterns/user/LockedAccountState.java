package com.oceanview.resort.patterns.user;

public class LockedAccountState implements AccountState {
    @Override
    public boolean canLogin() {
        return false;
    }

    @Override
    public String getStateName() {
        return "LOCKED";
    }
}
