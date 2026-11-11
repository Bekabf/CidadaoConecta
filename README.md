# CidadãoConecta

### Tecnologia conectando cidadãos e transformando cidades

**Global Solution 2026/2 — FIAP**  
**Curso:** Engenharia de Software  
**Projeto:** CidadãoConecta  
**Versão:** 2.0

---

## 1. Sobre o projeto

O **CidadãoConecta** é uma plataforma web de participação cidadã voltada ao registro, à consulta e à visualização de problemas urbanos.

A solução permite que cidadãos registrem ocorrências relacionadas à infraestrutura das cidades, como buracos nas ruas, iluminação pública, saneamento, limpeza urbana e acessibilidade.

O projeto combina uma interface web responsiva com um backend desenvolvido em Java, utilizando JDBC para comunicação com um banco de dados Oracle.

A proposta é facilitar a participação da população, organizar informações sobre problemas urbanos e promover maior visibilidade das demandas das comunidades.

## 2. Problema identificado

Muitos cidadãos enfrentam dificuldades para comunicar problemas de infraestrutura urbana e acompanhar as situações registradas.

A ausência de canais acessíveis e de informações organizadas pode dificultar a identificação das principais necessidades de cada região.

Além disso, a fragmentação das informações prejudica a compreensão coletiva dos problemas existentes nas cidades.

## 3. Solução proposta

O CidadãoConecta oferece uma plataforma digital que permite:

- Registrar problemas urbanos por meio de formulário web.
- Classificar ocorrências em categorias.
- Informar dados de localização e descrição.
- Gerar um protocolo de acompanhamento.
- Consultar ocorrências pelo protocolo.
- Visualizar os detalhes e a situação de um registro.
- Consultar ocorrências resumidas na Comunidade.
- Explorar indicadores sobre os problemas cadastrados.

O cadastro de ocorrências não exige criação de conta ou autenticação.

## 4. Tecnologias utilizadas

### Backend

- Java 21 LTS
- Java JDBC
- Programação Orientada a Objetos
- Java Collections
- Padrão DAO
- Camada Service
- API HTTP com `HttpServer`
- Tratamento de exceções
- Validação de dados

### Banco de dados

- Oracle Database
- SQL
- Modelagem relacional
- Chaves primárias e estrangeiras
- Relacionamentos N:N
- Restrições de integridade
- Consultas com JOIN, GROUP BY, COUNT e subconsultas

### Frontend

- HTML5
- CSS3
- JavaScript
- Fetch API
- Layout responsivo
- Formulários interativos
- Consulta dinâmica de ocorrências
- Indicadores e visualizações

### Ferramentas

- Visual Studio Code
- Oracle SQL Developer
- Oracle SQL Developer Data Modeler
- Git e GitHub

## 5. Estrutura do projeto

```text
CidadaoConecta/
|
|-- backend/
|   |-- src/br/com/cidadaoconecta/
|   |   |-- config/
|   |   |   |-- ConexaoOracle.java
|   |   |-- dao/
|   |   |   |-- CategoriaDAO.java
|   |   |   |-- OcorrenciaDAO.java
|   |   |-- model/
|   |   |   |-- Categoria.java
|   |   |   |-- Ocorrencia.java
|   |   |   |-- Usuario.java
|   |   |-- service/
|   |   |   |-- OcorrenciaService.java
|   |   |-- interfaceapp/
|   |       |-- ServidorWeb.java
|   |       |-- MenuPrincipal.java
|   |       |-- Classes de teste
|   |
|   |-- lib/
|   |   |-- ojdbc11.jar
|   |
|   |-- bin/
|       |-- Classes compiladas
|
|-- database/
|   |-- 01_criar_tabelas.sql
|   |-- 02_dados_iniciais.sql
|   |-- 03_consultas.sql
|
|-- diagramas/
|   |-- CidadaoConecta_DER.pdf
|   |-- CidadaoConecta_UML_Classes.pdf
|
|-- frontend/
|   |-- CSS/
|   |-- JS/
|   |-- index.html
|   |-- servicos.html
|   |-- ocorrencias.html
|   |-- consultar.html
|   |-- registrar.html
|
|-- .gitignore
|-- README.md
```

