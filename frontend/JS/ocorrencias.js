
/* =====================================================
   CIDADÃOCONECTA — URBAN OS
   OBSERVATÓRIO URBANO — OCORRENCIAS.JS

   Funcionalidades:
   01. Carregar registros demonstrativos
   02. Atualizar indicadores
   03. Gráfico por categoria
   04. Ranking de bairros
   05. Filtros e pesquisa
   06. Ordenação
   07. Cartões de ocorrências
   08. Abrir página de detalhes
   09. Estados vazios e tratamento de erros
   10. Atualização entre abas

   Os registros são armazenados no navegador.
   Não existe integração com a prefeitura.
   ===================================================== */

"use strict";

document.addEventListener(
  "DOMContentLoaded",
  iniciarOcorrencias
);

function iniciarOcorrencias() {

  /* ===================================================
     01 — CONFIGURAÇÕES
     =================================================== */

  const $ = (id) => document.getElementById(id);

  const lista = $("listaOcorrencias");

  if (!lista) return;

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

  let registros = [];

  /* ===================================================
     02 — FUNÇÕES AUXILIARES
     =================================================== */

  function normalizar(valor) {
    return String(valor ?? "")
      .normalize("NFD")
      .replace(/[\u0300-\u036f]/g, "")
      .toLowerCase()
      .trim();
  }

  function criarElemento(tag, classe = "", texto) {
    const elemento = document.createElement(tag);

    if (classe) {
      elemento.className = classe;
    }

    if (texto !== undefined) {
      elemento.textContent = String(texto);
    }

    return elemento;
  }

  function formatarData(valor) {
    if (!valor) return "Data não informada";

    const data = new Date(valor);

    if (Number.isNaN(data.getTime())) {
      return "Data não informada";
    }

    return new Intl.DateTimeFormat("pt-BR", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric"
    }).format(data);
  }

  function dataNumerica(valor) {
    const numero = new Date(valor).getTime();

    return Number.isFinite(numero) ? numero : 0;
  }

  function nomeCategoria(codigo) {
    return categorias[codigo]?.nome ||
      "Outra categoria";
  }

  function criarAvisoVazio(mensagem) {
    return criarElemento(
      "div",
      "analise-sem-dados",
      mensagem
    );
  }

  /* ===================================================
     03 — CARREGAR REGISTROS
     =================================================== */

  function carregarRegistros() {
    const aviso = $("mensagemOcorrencias");

    if (aviso) {
      aviso.hidden = true;
      aviso.textContent = "";
    }

    try {
      const conteudo = localStorage.getItem(
        CHAVE_REGISTROS
      );

      if (!conteudo) return [];

      const dados = JSON.parse(conteudo);

      if (!Array.isArray(dados)) {
        throw new Error(
          "Formato de registros inválido"
        );
      }

      return dados.filter((registro) => {
        return (
          registro !== null &&
          typeof registro === "object" &&
          typeof registro.idLocal === "string" &&
          typeof registro.titulo === "string" &&
          typeof registro.categoria === "string"
        );
      });

    } catch (erro) {
      console.error(
        "Erro ao carregar registros:",
        erro
      );

      if (aviso) {
        aviso.textContent =
          "Não foi possível carregar os registros " +
          "deste navegador. Verifique o armazenamento local.";

        aviso.hidden = false;
      }

      return [];
    }
  }

  /* ===================================================
     04 — INDICADORES PRINCIPAIS
     =================================================== */

  function atualizarIndicadores() {
    const bairros = new Set();
    const tipos = new Set();

    registros.forEach((registro) => {
      const bairro = normalizar(registro.bairro);

      if (bairro) {
        bairros.add(bairro);
      }

      if (registro.categoria) {
        tipos.add(registro.categoria);
      }
    });

    $("totalOcorrencias").textContent =
      registros.length;

    $("totalBairros").textContent =
      bairros.size;

    $("totalCategorias").textContent =
      tipos.size;
  }

  /* ===================================================
     05 — GRÁFICO POR CATEGORIA
     =================================================== */

  function atualizarGraficoCategorias() {
    const grafico = $("graficoCategorias");

    if (!grafico) return;

    grafico.replaceChildren();

    if (!registros.length) {
      grafico.appendChild(
        criarAvisoVazio(
          "Ainda não existem registros demonstrativos " +
          "para gerar o gráfico."
        )
      );

      return;
    }

    const contagem = new Map();

    registros.forEach((registro) => {
      const nome = nomeCategoria(
        registro.categoria
      );

      contagem.set(
        nome,
        (contagem.get(nome) || 0) + 1
      );
    });

    const ordenadas = [...contagem.entries()]
      .sort((a, b) => {
        return b[1] - a[1] ||
          a[0].localeCompare(b[0], "pt-BR");
      });

    const fragmento =
      document.createDocumentFragment();

    ordenadas.forEach(([nome, quantidade]) => {
      const porcentagem =
        (quantidade / registros.length) * 100;

      const linha = criarElemento(
        "div",
        "categoria-linha"
      );

      const topo = criarElemento(
        "div",
        "categoria-linha-topo"
      );

      const rotulo = criarElemento(
        "span",
        "",
        nome
      );

      const valor = criarElemento(
        "strong",
        "",
        `${quantidade} (${Math.round(porcentagem)}%)`
      );

      topo.append(rotulo, valor);

      const trilho = criarElemento(
        "div",
        "categoria-trilho"
      );

      const preenchimento = criarElemento(
        "div",
        "categoria-preenchimento"
      );

      preenchimento.style.width =
        `${porcentagem}%`;

      preenchimento.setAttribute(
        "role",
        "img"
      );

      preenchimento.setAttribute(
        "aria-label",
        `${nome}: ${quantidade} registro(s), ` +
        `${Math.round(porcentagem)}% do total`
      );

      trilho.appendChild(preenchimento);

      linha.append(topo, trilho);

      fragmento.appendChild(linha);
    });

    grafico.appendChild(fragmento);
  }

  /* ===================================================
     06 — RANKING DE BAIRROS
     =================================================== */

  function atualizarRankingBairros() {
    const ranking = $("rankingBairros");

    if (!ranking) return;

    ranking.replaceChildren();

    const bairros = new Map();

    registros.forEach((registro) => {
      const nome = String(
        registro.bairro || ""
      ).trim();

      const chave = normalizar(nome);

      if (!chave) return;

      if (!bairros.has(chave)) {
        bairros.set(chave, {
          nome,
          quantidade: 0
        });
      }

      bairros.get(chave).quantidade++;
    });

    if (bairros.size === 0) {
      ranking.appendChild(
        criarAvisoVazio(
          "Os bairros aparecerão aqui quando " +
          "existirem registros com localização."
        )
      );

      return;
    }

    const ordenados = [...bairros.values()]
      .sort((a, b) => {
        return (
          b.quantidade - a.quantidade ||
          a.nome.localeCompare(b.nome, "pt-BR")
        );
      })
      .slice(0, 5);

    const fragmento =
      document.createDocumentFragment();

    ordenados.forEach((bairro, indice) => {
      const item = criarElemento(
        "div",
        "bairro-ranking-item"
      );

      const posicao = criarElemento(
        "span",
        "bairro-ranking-posicao",
        String(indice + 1).padStart(2, "0")
      );

      const dados = criarElemento(
        "div",
        "bairro-ranking-dados"
      );

      const titulo = criarElemento(
        "strong",
        "",
        bairro.nome
      );

      const legenda = criarElemento(
        "small",
        "",
        bairro.quantidade === 1
          ? "1 registro local"
          : `${bairro.quantidade} registros locais`
      );

      const quantidade = criarElemento(
        "span",
        "bairro-ranking-quantidade",
        bairro.quantidade
      );

      dados.append(titulo, legenda);

      item.append(
        posicao,
        dados,
        quantidade
      );

      fragmento.appendChild(item);
    });

    ranking.appendChild(fragmento);
  }

  /* ===================================================
     07 — FILTRO DE BAIRROS
     =================================================== */

  function preencherFiltroBairros() {
    const select = $("filtroBairro");

    const valorAnterior = select.value;

    select.replaceChildren(
      new Option("Todos os bairros", "")
    );

    const bairros = new Map();

    registros.forEach((registro) => {
      const nome = String(
        registro.bairro || ""
      ).trim();

      const chave = normalizar(nome);

      if (chave && !bairros.has(chave)) {
        bairros.set(chave, nome);
      }
    });

    const ordenados = [...bairros.entries()]
      .sort((a, b) => {
        return a[1].localeCompare(
          b[1],
          "pt-BR"
        );
      });

    ordenados.forEach(([chave, nome]) => {
      select.add(
        new Option(nome, chave)
      );
    });

    const existeValor = [
      ...select.options
    ].some((opcao) => {
      return opcao.value === valorAnterior;
    });

    if (existeValor) {
      select.value = valorAnterior;
    }
  }

  /* ===================================================
     08 — FILTROS E ORDENAÇÃO
     =================================================== */

  function obterRegistrosFiltrados() {
    const pesquisa = normalizar(
      $("filtroPesquisa").value
    );

    const bairro = $("filtroBairro").value;

    const categoria = $("filtroCategoria").value;

    const situacao = $("filtroSituacao").value;

    const ordem = $("ordenarOcorrencias").value;

    const resultado = registros.filter(
      (registro) => {
        const textoPesquisa = [
          registro.titulo,
          registro.bairro,
          nomeCategoria(registro.categoria)
        ]
          .map(normalizar)
          .join(" ");

        const correspondePesquisa =
          !pesquisa ||
          textoPesquisa.includes(pesquisa);

        const correspondeBairro =
          !bairro ||
          normalizar(registro.bairro) === bairro;

        const correspondeCategoria =
          !categoria ||
          registro.categoria === categoria;

        // Todos os registros desta versão
        // possuem situação "local".
        const correspondeSituacao =
          !situacao ||
          situacao === "local";

        return (
          correspondePesquisa &&
          correspondeBairro &&
          correspondeCategoria &&
          correspondeSituacao
        );
      }
    );

    resultado.sort((a, b) => {
      if (ordem === "antigas") {
        return (
          dataNumerica(a.criadoEm) -
          dataNumerica(b.criadoEm)
        );
      }

      if (ordem === "bairro") {
        return String(
          a.bairro || ""
        ).localeCompare(
          String(b.bairro || ""),
          "pt-BR"
        );
      }

      return (
        dataNumerica(b.criadoEm) -
        dataNumerica(a.criadoEm)
      );
    });

    return resultado;
  }

  /* ===================================================
     09 — CARTÕES DE OCORRÊNCIAS

     AJUSTADO:
     Comunidade → Detalhes da ocorrência
     =================================================== */

  function criarCard(registro) {
    const categoria =
      categorias[registro.categoria] || {
        nome: "Outra categoria",
        icone: "◫"
      };

    const cartao = criarElemento(
      "article",
      "ocorrencia-comunidade"
    );

    // CABEÇALHO

    const topo = criarElemento(
      "div",
      "ocorrencia-card-topo"
    );

    const etiqueta = criarElemento(
      "span",
      "ocorrencia-card-codigo",
      "CIDADÃOCONECTA / DEMONSTRAÇÃO"
    );

    const situacao = criarElemento(
      "span",
      "ocorrencia-card-status",
      "Registro local"
    );

    topo.append(etiqueta, situacao);

    // CATEGORIA

    const icone = criarElemento(
      "span",
      "ocorrencia-card-icone",
      categoria.icone
    );

    icone.setAttribute(
      "aria-hidden",
      "true"
    );

    // TÍTULO

    const titulo = criarElemento(
      "h3",
      "",
      registro.titulo || "Ocorrência sem título"
    );

    // RESUMO DEMONSTRATIVO

    // A descrição original não é exibida aqui
    // porque pode conter informações pessoais.

    const resumo = criarElemento(
      "p",
      "",
      `Registro demonstrativo relacionado a ` +
      `${categoria.nome.toLowerCase()}. ` +
      `As informações estão salvas apenas ` +
      `neste navegador.`
    );

    // METADADOS

    const metadados = criarElemento(
      "div",
      "ocorrencia-card-metadados"
    );

    const bairro = criarElemento(
      "span",
      "",
      `⌖ ${registro.bairro || "Bairro não informado"}`
    );

    const categoriaTag = criarElemento(
      "span",
      "",
      categoria.nome
    );

    const localidade = criarElemento(
      "span",
      "",
      [
        registro.cidade,
        registro.uf
      ].filter(Boolean).join(" / ") ||
      "Localidade não informada"
    );

    metadados.append(
      bairro,
      categoriaTag,
      localidade
    );

    // RODAPÉ

    const rodape = criarElemento(
      "div",
      "ocorrencia-card-rodape"
    );

    const data = criarElemento(
      "span",
      "",
      formatarData(registro.criadoEm)
    );

    /* ==========================================
       LINK PARA A NOVA PÁGINA DE DETALHES
       ========================================== */

    const consultar = criarElemento(
      "a",
      "ocorrencia-card-link",
      "Ver detalhes da ocorrência →"
    );

    consultar.href =
      "./detalhes.html?id=" +
      encodeURIComponent(registro.idLocal);

    consultar.setAttribute(
      "aria-label",
      `Ver detalhes da ocorrência: ${registro.titulo}`
    );

    // O protocolo é utilizado somente nesta
    // demonstração local. Na versão integrada,
    // a página pública deverá usar outro
    // identificador, sem conceder acesso
    // aos dados privados da ocorrência.

    rodape.append(
      data,
      consultar
    );

    cartao.append(
      topo,
      icone,
      titulo,
      resumo,
      metadados,
      rodape
    );

    return cartao;
  }

  /* ===================================================
     10 — RENDERIZAR CARTÕES
     =================================================== */

  function renderizarOcorrencias() {
    const filtrados = obterRegistrosFiltrados();

    lista.replaceChildren();

    $("quantidadeResultados").textContent =
      filtrados.length;

    const estadoVazio = $("estadoVazio");

    if (!filtrados.length) {
      estadoVazio.hidden = false;

      $("textoEstadoVazio").textContent =
        registros.length === 0
          ? "Ainda não existem registros demonstrativos " +
            "neste navegador. Registre um problema " +
            "para começar."
          : "Nenhuma ocorrência corresponde aos " +
            "filtros selecionados. Experimente " +
            "outra pesquisa, bairro ou categoria.";

      $("botaoLimparVazio").hidden =
        registros.length === 0;

      return;
    }

    estadoVazio.hidden = true;

    const fragmento =
      document.createDocumentFragment();

    filtrados.forEach((registro) => {
      fragmento.appendChild(
        criarCard(registro)
      );
    });

    lista.appendChild(fragmento);
  }

  /* ===================================================
     11 — ATUALIZAR TODO O OBSERVATÓRIO
     =================================================== */

  function atualizarObservatorio() {
    registros = carregarRegistros();

    atualizarIndicadores();

    atualizarGraficoCategorias();

    atualizarRankingBairros();

    preencherFiltroBairros();

    renderizarOcorrencias();
  }

  /* ===================================================
     12 — LIMPAR FILTROS
     =================================================== */

  function limparFiltros() {
    $("formFiltros").reset();

    $("ordenarOcorrencias").value =
      "recentes";

    renderizarOcorrencias();
  }

  /* ===================================================
     13 — EVENTOS
     =================================================== */

  $("formFiltros").addEventListener(
    "submit",
    (evento) => {
      evento.preventDefault();
    }
  );

  $("filtroPesquisa").addEventListener(
    "input",
    renderizarOcorrencias
  );

  $("filtroBairro").addEventListener(
    "change",
    renderizarOcorrencias
  );

  $("filtroCategoria").addEventListener(
    "change",
    renderizarOcorrencias
  );

  $("filtroSituacao").addEventListener(
    "change",
    renderizarOcorrencias
  );

  $("ordenarOcorrencias").addEventListener(
    "change",
    renderizarOcorrencias
  );

  $("limparFiltros").addEventListener(
    "click",
    limparFiltros
  );

  $("botaoLimparVazio").addEventListener(
    "click",
    limparFiltros
  );

  /* ===================================================
     14 — SINCRONIZAÇÃO ENTRE ABAS
     =================================================== */

  window.addEventListener(
    "storage",
    (evento) => {
      if (evento.key === CHAVE_REGISTROS) {
        atualizarObservatorio();
      }
    }
  );

  document.addEventListener(
    "visibilitychange",
    () => {
      if (!document.hidden) {
        atualizarObservatorio();
      }
    }
  );

  /* ===================================================
     15 — INICIALIZAÇÃO
     =================================================== */

  atualizarObservatorio();
}