package com.diplom.serverboot.dao;

import com.diplom.serverboot.entity.User;
import com.diplom.serverboot.exception.UserNotFoundException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@Component
public class UserDao {
    private static List<User> USERS;

    private static final String USER_FILE_PATH = "src/main/resources/users.json";

    private UserDao() {

    }

    @PostConstruct
    private void init() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            USERS = Arrays.asList(mapper.readValue(Path.of(USER_FILE_PATH).toFile(), User[].class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PreDestroy
    private void destroy() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        try {
            writer.writeValue(Path.of(USER_FILE_PATH).toFile(), USERS);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public User getUserByIdentifier(String identifier) throws UserNotFoundException {
        return USERS.stream().filter(user -> user.getIdentifier().equals(identifier))
                .findFirst()
                .orElseThrow(UserNotFoundException::new);
    }

    public User getUserByAuthToken(String authToken) throws UserNotFoundException {
        return USERS.stream().filter(user -> user.getAuthToken() != null && user.getAuthToken().equals(authToken))
                .findFirst()
                .orElseThrow(UserNotFoundException::new);
    }
}
