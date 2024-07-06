package com.example.tuempleoblind;

public class AppState {
    private static AppState instance;
    private boolean modoEdicionActivo;
    private boolean isActiveAssistant;

    private AppState() {
        // Constructor privado para prevenir instanciación
    }

    public static synchronized AppState getInstance() {
        if (instance == null) {
            instance = new AppState();
        }
        return instance;
    }

    public boolean isModoEdicionActivo() {
        return modoEdicionActivo;
    }

    public void setModoEdicionActivo(boolean modoEdicionActivo) {
        this.modoEdicionActivo = modoEdicionActivo;
    }

    public boolean isActiveAssistant() {
        return isActiveAssistant;
    }

    public void setActiveAssistant(boolean activeAssistant) {
        this.isActiveAssistant = activeAssistant;
    }
}

