
package br.com.cidadaoconecta.interfaceapp;

import br.com.cidadaoconecta.dao.CategoriaDAO;
import br.com.cidadaoconecta.dao.OcorrenciaDAO;
import br.com.cidadaoconecta.model.Categoria;
import br.com.cidadaoconecta.model.Ocorrencia;
import br.com.cidadaoconecta.service.OcorrenciaService;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/*
 * CIDADÃOCONECTA - SERVIDOR WEB
 *
 * FIAP Global Solution 2026/2
 *
 * Java + JDBC + Oracle + HTML + CSS + JavaScript
 *
 * ROTAS:
 *
 * POST /api/ocorrencias
 * Cadastro de ocorrências
 *
 * GET /api/ocorrencias?protocolo=CC-...
 * Consulta individual pelo protocolo
 *
 * GET /api/ocorrencias/publicas
 * Listagem resumida para o Observatório Urbano
 *
 * Servidor local para desenvolvimento acadêmico.
 */

public class ServidorWeb {

    // ==========================================
    // CONFIGURAÇÕES
    // ==========================================

    private static final int PORTA = 8080;

    private static final int LIMITE_CORPO = 65536;

    private static final Path FRONTEND =
            Path.of("frontend")
                .toAbsolutePath()
                .normalize();

    private static final OcorrenciaService service =
            new OcorrenciaService();

    private static final CategoriaDAO categoriaDAO =
            new CategoriaDAO();

    private static final OcorrenciaDAO ocorrenciaDAO =
            new OcorrenciaDAO();

    // ==========================================
    // INICIAR SERVIDOR
    // ==========================================

    public static void main(String[] args)
            throws IOException {

        HttpServer servidor = HttpServer.create(
            new InetSocketAddress(
                "127.0.0.1",
                PORTA
            ),
            0
        );

        servidor.createContext(
            "/api/ocorrencias",
            ServidorWeb::processarOcorrencias
        );

        servidor.createContext(
            "/",
            ServidorWeb::servirPagina
        );

        servidor.start();

        System.out.println();

        System.out.println(
            "========================================"
        );

        System.out.println(
            "          CIDADAOCONECTA WEB            "
        );

        System.out.println(
            "          Observatorio Urbano           "
        );

        System.out.println(
            "========================================"
        );

        System.out.println(
            "Servidor iniciado com sucesso!"
        );

        System.out.println(
            "Endereco: http://localhost:" + PORTA
        );

        System.out.println();

        System.out.println(
            "ROTAS DISPONIVEIS:"
        );

        System.out.println(
            "POST /api/ocorrencias"
        );

        System.out.println(
            "GET /api/ocorrencias?protocolo=CC-..."
        );

        System.out.println(
            "GET /api/ocorrencias/publicas"
        );

        System.out.println(
            "========================================"
        );
    }

    // ==========================================
    // 01 - ROTEAMENTO DA API
    // ==========================================

    private static void processarOcorrencias(
            HttpExchange requisicao)
            throws IOException {

        String caminho =
                requisicao.getRequestURI().getPath();

        String metodo =
                requisicao.getRequestMethod();

        // LISTAGEM RESUMIDA DO OBSERVATÓRIO

        if (
            "/api/ocorrencias/publicas".equals(caminho)
        ) {

            if ("GET".equals(metodo)) {

                listarOcorrenciasPublicas(requisicao);

            } else {

                requisicao.getResponseHeaders().set(
                    "Allow",
                    "GET"
                );

                responderErro(
                    requisicao,
                    405,
                    "Metodo nao permitido."
                );
            }

            return;
        }

        // CADASTRO E CONSULTA INDIVIDUAL

        if (
            !"/api/ocorrencias".equals(caminho)
        ) {

            responderErro(
                requisicao,
                404,
                "Rota nao encontrada."
            );

            return;
        }

        switch (metodo) {

            case "POST":

                cadastrarOcorrencia(requisicao);

                break;

            case "GET":

                consultarOcorrencia(requisicao);

                break;

            default:

                requisicao.getResponseHeaders().set(
                    "Allow",
                    "GET, POST"
                );

                responderErro(
                    requisicao,
                    405,
                    "Metodo nao permitido."
                );

                break;
        }
    }

