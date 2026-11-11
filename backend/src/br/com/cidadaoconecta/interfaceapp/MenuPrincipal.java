
package br.com.cidadaoconecta.interfaceapp;

import br.com.cidadaoconecta.dao.CategoriaDAO;
import br.com.cidadaoconecta.model.Categoria;
import br.com.cidadaoconecta.model.Ocorrencia;
import br.com.cidadaoconecta.service.OcorrenciaService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class MenuPrincipal {

    private static final Scanner scanner =
            new Scanner(System.in, StandardCharsets.UTF_8);

    private static final OcorrenciaService service =
            new OcorrenciaService();

    private static final CategoriaDAO categoriaDAO =
            new CategoriaDAO();

    public static void main(String[] args) {

        int opcao = -1;

        do {
            exibirMenu();

            try {

                String entrada = scanner.nextLine().trim();
                opcao = Integer.parseInt(entrada);

                switch (opcao) {

                    case 1:
                        listarOcorrencias();
                        break;

                    case 2:
                        buscarPorProtocolo();
                        break;

                    case 3:
                        cadastrarOcorrencia();
                        break;

                    case 4:
                        atualizarOcorrencia();
                        break;

                    case 5:
                        excluirOcorrencia();
                        break;

                    case 0:
                        System.out.println(
                            "\nObrigado por utilizar o CidadaoConecta!"
                        );
                        break;

                    default:
                        System.out.println(
                            "\nOpcao invalida. Tente novamente."
                        );
                }

            } catch (NumberFormatException erro) {

                System.out.println(
                    "\nDigite apenas numeros validos."
                );

            } catch (IllegalArgumentException erro) {

                System.out.println(
                    "\nDados invalidos: " + erro.getMessage()
                );

            } catch (Exception erro) {

                System.out.println(
                    "\nErro: " + erro.getMessage()
                );
            }

        } while (opcao != 0);

        scanner.close();
    }

    // ==========================================
    // MENU PRINCIPAL
    // ==========================================

    private static void exibirMenu() {

        System.out.println();
        System.out.println("===================================");
        System.out.println("          CIDADAOCONECTA           ");
        System.out.println("       Observatorio Urbano         ");
        System.out.println("===================================");
        System.out.println("1 - Listar ocorrencias");
        System.out.println("2 - Buscar por protocolo");
        System.out.println("3 - Cadastrar ocorrencia");
        System.out.println("4 - Atualizar ocorrencia");
        System.out.println("5 - Excluir ocorrencia");
        System.out.println("0 - Sair");
        System.out.println("===================================");
        System.out.print("Escolha uma opcao: ");
    }

    // ==========================================
    // 1 - LISTAR OCORRENCIAS
    // ==========================================

    private static void listarOcorrencias()
            throws Exception {

        System.out.println(
            "\n=== OCORRENCIAS CADASTRADAS ==="
        );

        List<Ocorrencia> ocorrencias = service.listar();

        if (ocorrencias.isEmpty()) {

            System.out.println(
                "Nenhuma ocorrencia cadastrada."
            );

            return;
        }

        for (Ocorrencia ocorrencia : ocorrencias) {

            System.out.println("-----------------------------");

            System.out.println(
                "ID: " + ocorrencia.getIdOcorrencia()
            );

            System.out.println(
                "Protocolo: " + ocorrencia.getProtocolo()
            );

            System.out.println(
                "Titulo: " + ocorrencia.getTitulo()
            );

            System.out.println(
                "Cidade: " + ocorrencia.getCidade()
            );

            System.out.println(
                "Status: " + ocorrencia.getStatus()
            );

            System.out.println("Categorias:");

            exibirCategorias(ocorrencia.getCategorias());
        }

        System.out.println("-----------------------------");

        System.out.println(
            "Total de ocorrencias: " + ocorrencias.size()
        );
    }

    // ==========================================
    // 2 - BUSCAR POR PROTOCOLO
    // ==========================================

    private static void buscarPorProtocolo()
            throws Exception {

        System.out.println(
            "\n=== CONSULTAR OCORRENCIA ==="
        );

        String protocolo = lerCampo(
            "Digite o protocolo: "
        );

        Ocorrencia ocorrencia =
                service.buscarPorProtocolo(protocolo);

        if (ocorrencia == null) {

            System.out.println(
                "\nNenhuma ocorrencia encontrada."
            );

            return;
        }

        exibirDetalhes(ocorrencia);
    }

    // ==========================================
    // 3 - CADASTRAR OCORRENCIA
    // ==========================================

    private static void cadastrarOcorrencia()
            throws Exception {

        System.out.println(
            "\n=== CADASTRAR NOVA OCORRENCIA ==="
        );

        String titulo = lerCampo("Titulo: ");
        String descricao = lerCampo("Descricao: ");
        String cidade = lerCampo("Cidade: ");
        String uf = lerCampo("UF (exemplo: GO): ");
        String bairro = lerCampo("Bairro: ");
        String rua = lerCampo("Rua: ");
        String numero = lerCampo("Numero: ");

        String referencia = lerCampo(
            "Ponto de referencia: "
        );

        List<Categoria> categoriasSelecionadas =
                selecionarCategorias();

        Ocorrencia nova = new Ocorrencia(
            titulo,
            descricao,
            cidade,
            uf,
            bairro,
            rua,
            numero,
            referencia
        );

        nova.setCategorias(categoriasSelecionadas);

        System.out.println(
            "\n=== CONFIRMACAO DO CADASTRO ==="
        );

        System.out.println("Titulo: " + titulo);
        System.out.println("Descricao: " + descricao);
        System.out.println("Cidade: " + cidade);
        System.out.println("UF: " + uf);
        System.out.println("Bairro: " + bairro);
        System.out.println("Rua: " + rua);

        System.out.println("Categorias selecionadas:");
        exibirCategorias(categoriasSelecionadas);

        String confirmacao = lerCampo(
            "\nDeseja salvar no Oracle? (S/N): "
        );

        if (!confirmacao.equalsIgnoreCase("S")) {

            System.out.println(
                "Cadastro cancelado. Nenhum registro salvo."
            );

            return;
        }

        service.cadastrar(nova);

        System.out.println(
            "\nOCORRENCIA CADASTRADA COM SUCESSO!"
        );

        System.out.println(
            "Status inicial: " + nova.getStatus()
        );

        System.out.println(
            "Protocolo: " + nova.getProtocolo()
        );
    }

    // ==========================================
    // 4 - ATUALIZAR OCORRENCIA
    // ==========================================

    private static void atualizarOcorrencia()
            throws Exception {

        System.out.println(
            "\n=== ATUALIZAR OCORRENCIA ==="
        );

        String protocolo = lerCampo(
            "Informe o protocolo da ocorrencia: "
        );

        Ocorrencia ocorrencia =
                service.buscarPorProtocolo(protocolo);

        if (ocorrencia == null) {

            System.out.println(
                "\nOcorrencia nao encontrada."
            );

            return;
        }

        System.out.println(
            "\n=== DADOS ATUAIS ==="
        );

        exibirDetalhes(ocorrencia);

        System.out.println(
            "\n=== EDITAR INFORMACOES ==="
        );

        System.out.println(
            "Pressione ENTER para manter o valor atual."
        );

        ocorrencia.setTitulo(
            lerAtualizacao(
                "Titulo",
                ocorrencia.getTitulo()
            )
        );

        ocorrencia.setDescricao(
            lerAtualizacao(
                "Descricao",
                ocorrencia.getDescricao()
            )
        );

        ocorrencia.setCidade(
            lerAtualizacao(
                "Cidade",
                ocorrencia.getCidade()
            )
        );

        ocorrencia.setUf(
            lerAtualizacao(
                "UF",
                ocorrencia.getUf()
            )
        );

        ocorrencia.setBairro(
            lerAtualizacao(
                "Bairro",
                ocorrencia.getBairro()
            )
        );

        ocorrencia.setRua(
            lerAtualizacao(
                "Rua",
                ocorrencia.getRua()
            )
        );

        ocorrencia.setNumeroEndereco(
            lerAtualizacao(
                "Numero",
                ocorrencia.getNumeroEndereco()
            )
        );

        ocorrencia.setPontoReferencia(
            lerAtualizacao(
                "Ponto de referencia",
                ocorrencia.getPontoReferencia()
            )
        );

        System.out.println(
            "\n=== CONFIRMACAO DA ATUALIZACAO ==="
        );

        exibirDetalhes(ocorrencia);

        String confirmacao = lerCampo(
            "\nConfirmar atualizacao no Oracle? (S/N): "
        );

        if (!confirmacao.equalsIgnoreCase("S")) {

            System.out.println(
                "Atualizacao cancelada."
            );

            return;
        }

        boolean atualizado =
                service.atualizar(ocorrencia);

        if (!atualizado) {

            System.out.println(
                "Nao foi possivel atualizar a ocorrencia."
            );

            return;
        }

        System.out.println(
            "\nOCORRENCIA ATUALIZADA COM SUCESSO!"
        );

        Ocorrencia conferida =
                service.buscarPorProtocolo(
                    ocorrencia.getProtocolo()
                );

        if (conferida != null) {

            System.out.println(
                "\n=== DADOS CONFIRMADOS NO ORACLE ==="
            );

            exibirDetalhes(conferida);
        }
    }

    // ==========================================
    // 5 - EXCLUIR OCORRENCIA
    // ==========================================

    private static void excluirOcorrencia()
            throws Exception {

        System.out.println(
            "\n=== EXCLUIR OCORRENCIA ==="
        );

        System.out.println(
            "ATENCAO: Esta operacao remove dados do Oracle."
        );

        System.out.println(
            "Use somente ocorrencias criadas para testes."
        );

        String protocolo = lerCampo(
            "\nInforme o protocolo da ocorrencia: "
        );

        Ocorrencia ocorrencia =
                service.buscarPorProtocolo(protocolo);

        if (ocorrencia == null) {

            System.out.println(
                "\nOcorrencia nao encontrada."
            );

            return;
        }

        // Protege a ocorrencia original de demonstracao
        if (ocorrencia.getIdOcorrencia() == 1 ||
            "CC-TESTE-0001".equals(
                ocorrencia.getProtocolo()
            )) {

            System.out.println(
                "\nEsta ocorrencia de demonstracao"
                + " nao pode ser excluida pelo menu."
            );

            return;
        }

        System.out.println(
            "\n=== OCORRENCIA ENCONTRADA ==="
        );

        exibirDetalhes(ocorrencia);

        System.out.println(
            "\nATENCAO: A exclusao e permanente."
        );

        System.out.println(
            "O registro e seus relacionamentos"
            + " serao removidos do Oracle."
        );

        String confirmacao = lerCampo(
            "\nDigite EXCLUIR para confirmar: "
        );

        if (!"EXCLUIR".equals(confirmacao)) {

            System.out.println(
                "\nExclusao cancelada."
            );

            return;
        }

        boolean excluida =
                service.excluir(
                    ocorrencia.getIdOcorrencia()
                );

        if (!excluida) {

            System.out.println(
                "\nNao foi possivel excluir a ocorrencia."
            );

            return;
        }

        // Consultar novamente para verificar
        Ocorrencia verificacao =
                service.buscarPorId(
                    ocorrencia.getIdOcorrencia()
                );

        if (verificacao != null) {

            System.out.println(
                "\nATENCAO: A ocorrencia ainda foi encontrada."
            );

            return;
        }

        System.out.println(
            "\nOCORRENCIA EXCLUIDA COM SUCESSO!"
        );

        System.out.println(
            "Protocolo excluido: " + protocolo
        );

        System.out.println(
            "A exclusao foi confirmada no Oracle."
        );
    }

    // ==========================================
    // SELECIONAR CATEGORIAS DO ORACLE
    // ==========================================

    private static List<Categoria> selecionarCategorias()
            throws Exception {

        List<Categoria> todasCategorias =
                categoriaDAO.listarTodas();

        List<Categoria> categoriasAtivas =
                new ArrayList<>();

        for (Categoria categoria : todasCategorias) {

            if (categoria.isAtivo()) {
                categoriasAtivas.add(categoria);
            }
        }

        if (categoriasAtivas.isEmpty()) {

            throw new IllegalStateException(
                "Nenhuma categoria ativa encontrada no Oracle."
            );
        }

        System.out.println(
            "\n=== CATEGORIAS DISPONIVEIS ==="
        );

        for (Categoria categoria : categoriasAtivas) {

            System.out.println(
                categoria.getIdCategoria()
                + " - "
                + categoria.getNome()
            );
        }

        System.out.println(
            "\nVoce pode selecionar uma ou mais categorias."
        );

        System.out.println(
            "Separe os IDs por virgula."
        );

        String entrada = lerCampo(
            "IDs das categorias (exemplo: 1,2): "
        );

        if (entrada.isBlank()) {

            throw new IllegalArgumentException(
                "Selecione ao menos uma categoria."
            );
        }

        String[] idsInformados = entrada.split(",", -1);

        Set<Integer> idsSelecionados =
                new HashSet<>();

        List<Categoria> selecionadas =
                new ArrayList<>();

        for (String textoId : idsInformados) {

            int id;

            try {

                id = Integer.parseInt(textoId.trim());

            } catch (NumberFormatException erro) {

                throw new IllegalArgumentException(
                    "Informe apenas IDs numericos separados por virgula."
                );
            }

            if (!idsSelecionados.add(id)) {

                throw new IllegalArgumentException(
                    "Nao repita a mesma categoria."
                );
            }

            Categoria categoria =
                    categoriaDAO.buscarPorId(id);

            if (categoria == null) {

                throw new IllegalArgumentException(
                    "Categoria com ID " + id
                    + " nao encontrada."
                );
            }

            if (!categoria.isAtivo()) {

                throw new IllegalArgumentException(
                    "A categoria " + categoria.getNome()
                    + " esta inativa."
                );
            }

            selecionadas.add(categoria);
        }

        System.out.println(
            "\nCategorias selecionadas com sucesso!"
        );

        return selecionadas;
    }

    // ==========================================
    // EXIBIR DETALHES
    // ==========================================

    private static void exibirDetalhes(
            Ocorrencia ocorrencia) {

        System.out.println(
            "\n=== DADOS DA OCORRENCIA ==="
        );

        System.out.println(
            "Protocolo: " + ocorrencia.getProtocolo()
        );

        System.out.println(
            "Titulo: " + ocorrencia.getTitulo()
        );

        System.out.println(
            "Descricao: " + ocorrencia.getDescricao()
        );

        System.out.println(
            "Cidade: " + ocorrencia.getCidade()
        );

        System.out.println(
            "UF: " + ocorrencia.getUf()
        );

        System.out.println(
            "Bairro: " + ocorrencia.getBairro()
        );

        System.out.println(
            "Rua: " + ocorrencia.getRua()
        );

        System.out.println(
            "Numero: " + ocorrencia.getNumeroEndereco()
        );

        System.out.println(
            "Ponto de referencia: "
            + ocorrencia.getPontoReferencia()
        );

        System.out.println(
            "Status: " + ocorrencia.getStatus()
        );

        System.out.println("Categorias:");

        exibirCategorias(ocorrencia.getCategorias());
    }

    // ==========================================
    // EXIBIR CATEGORIAS
    // ==========================================

    private static void exibirCategorias(
            List<Categoria> categorias) {

        if (categorias == null || categorias.isEmpty()) {

            System.out.println(
                " - Nenhuma categoria associada."
            );

            return;
        }

        for (Categoria categoria : categorias) {

            System.out.println(
                " - " + categoria.getNome()
            );
        }
    }

    // ==========================================
    // LEITURA DOS CAMPOS
    // ==========================================

    private static String lerCampo(String mensagem) {

        System.out.print(mensagem);

        return scanner.nextLine().trim();
    }

    // Mantem o valor anterior quando ENTER e pressionado
    private static String lerAtualizacao(
            String campo,
            String valorAtual) {

        String atual = valorAtual == null
                ? ""
                : valorAtual;

        System.out.print(
            campo + " [" + atual + "]: "
        );

        String novoValor = scanner.nextLine().trim();

        if (novoValor.isEmpty()) {
            return atual;
        }

        return novoValor;
    }
}