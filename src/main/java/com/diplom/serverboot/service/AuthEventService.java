package com.diplom.serverboot.service;

import com.diplom.serverboot.entity.User;
import com.diplom.serverboot.event.AuthEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class AuthEventService {
    private final List<AuthEvent> authEvents = new ArrayList<>();

    public void addEvent(User user) {
        Optional<AuthEvent> userEvent = authEvents.stream().filter(event -> event.getUser().equals(user)).findAny();
        if (userEvent.isPresent()) {
            userEvent.get().setCreationTime(System.currentTimeMillis());
        }
        else {
            AuthEvent newEvent = new AuthEvent(user, System.currentTimeMillis());
        }
    }

    public boolean isNeededToAuth(User user) {
        Optional<AuthEvent> userEvent = authEvents.stream().filter(event -> event.getUser().equals(user)).findAny();
        return userEvent.isPresent();
    }

    public void removeEvent(User user) {
        List<AuthEvent> events = authEvents.stream().filter(event -> event.getUser().equals(user)).toList();
        authEvents.removeAll(events);
    }
}