    // ==========================================
    // 02 - CADASTRAR OCORRÊNCIA
    // POST /api/ocorrencias
    // ==========================================

    private static void cadastrarOcorrencia(
            HttpExchange requisicao)
            throws IOException {

        String tipo = requisicao
                .getRequestHeaders()
                .getFirst("Content-Type");

        if (
            tipo == null ||
            !tipo.toLowerCase(Locale.ROOT).startsWith(
                "application/x-www-form-urlencoded"
            )
        ) {

            responderErro(
                requisicao,
                415,
                "Formato de envio nao suportado."
            );

            return;
        }

        byte[] corpo = requisicao
                .getRequestBody()
                .readNBytes(LIMITE_CORPO + 1);

        if (corpo.length > LIMITE_CORPO) {

            responderErro(
                requisicao,
                413,
                "Dados enviados muito grandes."
            );

            return;
        }

        try {

            Map<String, String> dados =
                    interpretarFormulario(
                        new String(
                            corpo,
                            StandardCharsets.UTF_8
                        )
                    );

            // LOCALIZAR CATEGORIA

            String codigoCategoria =
                    dados.getOrDefault(
                        "categoria",
                        ""
                    );

            Categoria categoriaSelecionada = null;

            List<Categoria> categorias =
                    categoriaDAO.listarTodas();

            for (Categoria categoria : categorias) {

                if (
                    categoria.getCodigo() != null &&
                    categoria.getCodigo().equalsIgnoreCase(
                        codigoCategoria
                    ) &&
                    categoria.isAtivo()
                ) {

                    categoriaSelecionada = categoria;

                    break;
                }
            }

            if (categoriaSelecionada == null) {

                throw new IllegalArgumentException(
                    "Selecione uma categoria valida."
                );
            }

            // CRIAR OCORRÊNCIA

            Ocorrencia ocorrencia = new Ocorrencia(
                dados.getOrDefault("titulo", ""),
                dados.getOrDefault("descricao", ""),
                dados.getOrDefault("cidade", ""),
                dados.getOrDefault("uf", ""),
                dados.getOrDefault("bairro", ""),
                dados.getOrDefault("rua", ""),
                dados.getOrDefault("numero", ""),
                dados.getOrDefault("referencia", "")
            );

            ocorrencia.setCategorias(
                List.of(categoriaSelecionada)
            );

            // CADASTRAR NO ORACLE

            service.cadastrar(ocorrencia);

            // RESPOSTA PARA registrar.js

            String resposta = "{"
                + "\"protocolo\":"
                + jsonString(
                    ocorrencia.getProtocolo()
                ) + ","

                + "\"status\":"
                + jsonString(
                    ocorrencia.getStatus()
                )
                + "}";

            responder(
                requisicao,
                201,
                resposta
            );

            System.out.println(
                "Ocorrencia cadastrada: "
                + ocorrencia.getProtocolo()
            );

        } catch (IllegalArgumentException erro) {

            responderErro(
                requisicao,
                400,
                erro.getMessage()
            );

        } catch (SQLException erro) {

            System.err.println(
                "Erro Oracle no cadastro: "
                + erro.getMessage()
            );

            responderErro(
                requisicao,
                500,
                "Falha ao cadastrar no banco de dados."
            );

        } catch (Exception erro) {

            System.err.println(
                "Erro inesperado no cadastro: "
                + erro.getMessage()
            );

            responderErro(
                requisicao,
                500,
                "Erro interno ao cadastrar a ocorrencia."
            );
        }
    }

    // ==========================================
    // 03 - CONSULTAR PELO PROTOCOLO
    // GET /api/ocorrencias?protocolo=CC-...
    // ==========================================

