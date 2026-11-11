
package br.com.cidadaoconecta.interfaceapp;

import br.com.cidadaoconecta.dao.CategoriaDAO;
import br.com.cidadaoconecta.model.Categoria;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * Testa o CRUD completo da tabela CC2_CATEGORIA.
 *
 * Global Solution FIAP 2026/2
 * Projeto: CidadaoConecta
 *
 * Utiliza uma categoria temporaria e tenta
 * remove-la ao terminar.
 */
public class TesteCategoriaDAO {

    public static void main(String[] args) {

        CategoriaDAO dao = new CategoriaDAO();

        int idTeste = 0;

        System.out.println("==============================");
        System.out.println("CIDADAOCONECTA - TESTE CRUD");
        System.out.println("==============================");

        try {

            // 1 - CREATE: inserir
            System.out.println("\n1 - INSERIR CATEGORIA");

            String codigoTeste = "teste_" +
                    UUID.randomUUID()
                        .toString()
                        .substring(0, 8);

            Categoria categoria = new Categoria(
                codigoTeste,
                "Categoria de teste",
                "Registro temporario para testar o JDBC"
            );

            dao.inserir(categoria);

            idTeste = categoria.getIdCategoria();

            if (idTeste <= 0) {
                throw new SQLException(
                    "O Oracle nao retornou o ID gerado."
                );
            }

            System.out.println(
                "Categoria inserida. ID: " + idTeste
            );


            // 2 - READ: listar
            System.out.println("\n2 - LISTAR CATEGORIAS");

            List<Categoria> categorias = dao.listarTodas();

            for (Categoria item : categorias) {
                System.out.println(
                    item.getIdCategoria() + " - " +
                    item.getNome()
                );
            }


            // 3 - READ: buscar por ID
            System.out.println("\n3 - BUSCAR POR ID");

            Categoria encontrada = dao.buscarPorId(idTeste);

            if (encontrada == null) {
                throw new SQLException(
                    "Categoria de teste nao encontrada."
                );
            }

            System.out.println(
                "Encontrada: " + encontrada.getNome()
            );


            // 4 - UPDATE: atualizar
            System.out.println("\n4 - ATUALIZAR CATEGORIA");

            encontrada.setNome("Categoria atualizada");
            encontrada.setDescricao(
                "Descricao atualizada pelo teste JDBC"
            );

            boolean atualizou = dao.atualizar(encontrada);

            if (!atualizou) {
                throw new SQLException(
                    "Nenhuma categoria foi atualizada."
                );
            }

            Categoria atualizada = dao.buscarPorId(idTeste);

            if (atualizada == null ||
                !"Categoria atualizada".equals(
                    atualizada.getNome())) {

                throw new SQLException(
                    "A atualizacao nao foi confirmada."
                );
            }

            System.out.println(
                "Atualizada: " + atualizada.getNome()
            );


            // 5 - DELETE: excluir
            System.out.println("\n5 - EXCLUIR CATEGORIA");

            boolean excluiu = dao.excluir(idTeste);

            if (!excluiu ||
                dao.buscarPorId(idTeste) != null) {

                throw new SQLException(
                    "A exclusao nao foi confirmada."
                );
            }

            idTeste = 0;

            System.out.println(
                "Categoria temporaria excluida."
            );

            System.out.println(
                "\nCRUD CONCLUIDO COM SUCESSO!"
            );

        } catch (SQLException erro) {

            System.err.println(
                "\nERRO NO TESTE JDBC:"
            );

            System.err.println(erro.getMessage());

        } finally {

            // Limpeza em caso de interrupcao do teste.
            if (idTeste > 0) {

                try {
                    dao.excluir(idTeste);

                    System.out.println(
                        "Limpeza da categoria temporaria concluida."
                    );

                } catch (SQLException erro) {

                    System.err.println(
                        "Nao foi possivel remover a " +
                        "categoria temporaria de ID " +
                        idTeste
                    );

                    System.err.println(erro.getMessage());
                }
            }
        }
    }
}