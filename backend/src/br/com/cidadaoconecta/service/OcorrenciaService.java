
package br.com.cidadaoconecta.service;

import br.com.cidadaoconecta.dao.OcorrenciaDAO;
import br.com.cidadaoconecta.model.Categoria;
import br.com.cidadaoconecta.model.Ocorrencia;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Camada de regras de negocio das ocorrencias.
 *
 * Projeto: CidadaoConecta
 * Global Solution FIAP 2026/2
 */
public class OcorrenciaService {

    private final OcorrenciaDAO ocorrenciaDAO;

    public OcorrenciaService() {
        this.ocorrenciaDAO = new OcorrenciaDAO();
    }

    // CREATE - Validar e cadastrar
    public void cadastrar(Ocorrencia ocorrencia)
            throws SQLException {

        validarOcorrencia(ocorrencia);

        // Novos registros devem iniciar como ABERTA.
        ocorrencia.setStatus("ABERTA");
        ocorrencia.setVisibilidadePublica(false);
        ocorrencia.setTituloPublico(null);
        ocorrencia.setResumoPublico(null);

        ocorrenciaDAO.inserir(ocorrencia);
    }

    // READ - Listar
    public List<Ocorrencia> listar()
            throws SQLException {

        return ocorrenciaDAO.listarTodas();
    }

    // READ - Buscar por ID
    public Ocorrencia buscarPorId(int id)
            throws SQLException {

        if (id <= 0) {
            throw new IllegalArgumentException(
                "O ID da ocorrencia deve ser positivo."
            );
        }

        return ocorrenciaDAO.buscarPorId(id);
    }

    // READ - Buscar por protocolo
    public Ocorrencia buscarPorProtocolo(String protocolo)
            throws SQLException {

        if (protocolo == null || protocolo.isBlank()) {
            throw new IllegalArgumentException(
                "Informe um protocolo valido."
            );
        }

        return ocorrenciaDAO.buscarPorProtocolo(
            protocolo.trim()
        );
    }

    // UPDATE - Atualizar dados
    public boolean atualizar(Ocorrencia ocorrencia)
            throws SQLException {

        if (ocorrencia == null ||
            ocorrencia.getIdOcorrencia() <= 0) {

            throw new IllegalArgumentException(
                "Informe uma ocorrencia existente."
            );
        }

        validarOcorrencia(ocorrencia);

        return ocorrenciaDAO.atualizar(ocorrencia);
    }

    // UPDATE - Alterar categorias
    public void atualizarCategorias(
            int idOcorrencia,
            List<Categoria> categorias)
            throws SQLException {

        if (idOcorrencia <= 0) {
            throw new IllegalArgumentException(
                "ID da ocorrencia invalido."
            );
        }

        validarCategorias(categorias);

        if (ocorrenciaDAO.buscarPorId(idOcorrencia) == null) {
            throw new IllegalArgumentException(
                "Ocorrencia nao encontrada."
            );
        }

        ocorrenciaDAO.atualizarCategorias(
            idOcorrencia,
            categorias
        );
    }

    // DELETE - Excluir por ID
    public boolean excluir(int id)
            throws SQLException {

        if (id <= 0) {
            throw new IllegalArgumentException(
                "ID da ocorrencia invalido."
            );
        }

        return ocorrenciaDAO.excluir(id);
    }

    // Validacoes utilizadas no cadastro e atualizacao
    private void validarOcorrencia(Ocorrencia ocorrencia) {

        if (ocorrencia == null) {
            throw new IllegalArgumentException(
                "Os dados da ocorrencia sao obrigatorios."
            );
        }

        validarTexto(
            ocorrencia.getTitulo(),
            "Titulo",
            3,
            160
        );

        validarTexto(
            ocorrencia.getDescricao(),
            "Descricao",
            10,
            10000
        );

        validarTexto(
            ocorrencia.getCidade(),
            "Cidade",
            2,
            120
        );

        validarTexto(
            ocorrencia.getBairro(),
            "Bairro",
            2,
            120
        );

        validarTexto(
            ocorrencia.getRua(),
            "Rua",
            2,
            180
        );

        validarTexto(
            ocorrencia.getUf(),
            "UF",
            2,
            2
        );

        String uf = ocorrencia.getUf().trim().toUpperCase();

        Set<String> estados = Set.of(
            "AC", "AL", "AP", "AM", "BA",
            "CE", "DF", "ES", "GO", "MA",
            "MT", "MS", "MG", "PA", "PB",
            "PR", "PE", "PI", "RJ", "RN",
            "RS", "RO", "RR", "SC", "SP",
            "SE", "TO"
        );

        if (!estados.contains(uf)) {
            throw new IllegalArgumentException(
                "UF invalida. Informe uma sigla brasileira."
            );
        }

        ocorrencia.setUf(uf);

        if (ocorrencia.getNumeroEndereco() != null &&
            ocorrencia.getNumeroEndereco().length() > 30) {

            throw new IllegalArgumentException(
                "Numero do endereco muito longo."
            );
        }

        if (ocorrencia.getPontoReferencia() != null &&
            ocorrencia.getPontoReferencia().length() > 300) {

            throw new IllegalArgumentException(
                "Ponto de referencia muito longo."
            );
        }

        validarCategorias(ocorrencia.getCategorias());
    }

    // Validar categorias selecionadas
    private void validarCategorias(
            List<Categoria> categorias) {

        if (categorias == null || categorias.isEmpty()) {
            throw new IllegalArgumentException(
                "Selecione ao menos uma categoria."
            );
        }

        Set<Integer> ids = new HashSet<>();

        for (Categoria categoria : categorias) {

            if (categoria == null ||
                categoria.getIdCategoria() <= 0) {

                throw new IllegalArgumentException(
                    "Categoria invalida."
                );
            }

            if (!categoria.isAtivo()) {
                throw new IllegalArgumentException(
                    "Nao e permitido utilizar categoria inativa."
                );
            }

            if (!ids.add(categoria.getIdCategoria())) {
                throw new IllegalArgumentException(
                    "Nao repita a mesma categoria."
                );
            }
        }
    }

    // Validacao generica de campos de texto
    private void validarTexto(
            String valor,
            String campo,
            int minimo,
            int maximo) {

        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(
                campo + " e obrigatorio."
            );
        }

        int tamanho = valor.trim().length();

        if (tamanho < minimo || tamanho > maximo) {
            throw new IllegalArgumentException(
                campo + " deve ter entre " +
                minimo + " e " + maximo +
                " caracteres."
            );
        }
    }
}