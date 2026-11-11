
/* ==========================================
   CIDADÃOCONECTA — URBAN OS
   CONSULTA DE PROTOCOLOS
   VERSÃO DEMONSTRATIVA
   ========================================== */

"use strict";

document.addEventListener(
  "DOMContentLoaded",
  iniciarConsulta
);

function iniciarConsulta() {
  const form = document.getElementById("formConsulta");

  if (!form) {
    return;
  }

  const $ = (id) => document.getElementById(id);

  const campoProtocolo = $("protocolo");
  const mensagem = $("mensagemConsulta");
  const resultado = $("resultadoConsulta");

  const CHAVE_REGISTROS =
    "cidadaoconecta_registros_demo";

  const nomesCategorias = {
    pavimentacao: "Ruas e calçadas",
    iluminacao: "Iluminação pública",
    saneamento: "Água e saneamento",
    acessibilidade: "Acessibilidade",
    limpeza: "Limpeza urbana",
    outro: "Outro problema"
  };

  /* ------------------------------------------
     MENSAGENS
     ------------------------------------------ */

  function limparMensagem() {
    mensagem.textContent = "";
    mensagem.hidden = true;
    campoProtocolo.removeAttribute("aria-invalid");
  }

  function mostrarMensagem(texto) {
    mensagem.textContent = texto;
    mensagem.hidden = false;
    campoProtocolo.setAttribute("aria-invalid", "true");
  }

  /* ------------------------------------------
     LER REGISTROS LOCAIS
     ------------------------------------------ */

  function carregarRegistros() {
    try {
      const conteudo = localStorage.getItem(
        CHAVE_REGISTROS
      );

      if (!conteudo) {
        return [];
      }

      const registros = JSON.parse(conteudo);

      return Array.isArray(registros)
        ? registros
        : [];
    } catch {
      mostrarMensagem(
        "Não foi possível acessar os registros " +
        "salvos neste navegador."
      );

      return null;
    }
  }

  /* ------------------------------------------
     FORMATAR DATA
     ------------------------------------------ */

  function formatarData(valor) {
    if (!valor) {
      return "Não informada";
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

  /* ------------------------------------------
     EXIBIR DADOS PÚBLICOS
     ------------------------------------------ */

  function apresentarRegistro(registro) {
    const categoria =
      nomesCategorias[registro.categoria] ||
      "Outra categoria";

    const data = formatarData(registro.criadoEm);

    $("resultadoCodigo").textContent =
      registro.idLocal || "";

    $("resultadoNome").textContent =
      registro.titulo || "Sem título";

    $("resultadoCategoria").textContent =
      categoria;

    $("resultadoBairro").textContent =
      registro.bairro || "Não informado";

    $("resultadoCidade").textContent =
      [registro.cidade, registro.uf]
        .filter(Boolean)
        .join(" / ") || "Não informado";

    $("resultadoData").textContent = data;

    $("resultadoSituacao").textContent =
      "Registrada localmente";

    $("resultadoDescricao").textContent =
      registro.descricao || "Sem descrição";

    $("historicoData").textContent =
      `Registro demonstrativo realizado em ${data}.`;

    resultado.hidden = false;

    resultado.scrollIntoView({
      behavior: "smooth",
      block: "start"
    });
  }

  /* ------------------------------------------
     BUSCAR PROTOCOLO
     ------------------------------------------ */

  function consultarProtocolo() {
    limparMensagem();
    resultado.hidden = true;

    const protocolo = campoProtocolo.value
      .trim()
      .toUpperCase();

    campoProtocolo.value = protocolo;

    if (!protocolo) {
      mostrarMensagem(
        "Digite o número do protocolo para continuar."
      );
      campoProtocolo.focus();
      return;
    }

    if (protocolo.length > 80) {
      mostrarMensagem("O protocolo informado é inválido.");
      campoProtocolo.focus();
      return;
    }

    const registros = carregarRegistros();

    if (registros === null) {
      return;
    }

    const registro = registros.find((item) => {
      if (
        !item ||
        typeof item !== "object" ||
        typeof item.idLocal !== "string"
      ) {
        return false;
      }

      return item.idLocal.toUpperCase() === protocolo;
    });

    if (!registro) {
      mostrarMensagem(
        "Não encontramos esse protocolo neste navegador. " +
        "Confira o código informado. Se o registro foi feito " +
        "em outro dispositivo, ele ainda não estará disponível " +
        "nesta versão demonstrativa."
      );
      return;
    }

    apresentarRegistro(registro);
  }

  /* ------------------------------------------
     ENVIAR CONSULTA
     ------------------------------------------ */

  form.addEventListener("submit", (evento) => {
    evento.preventDefault();
    consultarProtocolo();
  });

  /* ------------------------------------------
     LIMPAR E CONSULTAR NOVAMENTE
     ------------------------------------------ */

  $("novaConsulta").addEventListener("click", () => {
    form.reset();

    resultado.hidden = true;
    limparMensagem();

    campoProtocolo.focus();

    form.scrollIntoView({
      behavior: "smooth",
      block: "center"
    });
  });

  /* ------------------------------------------
     PROTOCOLO RECEBIDO PELA URL
     ------------------------------------------ */

  const parametros = new URLSearchParams(
    window.location.search
  );

  const protocoloDaUrl = parametros.get("protocolo");

  if (protocoloDaUrl) {
    campoProtocolo.value = protocoloDaUrl;
    consultarProtocolo();
  }
}