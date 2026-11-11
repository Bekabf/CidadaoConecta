
# CidadãoConecta
### Tecnologia conectando cidadãos e transformando cidades

**Global Solution 2026/2 — FIAP**  
**Projeto:** CidadãoConecta  
**Área:** Engenharia de Software

---

## 1. Sobre o projeto

O CidadãoConecta é uma plataforma de participação cidadã voltada ao registro e acompanhamento de problemas urbanos.

A proposta é facilitar a comunicação de ocorrências relacionadas à infraestrutura das cidades, como buracos nas ruas, iluminação pública, saneamento, limpeza urbana e acessibilidade.

O sistema busca incentivar a participação da população, organizar as informações registradas e contribuir para uma gestão urbana mais transparente.

## 2. Problema identificado

Muitos cidadãos enfrentam dificuldades para registrar problemas urbanos e acompanhar seu andamento.

A falta de canais acessíveis e de informações organizadas pode dificultar a identificação e a priorização das demandas da população.

## 3. Solução proposta

O CidadãoConecta propõe uma plataforma digital com funcionalidades para:

- Registrar ocorrências urbanas.
- Classificar problemas por categorias.
- Consultar ocorrências por protocolo.
- Acompanhar o status dos registros.
- Visualizar informações sobre problemas urbanos.
- Organizar dados para apoiar a análise das demandas.

## 4. Tecnologias utilizadas

### Backend
- Java 21
- JDBC
- Programação Orientada a Objetos
- Padrão DAO
- Camada Service

### Banco de dados
- Oracle Database
- SQL
- Modelo relacional
- Relacionamentos entre entidades

### Frontend
- HTML5
- CSS3
- JavaScript
- Layout responsivo

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
|   |-- lib/
|   |   |-- ojdbc11.jar
|   |
|   |-- src/br/com/cidadaoconecta/
|       |-- config/
|       |-- dao/
|       |-- interfaceapp/
|       |-- model/
|       |-- service/
|
|-- database/
|   |-- 01_criar_tabelas.sql
|   |-- 02_dados_iniciais.sql
|   |-- 03_consultas.sql
|
|-- diagramas/
|
|-- frontend/
|
|-- README.md
```

## 6. Arquitetura Java

O backend foi organizado em camadas:

**Model:** representa as entidades de negócio, incluindo Usuario, Ocorrencia e Categoria.

**DAO:** realiza o acesso e a persistência dos dados no Oracle utilizando JDBC.

**Service:** concentra regras de negócio e validações das ocorrências.

**Config:** disponibiliza a conexão com o banco de dados.

**Interface:** disponibiliza um menu de terminal para testar as funcionalidades do sistema.

### Fluxo da aplicação Java

```text
MenuPrincipal
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

## 7. Banco de dados

O banco relacional utiliza nove tabelas:

1. CC2_USUARIO
2. CC2_CATEGORIA
3. CC2_OCORRENCIA
4. CC2_OCORRENCIA_CATEGORIA
5. CC2_HISTORICO_STATUS
6. CC2_APOIO
7. CC2_EVIDENCIA
8. CC2_FEEDBACK
9. CC2_DUPLICIDADE

A tabela CC2_OCORRENCIA_CATEGORIA implementa o relacionamento muitos para muitos (N:N) entre ocorrências e categorias.

Os scripts SQL estão disponíveis na pasta `database`.

O diagrama entidade-relacionamento está disponível na pasta `diagramas`.

## 8. Funcionalidades implementadas no backend

- Cadastrar ocorrências.
- Listar ocorrências.
- Consultar ocorrências por protocolo.
- Atualizar dados das ocorrências.
- Excluir ocorrências pelo menu de demonstração.
- Selecionar múltiplas categorias.
- Consultar categorias cadastradas.
- Validar campos obrigatórios.
- Validar siglas de estados brasileiros.
- Impedir categorias inválidas e repetidas.
- Gerar protocolos automaticamente.
- Registrar o histórico inicial das ocorrências.

## 9. Como executar o backend

### Pré-requisitos

