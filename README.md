#  Sistema de Biblioteca — WebService SOAP

> **Disciplina:** Arquitetura SOA e Web Services — FIAP  
> **Aluno:** Gabriel Machado Lacerda RM:556714 3ESPW  
> **Professor:** Salatiel Luz Marinho  
> **Tecnologias:** Java 21 · JAX-WS · JAXB · Maven · IntelliJ IDEA

---

##  Índice

1. [Contexto de Implantação](#contexto)
2. [Problemas Resolvidos](#problemas)
3. [Arquitetura do Sistema](#arquitetura)
4. [Boas Práticas Aplicadas](#boas-praticas)
5. [Estrutura do Projeto](#estrutura)
6. [Guia de Instalação (IntelliJ)](#instalacao)
7. [Como Executar](#executar)
8. [Testando com Insomnia/Postman](#testes)
9. [Exemplos de Requisições SOAP](#exemplos)
10. [Próximas Features](#proximas)

---

##  Contexto de Implantação <a name="contexto"></a>

O **Sistema de Biblioteca** foi desenvolvido para uma instituição de ensino que precisava modernizar o controle do seu acervo bibliográfico. Anteriormente, o gerenciamento era feito com planilhas e anotações manuais, gerando problemas de consistência e rastreabilidade.

### Cenário

- **Público-alvo:** Bibliotecários e alunos da instituição
- **Volume esperado:** ~500 livros no catálogo, ~100 empréstimos simultâneos
- **Ambiente:** Rede interna (intranet institucional)
- **Protocolo escolhido:** SOAP — por ser um protocolo formal com contrato explícito (WSDL), facilitando integração com outros sistemas legados da instituição

---

##  Problemas Resolvidos <a name="problemas"></a>

| Problema Anterior | Solução Implementada |
|---|---|
| Controle manual por planilhas | Serviço centralizado com estado compartilhado |
| Sem controle de disponibilidade | Contagem de exemplares por livro |
| Empréstimos sem rastreabilidade | IDs únicos + registro de datas |
| Difícil integração com outros sistemas | Contrato WSDL formal e interoperável |
| Sem validação de dados | Validações no Service com códigos de erro |

---

## 🏛 Arquitetura do Sistema <a name="arquitetura"></a>

```
┌─────────────────────────────────────────────────────────┐
│                    soap-client                          │
│  BibliotecaClienteApp  →  SoapClientHelper              │
│         (Menu Interativo)     (HTTP + SOAP Envelope)    │
└────────────────────┬────────────────────────────────────┘
                     │ HTTP POST (SOAP XML)
                     ▼
┌─────────────────────────────────────────────────────────┐
│                    soap-server                          │
│  ServicePublisher  →  BibliotecaService (JAX-WS)        │
│         (Endpoint.publish)    (8 operações SOAP)        │
│                               ↓                         │
│                    BibliotecaRepository                 │
│                    (Catálogo + Empréstimos em memória)   │
└─────────────────────────────────────────────────────────┘
```

### Fluxo de uma Operação SOAP

```
Cliente                    Servidor
  │                           │
  │──── SOAP Request XML ────►│
  │     (HTTP POST)           │── Valida entrada
  │                           │── Processa regra de negócio
  │                           │── Acessa repositório
  │◄─── SOAP Response XML ───│
  │     (resultado)           │
```

---

##  Boas Práticas Aplicadas <a name="boas-praticas"></a>

### 1. Separação de Responsabilidades (SoC)
- **Model:** objetos de domínio com anotações JAXB
- **Repository:** acesso a dados (padrão Repository)
- **Service:** lógica de negócio e operações SOAP
- **Publisher:** inicialização do servidor
- **Client:** consumo do serviço

### 2. Interface como Contrato (`IBibliotecaService`)
- Baixo acoplamento entre interface e implementação
- Facilita testes unitários com mocks
- Permite trocar a implementação sem impactar clientes

### 3. Singleton Thread-Safe no Repository
- Estado compartilhado entre requisições concorrentes
- `synchronized` nas operações de escrita (empréstimo/devolução)
- `AtomicInteger` para geração segura de IDs

### 4. Validação na Camada de Serviço
- Verificação de campos obrigatórios antes de acessar dados
- Códigos de erro semânticos (`ERR_ISBN_VAZIO`, `ERR_SEM_EXEMPLARES`, etc.)
- Resposta padronizada com `RespostaOperacao`

### 5. Nomes Explícitos com `@WebParam`
```java
@WebParam(name = "isbn") String isbn
@WebParam(name = "nomeUsuario") String nomeUsuario
```
Garante que o XML SOAP gerado seja legível e previsível.

### 6. Timeouts no Cliente HTTP
```java
conn.setConnectTimeout(5_000);  // 5 segundos
conn.setReadTimeout(10_000);    // 10 segundos
```
Evita bloqueio indefinido em caso de servidor indisponível.

### 7. Logs de Diagnóstico no Servidor
Cada operação do `BibliotecaService` imprime logs prefixados com `[Service]`, facilitando debugging.

---

##  Estrutura do Projeto <a name="estrutura"></a>

```
biblioteca-soap/
│
├── README.md
├── DOCUMENTACAO.md
├── exemplos-insomnia/
│   └── colecao-biblioteca-soap.json
│
├── soap-server/                         ← Projeto Maven do Servidor
│   ├── pom.xml
│   └── src/main/java/br/com/fiap/biblioteca/
│       ├── model/
│       │   ├── Livro.java
│       │   ├── Emprestimo.java
│       │   ├── RespostaOperacao.java
│       │   ├── ListaLivros.java
│       │   └── ListaEmprestimos.java
│       ├── repository/
│       │   └── BibliotecaRepository.java
│       ├── service/
│       │   ├── IBibliotecaService.java
│       │   └── BibliotecaService.java
│       └── publisher/
│           └── ServicePublisher.java
│
└── soap-client/                         ← Projeto Maven do Cliente
    ├── pom.xml
    └── src/main/java/br/com/fiap/cliente/
        ├── BibliotecaClienteApp.java
        ├── SoapClientHelper.java
        └── XmlParser.java
```

---

##  Guia de Instalação no IntelliJ IDEA <a name="instalacao"></a>

### Pré-requisitos

| Ferramenta | Versão mínima | Download |
|---|---|---|
| JDK | 21 | https://adoptium.net |
| Maven | 3.8+ | https://maven.apache.org (ou bundled no IntelliJ) |
| IntelliJ IDEA | 2023.1+ | https://www.jetbrains.com/idea |

---

### Passo 1 — Abrir o Projeto do Servidor

1. Abra o IntelliJ IDEA
2. Clique em **File → Open...**
3. Navegue até a pasta `biblioteca-soap/soap-server`
4. Selecione a pasta e clique **OK**
5. O IntelliJ detectará o `pom.xml` automaticamente — clique em **Trust Project**
6. Aguarde o download das dependências Maven (barra de progresso no rodapé)

### Passo 2 — Configurar o JDK no Servidor

1. Acesse **File → Project Structure** (Ctrl+Alt+Shift+S)
2. Em **Project → SDK**, selecione **Java 21** (ou adicione clicando em **+**)
3. Em **Project language level**, selecione **21**
4. Clique **OK**

### Passo 3 — Criar a Run Configuration do Servidor

1. No menu superior, clique em **Run → Edit Configurations...**
2. Clique no **+** e escolha **Application**
3. Preencha:
   - **Name:** `Biblioteca Server`
   - **Main class:** `br.com.fiap.biblioteca.publisher.ServicePublisher`
   - **Module:** `soap-server`
4. Clique **Apply → OK**

### Passo 4 — Abrir o Projeto do Cliente

1. Vá em **File → Open...** novamente (ou use **File → New → Project from Existing Sources**)
2. Selecione a pasta `biblioteca-soap/soap-client`

> **Dica:** Para trabalhar com os dois projetos ao mesmo tempo, use **File → New → Module from Existing Sources** para adicionar o cliente como módulo do mesmo workspace, ou abra uma segunda janela do IntelliJ.

### Passo 5 — Criar a Run Configuration do Cliente

1. **Run → Edit Configurations → +** → **Application**
2. Preencha:
   - **Name:** `Biblioteca Cliente`
   - **Main class:** `br.com.fiap.cliente.BibliotecaClienteApp`
   - **Module:** `soap-client`
3. Clique **Apply → OK**

---

##  Como Executar <a name="executar"></a>

###  IMPORTANTE: O servidor deve ser iniciado ANTES do cliente!

---

### Passo 1 — Iniciar o Servidor

1. No IntelliJ, abra o projeto `soap-server`
2. Selecione a Run Configuration **"Biblioteca Server"**
3. Clique em **▶ Run** (ou Shift+F10)
4. Aguarde a mensagem no console:

```
============================================================
  Sistema de Biblioteca - SOAP WebService
  Professor: Salatiel Luz Marinho | FIAP
============================================================

Iniciando publicação do WebService...

 WebService publicado com sucesso!

 Endpoint : http://localhost:8080/biblioteca
 WSDL     : http://localhost:8080/biblioteca?wsdl
```

5. **Verifique:** abra o navegador em `http://localhost:8080/biblioteca?wsdl`  
   Você deverá ver o XML do WSDL.

---

### Passo 2 — Iniciar o Cliente

1. No IntelliJ, abra o projeto `soap-client`
2. Selecione a Run Configuration **"Biblioteca Cliente"**
3. Clique em **▶ Run**
4. O menu interativo será exibido no console:

```
======================================================================
     SISTEMA DE BIBLIOTECA — Cliente SOAP
   Disciplina: Arquitetura SOA | FIAP
======================================================================

Verificando conexão com o servidor...  Conectado!

═══════════════ MENU ═══════════════
  [1] Buscar livro por ISBN
  [2] Listar livros disponíveis
  [3] Realizar empréstimo
  [4] Devolver livro
  [5] Buscar por autor
  [6] Buscar por título
  [7] Consultar empréstimo pelo ID
  [8] Listar empréstimos por CPF
  [0] Sair
═════════════════════════════════════
Escolha uma opção:
```

5. **Interaja** digitando as opções e pressionando **Enter**.

---

### Execução via Terminal (alternativa)

```bash
# Compilar e executar o servidor
cd soap-server
mvn clean compile exec:java -Dexec.mainClass="br.com.fiap.biblioteca.publisher.ServicePublisher"

# Em outro terminal — compilar e executar o cliente
cd soap-client
mvn clean compile exec:java -Dexec.mainClass="br.com.fiap.cliente.BibliotecaClienteApp"
```

---

##  Testando com Insomnia / Postman <a name="testes"></a>

### Configuração Básica

- **Método:** POST
- **URL:** `http://localhost:8080/biblioteca`
- **Header:** `Content-Type: text/xml;charset=UTF-8`

---

##  Exemplos de Requisições SOAP <a name="exemplos"></a>

### 1. Listar Livros Disponíveis

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:bib="http://biblioteca.fiap.com.br/">
    <soapenv:Header/>
    <soapenv:Body>
        <bib:listarLivrosDisponiveis/>
    </soapenv:Body>
</soapenv:Envelope>
```

### 2. Buscar Livro por ISBN

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:bib="http://biblioteca.fiap.com.br/">
    <soapenv:Header/>
    <soapenv:Body>
        <bib:buscarLivroPorIsbn>
            <isbn>978-85-7522-743-0</isbn>
        </bib:buscarLivroPorIsbn>
    </soapenv:Body>
</soapenv:Envelope>
```

### 3. Registrar Empréstimo

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:bib="http://biblioteca.fiap.com.br/">
    <soapenv:Header/>
    <soapenv:Body>
        <bib:registrarEmprestimo>
            <isbn>978-85-7522-743-0</isbn>
            <nomeUsuario>Maria Silva</nomeUsuario>
            <cpfUsuario>12345678900</cpfUsuario>
        </bib:registrarEmprestimo>
    </soapenv:Body>
</soapenv:Envelope>
```

**Resposta esperada:**
```xml
<S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/">
    <S:Body>
        <ns2:registrarEmprestimoResponse xmlns:ns2="http://biblioteca.fiap.com.br/">
            <return>
                <sucesso>true</sucesso>
                <mensagem>Empréstimo registrado com sucesso! ID: EMP-1001 | Livro: 'Clean Code' | Devolução prevista: 13/04/2026</mensagem>
            </return>
        </ns2:registrarEmprestimoResponse>
    </S:Body>
</S:Envelope>
```

### 4. Devolver Livro

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:bib="http://biblioteca.fiap.com.br/">
    <soapenv:Header/>
    <soapenv:Body>
        <bib:devolverLivro>
            <idEmprestimo>EMP-1001</idEmprestimo>
        </bib:devolverLivro>
    </soapenv:Body>
</soapenv:Envelope>
```

### 5. Buscar por Autor

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:bib="http://biblioteca.fiap.com.br/">
    <soapenv:Header/>
    <soapenv:Body>
        <bib:buscarPorAutor>
            <autor>Robert</autor>
        </bib:buscarPorAutor>
    </soapenv:Body>
</soapenv:Envelope>
```

### 6. Listar Empréstimos por CPF

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:bib="http://biblioteca.fiap.com.br/">
    <soapenv:Header/>
    <soapenv:Body>
        <bib:listarEmprestimosPorCpf>
            <cpfUsuario>12345678900</cpfUsuario>
        </bib:listarEmprestimosPorCpf>
    </soapenv:Body>
</soapenv:Envelope>
```

### ISBNs disponíveis no catálogo inicial

| ISBN | Título | Autor |
|---|---|---|
| 978-85-7522-743-0 | Clean Code | Robert C. Martin |
| 978-85-7522-554-2 | Domain-Driven Design | Eric Evans |
| 978-85-508-0085-2 | O Programador Pragmático | Andrew Hunt |
| 978-65-5452-002-1 | Arquitetura Limpa | Robert C. Martin |
| 978-85-7522-619-8 | Padrões de Projeto | Gang of Four |
| 978-85-7868-031-5 | Fundamentos de Banco de Dados | Ramez Elmasri |
| 978-85-7652-122-0 | Introdução a Algoritmos | Thomas H. Cormen |
| 978-85-7522-419-4 | Microsserviços Prontos para Produção | Susan Fowler |

---

##  Próximas Features <a name="proximas"></a>

### Curto Prazo
- [ ] **Persistência real** — substituir o repositório em memória por banco de dados (H2 ou PostgreSQL)
- [ ] **Autenticação no Header SOAP** — token de acesso no `<soapenv:Header>`
- [ ] **Multa por atraso** — calcular e registrar multas em empréstimos não devolvidos no prazo

### Médio Prazo
- [ ] **Interface Web** — frontend HTML/JS consumindo o SOAP via Fetch API
- [ ] **Notificação por e-mail** — alertas automáticos de prazo de devolução
- [ ] **Relatórios** — operações para gerar relatórios de livros mais emprestados

### Longo Prazo
- [ ] **Migração para REST/OpenAPI** — coexistência SOAP + REST para modernização gradual
- [ ] **Containerização** — Docker Compose para facilitar deploy
- [ ] **Deploy em nuvem** — publicação em ambiente AWS/Azure com HTTPS

---

##  Comparativo SOAP x REST (Aplicado ao Projeto)

| Critério | SOAP (este projeto) | REST (alternativa) |
|---|---|---|
| Protocolo | Sim — regras formais | Não — estilo arquitetural |
| Formato | XML obrigatório | JSON (geralmente) |
| Contrato | WSDL gerado automaticamente | OpenAPI/Swagger (manual) |
| Verbosidade | Alta | Baixa |
| Validação | Nativa pelo schema WSDL | Manual |
| Legibilidade do XML | Média | Alta (JSON) |
| Integração com sistemas legados |  Excelente |  Depende |

---

*Projeto desenvolvido para fins didáticos — FIAP 2026*
