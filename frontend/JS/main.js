
/* =====================================================
   CIDADÃOCONECTA — URBAN OS
   JAVASCRIPT GLOBAL — MAIN.JS

   Funcionalidades:
   1. Navegação principal padronizada
   2. Links: Início, Serviços, Consultar e Comunidade
   3. Identificação automática da página ativa
   4. Menu responsivo para celulares
   5. Fechamento do menu ao navegar ou pressionar ESC
   6. Preservação do botão Registrar problema

   Estrutura:
   frontend/
     JS/main.js
     index.html
     servicos.html
     registrar.html
     consultar.html
     ocorrencias.html
   ===================================================== */

"use strict";

document.addEventListener("DOMContentLoaded", () => {
  configurarNavegacao();
  iniciarMenuMobile();
});

/* =====================================================
   01 — CONFIGURAÇÕES DA NAVEGAÇÃO
   ===================================================== */

const PAGINAS_CIDADAOCONECTA = [
  {
    arquivo: "index.html",
    titulo: "Início"
  },
  {
    arquivo: "servicos.html",
    titulo: "Serviços"
  },
  {
    arquivo: "consultar.html",
    titulo: "Consultar"
  },
  {
    arquivo: "ocorrencias.html",
    titulo: "Comunidade"
  }
];

/* =====================================================
   02 — IDENTIFICAR PÁGINA ATUAL
   ===================================================== */

function obterPaginaAtual() {
  const caminho = window.location.pathname;

  const arquivo = caminho.split("/").pop();

  return arquivo || "index.html";
}

/* =====================================================
   03 — IDENTIFICAR DESTINO DE UM LINK
   ===================================================== */

function obterArquivoDoLink(link) {
  try {
    const endereco = new URL(
      link.getAttribute("href"),
      window.location.href
    );

    return endereco.pathname.split("/").pop();

  } catch (erro) {
    return "";
  }
}

/* =====================================================
   04 — ATUALIZAR NAVEGAÇÃO
   ===================================================== */

function configurarNavegacao() {
  const paginaAtual = obterPaginaAtual();

  const navegacaoDesktop = document.querySelector(
    ".navegacao"
  );

  const navegacaoMobile = document.getElementById(
    "menuMobile"
  );

  atualizarLinks(
    navegacaoDesktop,
    paginaAtual,
    false
  );

  atualizarLinks(
    navegacaoMobile,
    paginaAtual,
    true
  );
}

/* =====================================================
   05 — ORGANIZAR LINKS
   ===================================================== */

function atualizarLinks(menu, paginaAtual, mobile) {
  if (!menu) {
    return;
  }

  const linksExistentes = [
    ...menu.querySelectorAll("a")
  ];

  // Identifica o link Registrar problema.
  const linkRegistrar = linksExistentes.find(
    (link) =>
      obterArquivoDoLink(link) === "registrar.html"
  );

  // Cria ou reaproveita os links principais.
  PAGINAS_CIDADAOCONECTA.forEach((pagina) => {
    let link = [
      ...menu.querySelectorAll("a")
    ].find(
      (elemento) =>
        obterArquivoDoLink(elemento) === pagina.arquivo
    );

    if (!link) {
      link = document.createElement("a");

      link.href = "./" + pagina.arquivo;
    }

    link.textContent = pagina.titulo;

    const ativo = pagina.arquivo === paginaAtual;

    // Remove destaques antigos.
    link.classList.remove("ativo");

    if (ativo) {
      link.setAttribute(
        "aria-current",
        "page"
      );

      if (!mobile) {
        link.classList.add("ativo");
      }

    } else {
      link.removeAttribute("aria-current");
    }

    // Organiza a ordem dos links.
    if (mobile && linkRegistrar) {
      menu.insertBefore(
        link,
        linkRegistrar
      );
    } else {
      menu.appendChild(link);
    }
  });

  // Atualiza o estado do botão Registrar problema.
  if (linkRegistrar) {
    if (paginaAtual === "registrar.html") {
      linkRegistrar.setAttribute(
        "aria-current",
        "page"
      );

    } else {
      linkRegistrar.removeAttribute(
        "aria-current"
      );
    }
  }
}

/* =====================================================
   06 — MENU MOBILE
   ===================================================== */

function iniciarMenuMobile() {
  const botaoMenu = document.getElementById(
    "botaoMenu"
  );

  const menuMobile = document.getElementById(
    "menuMobile"
  );

  // Algumas páginas podem não possuir menu mobile.
  if (!botaoMenu || !menuMobile) {
    return;
  }

  const telaDesktop = window.matchMedia(
    "(min-width: 901px)"
  );

  /* -------------------------------------------------
     FECHAR MENU
     ------------------------------------------------- */

  function fecharMenu() {
    menuMobile.hidden = true;

    botaoMenu.setAttribute(
      "aria-expanded",
      "false"
    );

    botaoMenu.setAttribute(
      "aria-label",
      "Abrir menu"
    );
  }

  /* -------------------------------------------------
     ABRIR MENU
     ------------------------------------------------- */

  function abrirMenu() {
    menuMobile.hidden = false;

    botaoMenu.setAttribute(
      "aria-expanded",
      "true"
    );

    botaoMenu.setAttribute(
      "aria-label",
      "Fechar menu"
    );
  }

  // Inicialmente fechado.
  fecharMenu();

  /* -------------------------------------------------
     CLIQUE NO BOTÃO
     ------------------------------------------------- */

  botaoMenu.addEventListener("click", () => {
    if (menuMobile.hidden) {
      abrirMenu();
    } else {
      fecharMenu();
    }
  });

  /* -------------------------------------------------
     FECHAR APÓS CLICAR EM UM LINK
     ------------------------------------------------- */

  menuMobile.querySelectorAll("a").forEach((link) => {
    link.addEventListener(
      "click",
      fecharMenu
    );
  });

  /* -------------------------------------------------
     FECHAR COM ESC
     ------------------------------------------------- */

  document.addEventListener("keydown", (evento) => {
    if (
      evento.key === "Escape" &&
      !menuMobile.hidden
    ) {
      fecharMenu();

      botaoMenu.focus();
    }
  });

  /* -------------------------------------------------
     FECHAR AO CLICAR FORA DO MENU
     ------------------------------------------------- */

  document.addEventListener("click", (evento) => {
    const clicouNoMenu = menuMobile.contains(
      evento.target
    );

    const clicouNoBotao = botaoMenu.contains(
      evento.target
    );

    if (
      !menuMobile.hidden &&
      !clicouNoMenu &&
      !clicouNoBotao
    ) {
      fecharMenu();
    }
  });

  /* -------------------------------------------------
     FECHAR AO VOLTAR PARA DESKTOP
     ------------------------------------------------- */

  function verificarTamanhoTela() {
    if (telaDesktop.matches) {
      fecharMenu();
    }
  }

  if (
    typeof telaDesktop.addEventListener === "function"
  ) {
    telaDesktop.addEventListener(
      "change",
      verificarTamanhoTela
    );

  } else if (
    typeof telaDesktop.addListener === "function"
  ) {
    telaDesktop.addListener(
      verificarTamanhoTela
    );
  }
}