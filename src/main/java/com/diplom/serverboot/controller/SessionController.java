package com.diplom.serverboot.controller;

import com.diplom.serverboot.dto.AuthMessage;
import com.diplom.serverboot.dto.Message;
import com.diplom.serverboot.entity.User;
import com.diplom.serverboot.exception.BadCreditionalsException;
import com.diplom.serverboot.exception.UserNotFoundException;
import com.diplom.serverboot.service.CryptoService;
import com.diplom.serverboot.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyPair;
import java.util.Base64;
import java.util.stream.Collectors;

@RestController
public class SessionController {
    private static final String API_FILE_PATH = "src/main/resources/api";
    @Autowired
    private UserService userService;
    @Autowired
    private CryptoService cryptoService;

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

        session.setAttribute("clientPublicKey", cryptoService.createPublicKeyFromBytes(
                Base64.getDecoder().decode(message.getMessage())));

        KeyPair serverKeys = cryptoService.generateSessionKeys();
        session.setAttribute("serverPrivateKey", serverKeys.getPrivate());
        session.setAttribute("serverPublicKey", serverKeys.getPublic());

        message.setCode(100);
        message.setMessage(Base64.getEncoder().encodeToString(serverKeys.getPublic().getEncoded()));

        return message;
    }

    @PostMapping("/login")
    public Message loginByCreditionals(HttpServletRequest request, @RequestBody AuthMessage message) {
        Message responceMessage = new Message();

        HttpSession session = request.getSession();
        Key serverPrivateKey = (Key) session.getAttribute("serverPrivateKey");

        if (serverPrivateKey == null) {
            responceMessage.setCode(409);
            responceMessage.setMessage("Session wasn't open");
            return responceMessage;
        }

        String decryptedIdentifier = new String(
                cryptoService.sessionDecryption(message.getIdentifier(), serverPrivateKey),
                StandardCharsets.UTF_8);
        String decryptedPassword = new String(
                cryptoService.sessionDecryption(message.getPassword(), serverPrivateKey),
                StandardCharsets.UTF_8);

        try {
            User user = userService.authenticateUserByCreditionals(decryptedIdentifier, decryptedPassword);

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

    @PutMapping("/login")
    public Message loginByAuthToken(HttpServletRequest request, @RequestBody Message authToken) {
        Message responceMessage = new Message();

        HttpSession session = request.getSession();
        Key serverPrivateKey = (Key) session.getAttribute("serverPrivateKey");

        if (serverPrivateKey == null) {
            responceMessage.setCode(409);
            responceMessage.setMessage("Session wasn't open");
            return responceMessage;
        }

        String decryptedAuthToken = new String(
                cryptoService.sessionDecryption(authToken.getMessage(), serverPrivateKey),
                StandardCharsets.UTF_8);

        try {
            User user = userService.authenticateUserByAuthToken(authToken.getMessage());

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
