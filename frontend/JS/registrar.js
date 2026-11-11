
/* ==========================================
   CIDADÃOCONECTA — URBAN OS
   REGISTRO DE OCORRÊNCIAS

   - Formulário em 3 etapas
   - Validação de dados
   - Fotos com pré-visualização
   - Revisão das informações
   - Salvamento demonstrativo
   - Copiar protocolo
   - Registrar outro problema
   ========================================== */

"use strict";

document.addEventListener(
  "DOMContentLoaded",
  iniciarRegistro
);

function iniciarRegistro() {
  const form = document.getElementById(
    "formOcorrencia"
  );

  if (!form) {
    return;
  }

  const $ = (id) => document.getElementById(id);

  const secoes = [
    ...form.querySelectorAll("[data-etapa]")
  ];

  const nomesCategorias = {
    pavimentacao: "Ruas e calçadas",
    iluminacao: "Iluminação pública",
    saneamento: "Água e saneamento",
    acessibilidade: "Acessibilidade",
    limpeza: "Limpeza urbana",
    outro: "Outro problema"
  };

  const estadosValidos = new Set([
    "AC", "AL", "AP", "AM", "BA", "CE",
    "DF", "ES", "GO", "MA", "MT", "MS",
    "MG", "PA", "PB", "PR", "PE", "PI",
    "RJ", "RN", "RS", "RO", "RR",
    "SC", "SP", "SE", "TO"
  ]);

  const tiposPermitidos = new Set([
    "image/jpeg",
    "image/png",
    "image/webp"
  ]);

  const MAX_FOTOS = 3;
  const MAX_TAMANHO = 5 * 1024 * 1024;

  const CHAVE_REGISTROS =
    "cidadaoconecta_registros_demo";

  let fotosSelecionadas = [];
  let ultimoProtocolo = "";

  /* ==========================================
     01 — MENSAGENS DE ERRO
     ========================================== */

  function limparErros() {
    form.querySelectorAll(".form-erro").forEach(
      (elemento) => {
        elemento.textContent = "";
        elemento.hidden = true;
      }
    );

    form.querySelectorAll(
      '[aria-invalid="true"]'
    ).forEach((campo) => {
      campo.removeAttribute("aria-invalid");
    });
  }

  function mostrarErro(
    etapa,
    mensagem,
    campo = null
  ) {
    const aviso = $(`erroEtapa${etapa}`);

    if (!aviso) {
      return false;
    }

    aviso.textContent = mensagem;
    aviso.hidden = false;

    if (campo) {
      campo.setAttribute(
        "aria-invalid",
        "true"
      );

      campo.focus();
    } else {
      aviso.scrollIntoView({
        behavior: "auto",
        block: "center"
      });
    }

    return false;
  }

  /* ==========================================
     02 — CONTROLE DAS ETAPAS
     ========================================== */

  function mostrarEtapa(
    numero,
    focar = true
  ) {
    secoes.forEach((secao) => {
      secao.hidden =
        Number(secao.dataset.etapa) !== numero;
    });

    const porcentagem = Math.round(
      (numero / 3) * 100
    );

    $("textoEtapa").textContent =
      `Etapa ${numero} de 3`;

    $("textoPorcentagem").textContent =
      `${porcentagem}%`;

    $("progressoAtual").style.width =
      `${porcentagem}%`;

    $("barraProgresso").setAttribute(
      "aria-valuenow",
      String(porcentagem)
    );

    document.querySelectorAll(
      "[data-passo]"
    ).forEach((passo) => {
      passo.classList.toggle(
        "atual",
        Number(passo.dataset.passo) === numero
      );
    });

    limparErros();

    if (focar) {
      const secao = secoes.find(
        (item) =>
          Number(item.dataset.etapa) === numero
      );

      if (secao) {
        secao.scrollIntoView({
          behavior: "auto",
          block: "start"
        });

        const titulo = secao.querySelector("h2");

        if (titulo) {
          titulo.setAttribute(
            "tabindex",
            "-1"
          );

          titulo.focus({
            preventScroll: true
          });
        }
      }
    }
  }

  /* ==========================================
     03 — VALIDAÇÃO
     ========================================== */

  function validarTexto(
    id,
    minimo,
    etapa
  ) {
    const campo = $(id);
    const valor = campo.value.trim();

    if (valor.length >= minimo) {
      return true;
    }

    const label = form.querySelector(
      `label[for="${id}"]`
    );

    const nome = label
      ? label.textContent
          .replace("*", "")
          .trim()
      : id;

    if (!valor) {
      return mostrarErro(
        etapa,
        `Preencha o campo "${nome}".`,
        campo
      );
    }

    return mostrarErro(
      etapa,
      `O campo "${nome}" precisa ter pelo menos ${minimo} caracteres.`,
      campo
    );
  }

  function validarEtapa1() {
    limparErros();

    const categoria = form.querySelector(
      'input[name="categoria"]:checked'
    );

    if (!categoria) {
      return mostrarErro(
        1,
        "Selecione o tipo de problema para continuar.",
        form.querySelector(
          'input[name="categoria"]'
        )
      );
    }

    if (!validarTexto("titulo", 5, 1)) {
      return false;
    }

    if (!validarTexto("descricao", 15, 1)) {
      return false;
    }

    return true;
  }

  function validarEtapa2() {
    limparErros();

    $("uf").value = $("uf").value
      .trim()
      .toUpperCase();

    if (!validarTexto("cidade", 2, 2)) {
      return false;
    }

    if (
      !estadosValidos.has(
        $("uf").value
      )
    ) {
      return mostrarErro(
        2,
        "Informe uma UF válida, como GO, SP ou MG.",
        $("uf")
      );
    }

    if (!validarTexto("bairro", 2, 2)) {
      return false;
    }

    if (!validarTexto("rua", 3, 2)) {
      return false;
    }

    return true;
  }

  /* ==========================================
     04 — CONTADOR DE CARACTERES
     ========================================== */

  function atualizarContador() {
    $("contadorDescricao").textContent =
      `${$("descricao").value.length}/1000`;
  }

  $("descricao").addEventListener(
    "input",
    atualizarContador
  );

  atualizarContador();

  /* ==========================================
     05 — FOTOS
     ========================================== */

  function criarMiniatura(
    foto,
    indice
  ) {
    const cartao = document.createElement(
      "div"
    );

    cartao.className = "foto-item";

    const imagem = document.createElement(
      "img"
    );

    imagem.src = foto.url;
    imagem.alt =
      `Prévia da foto ${indice + 1}`;

    const informacoes =
      document.createElement("div");

    informacoes.className =
      "foto-item-info";

    const nome =
      document.createElement("span");

    nome.className = "foto-item-nome";
    nome.textContent = foto.arquivo.name;
    nome.title = foto.arquivo.name;

    const remover =
      document.createElement("button");

    remover.type = "button";
    remover.className = "foto-remover";
    remover.textContent = "Remover foto";

    remover.setAttribute(
      "aria-label",
      `Remover foto ${foto.arquivo.name}`
    );

    remover.addEventListener(
      "click",
      () => {
        URL.revokeObjectURL(
          foto.url
        );

        fotosSelecionadas =
          fotosSelecionadas.filter(
            (item) => item !== foto
          );

        renderizarFotos();
      }
    );

    informacoes.append(
      nome,
      remover
    );

    cartao.append(
      imagem,
      informacoes
    );

    return cartao;
  }

  function renderizarFotos() {
    const lista = $("listaFotos");

    lista.replaceChildren();

    fotosSelecionadas.forEach(
      (foto, indice) => {
        lista.appendChild(
          criarMiniatura(
            foto,
            indice
          )
        );
      }
    );
  }

  function liberarFotos() {
    fotosSelecionadas.forEach(
      (foto) => {
        URL.revokeObjectURL(
          foto.url
        );
      }
    );

    fotosSelecionadas = [];

    $("fotos").value = "";

    renderizarFotos();
  }

  $("fotos").addEventListener(
    "change",
    (evento) => {
      const arquivos = [
        ...evento.target.files
      ];

      limparErros();

      for (const arquivo of arquivos) {
        if (
          fotosSelecionadas.length >=
          MAX_FOTOS
        ) {
          mostrarErro(
            1,
            "Você pode adicionar no máximo 3 fotos."
          );

          break;
        }

        if (
          !tiposPermitidos.has(
            arquivo.type
          )
        ) {
          mostrarErro(
            1,
            `O arquivo "${arquivo.name}" não é JPG, PNG ou WebP.`
          );

          continue;
        }

        if (
          arquivo.size === 0 ||
          arquivo.size > MAX_TAMANHO
        ) {
          mostrarErro(
            1,
            `A foto "${arquivo.name}" deve ter até 5 MB e não pode estar vazia.`
          );

          continue;
        }

        const duplicada =
          fotosSelecionadas.some(
            (foto) =>
              foto.arquivo.name ===
                arquivo.name &&
              foto.arquivo.size ===
                arquivo.size &&
              foto.arquivo.lastModified ===
                arquivo.lastModified
          );

        if (duplicada) {
          continue;
        }

        fotosSelecionadas.push({
          arquivo,
          url: URL.createObjectURL(
            arquivo
          )
        });
      }

      evento.target.value = "";

      renderizarFotos();
    }
  );

  window.addEventListener(
    "pagehide",
    () => {
      fotosSelecionadas.forEach(
        (foto) => {
          URL.revokeObjectURL(
            foto.url
          );
        }
      );
    }
  );

  /* ==========================================
     06 — COLETAR DADOS
     ========================================== */

  function coletarDados() {
    const categoria =
      form.querySelector(
        'input[name="categoria"]:checked'
      )?.value || "";

    return {
      categoria,

      titulo:
        $("titulo").value.trim(),

      descricao:
        $("descricao").value.trim(),

      cidade:
        $("cidade").value.trim(),

      uf:
        $("uf").value
          .trim()
          .toUpperCase(),

      bairro:
        $("bairro").value.trim(),

      rua:
        $("rua").value.trim(),

      numero:
        $("numero").value.trim(),

      referencia:
        $("referencia").value.trim()
    };
  }

  /* ==========================================
     07 — REVISÃO
     ========================================== */

  function preencherRevisao() {
    const dados = coletarDados();

    $("revCategoria").textContent =
      nomesCategorias[dados.categoria] ||
      "Não informado";

    $("revTitulo").textContent =
      dados.titulo;

    $("revDescricao").textContent =
      dados.descricao;

    const quantidadeFotos =
      fotosSelecionadas.length;

    $("revFotos").textContent =
      quantidadeFotos === 0
        ? "Nenhuma foto selecionada"
        : `${quantidadeFotos} foto(s) selecionada(s), apenas para prévia`;

    $("revCidade").textContent =
      `${dados.cidade} / ${dados.uf}`;

    $("revBairro").textContent =
      dados.bairro;

    $("revEndereco").textContent =
      dados.numero
        ? `${dados.rua}, ${dados.numero}`
        : dados.rua;

    $("revReferencia").textContent =
      dados.referencia ||
      "Não informado";
  }

  /* ==========================================
     08 — BOTÕES ENTRE ETAPAS
     ========================================== */

  $("avancar1").addEventListener(
    "click",
    () => {
      if (validarEtapa1()) {
        mostrarEtapa(2);
      }
    }
  );

  $("avancar2").addEventListener(
    "click",
    () => {
      if (validarEtapa2()) {
        preencherRevisao();
        mostrarEtapa(3);
      }
    }
  );

  $("voltar1").addEventListener(
    "click",
    () => {
      mostrarEtapa(1);
    }
  );

  $("voltar2").addEventListener(
    "click",
    () => {
      mostrarEtapa(2);
    }
  );

  $("editarProblema").addEventListener(
    "click",
    () => {
      mostrarEtapa(1);
    }
  );

  $("editarLocal").addEventListener(
    "click",
    () => {
      mostrarEtapa(2);
    }
  );

  /* ==========================================
     09 — CATEGORIA PELA URL
     ========================================== */

  const parametros =
    new URLSearchParams(
      window.location.search
    );

  const categoriaInicial =
    parametros.get("categoria");

  if (
    categoriaInicial &&
    Object.prototype.hasOwnProperty.call(
      nomesCategorias,
      categoriaInicial
    )
  ) {
    form.querySelectorAll(
      'input[name="categoria"]'
    ).forEach(
      (radio) => {
        radio.checked =
          radio.value ===
          categoriaInicial;
      }
    );
  }

  /* ==========================================
     10 — IDENTIFICADOR LOCAL
     ========================================== */

  function gerarProtocoloLocal() {
    const tempo = Date.now()
      .toString(36)
      .toUpperCase();

    const aleatorio = Array.from(
      crypto.getRandomValues(
        new Uint8Array(6)
      )
    )
      .map((numero) =>
        numero.toString(16).padStart(2, "0")
      )
      .join("")
      .toUpperCase();

    return `LOCAL-${tempo}-${aleatorio}`;
  }

  /* ==========================================
     11 — COPIAR PROTOCOLO
     ========================================== */

  async function copiarProtocolo(
    botao
  ) {
    if (!ultimoProtocolo) {
      return;
    }

    const textoOriginal =
      "Copiar protocolo";

    try {
      if (
        !navigator.clipboard ||
        !navigator.clipboard.writeText
      ) {
        throw new Error(
          "Área de transferência indisponível"
        );
      }

      await navigator.clipboard.writeText(
        ultimoProtocolo
      );

      botao.textContent =
        "✓ Protocolo copiado";

    } catch {
      // Permite selecionar o código manualmente.
      const codigo = $("codigoGerado");

      codigo.setAttribute(
        "tabindex",
        "-1"
      );

      codigo.focus();

      const selecao =
        window.getSelection();

      const intervalo =
        document.createRange();

      intervalo.selectNodeContents(
        codigo
      );

      selecao.removeAllRanges();
      selecao.addRange(intervalo);

      botao.textContent =
        "Código selecionado: Ctrl + C";
    }

    window.setTimeout(
      () => {
        botao.textContent =
          textoOriginal;
      },
      3000
    );
  }

  /* ==========================================
     12 — BOTÕES DA CONFIRMAÇÃO

     APENAS:
     - Copiar protocolo
     - Registrar outro problema
     ========================================== */

  function configurarConfirmacao() {
    const area =
      document.querySelector(
        "#confirmacao .confirmacao-botoes"
      );

    const botaoNovo =
      $("novoRegistro");

    if (!area || !botaoNovo) {
      return;
    }

    // Cria o botão Copiar.
    const botaoCopiar =
      document.createElement("button");

    botaoCopiar.type = "button";
    botaoCopiar.id = "copiarProtocolo";

    botaoCopiar.className =
      "botao botao-primario";

    botaoCopiar.textContent =
      "Copiar protocolo";

    botaoCopiar.addEventListener(
      "click",
      () => {
        copiarProtocolo(
          botaoCopiar
        );
      }
    );

    // O segundo botão será Registrar outro.
    botaoNovo.className =
      "botao botao-secundario";

    botaoNovo.textContent =
      "Registrar outro problema";

    // Remove os outros botões somente
    // da confirmação. Não altera o restante.
    area.replaceChildren(
      botaoCopiar,
      botaoNovo
    );
  }

  configurarConfirmacao();

  /* ==========================================
     13 — SALVAMENTO DEMONSTRATIVO
     ========================================== */

  form.addEventListener(
    "submit",
    (evento) => {
      evento.preventDefault();

      // Mostra a etapa antes de validar
      // para que o erro permaneça visível.
      if (!validarEtapa1()) {
        mostrarEtapa(1);
        validarEtapa1();
        return;
      }

      if (!validarEtapa2()) {
        mostrarEtapa(2);
        validarEtapa2();
        return;
      }

      const dados = coletarDados();

      let idLocal;

      try {
        idLocal =
          gerarProtocoloLocal();
      } catch {
        mostrarEtapa(3);

        mostrarErro(
          3,
          "Não foi possível gerar o identificador neste navegador."
        );

        return;
      }

      const registro = {
        ...dados,

        idLocal,

        criadoEm:
          new Date().toISOString(),

        demonstrativo: true,

        fotosArmazenadas: false
      };

      try {
        let anteriores = [];

        const conteudo =
          localStorage.getItem(
            CHAVE_REGISTROS
          );

        if (conteudo) {
          try {
            const dadosSalvos =
              JSON.parse(conteudo);

            if (
              Array.isArray(
                dadosSalvos
              )
            ) {
              anteriores =
                dadosSalvos;
            }
          } catch {
            anteriores = [];
          }
        }

        // Mantém até 10 registros locais.
        const registros = [
          registro,
          ...anteriores
        ].slice(0, 10);

        localStorage.setItem(
          CHAVE_REGISTROS,
          JSON.stringify(registros)
        );

      } catch {
        mostrarEtapa(3);

        mostrarErro(
          3,
          "Não foi possível salvar o registro neste navegador. Verifique o armazenamento local."
        );

        return;
      }

      ultimoProtocolo =
        idLocal;

      $("codigoGerado").textContent =
        ultimoProtocolo;

      const botaoCopiar =
        $("copiarProtocolo");

      if (botaoCopiar) {
        botaoCopiar.textContent =
          "Copiar protocolo";
      }

      form.hidden = true;

      $("painelProgresso").hidden =
        true;

      $("confirmacao").hidden =
        false;

      liberarFotos();

      $("confirmacao").scrollIntoView({
        behavior: "auto",
        block: "start"
      });

      const titulo =
        $("tituloConfirmacao");

      titulo.setAttribute(
        "tabindex",
        "-1"
      );

      titulo.focus({
        preventScroll: true
      });
    }
  );

  /* ==========================================
     14 — REGISTRAR OUTRO PROBLEMA
     ========================================== */

  $("novoRegistro").addEventListener(
    "click",
    () => {
      form.reset();

      liberarFotos();

      ultimoProtocolo = "";

      form.hidden = false;

      $("painelProgresso").hidden =
        false;

      $("confirmacao").hidden =
        true;

      atualizarContador();

      mostrarEtapa(1);
    }
  );

  /* ==========================================
     15 — INICIALIZAÇÃO
     ========================================== */

  mostrarEtapa(1, false);
}