- JDK 21 instalado.
- Acesso a um banco Oracle compatível.
- Driver JDBC Oracle disponível em `backend/lib/ojdbc11.jar`.
- Tabelas e dados necessários criados no banco.

### Configurar a conexão

A classe `ConexaoOracle.java` utiliza as variáveis de ambiente:

- CC_DB_URL
- CC_DB_USER
- CC_DB_PASSWORD

Exemplo de configuração no PowerShell:

```powershell
$env:CC_DB_URL = "jdbc:oracle:thin:@//SERVIDOR:1521/SERVICO"
$env:CC_DB_USER = "SEU_USUARIO"

$senhaSegura = Read-Host "Senha Oracle" -AsSecureString
$env:CC_DB_PASSWORD = [System.Net.NetworkCredential]::new("", $senhaSegura).Password
Remove-Variable senhaSegura
```

Não armazene senhas diretamente no código ou no repositório.

### Configurar UTF-8 no PowerShell

```powershell
chcp 65001

[Console]::InputEncoding = [System.Text.UTF8Encoding]::new($false)
[Console]::OutputEncoding = [System.Text.UTF8Encoding]::new($false)
```

### Compilar

Execute na pasta principal do projeto:

```powershell
javac -encoding UTF-8 -cp "backend/src;backend/lib/ojdbc11.jar" -d backend/src backend/src/br/com/cidadaoconecta/interfaceapp/MenuPrincipal.java
```

### Executar

```powershell
java "-Dstdin.encoding=UTF-8" "-Dstdout.encoding=UTF-8" "-Dstderr.encoding=UTF-8" -cp "backend/src;backend/lib/ojdbc11.jar" br.com.cidadaoconecta.interfaceapp.MenuPrincipal
```

### Menu disponível

```text
1 - Listar ocorrencias
2 - Buscar por protocolo
3 - Cadastrar ocorrencia
4 - Atualizar ocorrencia
5 - Excluir ocorrencia
0 - Sair
```

A exclusão e a atualização pelo terminal são funcionalidades de demonstração administrativa. O projeto ainda não implementa autenticação e autorização para disponibilizá-las publicamente.

## 10. Frontend

O frontend está disponível na pasta `frontend`.

As páginas foram desenvolvidas com HTML, CSS e JavaScript, buscando oferecer uma interface moderna, responsiva e acessível.

A versão atual utiliza dados demonstrativos e armazenamento local do navegador em determinadas funcionalidades.

**Importante:** a integração direta do frontend com o backend Java e o banco Oracle ainda não foi concluída. As operações JDBC testadas são realizadas pela aplicação Java.

## 11. Testes realizados

Foram realizados testes de:

- Conexão Java com Oracle.
- CRUD de categorias.
- CRUD de ocorrências.
- Validação de campos obrigatórios.
- Integração entre Service, DAO e Oracle.
- Cadastro e consulta por protocolo.
- Atualização e exclusão de ocorrências.
- Associação de múltiplas categorias.
- Persistência de caracteres acentuados.

Os testes de validação da camada Service tiveram oito cenários aprovados.

## 12. Diagramas

A pasta `diagramas` contém os artefatos de modelagem do projeto, incluindo o diagrama entidade-relacionamento e o diagrama UML de classes.

## 13. Segurança e privacidade

O projeto contempla medidas iniciais de organização e privacidade, incluindo:

- Configuração de acesso ao Oracle por variáveis de ambiente.
- Uso de PreparedStatement nas operações JDBC.
- Separação entre identificadores públicos e protocolos.
- Campos de controle de visibilidade das ocorrências.

O sistema ainda necessita de autenticação, autorização e demais controles de segurança antes de uma eventual utilização pública.

## 14. Evoluções planejadas

- Integrar o frontend com uma API Java.
- Implementar autenticação e autorização.
- Ampliar o acompanhamento dos status.
- Finalizar indicadores e visualizações geográficas.
- Realizar testes integrados da aplicação web.
- Refinar o controle de privacidade e a experiência do usuário.

---

CidadãoConecta — Tecnologia para cidades mais participativas.