    private static void consultarOcorrencia(
            HttpExchange requisicao)
            throws IOException {

        try {

            String consulta =
                requisicao.getRequestURI().getRawQuery();

            Map<String, String> parametros =
                    interpretarFormulario(
                        consulta == null ? "" : consulta
                    );

            String protocolo = parametros
                    .getOrDefault("protocolo", "")
                    .trim()
                    .toUpperCase(Locale.ROOT);

            if (
                protocolo.isBlank() ||
                protocolo.length() > 80 ||
                !protocolo.matches("CC-[A-Z0-9-]+")
            ) {

                responderErro(
                    requisicao,
                    400,
                    "Informe um protocolo valido do CidadaoConecta."
                );

                return;
            }

            // CONSULTA NO ORACLE

            Ocorrencia ocorrencia =
                    service.buscarPorProtocolo(
                        protocolo
                    );

            if (ocorrencia == null) {

                responderErro(
                    requisicao,
                    404,
                    "Ocorrencia nao encontrada."
                );

                return;
            }

            // GERAR JSON PARA consultar.js

            String resposta =
                    montarJsonConsulta(ocorrencia);

            responder(
                requisicao,
                200,
                resposta
            );

            System.out.println(
                "Ocorrencia consultada: "
                + protocolo
            );

        } catch (IllegalArgumentException erro) {

            responderErro(
                requisicao,
                400,
                "Protocolo invalido."
            );

        } catch (SQLException erro) {

            System.err.println(
                "Erro Oracle na consulta: "
                + erro.getMessage()
            );

            responderErro(
                requisicao,
                500,
                "Falha ao consultar o banco de dados."
            );

        } catch (Exception erro) {

            System.err.println(
                "Erro inesperado na consulta: "
                + erro.getMessage()
            );

            responderErro(
                requisicao,
                500,
                "Erro interno ao consultar a ocorrencia."
            );
        }
    }

    // ==========================================
    // 04 - LISTAR OCORRÊNCIAS
    // GET /api/ocorrencias/publicas
    // ==========================================

    private static void listarOcorrenciasPublicas(
            HttpExchange requisicao)
            throws IOException {

        try {

            // Aproveita o DAO existente.
            List<Ocorrencia> ocorrencias =
                    ocorrenciaDAO.listarTodas();

            StringBuilder json =
                    new StringBuilder();

            json.append("{\"ocorrencias\":[");

            int quantidade = 0;

            for (Ocorrencia ocorrencia : ocorrencias) {

                if (ocorrencia == null) {
                    continue;
                }

                if (quantidade > 0) {
                    json.append(",");
                }

                json.append(
                    montarJsonPublico(ocorrencia)
                );

                quantidade++;
            }

            json.append("]}");

            responder(
                requisicao,
                200,
                json.toString()
            );

            System.out.println(
                "Observatorio consultado: "
                + quantidade
                + " ocorrencia(s)."
            );

        } catch (SQLException erro) {

            System.err.println(
                "Erro Oracle na listagem: "
                + erro.getMessage()
            );

            responderErro(
                requisicao,
                500,
                "Falha ao carregar as ocorrencias."
            );

        } catch (Exception erro) {

            System.err.println(
                "Erro inesperado na listagem: "
                + erro.getMessage()
            );

            responderErro(
                requisicao,
                500,
                "Erro interno ao carregar o Observatorio."
            );
        }
    }

    // ==========================================
    // 05 - JSON DA CONSULTA INDIVIDUAL
    // ==========================================

    private static String montarJsonConsulta(
            Ocorrencia ocorrencia) {

        String categoria =
                obterNomesCategorias(ocorrencia);

        String dataCadastro =
                obterDataCadastro(ocorrencia);

        return "{"

            + "\"protocolo\":"
            + jsonString(
                ocorrencia.getProtocolo()
            ) + ","

            + "\"titulo\":"
            + jsonString(
                ocorrencia.getTitulo()
            ) + ","

            + "\"descricao\":"
            + jsonString(
                ocorrencia.getDescricao()
            ) + ","

            + "\"categoria\":"
            + jsonString(
                categoria
            ) + ","

            + "\"bairro\":"
            + jsonString(
                ocorrencia.getBairro()
            ) + ","

            + "\"cidade\":"
            + jsonString(
                ocorrencia.getCidade()
            ) + ","

            + "\"uf\":"
            + jsonString(
                ocorrencia.getUf()
            ) + ","

            + "\"status\":"
            + jsonString(
                ocorrencia.getStatus()
            ) + ","

            + "\"dataCadastro\":"
            + jsonString(
                dataCadastro
            )

            + "}";
    }

