package com.diplom.serverboot.entity;

public class User {
    private String identifier;
    private String password;
    private String authToken;
    private String hardwareSerialNumber;
    private String embeddedFaceVector;
    private EncryptionData encryptionData;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getHardwareSerialNumber() {
        return hardwareSerialNumber;
    }

    public void setHardwareSerialNumber(String hardwareSerialNumber) {
        this.hardwareSerialNumber = hardwareSerialNumber;
    }

    public String getEmbeddedFaceVector() {
        return embeddedFaceVector;
    }

    public void setEmbeddedFaceVector(String embeddedFaceVector) {
        this.embeddedFaceVector = embeddedFaceVector;
    }

    public EncryptionData getEncryptionData() {
        return encryptionData;
    }

    public void setEncryptionData(EncryptionData encryptionData) {
        this.encryptionData = encryptionData;
    }

    @Override
    public String toString() {
        return "User{" +
                "identifier='" + identifier + '\'' +
                ", password='" + password + '\'' +
                ", authToken='" + authToken + '\'' +
                ", motherboardSerialNumber='" + hardwareSerialNumber + '\'' +
                ", embeddedFaceVector='" + embeddedFaceVector + '\'' +
                ", encryptionData=" + encryptionData +
                '}';
    }
}
