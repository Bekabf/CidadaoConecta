
package br.com.cidadaoconecta.dao;

import br.com.cidadaoconecta.config.ConexaoOracle;
import br.com.cidadaoconecta.model.Categoria;
import br.com.cidadaoconecta.model.Ocorrencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * DAO da tabela CC2_OCORRENCIA.
 *
 * Operacoes CRUD com JDBC e Oracle.
 * Inclui relacionamento N:N com categorias.
 *
 * Global Solution FIAP 2026/2
 * Projeto: CidadaoConecta
 */
public class OcorrenciaDAO {

    // CREATE - Cadastrar ocorrencia
    public void inserir(Ocorrencia ocorrencia)
            throws SQLException {

        String sql = """
            INSERT INTO CC2_OCORRENCIA (
                ID_USUARIO,
                PROTOCOLO,
                ID_PUBLICO,
                TITULO,
                DESCRICAO,
                CIDADE,
                UF,
                BAIRRO,
                RUA,
                NUMERO_ENDERECO,
                PONTO_REFERENCIA,
                STATUS,
                VISIBILIDADE_PUBLICA,
                TITULO_PUBLICO,
                RESUMO_PUBLICO
            ) VALUES (
                ?, ?, ?, ?, ?, ?, ?, ?,
                ?, ?, ?, ?, ?, ?, ?
            )
            """;

        try (Connection conexao = ConexaoOracle.conectar()) {

            conexao.setAutoCommit(false);

            try {

                if (ocorrencia.getProtocolo() == null ||
                    ocorrencia.getProtocolo().isBlank()) {

                    ocorrencia.setProtocolo(
                        "CC-" + UUID.randomUUID()
                            .toString()
                            .toUpperCase()
                    );
                }

                if (ocorrencia.getIdPublico() == null ||
                    ocorrencia.getIdPublico().isBlank()) {

                    ocorrencia.setIdPublico(
                        "PUB-" + UUID.randomUUID()
                            .toString()
                            .toUpperCase()
                    );
                }

                try (PreparedStatement comando =
                        conexao.prepareStatement(
                            sql,
                            new String[]{"ID_OCORRENCIA"}
                        )) {

                    if (ocorrencia.getIdUsuario() == null) {
                        comando.setNull(1, Types.NUMERIC);
                    } else {
                        comando.setInt(
                            1, ocorrencia.getIdUsuario()
                        );
                    }

                    comando.setString(
                        2, ocorrencia.getProtocolo()
                    );
                    comando.setString(
                        3, ocorrencia.getIdPublico()
                    );
                    comando.setString(
                        4, ocorrencia.getTitulo()
                    );
                    comando.setString(
                        5, ocorrencia.getDescricao()
                    );
                    comando.setString(
                        6, ocorrencia.getCidade()
                    );
                    comando.setString(
                        7, ocorrencia.getUf()
                    );
                    comando.setString(
                        8, ocorrencia.getBairro()
                    );
                    comando.setString(
                        9, ocorrencia.getRua()
                    );
                    comando.setString(
                        10, ocorrencia.getNumeroEndereco()
                    );
                    comando.setString(
                        11, ocorrencia.getPontoReferencia()
                    );
                    comando.setString(
                        12, ocorrencia.getStatus()
                    );
                    comando.setInt(
                        13,
                        ocorrencia.isVisibilidadePublica()
                            ? 1 : 0
                    );
                    comando.setString(
                        14, ocorrencia.getTituloPublico()
                    );
                    comando.setString(
                        15, ocorrencia.getResumoPublico()
                    );

                    int linhas = comando.executeUpdate();

                    if (linhas != 1) {
                        throw new SQLException(
                            "Nao foi possivel inserir a ocorrencia."
                        );
                    }

                    try (ResultSet chaves =
                            comando.getGeneratedKeys()) {

                        if (!chaves.next()) {
                            throw new SQLException(
                                "O Oracle nao retornou o ID."
                            );
                        }

                        ocorrencia.setIdOcorrencia(
                            chaves.getInt(1)
                        );
                    }
                }

                salvarCategorias(
                    conexao,
                    ocorrencia.getIdOcorrencia(),
                    ocorrencia.getCategorias()
                );

                registrarCriacao(
                    conexao,
                    ocorrencia.getIdOcorrencia()
                );

                conexao.commit();

            } catch (SQLException | RuntimeException erro) {
                try {
                    conexao.rollback();
                } catch (SQLException falhaRollback) {
                    erro.addSuppressed(falhaRollback);
                }
                throw erro;
            }
        }
    }