A pasta `src` contém os códigos-fonte Java, enquanto `bin` armazena as classes compiladas.

A pasta `backup-classes`, quando presente no ambiente de desenvolvimento, contém arquivos de backup temporários e não é necessária para a execução da aplicação.

## 6. Arquitetura Java

O backend foi organizado em camadas, com responsabilidades específicas.

### Model

Representa as entidades do sistema:

- `Usuario.java`
- `Categoria.java`
- `Ocorrencia.java`

As classes utilizam atributos privados, construtores, getters, setters e métodos de representação dos objetos.

### DAO

Responsável pelas operações de persistência no Oracle utilizando JDBC.

- `OcorrenciaDAO.java`
- `CategoriaDAO.java`

Os DAOs implementam operações de cadastro, consulta, atualização e exclusão.

### Service

A classe `OcorrenciaService.java` centraliza as regras de negócio e as validações das ocorrências.

Entre as validações implementadas estão os campos obrigatórios, as siglas de estados brasileiros e as categorias inválidas, inativas ou duplicadas.

### Config

A classe `ConexaoOracle.java` gerencia a abertura de conexões JDBC por meio de variáveis de ambiente.

### Interface e servidor

A classe `ServidorWeb.java` disponibiliza o frontend e as rotas HTTP utilizadas pelas páginas.

A classe `MenuPrincipal.java` oferece uma interface alternativa de terminal para demonstração das operações Java.

### Fluxo da aplicação web

```text
Frontend
HTML + CSS + JavaScript
          |
          v
     ServidorWeb
       API HTTP
          |
          v
   OcorrenciaService
          |
          v
     OcorrenciaDAO
          |
          v
     ConexaoOracle
          |
          v
    Oracle Database
```

O servidor também utiliza diretamente `CategoriaDAO` e `OcorrenciaDAO` em determinadas operações de consulta.

## 7. Banco de dados

O banco relacional Oracle possui nove tabelas:

1. `CC2_USUARIO`
2. `CC2_CATEGORIA`
3. `CC2_OCORRENCIA`
4. `CC2_OCORRENCIA_CATEGORIA`
5. `CC2_HISTORICO_STATUS`
6. `CC2_APOIO`
7. `CC2_EVIDENCIA`
8. `CC2_FEEDBACK`
9. `CC2_DUPLICIDADE`

A tabela `CC2_OCORRENCIA_CATEGORIA` implementa o relacionamento muitos-para-muitos entre ocorrências e categorias.

O modelo também contempla histórico de status, apoios, evidências, feedbacks e possíveis duplicidades como estruturas para funcionalidades existentes ou futuras.

### Scripts SQL

**`01_criar_tabelas.sql`**

Cria as nove tabelas e define suas chaves primárias, estrangeiras e restrições.

**`02_dados_iniciais.sql`**

Insere seis categorias e uma ocorrência demonstrativa, incluindo relacionamentos e histórico inicial.

**`03_consultas.sql`**

Contém 15 consultas SQL para análise dos registros e geração de indicadores.

### Ordem de execução

Em um esquema Oracle novo, sem as tabelas do projeto:

1. Executar `01_criar_tabelas.sql`.
2. Executar `02_dados_iniciais.sql`.
3. Executar `03_consultas.sql`.

**Atenção:** os scripts de criação e carga inicial não devem ser executados novamente em um esquema que já possua as tabelas e os registros. Isso poderá gerar erros de objetos existentes ou de restrições de unicidade.

As consultas do terceiro arquivo são somente de leitura.

## 8. Funcionalidades implementadas

### Cadastro de ocorrências

Permite registrar problemas urbanos com título, descrição, categoria e localização.

O backend valida as informações e realiza o cadastro no Oracle.

Os registros recebem automaticamente um protocolo exclusivo e o status inicial `ABERTA`.