    // ==========================================
    // 06 - JSON RESUMIDO DO OBSERVATÓRIO
    // ==========================================

    private static String montarJsonPublico(
            Ocorrencia ocorrencia) {

        String categoria =
                obterCodigoCategoria(ocorrencia);

        String dataCadastro =
                obterDataCadastro(ocorrencia);

        // Não retornar:
        // - protocolo
        // - ID interno
        // - descrição completa
        // - rua e número
        // - referência
        // - informações do usuário

        return "{"

            + "\"titulo\":"
            + jsonString(
                ocorrencia.getTitulo()
            ) + ","

            + "\"categoria\":"
            + jsonString(
                categoria
            ) + ","

            + "\"bairro\":"
            + jsonString(
                ocorrencia.getBairro()
            ) + ","

            + "\"cidade\":"
            + jsonString(
                ocorrencia.getCidade()
            ) + ","

            + "\"uf\":"
            + jsonString(
                ocorrencia.getUf()
            ) + ","

            + "\"status\":"
            + jsonString(
                ocorrencia.getStatus()
            ) + ","

            + "\"dataCadastro\":"
            + jsonString(
                dataCadastro
            )

            + "}";
    }

    // ==========================================
    // 07 - DADOS AUXILIARES
    // ==========================================

    private static String obterNomesCategorias(
            Ocorrencia ocorrencia) {

        List<Categoria> categorias =
                ocorrencia.getCategorias();

        if (
            categorias == null ||
            categorias.isEmpty()
        ) {

            return "Não informada";
        }

        StringBuilder nomes =
                new StringBuilder();

        for (Categoria categoria : categorias) {

            if (categoria == null) {
                continue;
            }

            if (nomes.length() > 0) {
                nomes.append(", ");
            }

            nomes.append(
                categoria.getNome()
            );
        }

        return nomes.length() > 0
                ? nomes.toString()
                : "Não informada";
    }

    private static String obterCodigoCategoria(
            Ocorrencia ocorrencia) {

        List<Categoria> categorias =
                ocorrencia.getCategorias();

        if (
            categorias == null ||
            categorias.isEmpty()
        ) {

            return "outro";
        }

        Categoria primeira = categorias.get(0);

        if (
            primeira == null ||
            primeira.getCodigo() == null
        ) {

            return "outro";
        }

        return primeira.getCodigo();
    }

    private static String obterDataCadastro(
            Ocorrencia ocorrencia) {

        return ocorrencia.getDataCadastro() == null
                ? ""
                : ocorrencia.getDataCadastro().toString();
    }

    // ==========================================
    // 08 - INTERPRETAR PARÂMETROS
    // ==========================================

    private static Map<String, String> interpretarFormulario(
            String conteudo) {

        Map<String, String> dados =
                new HashMap<>();

        if (
            conteudo == null ||
            conteudo.isBlank()
        ) {

            return dados;
        }

        for (String parte : conteudo.split("&")) {

            if (parte.isEmpty()) {
                continue;
            }

            String[] par = parte.split("=", 2);

            String chave = URLDecoder.decode(
                par[0],
                StandardCharsets.UTF_8
            );

            String valor = URLDecoder.decode(
                par.length > 1 ? par[1] : "",
                StandardCharsets.UTF_8
            );

            dados.put(chave, valor);
        }

        return dados;
    }

    // ==========================================
    // 09 - SERVIR FRONTEND
    // ==========================================

