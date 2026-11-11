
/* =====================================================
   CIDADÃOCONECTA — URBAN OS
   DETALHES DA OCORRÊNCIA

   Versão demonstrativa:
   - Consulta no localStorage
   - Exibe informações do registro local
   - Monta linha do tempo sem inventar eventos
   - Copia o protocolo
   - Abre a consulta individual
   - Não envia informações a servidores
   ===================================================== */

"use strict";

document.addEventListener(
  "DOMContentLoaded",
  iniciarDetalhes
);

function iniciarDetalhes() {

  /* ===================================================
     01 — CONFIGURAÇÕES
     =================================================== */

  const $ = (id) =>
    document.getElementById(id);

  const CHAVE_REGISTROS =
    "cidadaoconecta_registros_demo";

  const categorias = {
    pavimentacao: {
      nome: "Ruas e calçadas",
      icone: "▤"
    },
    iluminacao: {
      nome: "Iluminação pública",
      icone: "☼"
    },
    saneamento: {
      nome: "Água e saneamento",
      icone: "◉"
    },
    acessibilidade: {
      nome: "Acessibilidade",
      icone: "♿"
    },
    limpeza: {
      nome: "Limpeza urbana",
      icone: "✳"
    },
    outro: {
      nome: "Outro problema",
      icone: "◫"
    }
  };

  const parametros = new URLSearchParams(
    window.location.search
  );

  const protocolo = (
    parametros.get("id") ||
    parametros.get("protocolo") ||
    ""
  ).trim();

  let registroAtual = null;

  /* ===================================================
     02 — FUNÇÕES AUXILIARES
     =================================================== */

  function textoSeguro(valor, alternativa = "Não informado") {
    const texto = String(valor ?? "").trim();

    return texto || alternativa;
  }

  function formatarData(valor, incluirHorario = false) {
    if (!valor) {
      return "Data não informada";
    }

    const data = new Date(valor);

    if (Number.isNaN(data.getTime())) {
      return "Data não informada";
    }

    const opcoes = {
      day: "2-digit",
      month: "long",
      year: "numeric"
    };

    if (incluirHorario) {
      opcoes.hour = "2-digit";
      opcoes.minute = "2-digit";
    }

    return new Intl.DateTimeFormat(
      "pt-BR",
      opcoes
    ).format(data);
  }

  function criarElemento(tag, classe = "", texto) {
    const elemento =
      document.createElement(tag);

    if (classe) {
      elemento.className = classe;
    }

    if (texto !== undefined) {
      elemento.textContent = String(texto);
    }

    return elemento;
  }

  function obterCategoria(codigo) {
    return categorias[codigo] || {
      nome: "Outra categoria",
      icone: "◫"
    };
  }

  /* ===================================================
     03 — CARREGAR REGISTROS
     =================================================== */

  function buscarRegistro(id) {
    const conteudo = localStorage.getItem(
      CHAVE_REGISTROS
    );

    if (!conteudo) {
      return null;
    }

    const registros = JSON.parse(conteudo);

    if (!Array.isArray(registros)) {
      throw new Error(
        "O armazenamento não contém uma lista válida."
      );
    }

    return registros.find((item) => {
      return (
        item &&
        typeof item === "object" &&
        typeof item.idLocal === "string" &&
        item.idLocal.trim().toUpperCase() ===
          id.toUpperCase()
      );
    }) || null;
  }

  /* ===================================================
     04 — ESTADO DE ERRO
     =================================================== */

  function mostrarErro(mensagem) {
    $("detalhesCarregando").hidden = true;
    $("detalhesRegistro").hidden = true;

    $("detalhesMensagemErro").textContent =
      mensagem;

    $("detalhesNaoEncontrado").hidden = false;
  }

  /* ===================================================
     05 — PREENCHER CARTÃO PRINCIPAL
     =================================================== */

  function preencherDestaque(registro) {
    const categoria = obterCategoria(
      registro.categoria
    );

    $("detalhesStatus").textContent =
      "Registrada localmente";

    $("detalhesIcone").textContent =
      categoria.icone;

    $("detalhesCategoria").textContent =
      categoria.nome;

    $("detalhesTitulo").textContent =
      textoSeguro(
        registro.titulo,
        "Ocorrência sem título"
      );

    const localizacao = [
      registro.bairro,
      registro.cidade,
      registro.uf
    ]
      .map((valor) => String(valor || "").trim())
      .filter(Boolean)
      .join(" · ");

    $("detalhesLocalizacao").textContent =
      localizacao || "Localidade não informada";

    $("detalhesData").textContent =
      formatarData(
        registro.criadoEm,
        true
      );

    // A versão local registra apenas a data de criação.
    // Não existe data real de atualização posterior.
    $("detalhesAtualizacao").textContent =
      "Sem atualizações posteriores";
  }

  /* ===================================================
     06 — INFORMAÇÕES DA OCORRÊNCIA
     =================================================== */

  function preencherInformacoes(registro) {
    const categoria = obterCategoria(
      registro.categoria
    );

    // A descrição foi inserida pelo usuário
    // neste mesmo navegador.
    //
    // Não apresentamos rua, número ou referência
    // nesta página. Em uma futura listagem pública,
    // será necessário revisar os dados de publicação.

    $("detalhesDescricao").textContent =
      textoSeguro(
        registro.descricao,
        "Nenhuma descrição disponível."
      );

    $("detalhesBairro").textContent =
      textoSeguro(registro.bairro);

    const cidade = [
      registro.cidade,
      registro.uf
    ]
      .map((valor) => String(valor || "").trim())
      .filter(Boolean)
      .join(" / ");

    $("detalhesCidade").textContent =
      cidade || "Não informado";

    $("detalhesCategoriaResumo").textContent =
      categoria.nome;
  }

  /* ===================================================
     07 — CRIAR EVENTOS DA LINHA DO TEMPO
     =================================================== */

  function criarEventoTimeline({
    tipo,
    marcador,
    etiqueta,
    titulo,
    descricao,
    data
  }) {
    const item = criarElemento(
      "li",
      `detalhes-timeline-item ${tipo}`
    );

    const circulo = criarElemento(
      "span",
      "detalhes-timeline-marcador",
      marcador
    );

    circulo.setAttribute(
      "aria-hidden",
      "true"
    );

    const conteudo = criarElemento(
      "div",
      "detalhes-timeline-conteudo"
    );

    const identificacao = criarElemento(
      "span",
      "detalhes-timeline-etiqueta",
      etiqueta
    );

    const tituloElemento = criarElemento(
      "h4",
      "",
      titulo
    );

    const texto = criarElemento(
      "p",
      "",
      descricao
    );

    conteudo.append(
      identificacao,
      tituloElemento,
      texto
    );

    if (data) {
      const dataElemento = criarElemento(
        "span",
        "detalhes-timeline-data",
        data
      );

      conteudo.appendChild(
        dataElemento
      );
    }

    item.append(
      circulo,
      conteudo
    );

    return item;
  }

  /* ===================================================
     08 — MONTAR LINHA DO TEMPO

     Somente o primeiro evento é real
     dentro da demonstração local.

     Os demais são etapas previstas,
     não eventos já realizados.
     =================================================== */

  function preencherLinhaTempo(registro) {
    const timeline = $("detalhesLinhaTempo");

    timeline.replaceChildren();

    const fragmento =
      document.createDocumentFragment();

    const dataRegistro = formatarData(
      registro.criadoEm,
      true
    );

    // EVENTO REAL DA DEMONSTRAÇÃO

    fragmento.appendChild(
      criarEventoTimeline({
        tipo: "concluido",
        marcador: "✓",
        etiqueta: "EVENTO REGISTRADO",
        titulo: "Ocorrência salva no navegador",
        descricao:
          "O registro foi criado nesta demonstração " +
          "e recebeu um identificador local. " +
          "Nenhuma solicitação foi enviada à prefeitura.",
        data: dataRegistro
      })
    );

    // ETAPA FUTURA — NÃO CONCLUÍDA

    fragmento.appendChild(
      criarEventoTimeline({
        tipo: "futuro",
        marcador: "2",
        etiqueta: "ETAPA FUTURA · NÃO INICIADA",
        titulo: "Análise e atualização da situação",
        descricao:
          "Após a implementação do backend e de " +
          "um fluxo autorizado de atendimento, " +
          "poderão existir atualizações de situação. " +
          "Esta etapa ainda não aconteceu.",
        data: "Sem data registrada"
      })
    );

    // ETAPA FUTURA — NÃO CONCLUÍDA

    fragmento.appendChild(
      criarEventoTimeline({
        tipo: "futuro",
        marcador: "3",
        etiqueta: "ETAPA FUTURA · NÃO INICIADA",
        titulo: "Confirmação da solução",
        descricao:
          "O projeto prevê permitir que o autor " +
          "confirme se a situação foi resolvida. " +
          "A funcionalidade ainda não está disponível.",
        data: "Sem data registrada"
      })
    );

    timeline.appendChild(fragmento);
  }

  /* ===================================================
     09 — PROTOCOLO E CONSULTA INDIVIDUAL
     =================================================== */

  function preencherProtocolo(registro) {
    const codigo = textoSeguro(
      registro.idLocal
    );

    $("detalhesCodigo").textContent =
      codigo;

    $("detalhesConsultar").href =
      "./consultar.html?protocolo=" +
      encodeURIComponent(codigo);
  }

  /* ===================================================
     10 — COPIAR IDENTIFICADOR
     =================================================== */

  async function copiarIdentificador() {
    if (!registroAtual) {
      return;
    }

    const codigo = registroAtual.idLocal;
    const mensagem = $("detalhesMensagemCopia");

    mensagem.textContent = "";

    try {
      if (
        !navigator.clipboard ||
        typeof navigator.clipboard.writeText !==
          "function"
      ) {
        throw new Error(
          "Área de transferência indisponível."
        );
      }

      await navigator.clipboard.writeText(codigo);

      mensagem.textContent =
        "Protocolo copiado com sucesso.";

    } catch {
      // Alternativa: selecionar o código
      // para que o usuário copie manualmente.
      const elemento = $("detalhesCodigo");

      const selecao = window.getSelection();
      const intervalo = document.createRange();

      intervalo.selectNodeContents(elemento);

      selecao.removeAllRanges();
      selecao.addRange(intervalo);

      mensagem.textContent =
        "Código selecionado. Pressione Ctrl + C para copiar.";
    }
  }

  $("detalhesCopiar").addEventListener(
    "click",
    copiarIdentificador
  );

  /* ===================================================
     11 — EXIBIR INFORMAÇÕES
     =================================================== */

  function renderizarRegistro(registro) {
    preencherDestaque(registro);

    preencherInformacoes(registro);

    preencherLinhaTempo(registro);

    preencherProtocolo(registro);

    $("detalhesCarregando").hidden = true;
    $("detalhesNaoEncontrado").hidden = true;
    $("detalhesRegistro").hidden = false;

    document.title =
      `${textoSeguro(registro.titulo)} | CidadãoConecta`;
  }

  /* ===================================================
     12 — INICIALIZAÇÃO
     =================================================== */

  if (!protocolo) {
    mostrarErro(
      "Nenhum identificador foi informado. " +
      "Acesse a página Comunidade e selecione " +
      "uma ocorrência para visualizar os detalhes."
    );

    return;
  }

  try {
    registroAtual = buscarRegistro(protocolo);

    if (!registroAtual) {
      mostrarErro(
        "Não encontramos esse registro no armazenamento " +
        "deste navegador. Verifique se você está usando " +
        "o mesmo endereço e a mesma porta em que " +
        "a ocorrência foi cadastrada."
      );

      return;
    }

    renderizarRegistro(registroAtual);

  } catch (erro) {
    console.error(
      "Erro ao abrir detalhes:",
      erro
    );

    mostrarErro(
      "Houve um problema ao consultar os registros " +
      "salvos neste navegador. Os dados existentes " +
      "não foram apagados."
    );
  }
}