package com.diplom.serverboot.controller;

import com.diplom.serverboot.dto.Message;
import com.diplom.serverboot.dto.RecognitionMessage;
import com.diplom.serverboot.dto.ScriptMessage;
import com.diplom.serverboot.entity.User;
import com.diplom.serverboot.service.AuthEventService;
import com.diplom.serverboot.service.ScriptService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.stream.Collectors;

@RestController
public class FaceRecognitionController {
    @Autowired
    private AuthEventService eventService;
    @Autowired
    private ScriptService scriptService;

    @GetMapping("/registration")
    public ScriptMessage startFaceRegistration(HttpServletRequest request, @RequestParam String hardwareSerialNumber) {
        ScriptMessage responseMessage = new ScriptMessage();

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            responseMessage.setCode(401);
            responseMessage.setMessage("Unauthorized");
        } else if (user.getEmbeddedFaceVector() != null) {
            responseMessage.setCode(409);
            responseMessage.setMessage("Already face registered user");
        } else {
            String script = scriptService.readFile(ScriptService.FACE_REGISTRATION_FILE_PATH);
            if (script != null) {
                user.setHardwareSerialNumber(hardwareSerialNumber);
                responseMessage.setCode(200);
                responseMessage.setScriptPublicKey("ScriptPublicKey");
                responseMessage.setMessage(script);
            } else {
                responseMessage.setCode(500);
                responseMessage.setMessage("Server script file Error");
            }
        }
        return responseMessage;
    }

    @PostMapping("/registration")
    public Message finishFaceRegistration(HttpServletRequest request, @RequestParam String hardwareSerialNumber,
                                          @RequestBody Message message) {
        Message responseMessage = new Message();

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            responseMessage.setCode(401);
            responseMessage.setMessage("Unauthorized");
        } else if (user.getEmbeddedFaceVector() != null) {
            responseMessage.setCode(409);
            responseMessage.setMessage("Already face registered user");
        } else if (user.getHardwareSerialNumber() == null || !user.getHardwareSerialNumber().equals(hardwareSerialNumber)) {
            responseMessage.setCode(423);
            responseMessage.setMessage("Wrong Hardware Serial Number! Start face registration again");
        } else {
            //TODO check vector format
            user.setEmbeddedFaceVector(message.getMessage());
            String script = scriptService.readFile(ScriptService.FACE_RECOGNITION_FILE_PATH);
            if (script != null) {
                user.setAuthToken(scriptService.createAuthToken(hardwareSerialNumber, script));
                responseMessage.setCode(200);
                responseMessage.setMessage(script);
            } else {
                responseMessage.setCode(500);
                responseMessage.setMessage("Server script file Error");
            }
        }
        return responseMessage;
    }

    @GetMapping("/notify")
    public ScriptMessage userReadyToAuthenticate(HttpServletRequest request, @RequestParam(required = false) String needScriptHWSN) {
        ScriptMessage responseMessage = new ScriptMessage();

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            responseMessage.setCode(401);
            responseMessage.setMessage("Unauthorized");
        } else if (eventService.isNeededToAuth(user)) {
            responseMessage.setCode(100);
            responseMessage.setMessage("No needed to authenticate");
        } else {
            responseMessage.setCode(200);
            responseMessage.setScriptPublicKey(user.getEncryptionData().getPythonScriptPublicKey());
            if (needScriptHWSN != null && !needScriptHWSN.isEmpty()) {
                if (needScriptHWSN.equals(user.getHardwareSerialNumber())) {
                    String script = scriptService.readFile(ScriptService.FACE_RECOGNITION_FILE_PATH);
                    if (script != null) {
                        responseMessage.setMessage(script);
                    } else {
                        responseMessage.setCode(500);
                        responseMessage.setScriptPublicKey(null);
                        responseMessage.setMessage("Server script file Error");
                    }
                } else {
                    responseMessage.setCode(409);
                    responseMessage.setScriptPublicKey(null);
                    responseMessage.setMessage("Wrong Serial Number!");
                }
            }
        }

        return responseMessage;
    }

    @PostMapping("/recognition")
    public Message verifyPerson(HttpServletRequest request, @RequestBody RecognitionMessage message) {
        Message responseMessage = new ScriptMessage();
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            responseMessage.setCode(401);
            responseMessage.setMessage("Unauthorized");
        }
        if (!eventService.isNeededToAuth(user)) {
            responseMessage.setCode(409);
            responseMessage.setMessage("No needed to authenticate");
        } else {
            if (scriptService.verifyPerson(user, message.getEmbeddedFaceVectors())) {
                responseMessage.setCode(200);
                responseMessage.setMessage("Person verified, Good Luck!");
                eventService.removeEvent(user);
            } else {
                responseMessage.setCode(423);
                responseMessage.setMessage("Person not verified! Try again");
            }
        }

        return responseMessage;
    }
}
