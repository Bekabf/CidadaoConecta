
package br.com.cidadaoconecta.model;

/**
 * Representa uma categoria de problema urbano.
 *
 * Tabela Oracle: CC2_CATEGORIA
 *
 * Projeto: CidadaoConecta
 * Global Solution FIAP 2026/2
 */
public class Categoria {

    // Atributos privados (encapsulamento)
    private int idCategoria;
    private String codigo;
    private String nome;
    private String descricao;
    private boolean ativo;

    // Construtor vazio
    public Categoria() {
        this.ativo = true;
    }

    // Construtor com os dados principais
    public Categoria(String codigo, String nome, String descricao) {
        this.codigo = codigo;
        this.nome = nome;
        this.descricao = descricao;
        this.ativo = true;
    }

    // Construtor para objetos recuperados do Oracle
    public Categoria(
            int idCategoria,
            String codigo,
            String nome,
            String descricao,
            boolean ativo) {

        this.idCategoria = idCategoria;
        this.codigo = codigo;
        this.nome = nome;
        this.descricao = descricao;
        this.ativo = ativo;
    }

    // Getters e setters

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public String toString() {
        return "Categoria{" +
                "idCategoria=" + idCategoria +
                ", codigo='" + codigo + '\'' +
                ", nome='" + nome + '\'' +
                ", descricao='" + descricao + '\'' +
                ", ativo=" + ativo +
                '}';
    }
}