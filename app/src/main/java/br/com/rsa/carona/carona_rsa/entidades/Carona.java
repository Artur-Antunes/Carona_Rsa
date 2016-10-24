package br.com.rsa.carona.carona_rsa.entidades;

/**
 * Created by josehelder on 23/10/2016.
 */
public class Carona {
    private String origem;
    private String destino;
    private String ponto;
    private String horario;
    private String tipoVeiculo;
    private String restricao;
    private int vagas;
    private int status;
    private int ativo;
    private int id;
    private String dataCriacao;

    public Carona(String origem, String destino, String horario, String tipoVeiculo, String restricao, int vagas, String ponto){
        this.origem=origem;
        this.destino=destino;
        this.horario=horario;
        this.tipoVeiculo=tipoVeiculo;
        this.ponto=ponto;
        this.restricao=restricao;
        this.vagas=vagas;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getPonto() {
        return ponto;
    }

    public void setPonto(String ponto) {
        this.ponto = ponto;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String getTipoVeiculo() {
        return tipoVeiculo;
    }

    public void setTipoVeiculo(String tipoVeiculo) {
        this.tipoVeiculo = tipoVeiculo;
    }

    public String getRestricao() {
        return restricao;
    }

    public void setRestricao(String restricao) {
        this.restricao = restricao;
    }

    public int getVagas() {
        return vagas;
    }

    public void setVagas(int vagas) {
        this.vagas = vagas;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getAtivo() {
        return ativo;
    }

    public void setAtivo(int ativo) {
        this.ativo = ativo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(String dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
}
