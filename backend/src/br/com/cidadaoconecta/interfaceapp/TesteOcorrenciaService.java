
package br.com.cidadaoconecta.interfaceapp;

import br.com.cidadaoconecta.model.Categoria;
import br.com.cidadaoconecta.model.Ocorrencia;
import br.com.cidadaoconecta.service.OcorrenciaService;

import java.util.ArrayList;
import java.util.List;

/**
 * Teste das validacoes da camada Service.
 *
 * Projeto: CidadaoConecta
 * Global Solution FIAP 2026/2
 *
 * Nao realiza gravacoes no banco Oracle.
 */
public class TesteOcorrenciaService {

    private static int testesAprovados = 0;
    private static int testesReprovados = 0;

    public static void main(String[] args) {

        OcorrenciaService service = new OcorrenciaService();

        System.out.println(
            "======================================"
        );
        System.out.println(
            "CIDADAOCONECTA - TESTES DE VALIDACAO"
        );
        System.out.println(
            "======================================"
        );

        // TESTE 1 - Titulo vazio
        verificarErro(
            "Titulo vazio",
            "Titulo e obrigatorio.",
            () -> {
                Ocorrencia ocorrencia = criarOcorrenciaValida();
                ocorrencia.setTitulo("");
                service.cadastrar(ocorrencia);
            }
        );

        // TESTE 2 - UF invalida
        verificarErro(
            "UF invalida",
            "UF invalida. Informe uma sigla brasileira.",
            () -> {
                Ocorrencia ocorrencia = criarOcorrenciaValida();
                ocorrencia.setUf("XX");
                service.cadastrar(ocorrencia);
            }
        );

        // TESTE 3 - Sem categoria
        verificarErro(
            "Ocorrencia sem categoria",
            "Selecione ao menos uma categoria.",
            () -> {
                Ocorrencia ocorrencia = criarOcorrenciaValida();
                ocorrencia.setCategorias(new ArrayList<>());
                service.cadastrar(ocorrencia);
            }
        );

        // TESTE 4 - Categoria duplicada
        verificarErro(
            "Categoria duplicada",
            "Nao repita a mesma categoria.",
            () -> {
                Ocorrencia ocorrencia = criarOcorrenciaValida();

                Categoria categoria = ocorrencia
                    .getCategorias().get(0);

                ocorrencia.adicionarCategoria(categoria);

                service.cadastrar(ocorrencia);
            }
        );

        // TESTE 5 - Descricao curta
        verificarErro(
            "Descricao muito curta",
            "Descricao deve ter entre 10 e 10000 caracteres.",
            () -> {
                Ocorrencia ocorrencia = criarOcorrenciaValida();
                ocorrencia.setDescricao("Curta");
                service.cadastrar(ocorrencia);
            }
        );

        // TESTE 6 - Protocolo vazio
        verificarErro(
            "Consulta sem protocolo",
            "Informe um protocolo valido.",
            () -> service.buscarPorProtocolo("")
        );

        // TESTE 7 - ID invalido
        verificarErro(
            "Busca com ID invalido",
            "O ID da ocorrencia deve ser positivo.",
            () -> service.buscarPorId(0)
        );

        // TESTE 8 - Categoria inativa
        verificarErro(
            "Categoria inativa",
            "Nao e permitido utilizar categoria inativa.",
            () -> {
                Ocorrencia ocorrencia = criarOcorrenciaValida();
                ocorrencia.getCategorias().get(0).setAtivo(false);
                service.cadastrar(ocorrencia);
            }
        );

        System.out.println(
            "\n======================================"
        );

        System.out.println(
            "TESTES APROVADOS: " + testesAprovados
        );

        System.out.println(
            "TESTES REPROVADOS: " + testesReprovados
        );

        if (testesReprovados == 0) {
            System.out.println(
                "TODAS AS VALIDACOES FUNCIONARAM!"
            );
        } else {
            System.out.println(
                "ALGUMAS VALIDACOES PRECISAM DE AJUSTE."
            );
            System.exit(1);
        }
    }

    /**
     * Executa uma operacao que deve ser rejeitada
     * pela camada de validacao.
     */
    private static void verificarErro(
            String nome,
            String mensagemEsperada,
            Operacao operacao) {

        System.out.println("\nTESTE: " + nome);

        try {

            operacao.executar();

            testesReprovados++;

            System.out.println(
                "FALHOU: a operacao foi aceita indevidamente."
            );

        } catch (IllegalArgumentException erro) {

            if (mensagemEsperada.equals(erro.getMessage())) {

                testesAprovados++;

                System.out.println(
                    "APROVADO: " + erro.getMessage()
                );

            } else {

                testesReprovados++;

                System.out.println(
                    "FALHOU: mensagem inesperada."
                );

                System.out.println(
                    "Recebida: " + erro.getMessage()
                );
            }

        } catch (Exception erro) {

            testesReprovados++;

            System.out.println(
                "FALHOU: excecao inesperada: "
                + erro.getMessage()
            );
        }
    }

    /**
     * Cria dados validos em memoria.
     * Os registros nao sao persistidos pelo teste.
     */
    private static Ocorrencia criarOcorrenciaValida() {

        Ocorrencia ocorrencia = new Ocorrencia(
            "Buraco na via publica",
            "Buraco grande na rua, oferecendo risco.",
            "Anapolis",
            "GO",
            "Centro",
            "Rua das Flores",
            "150",
            "Proximo a praca"
        );

        Categoria categoria = new Categoria(
            1,
            "pavimentacao",
            "Ruas e calcadas",
            "Problemas na pavimentacao",
            true
        );

        ocorrencia.setCategorias(List.of(categoria));

        return ocorrencia;
    }

    @FunctionalInterface
    private interface Operacao {
        void executar() throws Exception;
    }
}