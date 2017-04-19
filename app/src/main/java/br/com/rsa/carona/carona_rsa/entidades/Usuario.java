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
    private String foto;
    private String extFoto;
    private int ativo;
    private int id;
    private String dataRegistro;
    private String senha;
    private int idCaronaSolicitada;
    private boolean editado;


    public Usuario(String nome,String sobrenome,String matricula,String email,String telefone,String sexo,Boolean cnh){
        this.nome=nome;
        this.sobrenome=sobrenome;
        this.matricula=matricula;
        this.email=email;
        this.telefone=telefone;
        this.sexo=sexo;
        this.cnh=cnh;
        this.editado=false;
    }

    public Usuario(String matricula,String senha){

        this.matricula=matricula;
        this.senha=senha;
        this.editado=false;

    }
    public Usuario(int id, String nome){
        this.id=id;
        this.nome=nome;
        this.editado=false;

    }
    public Usuario(Integer id){

        this.id=id;
        this.editado=false;

    }

    public Usuario(String nome){

        this.nome=nome;
        this.editado=false;

    }


    public void setIdCaronaSolicitada(int id){
        this.idCaronaSolicitada=id;
    }

    public int getIdCaronaSolicitada(){
        return this.idCaronaSolicitada;
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

    public void setEditado(boolean editado){
        this.editado=editado;
    }

    public boolean getEdicao(){
        return this.editado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getExtFoto() {
        return extFoto;
    }

    public void setExtFoto(String extFoto) {
        this.extFoto = extFoto;
    }
}