    // READ - Listar todas as ocorrencias
    public List<Ocorrencia> listarTodas()
            throws SQLException {

        String sql = """
            SELECT *
            FROM CC2_OCORRENCIA
            ORDER BY DATA_CADASTRO DESC,
                     ID_OCORRENCIA DESC
            """;

        List<Ocorrencia> lista = new ArrayList<>();

        try (Connection conexao = ConexaoOracle.conectar();
             PreparedStatement comando =
                 conexao.prepareStatement(sql);
             ResultSet resultado = comando.executeQuery()) {

            while (resultado.next()) {

                Ocorrencia ocorrencia =
                    mapearOcorrencia(resultado);

                ocorrencia.setCategorias(
                    carregarCategorias(
                        conexao,
                        ocorrencia.getIdOcorrencia()
                    )
                );

                lista.add(ocorrencia);
            }
        }

        return lista;
    }

    // READ - Buscar ocorrencia pelo ID
    public Ocorrencia buscarPorId(int id)
            throws SQLException {

        String sql = """
            SELECT *
            FROM CC2_OCORRENCIA
            WHERE ID_OCORRENCIA = ?
            """;

        try (Connection conexao = ConexaoOracle.conectar();
             PreparedStatement comando =
                 conexao.prepareStatement(sql)) {

            comando.setInt(1, id);

            try (ResultSet resultado =
                    comando.executeQuery()) {

                if (resultado.next()) {

                    Ocorrencia ocorrencia =
                        mapearOcorrencia(resultado);

                    ocorrencia.setCategorias(
                        carregarCategorias(conexao, id)
                    );

                    return ocorrencia;
                }
            }
        }

        return null;
    }

    // READ - Buscar ocorrencia pelo protocolo
    public Ocorrencia buscarPorProtocolo(String protocolo)
            throws SQLException {

        String sql = """
            SELECT *
            FROM CC2_OCORRENCIA
            WHERE PROTOCOLO = ?
            """;

        try (Connection conexao = ConexaoOracle.conectar();
             PreparedStatement comando =
                 conexao.prepareStatement(sql)) {

            comando.setString(1, protocolo);

            try (ResultSet resultado =
                    comando.executeQuery()) {

                if (resultado.next()) {

                    Ocorrencia ocorrencia =
                        mapearOcorrencia(resultado);

                    ocorrencia.setCategorias(
                        carregarCategorias(
                            conexao,
                            ocorrencia.getIdOcorrencia()
                        )
                    );

                    return ocorrencia;
                }
            }
        }

        return null;
    }

    // UPDATE - Atualizar dados da ocorrencia
    public boolean atualizar(Ocorrencia ocorrencia)
            throws SQLException {

        String sql = """
            UPDATE CC2_OCORRENCIA
            SET TITULO = ?,
                DESCRICAO = ?,
                CIDADE = ?,
                UF = ?,
                BAIRRO = ?,
                RUA = ?,
                NUMERO_ENDERECO = ?,
                PONTO_REFERENCIA = ?,
                DATA_ATUALIZACAO = LOCALTIMESTAMP
            WHERE ID_OCORRENCIA = ?
            """;

        try (Connection conexao = ConexaoOracle.conectar();
             PreparedStatement comando =
                 conexao.prepareStatement(sql)) {

            comando.setString(1, ocorrencia.getTitulo());
            comando.setString(2, ocorrencia.getDescricao());
            comando.setString(3, ocorrencia.getCidade());
            comando.setString(4, ocorrencia.getUf());
            comando.setString(5, ocorrencia.getBairro());
            comando.setString(6, ocorrencia.getRua());
            comando.setString(
                7, ocorrencia.getNumeroEndereco()
            );
            comando.setString(
                8, ocorrencia.getPontoReferencia()
            );
            comando.setInt(
                9, ocorrencia.getIdOcorrencia()
            );

            return comando.executeUpdate() > 0;
        }
    }

