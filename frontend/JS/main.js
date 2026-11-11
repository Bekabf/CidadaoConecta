
/* =====================================================
   CIDADÃOCONECTA — URBAN OS
   JAVASCRIPT GLOBAL — MAIN.JS

   Projeto acadêmico FIAP

   MENU PRINCIPAL:
   - Início
   - Serviços
   - Comunidade
   - Consultar
   - Registrar problema

   Funcionalidades:
   01. Identificação automática da página ativa
   02. Navegação padronizada
   03. Menu responsivo para celulares
   04. Fechamento ao clicar em um link
   05. Fechamento ao clicar fora do menu
   06. Fechamento com tecla ESC
   07. Fechamento ao voltar para desktop
   08. Destaque do botão Registrar problema

   Não utiliza dropdown de Ocorrências.
   ===================================================== */

"use strict";

/* =====================================================
   01 — INICIALIZAÇÃO
   ===================================================== */

document.addEventListener("DOMContentLoaded", () => {

  configurarNavegacao();

  configurarMenuMobile();

});

/* =====================================================
   02 — IDENTIFICAR PÁGINA ATUAL
   ===================================================== */

function obterPaginaAtual() {

  const caminho = window.location.pathname;

  const arquivo = caminho
    .split("/")
    .pop()
    .toLowerCase();

  return arquivo || "index.html";
}

/* =====================================================
   03 — IDENTIFICAR DESTINO DOS LINKS
   ===================================================== */

function obterArquivoDoLink(link) {

  if (!link) {
    return "";
  }

  const href = link.getAttribute("href");

  if (!href) {
    return "";
  }

  try {

    const endereco = new URL(
      href,
      window.location.href
    );

    return endereco.pathname
      .split("/")
      .pop()
      .toLowerCase();

  } catch (erro) {

    return "";

  }
}

/* =====================================================
   04 — CONFIGURAR NAVEGAÇÃO

   Não cria links.
   Não reorganiza o HTML.
   Não adiciona dropdowns.
   Apenas identifica a página ativa.
   ===================================================== */

function configurarNavegacao() {

  const paginaAtual = obterPaginaAtual();

  const navegacaoDesktop = document.querySelector(
    ".navegacao"
  );

  const navegacaoMobile = document.getElementById(
    "menuMobile"
  );

  const menus = [
    navegacaoDesktop,
    navegacaoMobile
  ];

  /*
    A página Detalhes será exibida
    futuramente dentro de um modal
    na página Consultar.

    Se um link antigo abrir detalhes.html,
    destacamos Consultar na navegação.
  */

  const paginaDestacada =
    paginaAtual === "detalhes.html"
      ? "consultar.html"
      : paginaAtual;

  menus.forEach((menu) => {

    if (!menu) {
      return;
    }

    const links = menu.querySelectorAll("a");

    links.forEach((link) => {

      const destino = obterArquivoDoLink(link);

      link.classList.remove("ativo");

      link.removeAttribute("aria-current");

      const ativo =
        destino === paginaDestacada &&
        destino !== "registrar.html";

      if (ativo) {

        link.classList.add("ativo");

        link.setAttribute(
          "aria-current",
          "page"
        );
      }

    });

  });

  /* ==========================================
     BOTÃO REGISTRAR PROBLEMA
     ========================================== */

  const botaoRegistrar = document.querySelector(
    ".botao-cabecalho"
  );

  if (botaoRegistrar) {

    botaoRegistrar.classList.remove("ativo");

    botaoRegistrar.removeAttribute(
      "aria-current"
    );

    if (paginaAtual === "registrar.html") {

      botaoRegistrar.classList.add("ativo");

      botaoRegistrar.setAttribute(
        "aria-current",
        "page"
      );

    }

  }

  /*
    No menu mobile, o botão Registrar
    também recebe o destaque correto.
  */

  if (navegacaoMobile) {

    const linksMobile =
      navegacaoMobile.querySelectorAll("a");

    linksMobile.forEach((link) => {

      if (
        obterArquivoDoLink(link) ===
        "registrar.html"
      ) {

        link.classList.toggle(
          "ativo",
          paginaAtual === "registrar.html"
        );

        if (paginaAtual === "registrar.html") {

          link.setAttribute(
            "aria-current",
            "page"
          );

        } else {

          link.removeAttribute(
            "aria-current"
          );

        }

      }

    });

  }

}

/* =====================================================
   05 — MENU MOBILE
   ===================================================== */

function configurarMenuMobile() {

  const botaoMenu = document.getElementById(
    "botaoMenu"
  );

  const menuMobile = document.getElementById(
    "menuMobile"
  );

  if (!botaoMenu || !menuMobile) {
    return;
  }

  const telaDesktop = window.matchMedia(
    "(min-width: 901px)"
  );

  /* ===================================================
     06 — FECHAR MENU
     =================================================== */

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

  /* ===================================================
     07 — ABRIR MENU
     =================================================== */

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

  /* ===================================================
     08 — ESTADO INICIAL
     =================================================== */

  fecharMenu();

  /* ===================================================
     09 — BOTÃO HAMBÚRGUER
     =================================================== */

  botaoMenu.addEventListener(
    "click",
    () => {

      if (menuMobile.hidden) {

        abrirMenu();

      } else {

        fecharMenu();

      }

    }
  );

  /* ===================================================
     10 — FECHAR APÓS CLICAR EM UM LINK
     =================================================== */

  menuMobile.querySelectorAll("a")
    .forEach((link) => {

      link.addEventListener(
        "click",
        fecharMenu
      );

    });

  /* ===================================================
     11 — FECHAR COM ESC
     =================================================== */

  document.addEventListener(
    "keydown",
    (evento) => {

      if (
        evento.key === "Escape" &&
        !menuMobile.hidden
      ) {

        fecharMenu();

        botaoMenu.focus();

      }

    }
  );

  /* ===================================================
     12 — FECHAR AO CLICAR FORA
     =================================================== */

  document.addEventListener(
    "pointerdown",
    (evento) => {

      if (menuMobile.hidden) {
        return;
      }

      const clicouNoMenu =
        menuMobile.contains(evento.target);

      const clicouNoBotao =
        botaoMenu.contains(evento.target);

      if (
        !clicouNoMenu &&
        !clicouNoBotao
      ) {

        fecharMenu();

      }

    }
  );

  /* ===================================================
     13 — FECHAR AO VOLTAR PARA DESKTOP
     =================================================== */

  function verificarTamanhoTela() {

    if (telaDesktop.matches) {

      fecharMenu();

    }

  }

  if (
    typeof telaDesktop.addEventListener ===
    "function"
  ) {

    telaDesktop.addEventListener(
      "change",
      verificarTamanhoTela
    );

  } else if (
    typeof telaDesktop.addListener ===
    "function"
  ) {

    telaDesktop.addListener(
      verificarTamanhoTela
    );

  }

}