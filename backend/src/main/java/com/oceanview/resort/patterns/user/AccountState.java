package com.oceanview.resort.patterns.user;


 // State pattern: Account lock/unlock states.

public interface AccountState {
    boolean canLogin();
    String getStateName();
}