    private static void servirPagina(
            HttpExchange requisicao)
            throws IOException {

        if (
            !"GET".equals(
                requisicao.getRequestMethod()
            )
        ) {

            responderErro(
                requisicao,
                405,
                "Metodo nao permitido."
            );

            return;
        }

        String caminho =
                requisicao.getRequestURI().getPath();

        if (caminho.equals("/")) {
            caminho = "/index.html";
        }

        Path arquivo = FRONTEND
                .resolve(caminho.substring(1))
                .normalize();

        if (
            !arquivo.startsWith(FRONTEND) ||
            !Files.isRegularFile(arquivo)
        ) {

            responderErro(
                requisicao,
                404,
                "Pagina nao encontrada."
            );

            return;
        }

        String tipo;

        if (caminho.endsWith(".html")) {

            tipo = "text/html; charset=UTF-8";

        } else if (caminho.endsWith(".css")) {

            tipo = "text/css; charset=UTF-8";

        } else if (caminho.endsWith(".js")) {

            tipo = "text/javascript; charset=UTF-8";

        } else if (caminho.endsWith(".svg")) {

            tipo = "image/svg+xml";

        } else if (caminho.endsWith(".png")) {

            tipo = "image/png";

        } else if (
            caminho.endsWith(".jpg") ||
            caminho.endsWith(".jpeg")
        ) {

            tipo = "image/jpeg";

        } else if (caminho.endsWith(".webp")) {

            tipo = "image/webp";

        } else if (caminho.endsWith(".ico")) {

            tipo = "image/x-icon";

        } else {

            responderErro(
                requisicao,
                403,
                "Tipo de arquivo nao permitido."
            );

            return;
        }

        byte[] conteudo =
                Files.readAllBytes(arquivo);

        requisicao.getResponseHeaders().set(
            "Content-Type",
            tipo
        );

        requisicao.getResponseHeaders().set(
            "X-Content-Type-Options",
            "nosniff"
        );

        requisicao.sendResponseHeaders(
            200,
            conteudo.length
        );

        try (
            var saida =
                requisicao.getResponseBody()
        ) {

            saida.write(conteudo);
        }
    }

    // ==========================================
    // 10 - RESPOSTAS HTTP
    // ==========================================

    private static void responderErro(
            HttpExchange requisicao,
            int status,
            String mensagem)
            throws IOException {

        responder(
            requisicao,
            status,
            "{\"erro\":"
                + jsonString(mensagem)
                + "}"
        );
    }

    private static void responder(
            HttpExchange requisicao,
            int status,
            String mensagem)
            throws IOException {

        byte[] resposta = mensagem.getBytes(
            StandardCharsets.UTF_8
        );

        requisicao.getResponseHeaders().set(
            "Content-Type",
            "application/json; charset=UTF-8"
        );

        requisicao.getResponseHeaders().set(
            "X-Content-Type-Options",
            "nosniff"
        );

        requisicao.getResponseHeaders().set(
            "Cache-Control",
            "no-store"
        );

        requisicao.sendResponseHeaders(
            status,
            resposta.length
        );

        try (
            var saida =
                requisicao.getResponseBody()
        ) {

            saida.write(resposta);
        }
    }

    // ==========================================
    // 11 - CONVERTER STRINGS PARA JSON
    // ==========================================

    private static String jsonString(
            String valor) {

        return "\""
            + escaparJson(valor)
            + "\"";
    }

    private static String escaparJson(
            String texto) {

        if (texto == null) {
            return "";
        }

        StringBuilder resultado =
                new StringBuilder();

        for (int i = 0; i < texto.length(); i++) {

            char caractere =
                    texto.charAt(i);

            switch (caractere) {

                case '\\':

                    resultado.append("\\\\");

                    break;

                case '"':

                    resultado.append("\\\"");

                    break;

                case '\n':

                    resultado.append("\\n");

                    break;

                case '\r':

                    resultado.append("\\r");

                    break;

                case '\t':

                    resultado.append("\\t");

                    break;

                default:

                    if (caractere < 0x20) {

                        resultado.append(
                            String.format(
                                "\\u%04x",
                                (int) caractere
                            )
                        );

                    } else {

                        resultado.append(caractere);
                    }
            }
        }

        return resultado.toString();
    }
}