    // UPDATE - Atualizar categorias associadas
    public void atualizarCategorias(
            int idOcorrencia,
            List<Categoria> categorias)
            throws SQLException {

        try (Connection conexao = ConexaoOracle.conectar()) {

            conexao.setAutoCommit(false);

            try {

                String excluirSql = """
                    DELETE FROM CC2_OCORRENCIA_CATEGORIA
                    WHERE ID_OCORRENCIA = ?
                    """;

                try (PreparedStatement comando =
                        conexao.prepareStatement(excluirSql)) {

                    comando.setInt(1, idOcorrencia);
                    comando.executeUpdate();
                }

                salvarCategorias(
                    conexao,
                    idOcorrencia,
                    categorias
                );

                conexao.commit();

            } catch (SQLException | RuntimeException erro) {

                try {
                    conexao.rollback();
                } catch (SQLException falhaRollback) {
                    erro.addSuppressed(falhaRollback);
                }

                throw erro;
            }
        }
    }

    // DELETE - Excluir ocorrencia pelo ID
    public boolean excluir(int id) throws SQLException {

        try (Connection conexao = ConexaoOracle.conectar()) {

            conexao.setAutoCommit(false);

            try {

                // Remove primeiro os relacionamentos N:N.
                String sqlCategorias = """
                    DELETE FROM CC2_OCORRENCIA_CATEGORIA
                    WHERE ID_OCORRENCIA = ?
                    """;

                try (PreparedStatement comando =
                        conexao.prepareStatement(sqlCategorias)) {

                    comando.setInt(1, id);
                    comando.executeUpdate();
                }

                // Remove o historico da ocorrencia.
                String sqlHistorico = """
                    DELETE FROM CC2_HISTORICO_STATUS
                    WHERE ID_OCORRENCIA = ?
                    """;

                try (PreparedStatement comando =
                        conexao.prepareStatement(sqlHistorico)) {

                    comando.setInt(1, id);
                    comando.executeUpdate();
                }

                String sqlOcorrencia = """
                    DELETE FROM CC2_OCORRENCIA
                    WHERE ID_OCORRENCIA = ?
                    """;

                boolean excluiu;

                try (PreparedStatement comando =
                        conexao.prepareStatement(sqlOcorrencia)) {

                    comando.setInt(1, id);

                    excluiu = comando.executeUpdate() > 0;
                }

                conexao.commit();

                return excluiu;

            } catch (SQLException | RuntimeException erro) {

                try {
                    conexao.rollback();
                } catch (SQLException falhaRollback) {
                    erro.addSuppressed(falhaRollback);
                }

                throw erro;
            }
        }
    }

    // Salvar categorias de uma ocorrencia
    private void salvarCategorias(
            Connection conexao,
            int idOcorrencia,
            List<Categoria> categorias)
            throws SQLException {

        if (categorias == null || categorias.isEmpty()) {
            return;
        }

        String sql = """
            INSERT INTO CC2_OCORRENCIA_CATEGORIA
                (ID_OCORRENCIA, ID_CATEGORIA)
            VALUES (?, ?)
            """;

        try (PreparedStatement comando =
                conexao.prepareStatement(sql)) {

            for (Categoria categoria : categorias) {

                if (categoria == null ||
                    categoria.getIdCategoria() <= 0) {

                    throw new SQLException(
                        "Categoria sem ID valido."
                    );
                }

                comando.setInt(1, idOcorrencia);
                comando.setInt(
                    2, categoria.getIdCategoria()
                );

                comando.addBatch();
            }

            comando.executeBatch();
        }
    }

