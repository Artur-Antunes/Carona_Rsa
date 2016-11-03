package br.com.rsa.carona.carona_rsa.entidades;

import android.graphics.Bitmap;

/**
 * Created by josehelder on 20/10/2016.
 */
public class Usuario {
    private String nome;
    private String sobrenome;
    private String matricula;
    private String email;
    private String telefone;
    private String sexo;
    private boolean cnh;
    private Bitmap foto;
    private int ativo;
    private int id;
    private String dataRegistro;
    private String senha;

    public Usuario(String nome,String sobrenome,String matricula,String email,String telefone,String sexo,Boolean cnh){
        this.nome=nome;
        this.sobrenome=sobrenome;
        this.matricula=matricula;
        this.email=email;
        this.telefone=telefone;
        this.sexo=sexo;
        this.cnh=cnh;
    }

    public Usuario(String matricula,String senha){

        this.matricula=matricula;
        this.senha=senha;
    }

    public Usuario(Integer id){

        this.id=id;
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

    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String  sexo) {
        this.sexo = sexo;
    }

    public boolean isCnh() {
        return cnh;
    }

    public void setChn(boolean chn) {
        this.cnh = chn;
    }

    public Bitmap getFoto() {
        return foto;
    }

    public void setFoto(Bitmap foto) {
        this.foto = foto;
    }

    public int getAtivo() {
        return ativo;
    }

    public void setAtivo(int ativo) {
        this.ativo = ativo;
    }

    public String getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(String dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }


    public int getId() {
        return id;
    }




    public void setId(int id) {
        this.id = id;
    }
}
