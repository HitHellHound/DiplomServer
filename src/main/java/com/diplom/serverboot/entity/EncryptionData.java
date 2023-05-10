package com.diplom.serverboot.entity;

public class EncryptionData {
    private String pythonScriptPrivateKey;
    private String pythonScriptPublicKey;

    public String getPythonScriptPrivateKey() {
        return pythonScriptPrivateKey;
    }

    public void setPythonScriptPrivateKey(String pythonScriptPrivateKey) {
        this.pythonScriptPrivateKey = pythonScriptPrivateKey;
    }

    public String getPythonScriptPublicKey() {
        return pythonScriptPublicKey;
    }

    public void setPythonScriptPublicKey(String pythonScriptPublicKey) {
        this.pythonScriptPublicKey = pythonScriptPublicKey;
    }
}
