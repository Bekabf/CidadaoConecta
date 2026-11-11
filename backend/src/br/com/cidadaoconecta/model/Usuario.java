
package br.com.cidadaoconecta.model;

import java.time.LocalDateTime;

/**
 * Representa um usuario do CidadaoConecta.
 *
 * Tabela Oracle: CC2_USUARIO
 * Global Solution FIAP 2026/2
 */
public class Usuario {

    // Atributos privados - encapsulamento
    private int idUsuario;
    private String nome;
    private String email;
    private String senhaHash;
    private String perfil;
    private boolean ativo;
    private LocalDateTime dataCadastro;

    // Construtor vazio
    public Usuario() {
        this.perfil = "CIDADAO";
        this.ativo = true;
    }

    // Construtor para cadastro
    public Usuario(String nome, String email, String senhaHash, String perfil) {
        this.nome = nome;
        this.email = email;
        this.senhaHash = senhaHash;
        this.perfil = perfil;
        this.ativo = true;
    }

    // Construtor para dados recuperados do Oracle
    public Usuario(
            int idUsuario,
            String nome,
            String email,
            String senhaHash,
            String perfil,
            boolean ativo,
            LocalDateTime dataCadastro) {

        this.idUsuario = idUsuario;
        this.nome = nome;
        this.email = email;
        this.senhaHash = senhaHash;
        this.perfil = perfil;
        this.ativo = ativo;
        this.dataCadastro = dataCadastro;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", perfil='" + perfil + '\'' +
                ", ativo=" + ativo +
                ", dataCadastro=" + dataCadastro +
                '}';
    }
}