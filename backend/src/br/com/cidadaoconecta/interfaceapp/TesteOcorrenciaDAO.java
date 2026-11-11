
package br.com.cidadaoconecta.interfaceapp;

import br.com.cidadaoconecta.dao.CategoriaDAO;
import br.com.cidadaoconecta.dao.OcorrenciaDAO;
import br.com.cidadaoconecta.model.Categoria;
import br.com.cidadaoconecta.model.Ocorrencia;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * Teste do CRUD de ocorrencias.
 *
 * Projeto: CidadaoConecta
 * Global Solution FIAP 2026/2
 *
 * Utiliza uma ocorrencia temporaria.
 * Nao altera os registros oficiais.
 */
public class TesteOcorrenciaDAO {

    public static void main(String[] args) {

        OcorrenciaDAO ocorrenciaDAO = new OcorrenciaDAO();
        CategoriaDAO categoriaDAO = new CategoriaDAO();

        int idTeste = 0;

        String protocoloTeste = "CC-TESTE-" +
                UUID.randomUUID()
                    .toString()
                    .substring(0, 8)
                    .toUpperCase();

        System.out.println(
            "======================================"
        );
        System.out.println(
            "CIDADÃOCONECTA - TESTE CRUD OCORRENCIAS"
        );
        System.out.println(
            "======================================"
        );

        try {

            // =====================================
            // 1 - BUSCAR CATEGORIA EXISTENTE
            // =====================================

            System.out.println(
                "\n1 - PREPARAR CATEGORIA"
            );

            List<Categoria> categorias =
                categoriaDAO.listarTodas();

            Categoria pavimentacao = null;

            for (Categoria categoria : categorias) {

                if ("pavimentacao".equals(
                        categoria.getCodigo())) {

                    pavimentacao = categoria;
                    break;
                }
            }

            if (pavimentacao == null) {
                throw new SQLException(
                    "Categoria pavimentacao nao encontrada."
                );
            }

            System.out.println(
                "Categoria: " + pavimentacao.getNome()
            );


            // =====================================
            // 2 - CREATE: INSERIR OCORRENCIA
            // =====================================

            System.out.println(
                "\n2 - INSERIR OCORRENCIA"
            );

            Ocorrencia ocorrencia = new Ocorrencia(
                "Buraco de teste na via",
                "Ocorrencia temporaria criada pelo Java JDBC.",
                "Anapolis",
                "GO",
                "Centro",
                "Rua de Teste",
                "100",
                "Proximo a praca"
            );

            ocorrencia.setProtocolo(protocoloTeste);

            ocorrencia.adicionarCategoria(
                pavimentacao
            );

            ocorrenciaDAO.inserir(ocorrencia);

            idTeste = ocorrencia.getIdOcorrencia();

            if (idTeste <= 0) {
                throw new SQLException(
                    "ID da ocorrencia nao foi gerado."
                );
            }

            System.out.println(
                "Ocorrencia cadastrada com sucesso!"
            );

            System.out.println(
                "ID: " + idTeste
            );

            System.out.println(
                "Protocolo: " + protocoloTeste
            );


            // =====================================
            // 3 - READ: BUSCAR POR ID
            // =====================================

            System.out.println(
                "\n3 - BUSCAR OCORRENCIA POR ID"
            );

            Ocorrencia encontrada =
                ocorrenciaDAO.buscarPorId(idTeste);

            if (encontrada == null) {
                throw new SQLException(
                    "Ocorrencia nao encontrada pelo ID."
                );
            }

            System.out.println(
                "Titulo: " + encontrada.getTitulo()
            );

            System.out.println(
                "Status: " + encontrada.getStatus()
            );

            System.out.println(
                "Categorias: " +
                encontrada.getCategorias().size()
            );

            if (encontrada.getCategorias().size() != 1 ||
                !"pavimentacao".equals(
                    encontrada.getCategorias()
                        .get(0).getCodigo())) {

                throw new SQLException(
                    "Relacionamento N:N nao foi recuperado."
                );
            }


            // =====================================
            // 4 - READ: BUSCAR POR PROTOCOLO
            // =====================================

            System.out.println(
                "\n4 - BUSCAR POR PROTOCOLO"
            );

            Ocorrencia consulta =
                ocorrenciaDAO.buscarPorProtocolo(
                    protocoloTeste
                );

            if (consulta == null ||
                consulta.getIdOcorrencia() != idTeste) {

                throw new SQLException(
                    "Busca por protocolo falhou."
                );
            }

            System.out.println(
                "Ocorrencia encontrada: " +
                consulta.getTitulo()
            );


            // =====================================
            // 5 - READ: LISTAR OCORRENCIAS
            // =====================================

            System.out.println(
                "\n5 - LISTAR OCORRENCIAS"
            );

            List<Ocorrencia> lista =
                ocorrenciaDAO.listarTodas();

            boolean encontrouNaLista = false;

            for (Ocorrencia item : lista) {

                if (item.getIdOcorrencia() == idTeste) {
                    encontrouNaLista = true;
                }

                System.out.println(
                    item.getProtocolo() +
                    " - " +
                    item.getTitulo()
                );
            }

            if (!encontrouNaLista) {
                throw new SQLException(
                    "Ocorrencia nao apareceu na listagem."
                );
            }


            // =====================================
            // 6 - UPDATE: ATUALIZAR OCORRENCIA
            // =====================================

            System.out.println(
                "\n6 - ATUALIZAR OCORRENCIA"
            );

            encontrada.setTitulo(
                "Buraco de teste atualizado"
            );

            encontrada.setDescricao(
                "Descricao atualizada pelo teste JDBC."
            );

            boolean atualizou =
                ocorrenciaDAO.atualizar(encontrada);

            if (!atualizou) {
                throw new SQLException(
                    "Nao foi possivel atualizar a ocorrencia."
                );
            }

            Ocorrencia atualizada =
                ocorrenciaDAO.buscarPorId(idTeste);

            if (atualizada == null ||
                !"Buraco de teste atualizado".equals(
                    atualizada.getTitulo())) {

                throw new SQLException(
                    "Atualizacao nao foi confirmada."
                );
            }

            System.out.println(
                "Titulo atualizado: " +
                atualizada.getTitulo()
            );


            // =====================================
            // 7 - DELETE: EXCLUIR OCORRENCIA
            // =====================================

            System.out.println(
                "\n7 - EXCLUIR OCORRENCIA"
            );

            boolean excluiu =
                ocorrenciaDAO.excluir(idTeste);

            if (!excluiu ||
                ocorrenciaDAO.buscarPorId(idTeste) != null) {

                throw new SQLException(
                    "Exclusao nao foi confirmada."
                );
            }

            idTeste = 0;

            System.out.println(
                "Ocorrencia temporaria excluida!"
            );

            System.out.println(
                "\n======================================"
            );

            System.out.println(
                "CRUD DE OCORRENCIAS CONCLUIDO!"
            );

            System.out.println(
                "======================================"
            );

        } catch (SQLException erro) {

            System.err.println(
                "\nERRO NO TESTE JDBC:"
            );

            System.err.println(
                erro.getMessage()
            );

        } finally {

            // Tenta limpar registros temporarios
            // caso o teste falhe antes da exclusao.

            try {

                Ocorrencia restante =
                    ocorrenciaDAO.buscarPorProtocolo(
                        protocoloTeste
                    );

                if (restante != null) {

                    boolean removeu =
                        ocorrenciaDAO.excluir(
                            restante.getIdOcorrencia()
                        );

                    if (removeu) {
                        System.out.println(
                            "Limpeza temporaria concluida."
                        );
                    }
                }

            } catch (SQLException erro) {

                System.err.println(
                    "ATENCAO: nao foi possivel " +
                    "confirmar a limpeza do teste."
                );

                System.err.println(
                    "Protocolo: " + protocoloTeste
                );

                System.err.println(
                    erro.getMessage()
                );
            }
        }
    }
}