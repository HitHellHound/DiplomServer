package com.diplom.serverboot.controller;

import com.diplom.serverboot.dto.AuthMessage;
import com.diplom.serverboot.dto.Message;
import com.diplom.serverboot.entity.User;
import com.diplom.serverboot.exception.BadCreditionalsException;
import com.diplom.serverboot.exception.UserNotFoundException;
import com.diplom.serverboot.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.stream.Collectors;

@RestController
public class SessionController {
    private static final String API_FILE_PATH = "src/main/resources/api";
    @Autowired
    private UserService userService;

    @GetMapping("/start")
    public Message getAPI() {
        String message;
        try(BufferedReader reader = new BufferedReader(new FileReader(API_FILE_PATH))) {
            message = reader.lines().collect(Collectors.joining("\n"));
        }
        catch (Exception exception) {
            System.out.println(exception);
            return new Message(500, "Server API file Error");
        }
        return new Message(200, message);
    }

    @PostMapping("/session")
    public Message createSession(HttpServletRequest request, @RequestBody Message message) {
        HttpSession session = request.getSession();
        session.setAttribute("clientPublicKey", message.getMessage());
        session.setAttribute("serverPrivateKey", "serverPrivateKey");
        message.setCode(100);
        message.setMessage("serverPublicKey");
        return message;
    }

    @PostMapping("/login")
    public Message loginByCreditionals(HttpServletRequest request, @RequestBody AuthMessage message) {
        Message responceMessage = new Message();
        try {
            User user = userService.authenticateUserByCreditionals(message.getIdentifier(), message.getPassword());
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            responceMessage.setCode(200);
            responceMessage.setMessage(user.getAuthToken());
        } catch (UserNotFoundException e) {
            responceMessage.setCode(404);
            responceMessage.setMessage("User not found");
        } catch (BadCreditionalsException e) {
            responceMessage.setCode(401);
            responceMessage.setMessage("Bad creditionals");
        }
        return responceMessage;
    }

    @GetMapping("/login")
    public Message loginByAuthToken(HttpServletRequest request, @RequestParam String authToken) {
        Message responceMessage = new Message();
        System.out.println(authToken);
        try {
            User user = userService.authenticateUserByAuthToken(authToken);
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            responceMessage.setCode(200);
            responceMessage.setMessage("User authorized by AuthToken");
        }
        catch (UserNotFoundException e) {
            responceMessage.setCode(404);
            responceMessage.setMessage("User not found");
        }
        return responceMessage;
    }
}
