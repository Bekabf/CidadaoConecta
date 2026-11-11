
package br.com.cidadaoconecta.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa uma ocorrencia urbana.
 *
 * Tabela Oracle: CC2_OCORRENCIA
 * Projeto: CidadaoConecta
 * Global Solution FIAP 2026/2
 */
public class Ocorrencia {

    private int idOcorrencia;
    private Integer idUsuario;

    private String protocolo;
    private String idPublico;

    private String titulo;
    private String descricao;

    private String cidade;
    private String uf;
    private String bairro;
    private String rua;
    private String numeroEndereco;
    private String pontoReferencia;

    private String status;
    private boolean visibilidadePublica;

    private String tituloPublico;
    private String resumoPublico;

    private LocalDateTime dataCadastro;
    private LocalDateTime dataAtualizacao;

    private List<Categoria> categorias;

    public Ocorrencia() {
        this.status = "ABERTA";
        this.visibilidadePublica = false;
        this.categorias = new ArrayList<>();
    }

    public Ocorrencia(
            String titulo,
            String descricao,
            String cidade,
            String uf,
            String bairro,
            String rua,
            String numeroEndereco,
            String pontoReferencia) {

        this();

        this.titulo = titulo;
        this.descricao = descricao;
        this.cidade = cidade;
        this.uf = uf;
        this.bairro = bairro;
        this.rua = rua;
        this.numeroEndereco = numeroEndereco;
        this.pontoReferencia = pontoReferencia;
    }

    public int getIdOcorrencia() {
        return idOcorrencia;
    }

    public void setIdOcorrencia(int idOcorrencia) {
        this.idOcorrencia = idOcorrencia;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getProtocolo() {
        return protocolo;
    }

    public void setProtocolo(String protocolo) {
        this.protocolo = protocolo;
    }

    public String getIdPublico() {
        return idPublico;
    }

    public void setIdPublico(String idPublico) {
        this.idPublico = idPublico;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public String getNumeroEndereco() {
        return numeroEndereco;
    }

    public void setNumeroEndereco(String numeroEndereco) {
        this.numeroEndereco = numeroEndereco;
    }

    public String getPontoReferencia() {
        return pontoReferencia;
    }

    public void setPontoReferencia(String pontoReferencia) {
        this.pontoReferencia = pontoReferencia;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isVisibilidadePublica() {
        return visibilidadePublica;
    }

    public void setVisibilidadePublica(boolean visibilidadePublica) {
        this.visibilidadePublica = visibilidadePublica;
    }

    public String getTituloPublico() {
        return tituloPublico;
    }

    public void setTituloPublico(String tituloPublico) {
        this.tituloPublico = tituloPublico;
    }

    public String getResumoPublico() {
        return resumoPublico;
    }

    public void setResumoPublico(String resumoPublico) {
        this.resumoPublico = resumoPublico;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public List<Categoria> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<Categoria> categorias) {
        this.categorias = categorias == null
                ? new ArrayList<>()
                : new ArrayList<>(categorias);
    }

    public void adicionarCategoria(Categoria categoria) {
        if (categoria != null) {
            this.categorias.add(categoria);
        }
    }

    @Override
    public String toString() {
        return "Ocorrencia{" +
                "idOcorrencia=" + idOcorrencia +
                ", protocolo='" + protocolo + '\'' +
                ", titulo='" + titulo + '\'' +
                ", cidade='" + cidade + '\'' +
                ", bairro='" + bairro + '\'' +
                ", status='" + status + '\'' +
                ", categorias=" + categorias.size() +
                '}';
    }
}