package com.diplom.serverboot.event;

import com.diplom.serverboot.entity.User;

public class AuthEvent {
    private User user;
    private long creationTime;

    public AuthEvent() {
    }

    public AuthEvent(User user, long creationTime) {
        this.user = user;
        this.creationTime = creationTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }
}
