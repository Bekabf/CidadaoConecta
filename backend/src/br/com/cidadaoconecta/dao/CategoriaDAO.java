
package br.com.cidadaoconecta.dao;

import br.com.cidadaoconecta.config.ConexaoOracle;
import br.com.cidadaoconecta.model.Categoria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

/**
 * DAO responsavel pelas operacoes JDBC
 * da tabela CC2_CATEGORIA.
 *
 * Global Solution FIAP 2026/2
 * Projeto: CidadaoConecta
 */
public class CategoriaDAO {

    // CREATE - Cadastrar categoria
    public void inserir(Categoria categoria) throws SQLException {

        String sql = """
            INSERT INTO CC2_CATEGORIA
                (CODIGO, NOME, DESCRICAO, ATIVO)
            VALUES (?, ?, ?, ?)
            """;

        try (Connection conexao = ConexaoOracle.conectar();
             PreparedStatement comando = conexao.prepareStatement(
                     sql, new String[]{"ID_CATEGORIA"})) {

            comando.setString(1, categoria.getCodigo());
            comando.setString(2, categoria.getNome());
            comando.setString(3, categoria.getDescricao());
            comando.setInt(4, categoria.isAtivo() ? 1 : 0);

            int linhas = comando.executeUpdate();

            if (linhas > 0) {
                try (ResultSet chaves = comando.getGeneratedKeys()) {
                    if (chaves.next()) {
                        categoria.setIdCategoria(chaves.getInt(1));
                    }
                }
            }
        }
    }

    // READ - Listar todas as categorias
    public List<Categoria> listarTodas() throws SQLException {

        String sql = """
            SELECT
                ID_CATEGORIA,
                CODIGO,
                NOME,
                DESCRICAO,
                ATIVO
            FROM CC2_CATEGORIA
            ORDER BY ID_CATEGORIA
            """;

        List<Categoria> categorias = new ArrayList<>();

        try (Connection conexao = ConexaoOracle.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql);
             ResultSet resultado = comando.executeQuery()) {

            while (resultado.next()) {
                categorias.add(mapearCategoria(resultado));
            }
        }

        return categorias;
    }

    // READ - Buscar categoria pelo ID
    public Categoria buscarPorId(int id) throws SQLException {

        String sql = """
            SELECT
                ID_CATEGORIA,
                CODIGO,
                NOME,
                DESCRICAO,
                ATIVO
            FROM CC2_CATEGORIA
            WHERE ID_CATEGORIA = ?
            """;

        try (Connection conexao = ConexaoOracle.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setInt(1, id);

            try (ResultSet resultado = comando.executeQuery()) {
                if (resultado.next()) {
                    return mapearCategoria(resultado);
                }
            }
        }

        return null;
    }

    // UPDATE - Atualizar categoria pelo ID
    public boolean atualizar(Categoria categoria) throws SQLException {

        String sql = """
            UPDATE CC2_CATEGORIA
            SET
                CODIGO = ?,
                NOME = ?,
                DESCRICAO = ?,
                ATIVO = ?
            WHERE ID_CATEGORIA = ?
            """;

        try (Connection conexao = ConexaoOracle.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setString(1, categoria.getCodigo());
            comando.setString(2, categoria.getNome());
            comando.setString(3, categoria.getDescricao());
            comando.setInt(4, categoria.isAtivo() ? 1 : 0);
            comando.setInt(5, categoria.getIdCategoria());

            return comando.executeUpdate() > 0;
        }
    }

    // DELETE - Excluir categoria pelo ID
    public boolean excluir(int id) throws SQLException {

        String sql = """
            DELETE FROM CC2_CATEGORIA
            WHERE ID_CATEGORIA = ?
            """;

        try (Connection conexao = ConexaoOracle.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setInt(1, id);

            return comando.executeUpdate() > 0;
        }
    }

    // Converte ResultSet em objeto Categoria
    private Categoria mapearCategoria(ResultSet resultado)
            throws SQLException {

        return new Categoria(
            resultado.getInt("ID_CATEGORIA"),
            resultado.getString("CODIGO"),
            resultado.getString("NOME"),
            resultado.getString("DESCRICAO"),
            resultado.getInt("ATIVO") == 1
        );
    }
}