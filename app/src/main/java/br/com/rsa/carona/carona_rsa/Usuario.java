package br.com.rsa.carona.carona_rsa;

import android.media.Image;

import java.lang.reflect.Array;
import java.util.List;

public class Usuario {
    private String nome;
    private String sobrenome;
    private String email;
    private String senha;
    private Image foto;

    public Usuario(String nome, String sobrenome, String email, String senha) {
        setNome(nome);
        setSobrenome(sobrenome);
        setEmail(email);
        setSenha(senha);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public void setSobrenome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Image getFoto() {
        return foto;
    }

    public void setFoto(Image foto) {
        this.foto = foto;
    }

}
