package com.oceanview.resort.patterns.room;


 // State pattern: Room status transitions.

public interface RoomStatusState {
    String getCode();
    boolean canTransitionTo(String newStatus);
}
