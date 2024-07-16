package com.example.tuempleoblind;

import java.util.ArrayList;
import java.util.List;

public class AppState {
    private static AppState instance;
    private boolean modoEdicionActivo;
    private boolean isActiveAssistant;
    private boolean isHelpGoogleActive;
    private final List<Observer> observers = new ArrayList<>();
    private final List<TTSObserver> ttsObservers = new ArrayList<>();


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
        notifyObservers();
    }

    public boolean isHelpGoogleActive() {
        return isHelpGoogleActive;
    }

    public void setHelpGoogleActive(boolean helpGoogleActive) {
        isHelpGoogleActive = helpGoogleActive;
    }
    public void addTTSObserver(TTSObserver observer) {
        ttsObservers.add(observer);
    }

    public void removeTTSObserver(TTSObserver observer) {
        ttsObservers.remove(observer);
    }

    protected void notifyTTSObservers() {
        for (TTSObserver observer : ttsObservers) {
            observer.onTTSCompleted();
        }
    }

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    private void notifyObservers() {
        for (Observer observer : observers) {
            observer.onActiveAssistantChanged(isActiveAssistant);
        }
    }
    public interface Observer {
        void onActiveAssistantChanged(boolean isActive);
    }
    public interface TTSObserver {
        void onTTSCompleted();
    }

}