    // Registrar evento inicial da linha do tempo
    private void registrarCriacao(
            Connection conexao,
            int idOcorrencia)
            throws SQLException {

        String sql = """
            INSERT INTO CC2_HISTORICO_STATUS (
                ID_OCORRENCIA,
                STATUS_ANTERIOR,
                STATUS_NOVO,
                TIPO_EVENTO,
                DESCRICAO
            ) VALUES (?, NULL, 'ABERTA', 'CRIACAO', ?)
            """;

        try (PreparedStatement comando =
                conexao.prepareStatement(sql)) {

            comando.setInt(1, idOcorrencia);
            comando.setString(
                2, "Ocorrencia cadastrada no sistema."
            );

            comando.executeUpdate();
        }
    }

    // Carregar categorias da ocorrencia
    private List<Categoria> carregarCategorias(
            Connection conexao,
            int idOcorrencia)
            throws SQLException {

        String sql = """
            SELECT
                C.ID_CATEGORIA,
                C.CODIGO,
                C.NOME,
                C.DESCRICAO,
                C.ATIVO
            FROM CC2_CATEGORIA C
            JOIN CC2_OCORRENCIA_CATEGORIA OC
                ON OC.ID_CATEGORIA = C.ID_CATEGORIA
            WHERE OC.ID_OCORRENCIA = ?
            ORDER BY C.NOME
            """;

        List<Categoria> categorias = new ArrayList<>();

        try (PreparedStatement comando =
                conexao.prepareStatement(sql)) {

            comando.setInt(1, idOcorrencia);

            try (ResultSet resultado =
                    comando.executeQuery()) {

                while (resultado.next()) {

                    Categoria categoria = new Categoria(
                        resultado.getInt("ID_CATEGORIA"),
                        resultado.getString("CODIGO"),
                        resultado.getString("NOME"),
                        resultado.getString("DESCRICAO"),
                        resultado.getInt("ATIVO") == 1
                    );

                    categorias.add(categoria);
                }
            }
        }

        return categorias;
    }

    // Converter resultado SQL para objeto Java
    private Ocorrencia mapearOcorrencia(ResultSet resultado)
            throws SQLException {

        Ocorrencia ocorrencia = new Ocorrencia();

        ocorrencia.setIdOcorrencia(
            resultado.getInt("ID_OCORRENCIA")
        );

        int idUsuario = resultado.getInt("ID_USUARIO");

        ocorrencia.setIdUsuario(
            resultado.wasNull() ? null : idUsuario
        );

        ocorrencia.setProtocolo(
            resultado.getString("PROTOCOLO")
        );
        ocorrencia.setIdPublico(
            resultado.getString("ID_PUBLICO")
        );
        ocorrencia.setTitulo(
            resultado.getString("TITULO")
        );
        ocorrencia.setDescricao(
            resultado.getString("DESCRICAO")
        );
        ocorrencia.setCidade(
            resultado.getString("CIDADE")
        );
        ocorrencia.setUf(
            resultado.getString("UF")
        );
        ocorrencia.setBairro(
            resultado.getString("BAIRRO")
        );
        ocorrencia.setRua(
            resultado.getString("RUA")
        );
        ocorrencia.setNumeroEndereco(
            resultado.getString("NUMERO_ENDERECO")
        );
        ocorrencia.setPontoReferencia(
            resultado.getString("PONTO_REFERENCIA")
        );
        ocorrencia.setStatus(
            resultado.getString("STATUS")
        );

        ocorrencia.setVisibilidadePublica(
            resultado.getInt("VISIBILIDADE_PUBLICA") == 1
        );

        ocorrencia.setTituloPublico(
            resultado.getString("TITULO_PUBLICO")
        );
        ocorrencia.setResumoPublico(
            resultado.getString("RESUMO_PUBLICO")
        );

        if (resultado.getTimestamp("DATA_CADASTRO")
                != null) {

            ocorrencia.setDataCadastro(
                resultado.getTimestamp(
                    "DATA_CADASTRO"
                ).toLocalDateTime()
            );
        }

        if (resultado.getTimestamp("DATA_ATUALIZACAO")
                != null) {

            ocorrencia.setDataAtualizacao(
                resultado.getTimestamp(
                    "DATA_ATUALIZACAO"
                ).toLocalDateTime()
            );
        }

        return ocorrencia;
    }
}