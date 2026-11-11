
/* =====================================================
   CIDADÃOCONECTA — OBSERVATÓRIO URBANO
   Projeto acadêmico FIAP

   Integração com API Java e Oracle

   Funcionalidades:
   - Listagem de ocorrências
   - Indicadores e gráficos
   - Ranking de bairros
   - Filtros e ordenação
   - Ícones padronizados com a página Registrar
   ===================================================== */

"use strict";

document.addEventListener(
  "DOMContentLoaded",
  iniciarOcorrencias
);

function iniciarOcorrencias() {

  const $ = (id) => document.getElementById(id);

  const lista = $("listaOcorrencias");

  if (!lista) {
    return;
  }

  const ENDPOINT = "/api/ocorrencias/publicas";

  /* ==========================================
     01 — CATEGORIAS
     ========================================== */

  const categorias = {
    pavimentacao: {
      nome: "Ruas e calçadas",
      icone: "ruas"
    },

    iluminacao: {
      nome: "Iluminação pública",
      icone: "sol"
    },

    saneamento: {
      nome: "Água e saneamento",
      icone: "agua"
    },

    acessibilidade: {
      nome: "Acessibilidade",
      icone: "acessibilidade"
    },

    limpeza: {
      nome: "Limpeza urbana",
      icone: "limpeza"
    },

    outro: {
      nome: "Outro problema",
      icone: "localizacao"
    }
  };

  const situacoes = {
    ABERTA: "Aberta",
    EM_ANALISE: "Em análise",
    EM_ANDAMENTO: "Em andamento",
    RESOLVIDA: "Resolvida",
    ENCERRADA: "Encerrada"
  };

  let registros = [];
  let carregando = false;
  let houveErro = false;

  /* ==========================================
     02 — FUNÇÕES AUXILIARES
     ========================================== */

  function normalizar(valor) {
    return String(valor ?? "")
      .normalize("NFD")
      .replace(/[\u0300-\u036f]/g, "")
      .toLowerCase()
      .trim();
  }

  function criarElemento(
    tag,
    classe = "",
    texto
  ) {
    const elemento = document.createElement(tag);

    if (classe) {
      elemento.className = classe;
    }

    if (texto !== undefined) {
      elemento.textContent = String(texto);
    }

    return elemento;
  }

  function criarAvisoVazio(texto) {
    return criarElemento(
      "div",
      "analise-sem-dados",
      texto
    );
  }

  function mostrarMensagem(texto) {
    const aviso = $("mensagemOcorrencias");

    if (!aviso) {
      return;
    }

    aviso.textContent = texto;
    aviso.hidden = !texto;
  }

  function nomeCategoria(codigo) {
    return categorias[codigo]?.nome ||
      "Outro problema";
  }

  function nomeSituacao(status) {
    return situacoes[status] ||
      String(status || "Não informada")
        .replaceAll("_", " ");
  }

  function codigoCategoria(valor) {
    const texto = normalizar(valor);

    if (categorias[texto]) {
      return texto;
    }

    const equivalencias = {
      "ruas e calcadas": "pavimentacao",
      "iluminacao publica": "iluminacao",
      "agua e saneamento": "saneamento",
      "acessibilidade": "acessibilidade",
      "limpeza urbana": "limpeza",
      "outro problema": "outro"
    };

    return equivalencias[texto] || "outro";
  }

  /* ==========================================
     03 — ÍCONES SVG
     ========================================== */

  function criarIconeCategoria(tipo) {

    const desenhos = {

      /* RUAS E CALÇADAS */
      ruas: `
        <rect
          x="5" y="4"
          width="14" height="16"
          rx="1"
        />
        <path d="M8 8h8"/>
        <path d="M8 12h8"/>
        <path d="M8 16h8"/>
      `,

      /* ILUMINAÇÃO PÚBLICA */
      sol: `
        <circle cx="12" cy="12" r="5"/>
        <path d="M12 2v3"/>
        <path d="M12 19v3"/>
        <path d="M4.93 4.93l2.12 2.12"/>
        <path d="M16.95 16.95l2.12 2.12"/>
        <path d="M2 12h3"/>
        <path d="M19 12h3"/>
        <path d="M4.93 19.07l2.12-2.12"/>
        <path d="M2 12h3"/>
        <path d="M16.95 7.05l2.12-2.12"/>
      `,

      /* ÁGUA E SANEAMENTO */
      agua: `
        <path
          d="M12 22a8 8 0 0 0 8-8
             c0-4-8-12-8-12
             S4 10 4 14a8 8 0 0 0 8 8Z"
        />
        <path d="M8 14a4 4 0 0 0 4 4"/>
      `,

      /* ACESSIBILIDADE */
      acessibilidade: `
        <circle cx="12" cy="4" r="2"/>
        <path d="m4 10 8-2 8 2"/>
        <path d="M12 8v7"/>
        <path d="m8 21 4-6 4 6"/>
      `,

      /* LIMPEZA URBANA */
      limpeza: `
        <path d="M3 6h18"/>
        <path d="M8 6V4h8v2"/>
        <path d="M5 6l1 15h12l1-15"/>
        <path d="M10 10v7"/>
        <path d="M14 10v7"/>
      `,

      /* OUTROS PROBLEMAS */
      localizacao: `
        <path
          d="M20 10c0 5-8 12-8 12
             S4 15 4 10a8 8 0 1 1 16 0Z"
        />
        <circle cx="12" cy="10" r="3"/>
      `
    };

    const icone = criarElemento(
      "span",
      "ocorrencia-card-icone"
    );

    const svg = document.createElementNS(
      "http://www.w3.org/2000/svg",
      "svg"
    );

    svg.setAttribute("width", "25");
    svg.setAttribute("height", "25");
    svg.setAttribute("viewBox", "0 0 24 24");
    svg.setAttribute("fill", "none");
    svg.setAttribute("stroke", "currentColor");
    svg.setAttribute("stroke-width", "1.8");
    svg.setAttribute("stroke-linecap", "round");
    svg.setAttribute("stroke-linejoin", "round");
    svg.setAttribute("focusable", "false");

    // Os desenhos são constantes locais.
    // Nenhum dado da API entra neste HTML.
    svg.innerHTML =
      desenhos[tipo] || desenhos.localizacao;

    icone.appendChild(svg);

    icone.setAttribute(
      "aria-hidden",
      "true"
    );

    return icone;
  }

  /* ==========================================
     04 — DATAS
     ========================================== */

  function dataNumerica(valor) {
    if (!valor) {
      return 0;
    }

    const numero = new Date(valor).getTime();

    return Number.isFinite(numero)
      ? numero
      : 0;
  }

  function formatarData(valor) {
    if (!valor) {
      return "Data não informada";
    }

    // Preserva a data civil recebida do Oracle.
    const resultado = String(valor).match(
      /^(\d{4})-(\d{2})-(\d{2})/
    );

    if (resultado) {
      return (
        `${resultado[3]}/${resultado[2]}/${resultado[1]}`
      );
    }

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

  /* ==========================================
     05 — LOCALIDADES
     ========================================== */

  function chaveLocalidade(registro) {
    return [
      normalizar(registro.bairro),
      normalizar(registro.cidade),
      normalizar(registro.uf)
    ].join("|");
  }

  function nomeLocalidade(registro) {
    const bairro =
      registro.bairro || "Bairro não informado";

    const cidadeUF = [
      registro.cidade,
      registro.uf
    ]
      .filter(Boolean)
      .join(" / ");

    return cidadeUF
      ? `${bairro} — ${cidadeUF}`
      : bairro;
  }

  /* ==========================================
     06 — NORMALIZAR DADOS DA API
     ========================================== */

  function normalizarRegistro(item) {
    if (
      !item ||
      typeof item !== "object" ||
      typeof item.titulo !== "string"
    ) {
      return null;
    }

    return {
      titulo: item.titulo.trim(),

      categoria: codigoCategoria(
        item.categoria
      ),

      bairro: String(
        item.bairro || ""
      ).trim(),

      cidade: String(
        item.cidade || ""
      ).trim(),

      uf: String(
        item.uf || ""
      ).trim().toUpperCase(),

      status: String(
        item.status || "ABERTA"
      ).toUpperCase(),

      criadoEm: item.dataCadastro || null
    };
  }

  /* ==========================================
     07 — CARREGAR OCORRÊNCIAS DO ORACLE
     ========================================== */

  async function carregarRegistros() {
    if (carregando) {
      return;
    }

    carregando = true;

    mostrarMensagem(
      "Carregando ocorrências..."
    );

    try {
      const resposta = await fetch(
        ENDPOINT,
        {
          method: "GET",
          headers: {
            Accept: "application/json"
          },
          cache: "no-store"
        }
      );

      if (!resposta.ok) {
        throw new Error(
          "Não foi possível consultar a API."
        );
      }

      const dados = await resposta.json();

      const itens = Array.isArray(dados)
        ? dados
        : dados.ocorrencias;

      if (!Array.isArray(itens)) {
        throw new Error(
          "O servidor retornou dados inválidos."
        );
      }

      registros = itens
        .map(normalizarRegistro)
        .filter(Boolean);

      houveErro = false;

      atualizarObservatorio();

      mostrarMensagem("");

    } catch (erro) {
      console.error(
        "Erro ao carregar ocorrências:",
        erro
      );

      registros = [];
      houveErro = true;

      atualizarObservatorio();

      mostrarMensagem(
        "Não foi possível carregar as ocorrências. " +
        "Verifique a API Java e o Oracle."
      );

    } finally {
      carregando = false;
    }
  }

  /* ==========================================
     08 — INDICADORES
     ========================================== */

  function atualizarIndicadores() {
    const localidades = new Set();
    const tipos = new Set();

    registros.forEach((registro) => {
      if (registro.bairro) {
        localidades.add(
          chaveLocalidade(registro)
        );
      }

      tipos.add(registro.categoria);
    });

    $("totalOcorrencias").textContent =
      registros.length;

    $("totalBairros").textContent =
      localidades.size;

    $("totalCategorias").textContent =
      tipos.size;
  }

  /* ==========================================
     09 — GRÁFICO DE CATEGORIAS
     ========================================== */

  function atualizarGraficoCategorias() {
    const grafico = $("graficoCategorias");

    if (!grafico) {
      return;
    }

    grafico.replaceChildren();

    if (!registros.length) {
      grafico.appendChild(
        criarAvisoVazio(
          "Ainda não há ocorrências para gerar o gráfico."
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
      .sort((a, b) =>
        b[1] - a[1] ||
        a[0].localeCompare(b[0], "pt-BR")
      );

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

      const numero = criarElemento(
        "strong",
        "",
        `${quantidade} (${Math.round(porcentagem)}%)`
      );

      topo.append(rotulo, numero);

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
        `${nome}: ${quantidade} ocorrências`
      );

      trilho.appendChild(preenchimento);

      linha.append(topo, trilho);

      fragmento.appendChild(linha);
    });

    grafico.appendChild(fragmento);
  }

  /* ==========================================
     10 — RANKING DE BAIRROS
     ========================================== */

  function atualizarRankingBairros() {
    const ranking = $("rankingBairros");

    if (!ranking) {
      return;
    }

    ranking.replaceChildren();

    const localidades = new Map();

    registros.forEach((registro) => {
      if (!registro.bairro) {
        return;
      }

      const chave = chaveLocalidade(registro);

      if (!localidades.has(chave)) {
        localidades.set(chave, {
          nome: nomeLocalidade(registro),
          quantidade: 0
        });
      }

      localidades.get(chave).quantidade++;
    });

    if (!localidades.size) {
      ranking.appendChild(
        criarAvisoVazio(
          "Ainda não há bairros disponíveis para análise."
        )
      );

      return;
    }

    const ordenados = [...localidades.values()]
      .sort((a, b) =>
        b.quantidade - a.quantidade ||
        a.nome.localeCompare(b.nome, "pt-BR")
      )
      .slice(0, 5);

    const fragmento =
      document.createDocumentFragment();

    ordenados.forEach((localidade, indice) => {
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
        localidade.nome
      );

      const legenda = criarElemento(
        "small",
        "",
        localidade.quantidade === 1
          ? "1 ocorrência"
          : `${localidade.quantidade} ocorrências`
      );

      dados.append(titulo, legenda);

      const quantidade = criarElemento(
        "span",
        "bairro-ranking-quantidade",
        localidade.quantidade
      );

      item.append(
        posicao,
        dados,
        quantidade
      );

      fragmento.appendChild(item);
    });

    ranking.appendChild(fragmento);
  }

  /* ==========================================
     11 — FILTRO DE BAIRROS
     ========================================== */

  function preencherFiltroBairros() {
    const select = $("filtroBairro");
    const valorAnterior = select.value;

    select.replaceChildren(
      new Option("Todos os bairros", "")
    );

    const localidades = new Map();

    registros.forEach((registro) => {
      if (!registro.bairro) {
        return;
      }

      const chave = chaveLocalidade(registro);

      if (!localidades.has(chave)) {
        localidades.set(
          chave,
          nomeLocalidade(registro)
        );
      }
    });

    [...localidades.entries()]
      .sort((a, b) =>
        a[1].localeCompare(b[1], "pt-BR")
      )
      .forEach(([chave, nome]) => {
        select.add(
          new Option(nome, chave)
        );
      });

    const existe = [...select.options]
      .some((opcao) =>
        opcao.value === valorAnterior
      );

    if (existe) {
      select.value = valorAnterior;
    }
  }

  /* ==========================================
     12 — FILTRO DE SITUAÇÕES
     ========================================== */

  function preencherFiltroSituacoes() {
    const select = $("filtroSituacao");
    const valorAnterior = select.value;

    select.replaceChildren(
      new Option("Todas as situações", "")
    );

    const encontrados = new Set(
      registros.map(
        (registro) => registro.status
      )
    );

    [...encontrados]
      .sort((a, b) =>
        nomeSituacao(a).localeCompare(
          nomeSituacao(b),
          "pt-BR"
        )
      )
      .forEach((status) => {
        select.add(
          new Option(
            nomeSituacao(status),
            status
          )
        );
      });

    const existe = [...select.options]
      .some((opcao) =>
        opcao.value === valorAnterior
      );

    if (existe) {
      select.value = valorAnterior;
    }
  }

  /* ==========================================
     13 — PESQUISA, FILTROS E ORDENAÇÃO
     ========================================== */

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
          registro.cidade,
          registro.uf,
          nomeCategoria(registro.categoria)
        ]
          .map(normalizar)
          .join(" ");

        const correspondePesquisa =
          !pesquisa ||
          textoPesquisa.includes(pesquisa);

        const correspondeBairro =
          !bairro ||
          chaveLocalidade(registro) === bairro;

        const correspondeCategoria =
          !categoria ||
          registro.categoria === categoria;

        const correspondeSituacao =
          !situacao ||
          registro.status === situacao;

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
        return nomeLocalidade(a).localeCompare(
          nomeLocalidade(b),
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

  /* ==========================================
     14 — CRIAR CARTÃO
     ========================================== */

  function criarCard(registro) {
    const categoria =
      categorias[registro.categoria] ||
      categorias.outro;

    const cartao = criarElemento(
      "article",
      "ocorrencia-comunidade"
    );

    /* CABEÇALHO */

    const topo = criarElemento(
      "div",
      "ocorrencia-card-topo"
    );

    const etiqueta = criarElemento(
      "span",
      "ocorrencia-card-codigo",
      "CIDADÃOCONECTA / OBSERVATÓRIO"
    );

    const situacao = criarElemento(
      "span",
      "ocorrencia-card-status",
      nomeSituacao(registro.status)
    );

    topo.append(etiqueta, situacao);

    /* ÍCONE DA CATEGORIA */

    const icone = criarIconeCategoria(
      categoria.icone
    );

    /* TÍTULO */

    const titulo = criarElemento(
      "h3",
      "",
      registro.titulo || "Ocorrência sem título"
    );

    /* RESUMO */

    const resumo = criarElemento(
      "p",
      "",
      `Ocorrência relacionada a ${categoria.nome.toLowerCase()}, ` +
      "registrada no CidadãoConecta."
    );

    /* METADADOS */

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
      [registro.cidade, registro.uf]
        .filter(Boolean)
        .join(" / ") ||
        "Localidade não informada"
    );

    metadados.append(
      bairro,
      categoriaTag,
      localidade
    );

    /* RODAPÉ */

    const rodape = criarElemento(
      "div",
      "ocorrencia-card-rodape"
    );

    const data = criarElemento(
      "span",
      "",
      formatarData(registro.criadoEm)
    );

    const aviso = criarElemento(
      "span",
      "ocorrencia-card-link",
      "Registro acadêmico"
    );

    rodape.append(data, aviso);

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

  /* ==========================================
     15 — RENDERIZAR LISTAGEM
     ========================================== */

  function renderizarOcorrencias() {
    const filtrados = obterRegistrosFiltrados();

    lista.replaceChildren();

    $("quantidadeResultados").textContent =
      filtrados.length;

    const estadoVazio = $("estadoVazio");

    if (!filtrados.length) {
      estadoVazio.hidden = false;

      $("textoEstadoVazio").textContent =
        houveErro
          ? "Não foi possível carregar os dados do Oracle."
          : registros.length === 0
            ? "Nenhuma ocorrência disponível no momento."
            : "Nenhuma ocorrência corresponde aos filtros selecionados.";

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

  /* ==========================================
     16 — ATUALIZAR OBSERVATÓRIO
     ========================================== */

  function atualizarObservatorio() {
    atualizarIndicadores();
    atualizarGraficoCategorias();
    atualizarRankingBairros();
    preencherFiltroBairros();
    preencherFiltroSituacoes();
    renderizarOcorrencias();
  }

  /* ==========================================
     17 — LIMPAR FILTROS
     ========================================== */

  function limparFiltros() {
    $("formFiltros").reset();

    $("ordenarOcorrencias").value =
      "recentes";

    renderizarOcorrencias();
  }

  /* ==========================================
     18 — EVENTOS
     ========================================== */

  $("formFiltros").addEventListener(
    "submit",
    (evento) => {
      evento.preventDefault();
      renderizarOcorrencias();
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

  document.addEventListener(
    "visibilitychange",
    () => {
      if (!document.hidden) {
        carregarRegistros();
      }
    }
  );

  /* ==========================================
     19 — INICIALIZAÇÃO
     ========================================== */

  carregarRegistros();
}