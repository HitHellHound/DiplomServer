package com.diplom.serverboot.service;

import com.diplom.serverboot.entity.User;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ScriptService {
    private static final int LENGTH_OF_AUTH_TOKEN = 256;
    public static final String FACE_REGISTRATION_FILE_PATH = "src/main/resources/pyScripts/faceRegistration.py";
    public static final String FACE_RECOGNITION_FILE_PATH = "src/main/resources/pyScripts/faceRecognition.py";

    public String readFile(String path) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String script = reader.lines().collect(Collectors.joining("\n"));
            return script;
        } catch (Exception exception) {
            return null;
        }
    }

    public String createAuthToken(String hardwareSerialNumber, String script) {
        return script.substring(0, LENGTH_OF_AUTH_TOKEN - hardwareSerialNumber.length()) + hardwareSerialNumber;
    }

    public boolean verifyPerson(User user, List<String> vectors) {
        //TODO verification
        return true;
    }
}
