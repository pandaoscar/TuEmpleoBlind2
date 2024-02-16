package com.example.tuempleoblind.model;

public class User {
    String Contraeña, Usuario;
    public User(){}

    public User(String contraeña, String usuario) {
        Contraeña = contraeña;
        Usuario = usuario;
    }

    public String getContraeña() {
        return Contraeña;
    }

    public void setContraeña(String contraeña) {
        Contraeña = contraeña;
    }

    public String getUsuario() {
        return Usuario;
    }

    public void setUsuario(String usuario) {
        Usuario = usuario;
    }
}
