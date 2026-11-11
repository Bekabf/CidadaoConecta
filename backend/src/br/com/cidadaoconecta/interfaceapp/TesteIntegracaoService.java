
package br.com.cidadaoconecta.interfaceapp;

import br.com.cidadaoconecta.model.Categoria;
import br.com.cidadaoconecta.model.Ocorrencia;
import br.com.cidadaoconecta.service.OcorrenciaService;

import java.util.List;
import java.util.UUID;

public class TesteIntegracaoService {

    public static void main(String[] args) {

        OcorrenciaService service = new OcorrenciaService();

        String identificador = UUID.randomUUID()
                .toString()
                .substring(0, 8);

        String tituloTeste = "Teste Integracao " + identificador;

        int idTemporario = 0;
        boolean testeAprovado = false;
        boolean limpezaConcluida = true;

        System.out.println(
            "===================================="
        );
        System.out.println(
            "CIDADAOCONECTA - TESTE DE INTEGRACAO"
        );
        System.out.println(
            "===================================="
        );

        try {

            // 1 - Consultar uma ocorrencia existente
            System.out.println(
                "\n1 - Consultando ocorrencia original..."
            );

            Ocorrencia original = service.buscarPorProtocolo(
                "CC-TESTE-0001"
            );

            if (original == null) {
                throw new IllegalStateException(
                    "Ocorrencia original nao encontrada."
                );
            }

            List<Categoria> categorias =
                original.getCategorias();

            if (categorias == null || categorias.isEmpty()) {
                throw new IllegalStateException(
                    "Ocorrencia original sem categorias."
                );
            }

            System.out.println(
                "OK - Ocorrencia original encontrada."
            );

            // 2 - Criar uma ocorrencia temporaria
            System.out.println(
                "\n2 - Cadastrando nova ocorrencia..."
            );

            Ocorrencia nova = new Ocorrencia(
                tituloTeste,
                "Teste de integracao entre Service, DAO e Oracle.",
                "Anapolis",
                "GO",
                "Centro",
                "Rua de Teste",
                "100",
                "Registro temporario de integracao"
            );

            nova.setCategorias(
                List.of(categorias.get(0))
            );

            service.cadastrar(nova);

            System.out.println(
                "OK - Cadastro solicitado com sucesso."
            );

            // 3 - Localizar o registro salvo no Oracle
            System.out.println(
                "\n3 - Consultando o registro cadastrado..."
            );

            for (Ocorrencia item : service.listar()) {

                if (tituloTeste.equals(item.getTitulo())) {
                    idTemporario = item.getIdOcorrencia();
                    break;
                }
            }

            if (idTemporario <= 0) {
                throw new IllegalStateException(
                    "Registro temporario nao encontrado no Oracle."
                );
            }

            Ocorrencia encontrada = service.buscarPorId(
                idTemporario
            );

            if (encontrada == null) {
                throw new IllegalStateException(
                    "Falha ao buscar ocorrencia por ID."
                );
            }

            System.out.println(
                "OK - Ocorrencia localizada no banco."
            );

            System.out.println(
                "ID temporario: " + idTemporario
            );

            System.out.println(
                "Titulo: " + encontrada.getTitulo()
            );

            // 4 - Conferir status inicial
            System.out.println(
                "\n4 - Verificando status inicial..."
            );

            if (!"ABERTA".equals(encontrada.getStatus())) {
                throw new IllegalStateException(
                    "Status inicial diferente de ABERTA."
                );
            }

            System.out.println(
                "OK - Status inicial: ABERTA."
            );

            // 5 - Validar novamente a ocorrencia original
            System.out.println(
                "\n5 - Verificando registro original..."
            );

            if (service.buscarPorProtocolo(
                    "CC-TESTE-0001") == null) {

                throw new IllegalStateException(
                    "Registro original nao encontrado."
                );
            }

            System.out.println(
                "OK - Registro original preservado."
            );

            testeAprovado = true;

        } catch (Exception erro) {

            System.out.println(
                "\nERRO NO TESTE: " + erro.getMessage()
            );

            erro.printStackTrace();

        } finally {

            // Limpar apenas o registro criado neste teste
            System.out.println(
                "\n6 - Limpando registro temporario..."
            );

            try {

                // Recuperar ID mesmo se ocorreu erro
                // apos o cadastro.
                if (idTemporario <= 0) {

                    for (Ocorrencia item : service.listar()) {

                        if (tituloTeste.equals(item.getTitulo())) {
                            idTemporario = item.getIdOcorrencia();
                            break;
                        }
                    }
                }

                if (idTemporario > 0) {

                    boolean excluido = service.excluir(
                        idTemporario
                    );

                    if (!excluido) {
                        throw new IllegalStateException(
                            "Exclusao nao confirmada."
                        );
                    }

                    if (service.buscarPorId(
                            idTemporario) != null) {

                        throw new IllegalStateException(
                            "Registro ainda existe no Oracle."
                        );
                    }

                    System.out.println(
                        "OK - Registro temporario excluido."
                    );

                } else {

                    System.out.println(
                        "Nenhum registro temporario localizado."
                    );
                }

            } catch (Exception erro) {

                limpezaConcluida = false;

                System.out.println(
                    "ATENCAO - Falha na limpeza: "
                    + erro.getMessage()
                );
            }
        }

        System.out.println(
            "\n===================================="
        );

        if (testeAprovado && limpezaConcluida) {

            System.out.println(
                "INTEGRACAO SERVICE + DAO + ORACLE OK!"
            );

        } else {

            System.out.println(
                "TESTE DE INTEGRACAO NAO CONCLUIDO."
            );

            System.exit(1);
        }

        System.out.println(
            "===================================="
        );
    }
}