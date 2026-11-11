
/* =====================================================
   CIDADÃOCONECTA — URBAN OS
   CONSULTAR.JS

   Consulta pelo protocolo
   API Java -> Oracle
   Exibição dos detalhes em pop-up
   ===================================================== */

"use strict";

document.addEventListener("DOMContentLoaded", iniciarConsulta);

function iniciarConsulta() {

  const $ = (id) => document.getElementById(id);

  const form = $("formConsulta");
  const campoProtocolo = $("protocolo");
  const mensagem = $("mensagemConsulta");

  const modal = $("modalDetalhes");
  const botaoFechar = $("fecharModalDetalhes");
  const botaoNovaConsulta = $("novaConsulta");
  const botaoCopiar = $("copiarProtocoloConsulta");

  if (!form || !campoProtocolo || !mensagem || !modal) {
    console.error("Elementos da consulta não encontrados.");
    return;
  }

  const botaoConsultar = form.querySelector(
    'button[type="submit"]'
  );

  const API = "/api/ocorrencias";

  const categorias = {
    pavimentacao: "Ruas e calçadas",
    iluminacao: "Iluminação pública",
    saneamento: "Água e saneamento",
    acessibilidade: "Acessibilidade",
    limpeza: "Limpeza urbana",
    outro: "Outro problema"
  };

  const statusNomes = {
    ABERTA: "Aberta",
    EM_ANALISE: "Em análise",
    EM_ANDAMENTO: "Em andamento",
    RESOLVIDA: "Resolvida",
    CONCLUIDA: "Concluída",
    CANCELADA: "Cancelada"
  };

  let consultaEmAndamento = false;
  let protocoloAtual = "";

  /* ==========================================
     FUNÇÕES AUXILIARES
     ========================================== */

  function texto(valor, alternativa = "Não informado") {
    const resultado = String(valor ?? "").trim();
    return resultado || alternativa;
  }

  function normalizar(valor) {
    return String(valor ?? "")
      .normalize("NFD")
      .replace(/[\u0300-\u036f]/g, "")
      .trim()
      .toLowerCase();
  }

  function formatarCategoria(valor) {

    if (!valor) {
      return "Não informada";
    }

    const equivalencias = {
      "ruas e calcadas": "pavimentacao",
      "iluminacao publica": "iluminacao",
      "agua e saneamento": "saneamento",
      "limpeza urbana": "limpeza",
      "outro problema": "outro"
    };

    const partes = String(valor)
      .split(",")
      .map((parte) => normalizar(parte));

    return partes.map((parte) => {
      const codigo = equivalencias[parte] || parte;
      return categorias[codigo] || texto(parte);
    }).join(", ");
  }

  function normalizarStatus(valor) {
    return texto(valor, "")
      .toUpperCase()
      .replace(/\s+/g, "_");
  }

  function formatarStatus(valor) {

    const codigo = normalizarStatus(valor);

    if (!codigo) {
      return "Não informado";
    }

    return statusNomes[codigo] ||
      codigo.replaceAll("_", " ");
  }

  function formatarData(valor) {

    if (!valor) {
      return "Não informada";
    }

    const dataTexto = String(valor);

    // Preserva a hora de valores Oracle sem fuso horário.

    const partes = dataTexto.match(
      /^(\d{4})-(\d{2})-(\d{2})(?:[T ](\d{2}):(\d{2}))?/
    );

    if (partes) {

      const data =
        `${partes[3]}/${partes[2]}/${partes[1]}`;

      if (partes[4] && partes[5]) {
        return `${data} às ${partes[4]}:${partes[5]}`;
      }

      return data;
    }

    const data = new Date(valor);

    if (Number.isNaN(data.getTime())) {
      return "Não informada";
    }

    return new Intl.DateTimeFormat("pt-BR", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit"
    }).format(data);
  }

  function preencher(id, valor) {
    const elemento = $(id);

    if (elemento) {
      elemento.textContent = valor;
    }
  }

  /* ==========================================
     MENSAGENS
     ========================================== */

  function limparMensagem() {
    mensagem.textContent = "";
    mensagem.hidden = true;
    campoProtocolo.removeAttribute("aria-invalid");
  }

  function mostrarMensagem(conteudo) {
    mensagem.textContent = conteudo;
    mensagem.hidden = false;
    campoProtocolo.setAttribute("aria-invalid", "true");
  }

  /* ==========================================
     VALIDAR PROTOCOLO
     ========================================== */

  function validarProtocolo(valor) {

    if (!valor) {
      return "Digite o número do protocolo para continuar.";
    }

    if (
      valor.length > 80 ||
      !/^CC-[A-Z0-9-]+$/.test(valor)
    ) {
      return (
        "Informe um protocolo válido do CidadãoConecta, " +
        "começando com CC-."
      );
    }

    return "";
  }

  /* ==========================================
     PREENCHER POP-UP
     ========================================== */

  function apresentarRegistro(registro) {

    protocoloAtual = texto(registro.protocolo, "");

    const situacao = formatarStatus(registro.status);

    const data = formatarData(
      registro.dataCadastro
    );

    preencher("resultadoCodigo", protocoloAtual);

    preencher(
      "resultadoNome",
      texto(registro.titulo, "Sem título")
    );

    preencher(
      "resultadoCategoria",
      formatarCategoria(registro.categoria)
    );

    preencher(
      "resultadoBairro",
      texto(registro.bairro)
    );

    const cidade = [
      registro.cidade,
      registro.uf
    ]
      .filter(Boolean)
      .join(" / ");

    preencher(
      "resultadoCidade",
      cidade || "Não informado"
    );

    preencher("resultadoData", data);

    preencher("resultadoSituacao", situacao);

    preencher(
      "resultadoDescricao",
      texto(registro.descricao, "Sem descrição disponível")
    );

    preencher(
      "historicoData",
      registro.dataCadastro
        ? `Registro realizado em ${data}.`
        : "Registro cadastrado no sistema."
    );

    preencher(
      "historicoSituacao",
      `Situação informada pelo sistema: ${situacao}. ` +
      "Outras atualizações só serão exibidas quando " +
      "estiverem disponíveis na API."
    );

    const indicador = $("resultadoStatusBadge");

    if (indicador) {
      indicador.textContent = situacao;
      indicador.dataset.status =
        normalizarStatus(registro.status) || "NAO_INFORMADO";
    }

    preencher("mensagemCopiaConsulta", "");

    // O resultado é exibido dentro do dialog,
    // sem navegar para detalhes.html.

    if (!modal.open) {
      modal.showModal();
    }
  }

  /* ==========================================
     FECHAR MODAL
     ========================================== */

  function fecharModal() {

    if (modal.open) {
      modal.close();
    }
  }

  if (botaoFechar) {
    botaoFechar.addEventListener("click", fecharModal);
  }

  // Clique na área escura fecha a janela.

  modal.addEventListener("click", (evento) => {

    if (evento.target === modal) {
      fecharModal();
    }
  });

  // ESC é tratado nativamente pelo elemento dialog.

  /* ==========================================
     CONSULTAR API JAVA
     ========================================== */

  async function consultarProtocolo() {

    if (consultaEmAndamento) {
      return;
    }

    limparMensagem();

    const protocolo = campoProtocolo.value
      .trim()
      .toUpperCase();

    campoProtocolo.value = protocolo;

    const erroValidacao = validarProtocolo(protocolo);

    if (erroValidacao) {
      mostrarMensagem(erroValidacao);
      campoProtocolo.focus();
      return;
    }

    consultaEmAndamento = true;

    const textoOriginal = botaoConsultar
      ? botaoConsultar.innerHTML
      : "";

    if (botaoConsultar) {
      botaoConsultar.disabled = true;
      botaoConsultar.textContent = "Consultando...";
    }

    try {

      const endereco =
        API + "?protocolo=" +
        encodeURIComponent(protocolo);

      const resposta = await fetch(endereco, {
        method: "GET",
        headers: {
          Accept: "application/json"
        },
        cache: "no-store"
      });

      let dados;

      try {
        dados = await resposta.json();
      } catch {
        throw new Error(
          "O servidor retornou uma resposta inválida."
        );
      }

      if (resposta.status === 404) {
        mostrarMensagem(
          "Não encontramos uma ocorrência com esse protocolo. " +
          "Confira o código informado."
        );
        return;
      }

      if (!resposta.ok) {
        throw new Error(
          dados &&
          typeof dados.erro === "string"
            ? dados.erro
            : "Não foi possível consultar a ocorrência."
        );
      }

      if (
        !dados ||
        typeof dados.protocolo !== "string" ||
        !dados.protocolo.trim()
      ) {
        throw new Error(
          "Os dados retornados pelo servidor são inválidos."
        );
      }

      // Confere se a API retornou o registro solicitado.

      if (
        dados.protocolo.trim().toUpperCase() !==
        protocolo
      ) {
        throw new Error(
          "O servidor retornou um protocolo diferente do solicitado."
        );
      }

      apresentarRegistro(dados);

    } catch (erro) {

      console.error("Erro na consulta:", erro);

      mostrarMensagem(
        erro instanceof TypeError
          ? "Não foi possível conectar ao servidor Java."
          : (
              erro.message ||
              "Não foi possível consultar esta ocorrência."
            )
      );

    } finally {

      consultaEmAndamento = false;

      if (botaoConsultar) {
        botaoConsultar.disabled = false;
        botaoConsultar.innerHTML = textoOriginal;
      }
    }
  }

  /* ==========================================
     FORMULÁRIO
     ========================================== */

  form.addEventListener("submit", (evento) => {
    evento.preventDefault();
    consultarProtocolo();
  });

  campoProtocolo.addEventListener("input", () => {
    if (!mensagem.hidden) {
      limparMensagem();
    }
  });

  /* ==========================================
     NOVA CONSULTA
     ========================================== */

  if (botaoNovaConsulta) {

    botaoNovaConsulta.addEventListener("click", () => {

      fecharModal();

      form.reset();

      protocoloAtual = "";

      limparMensagem();

      const url = new URL(window.location.href);

      url.searchParams.delete("protocolo");

      window.history.replaceState(
        {},
        "",
        url.pathname + url.search + url.hash
      );

      campoProtocolo.focus();
    });
  }

  /* ==========================================
     COPIAR PROTOCOLO
     ========================================== */

  if (botaoCopiar) {

    botaoCopiar.addEventListener("click", async () => {

      if (!protocoloAtual) {
        return;
      }

      try {

        await navigator.clipboard.writeText(
          protocoloAtual
        );

        preencher(
          "mensagemCopiaConsulta",
          "Protocolo copiado com sucesso."
        );

      } catch {

        const elemento = $("resultadoCodigo");

        if (!elemento) {
          return;
        }

        const intervalo = document.createRange();

        intervalo.selectNodeContents(elemento);

        const selecao = window.getSelection();

        if (selecao) {
          selecao.removeAllRanges();
          selecao.addRange(intervalo);
        }

        preencher(
          "mensagemCopiaConsulta",
          "Protocolo selecionado. Pressione Ctrl + C para copiar."
        );
      }

    });
  }

  /* ==========================================
     PROTOCOLO PELA URL
     ========================================== */

  const parametros = new URLSearchParams(
    window.location.search
  );

  const protocoloDaUrl = parametros.get("protocolo");

  if (protocoloDaUrl) {

    campoProtocolo.value = protocoloDaUrl;

    consultarProtocolo();
  }
}