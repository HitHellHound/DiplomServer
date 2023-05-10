package com.diplom.serverboot.dto;

public class ScriptMessage extends Message {
    private String scriptPublicKey;

    public String getScriptPublicKey() {
        return scriptPublicKey;
    }

    public void setScriptPublicKey(String scriptPublicKey) {
        this.scriptPublicKey = scriptPublicKey;
    }
}