### Consulta por protocolo

Permite localizar uma ocorrência cadastrada utilizando seu protocolo.

Os detalhes são apresentados em uma janela interativa na página `consultar.html`.

### Comunidade

Exibe uma listagem resumida das ocorrências cadastradas no Oracle.

As informações fornecidas pela API incluem:

- Título
- Categoria
- Bairro
- Cidade e UF
- Status
- Data de cadastro

A resposta pública não contém o protocolo, o ID interno, a descrição completa, a rua, o número do endereço ou os dados do usuário.

### Indicadores

A página Comunidade apresenta informações e indicadores calculados a partir dos registros obtidos pela API.

### Operações administrativas em Java

As camadas DAO e Service disponibilizam operações CRUD.

O menu de terminal permite demonstrar funcionalidades de cadastro, consulta, atualização e exclusão sem disponibilizar essas ações diretamente ao público pela interface web.

### Histórico inicial

O cadastro registra o evento inicial da ocorrência na tabela `CC2_HISTORICO_STATUS`.

## 9. API HTTP

O servidor Java utiliza a porta `8080` e disponibiliza as seguintes rotas:

| Método | Rota | Finalidade |
|---|---|---|
| POST | `/api/ocorrencias` | Registrar ocorrência |
| GET | `/api/ocorrencias?protocolo=CC-...` | Consultar por protocolo |
| GET | `/api/ocorrencias/publicas` | Listar dados resumidos da Comunidade |

O cadastro recebe informações por meio de `application/x-www-form-urlencoded`.

As respostas da API são fornecidas em JSON.

### Exemplo de resposta de cadastro

```json
{
  "protocolo": "CC-EXEMPLO-1234",
  "status": "ABERTA"
}
```

O protocolo apresentado é apenas ilustrativo.

### Códigos HTTP utilizados

- `200` — Consulta realizada com sucesso.
- `201` — Ocorrência cadastrada.
- `400` — Dados ou protocolo inválidos.
- `404` — Registro ou rota não encontrado.
- `405` — Método HTTP não permitido.
- `413` — Corpo da requisição muito grande.
- `415` — Formato de envio não suportado.
- `500` — Falha interna ou de acesso ao banco.

A API não disponibiliza, nesta versão, rotas públicas para atualizar ou excluir ocorrências.

## 10. Como executar o projeto

### Pré-requisitos

- JDK 21 instalado.
- Oracle Database acessível.
- Driver `ojdbc11.jar` disponível em `backend/lib`.
- Estrutura das tabelas criada no Oracle.
- Terminal PowerShell no Windows.

Os comandos abaixo devem ser executados a partir da pasta principal `CidadaoConecta`.

### 10.1. Verificar o Java

```powershell
java -version
javac -version
```

O projeto foi testado utilizando Java 21.

### 10.2. Configurar o Oracle

A conexão utiliza três variáveis de ambiente:

- `CC_DB_URL`
- `CC_DB_USER`
- `CC_DB_PASSWORD`

Exemplo de configuração no PowerShell:

```powershell
$env:CC_DB_URL = "jdbc:oracle:thin:@//SERVIDOR:1521/SERVICO"
$env:CC_DB_USER = "SEU_USUARIO"

$senhaSegura = Read-Host "Senha Oracle" -AsSecureString
$env:CC_DB_PASSWORD = [System.Net.NetworkCredential]::new("", $senhaSegura).Password
Remove-Variable senhaSegura
```

Substitua o servidor, o serviço e o usuário pelos dados de acesso ao banco Oracle utilizado.

A senha deve ser informada no terminal e não deve ser publicada no repositório.

**Importante:** as variáveis configuradas dessa forma pertencem à sessão atual do PowerShell. Caso um novo terminal seja aberto, será necessário configurá-las novamente.

### 10.3. Compilar o backend

Crie a pasta de saída:

```powershell
New-Item -ItemType Directory -Force -Path "backend/bin"
```

Compile todos os arquivos Java:

```powershell
$arquivosJava = Get-ChildItem "backend/src" -Recurse -Filter "*.java" |
    ForEach-Object { $_.FullName }

javac -encoding UTF-8 -cp "backend/lib/ojdbc11.jar" -d "backend/bin" $arquivosJava
```

As classes compiladas serão armazenadas na pasta `backend/bin`.

### 10.4. Iniciar o servidor web

Execute:

```powershell
java "-Dfile.encoding=UTF-8" -cp "backend/bin;backend/lib/ojdbc11.jar" br.com.cidadaoconecta.interfaceapp.ServidorWeb
```

Após a inicialização, o terminal deverá apresentar uma mensagem indicando que o servidor está disponível na porta 8080.

### 10.5. Acessar o frontend

Abra o navegador e acesse:

**http://localhost:8080**

O servidor Java disponibilizará as páginas HTML, CSS e JavaScript.

Mantenha o terminal do servidor aberto durante a utilização.

Para encerrar o servidor, pressione `Ctrl + C` no terminal em que ele estiver em execução.

### 10.6. Executar o menu de terminal

Opcionalmente, também é possível executar:

```powershell
java "-Dfile.encoding=UTF-8" -cp "backend/bin;backend/lib/ojdbc11.jar" br.com.cidadaoconecta.interfaceapp.MenuPrincipal
```

O menu permite demonstrar as operações Java diretamente pelo terminal.

## 11. Páginas do frontend

O frontend está organizado em cinco páginas principais.

### `index.html` — Início

Apresenta o projeto, sua proposta e os caminhos para utilizar a plataforma.

### `servicos.html` — Serviços

Explica os serviços disponibilizados pelo CidadãoConecta e orienta os cidadãos.

### `ocorrencias.html` — Comunidade

Apresenta os registros urbanos resumidos e os indicadores obtidos a partir das informações fornecidas pelo backend.

### `consultar.html` — Consultar

Disponibiliza a pesquisa por protocolo e apresenta os detalhes da ocorrência em uma janela interativa.

### `registrar.html` — Registrar problema

Disponibiliza o formulário de registro de ocorrências, integrado à API Java e ao Oracle.

Os estilos estão organizados na pasta `frontend/CSS`, e os scripts na pasta `frontend/JS`.

## 12. Testes realizados

O projeto passou por testes de validação, conexão JDBC, operações CRUD e integração com o frontend.

### Validações do Service

Foram executados oito cenários:

1. Título vazio.
2. UF inválida.
3. Ocorrência sem categoria.
4. Categoria duplicada.
5. Descrição muito curta.
6. Consulta sem protocolo.
7. Busca com ID inválido.
8. Categoria inativa.

**Resultado: 8 testes aprovados e 0 reprovados.**

### Teste de conexão Oracle

A aplicação estabeleceu conexão JDBC com o Oracle e consultou com sucesso as seis categorias cadastradas.

### CRUD de ocorrências

Foram testadas:

- Inserção de ocorrência temporária.
- Consulta pelo ID.
- Consulta pelo protocolo.
- Listagem das ocorrências.
- Atualização de informações.
- Exclusão da ocorrência temporária.

**Resultado: CRUD concluído com sucesso.**

### CRUD de categorias

Foram testadas:

- Inserção de categoria temporária.
- Listagem de categorias.
- Consulta pelo ID.
- Atualização da categoria.
- Exclusão da categoria temporária.

**Resultado: CRUD concluído com sucesso.**

### Integração web

Foram verificados os fluxos principais:

- Inicialização do servidor Java.
- Carregamento do frontend.
- Cadastro de ocorrência.
- Consulta pelo protocolo.
- Exibição dos detalhes da ocorrência.
- Listagem da Comunidade com informações do Oracle.

### Execução dos testes

Com o projeto compilado, execute:

```powershell
java -cp "backend/bin;backend/lib/ojdbc11.jar" br.com.cidadaoconecta.interfaceapp.TesteOcorrenciaService
```

Para testar a conexão JDBC:

```powershell
java -cp "backend/bin;backend/lib/ojdbc11.jar" br.com.cidadaoconecta.interfaceapp.TesteConexao
```

Para executar o CRUD de ocorrências:

```powershell
java -cp "backend/bin;backend/lib/ojdbc11.jar" br.com.cidadaoconecta.interfaceapp.TesteOcorrenciaDAO
```

Para executar o CRUD de categorias:

```powershell
java -cp "backend/bin;backend/lib/ojdbc11.jar" br.com.cidadaoconecta.interfaceapp.TesteCategoriaDAO
```

**Atenção:** os testes CRUD realizam inserções, atualizações e exclusões reais no Oracle utilizando registros temporários. Recomenda-se executá-los em ambiente de desenvolvimento ou teste.

## 13. Diagramas

A pasta `diagramas` contém os documentos de modelagem do projeto.

### Diagrama Entidade-Relacionamento

**Arquivo:** `CidadaoConecta_DER.pdf`

Representa as nove tabelas Oracle, suas chaves e os relacionamentos entre as entidades.

### Diagrama UML de Classes

**Arquivo:** `CidadaoConecta_UML_Classes.pdf`

Representa a organização das classes Java e suas dependências, incluindo os modelos, a camada Service, os DAOs, a conexão Oracle e as interfaces do backend.

## 14. Segurança e privacidade

O CidadãoConecta apresenta medidas iniciais de segurança:

- Configuração da conexão Oracle por variáveis de ambiente.
- Uso de `PreparedStatement` nas operações SQL.
- Validação dos dados recebidos.
- Tratamento de erros HTTP.
- Restrição de métodos nas rotas da API.
- Limitação do tamanho das requisições de cadastro.
- Separação entre informações resumidas e detalhadas nas respostas.

A Comunidade exibe automaticamente os títulos e os dados resumidos de todas as ocorrências cadastradas, independentemente do campo `VISIBILIDADE_PUBLICA`.

O campo de visibilidade permanece no modelo de dados para uma possível evolução do controle de publicação.

Por não possuir autenticação na versão atual, a aplicação deve ser considerada um projeto acadêmico de execução local.

O sistema necessita de controles adicionais de autenticação, autorização, moderação e proteção de dados antes de uma eventual disponibilização pública.

## 15. Limitações da versão atual

Algumas funcionalidades estão previstas na modelagem do banco, mas ainda não possuem implementação completa na interface web.

Entre elas:

- Gerenciamento de usuários e autenticação.
- Atualização administrativa dos status pela interface web.
- Upload e persistência de fotografias no backend.
- Registro de apoios dos cidadãos.
- Feedback das ocorrências resolvidas.
- Detecção automática de ocorrências duplicadas.
- Controle avançado de publicação e moderação.

O projeto diferencia as funcionalidades implementadas das possibilidades de evolução previstas na arquitetura.

## 16. Evoluções futuras

Entre as melhorias previstas estão:

- Implementar autenticação e autorização.
- Desenvolver um painel administrativo para gerenciamento de ocorrências.
- Ampliar o acompanhamento dos status e do histórico.
- Implementar persistência de fotografias.
- Disponibilizar recursos de apoio comunitário.
- Desenvolver mecanismos de identificação de duplicidades.
- Aprimorar os indicadores e as visualizações geográficas.
- Reforçar os mecanismos de privacidade e acessibilidade.
- Expandir os testes automatizados e de integração.

## 17. Considerações finais

O CidadãoConecta demonstra a aplicação integrada dos conhecimentos de Engenharia de Software, Programação Orientada a Objetos, Java, JDBC, SQL, modelagem relacional e desenvolvimento web.

A integração entre o frontend, a API Java e o banco Oracle permite registrar e consultar informações reais em uma base relacional.

Por meio da tecnologia, o projeto propõe uma forma de estimular a participação cidadã e ampliar a visibilidade dos problemas urbanos.

---

**CidadãoConecta — Tecnologia para cidades mais participativas.**

*Global Solution 2026/2 — FIAP*