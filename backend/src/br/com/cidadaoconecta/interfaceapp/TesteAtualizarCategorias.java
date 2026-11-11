
package br.com.cidadaoconecta.interfaceapp;

import br.com.cidadaoconecta.dao.CategoriaDAO;
import br.com.cidadaoconecta.model.Categoria;
import br.com.cidadaoconecta.model.Ocorrencia;
import br.com.cidadaoconecta.service.OcorrenciaService;

import java.util.List;

public class TesteAtualizarCategorias {

    public static void main(String[] args) {

        final int idOcorrencia = 26;

        OcorrenciaService service =
                new OcorrenciaService();

        CategoriaDAO categoriaDAO =
                new CategoriaDAO();

        try {

            System.out.println(
                "===================================="
            );
            System.out.println(
                "CIDADAOCONECTA - TESTE DE CATEGORIAS"
            );
            System.out.println(
                "===================================="
            );

            // Consultar a ocorrencia existente
            Ocorrencia ocorrencia =
                    service.buscarPorId(idOcorrencia);

            if (ocorrencia == null) {

                throw new IllegalStateException(
                    "Ocorrencia 26 nao encontrada."
                );
            }

            System.out.println(
                "\nOcorrencia: " + ocorrencia.getTitulo()
            );

            System.out.println(
                "\nCategorias anteriores:"
            );

            for (Categoria categoria :
                    ocorrencia.getCategorias()) {

                System.out.println(
                    "- " + categoria.getNome()
                );
            }

            // Buscar categorias reais no Oracle
            Categoria categoria1 =
                    categoriaDAO.buscarPorId(1);

            Categoria categoria2 =
                    categoriaDAO.buscarPorId(2);

            if (categoria1 == null ||
                categoria2 == null) {

                throw new IllegalStateException(
                    "Categorias 1 e 2 nao encontradas."
                );
            }

            if (!categoria1.isAtivo() ||
                !categoria2.isAtivo()) {

                throw new IllegalStateException(
                    "Uma das categorias esta inativa."
                );
            }

            // Atualizar o relacionamento N:N
            service.atualizarCategorias(
                idOcorrencia,
                List.of(categoria1, categoria2)
            );

            // Consultar novamente o Oracle
            Ocorrencia atualizada =
                    service.buscarPorId(idOcorrencia);

            if (atualizada == null) {

                throw new IllegalStateException(
                    "Falha ao consultar ocorrencia atualizada."
                );
            }

            System.out.println(
                "\nCategorias apos atualizacao:"
            );

            for (Categoria categoria :
                    atualizada.getCategorias()) {

                System.out.println(
                    "- " + categoria.getNome()
                );
            }

            boolean possuiCategoria1 =
                    atualizada.getCategorias()
                        .stream()
                        .anyMatch(c -> c.getIdCategoria() == 1);

            boolean possuiCategoria2 =
                    atualizada.getCategorias()
                        .stream()
                        .anyMatch(c -> c.getIdCategoria() == 2);

            if (atualizada.getCategorias().size() != 2 ||
                !possuiCategoria1 ||
                !possuiCategoria2) {

                throw new IllegalStateException(
                    "As duas categorias nao foram associadas corretamente."
                );
            }

            System.out.println(
                "\nTESTE APROVADO!"
            );

            System.out.println(
                "A ocorrencia 26 possui duas categorias."
            );

        } catch (Exception erro) {

            System.out.println(
                "\nERRO: " + erro.getMessage()
            );

            erro.printStackTrace();

            System.exit(1);
        }
    }
}