# EcoCode — Recicla+

> *Menos lixo e um mundo melhor.*

Sistema de gestão de descarte e reciclagem de **resíduos eletrônicos em Goiânia**. A pessoa
cadastra o que quer descartar, descobre qual ponto de coleta fica mais perto e acompanha o
pedido até a coleta. Do outro lado, a equipe atende os pedidos por uma fila, e o gerente
cuida dos cargos de quem usa o sistema.

O back-end é feito em Java puro. A interface web vem depois, com Spring Boot e Thymeleaf.

| | |
| --- | --- |
| **Equipe** | Gilvan Pedro, Sidney Emanuel, Guilherme Scarcela, João Paulo e Dários |
| **Início do projeto** | 06/10/2026, na Missão 02 de outra disciplina |
| **Início do Projeto Integrador** | 23/11/2026 |
| **Entrega** | 05/12/2026 |
| **Apresentação** | 07/12/2026 |

## Sumário

1. [Equipe](#equipe)
2. [Como o projeto começou](#como-o-projeto-começou)
3. [Linha do tempo](#linha-do-tempo)
4. [Situação atual](#situação-atual)
5. [Categorias e código do resíduo](#categorias-e-código-do-resíduo)
6. [Cifras](#cifras)
7. [Tecnologias](#tecnologias)
8. [Como executar](#como-executar)
9. [Estrutura do projeto](#estrutura-do-projeto)
10. [Arquitetura](#arquitetura)
11. [Documentação](#documentação)

## Equipe

| Integrante |
| --- |
| Gilvan Pedro |
| Sidney Emanuel |
| Guilherme Scarcela |
| João Paulo |
| Dários |

## Como o projeto começou

O EcoCode nasceu em **06/10/2026**, na aula de outra professora, como resposta à Missão 02
(Auditoria Lógica dos Requisitos). A tarefa era montar um pequeno módulo para cadastrar
resíduos eletrônicos e classificá-los usando aritmética modular. A primeira versão era um
programa de terminal com tudo numa classe só e os dados numa lista em memória
([ADR-001](docs/ADRs/ADR-001.md)).

A ideia era que esse módulo pudesse virar parte de um aplicativo maior. Foi o que aconteceu:
em **23/11/2026** ele virou a base do Projeto Integrador, que acrescenta usuários com cargos,
solicitações de descarte, fila de coletas, pontos de coleta e a busca pelo ponto mais
próximo.

## Linha do tempo

| Data | O que mudou | ADRs |
| --- | --- | --- |
| 06/10/2026 | Versão 1.0: terminal, uma classe, lista em memória, código que indica a categoria | [001](docs/ADRs/ADR-001.md) a [009](docs/ADRs/ADR-009.md) |
| 22/10/2026 | Refatoração em camadas (tela, service, repository, domínio), validações, editar e excluir | [010](docs/ADRs/ADR-010.md) a [018](docs/ADRs/ADR-018.md) |
| 23 a 26/10/2026 | Cifra de César, módulo de usuários, persistência em JSON, Cifra Afim e Euclides Estendido | [019](docs/ADRs/ADR-019.md) a [037](docs/ADRs/ADR-037.md) |
| 27 e 28/10/2026 | Spring Boot no projeto, pacote `backend_ecocode`, login por e-mail, menu do gerente | [038](docs/ADRs/ADR-038.md) a [046](docs/ADRs/ADR-046.md) |
| 05/11/2026 | Sessão salva em arquivo ("lembrar login") | [047](docs/ADRs/ADR-047.md) |
| 23/11/2026 | Planejamento do Projeto Integrador: Dijkstra, AVL, solicitações, log, cargos, Java puro antes do front | [048](docs/ADRs/ADR-048.md) a [055](docs/ADRs/ADR-055.md) |
| 24/11/2026 | Grafo de bairros, correção de bugs, `Instanciador`, cadastro aberto, UUID, fila encadeada, solicitações, fim do modo offline, enum `Bairro`, `Grafo<T>` e as 30 arestas | [056](docs/ADRs/ADR-056.md) a [070](docs/ADRs/ADR-070.md) |

## Situação atual

### Pronto

**Resíduos** (menu do terminal)

- Cadastrar um resíduo (tipo, quantidade e descrição opcional), com código gerado automaticamente
- Listar todos os resíduos, por ordem de cadastro ou de código
- Ver a quantidade de cadastros
- Listar os resíduos de uma categoria, com o total de unidades
- Editar a quantidade e a descrição de um resíduo
- Excluir um resíduo pelo código, com confirmação

**Login** (terminal)

- Entrada por e-mail e senha, com a mesma mensagem de erro para e-mail inexistente e senha errada
- Sessão lembrada entre execuções, sem guardar a senha ([ADR-047](docs/ADRs/ADR-047.md))

**Usuários** (camada de service, sem tela nova ainda)

- Cadastro aberto: qualquer pessoa cria a própria conta, sempre com o cargo `USUARIO`
- Cadastro pelo gerente, já com o cargo escolhido
- Promoção de cargo: o gerente troca o cargo de alguém pelo CPF, e ninguém troca o próprio
- Edição com campos opcionais: o que não for informado continua como está
- CPF e e-mail únicos, conferidos no service; formato validado na entidade
- Cada usuário identificado por um `UUID`

**Solicitações de descarte** (domínio e repository, sem tela)

- Entidade `Solicitacao` com os status Pendente, Em rota, Concluída e Cancelada
- As mudanças de status são conferidas na própria classe: não dá para pular etapa
- O usuário cancela só enquanto está Pendente; o gerente cancela também quando já está Em rota
- Repository por `UUID`, salvando em JSON

**Estruturas próprias**

- `FilaEncadeada<T>`, uma fila genérica feita com nós, no pacote `Estrutura`
- `Grafo<T>`, um grafo genérico não direcionado com lista de adjacência, que recusa peso
  negativo e laço ([ADR-068](docs/ADRs/ADR-068.md))

**Grafo dos bairros** ([documento completo](docs/GrafoBairros.md))

- Enum `Bairro` com os 15 bairros âncora de Goiânia ([ADR-067](docs/ADRs/ADR-067.md))
- `GrafoBairros` monta o grafo com 30 arestas. Todas as distâncias passam a ser a distância
  de carro do Google Maps, com um critério único ([ADR-070](docs/ADRs/ADR-070.md)); 15 já
  foram medidas e 15 ainda usam a estimativa antiga até serem medidas

### Em desenvolvimento

- `GerenciarSolicitacoesService`: fila das pendentes, `Map` das que estão em rota,
  remontagem ao iniciar e posição na fila

### Planejado

- Pontos de coleta ligados a um dos 15 bairros do grafo ([ADR-056](docs/ADRs/ADR-056.md))
- Ponto de coleta mais próximo pelo algoritmo de Dijkstra sobre o grafo dos bairros
  ([ADR-048](docs/ADRs/ADR-048.md))
- Árvore AVL em memória como índice das buscas ([ADR-049](docs/ADRs/ADR-049.md))
- Log de auditoria imutável, consultado como pilha ([ADR-052](docs/ADRs/ADR-052.md))
- Banco de dados no lugar dos arquivos JSON, sem modo offline ([ADR-065](docs/ADRs/ADR-065.md))
- Sessão com validade de 30 dias ([ADR-054](docs/ADRs/ADR-054.md))
- Senha cifrada com a Cifra Afim no lugar da César ([ADR-066](docs/ADRs/ADR-066.md))
- Interface web com Spring Boot e Thymeleaf ([ADR-055](docs/ADRs/ADR-055.md))

## Categorias e código do resíduo

O código tem 4 dígitos e é gerado de forma que **`código mod 5`** seja igual ao número da
categoria:

| Resto | Categoria | Exemplos de código |
| --- | --- | --- |
| 0 | Computadores e Notebooks | 0005, 0010, 0015 |
| 1 | Celulares e Tablets | 0001, 0006, 0011 |
| 2 | Cabos e Periféricos | 0002, 0007, 0012 |
| 3 | Pilhas e Baterias | 0003, 0008, 0013 |
| 4 | Outros Eletrônicos | 0004, 0009, 0014 |

Só o último código gerado (de qualquer tipo) fica salvo, em `arquivos/codigo.json`. O próximo
código é o menor número depois dele cujo resto da divisão por 5 é o resto do tipo:

```text
candidato = último + 1
distância = (resto do tipo − candidato) mod 5
próximo   = candidato + distância
```

Exemplo, começando sem arquivo: Celular → 0001, Computador → 0005, Computador → 0010,
Bateria → 0013, Outros → 0014, Cabos → 0017. Assim o código também indica a ordem de cadastro,
e a sequência continua entre execuções. Detalhes no [ADR-032](docs/ADRs/ADR-032.md).

Desde o [ADR-061](docs/ADRs/ADR-061.md), o código é um campo único do resíduo, e não mais a
chave: a chave é um `UUID`. A busca pelo usuário continua sendo pelo código.

## Cifras

As duas cifras são **didáticas**: mostram a aritmética modular estudada na disciplina, mas não
servem para proteger senhas de verdade. Um sistema real usaria um hash de senha, como o BCrypt
([ADR-066](docs/ADRs/ADR-066.md)).

**Cifra de César** (`Util/CifraCesar`), usada hoje na senha:

- Maiúsculas: `E(x) = (x + k) mod 26`
- Minúsculas: `E(x) = (x − k) mod 26` (andam no sentido contrário)
- Dígitos: `E(x) = (x + k) mod 10`
- Espaços, pontuação e acentos não mudam
- Tem decifração (`descriptografarCifra`) e força bruta com todas as chaves
  (`descriptografarTodasChaves`)

**Cifra Afim** (`Util/CifraAfim`):

- Cifrar: `E(x) = (a · x + b) mod 26`
- Decifrar: `D(y) = a⁻¹ · (y − b) mod 26`, com o inverso calculado pelo Euclides Estendido
- `a` precisa ser ímpar e diferente de 13 (não pode ter divisor comum com 26)
- Só letras são cifradas

## Tecnologias

- Java 25
- Maven
- [Gson](https://github.com/google/gson) 2.12.1: leitura e escrita dos arquivos JSON
- [dotenv-java](https://github.com/cdimascio/dotenv-java) 3.0.0: leitura das chaves das cifras
- Spring Boot 4.0.6: já está no `pom.xml`, mas só vai ser usado na etapa da interface web

## Como executar

### Pré-requisitos

- JDK 25
- Maven 3.9+ (ou uma IDE como IntelliJ IDEA)

### 1. Configurar as chaves das cifras

Copie o arquivo de exemplo e preencha as chaves:

```bash
cp .env.example .env
```

```env
CIFRA_CESAR_CHAVE=3
CIFRA_AFIM_A=5
CIFRA_AFIM_B=8
```

O `.env` não vai para o Git. Sem ele, o programa para avisando qual variável está faltando.

> A chave da César precisa ser a mesma usada quando as senhas de `arquivos/usuarios.json`
> foram cifradas; com outra chave, nenhum login antigo funciona.

### 2. Compilar e rodar

Pela IDE, rode a classe `backend_ecocode.Main`.

Pelo terminal, a partir da raiz do projeto:

```bash
mvn compile
mvn dependency:copy-dependencies
java -cp "target/classes:target/dependency/*" backend_ecocode.Main
```

No Windows, troque `:` por `;` no classpath: `"target/classes;target/dependency/*"`.

Rode sempre a partir da raiz do projeto: os arquivos de dados são lidos pelo caminho relativo
`arquivos/`. A pasta não vai para o Git e é criada na primeira execução, se não existir.

## Estrutura do projeto

```text
.
├── arquivos/                      dados salvos em JSON (fora do Git)
│   ├── rediduos.json              resíduos cadastrados
│   ├── usuarios.json              usuários (senha cifrada)
│   ├── codigo.json                último código gerado
│   └── sessao.json                quem ficou logado e até quando
├── docs/
│   ├── ADRs/                      decisões de arquitetura (ADR-001 a ADR-070)
│   ├── Requisitos.md              requisitos funcionais, não funcionais e regras de negócio
│   └── GrafoBairros.md            os 15 bairros, as 30 arestas e de onde vêm as distâncias
├── src/main/java/backend_ecocode/
│   ├── Main.java                  menu do terminal
│   ├── ServidorApplication.java   ponto de entrada do Spring (para a etapa web)
│   ├── TelaTerminal/              leitura do teclado e exibição
│   ├── Service/                   regras de negócio (resíduos, usuários, solicitações)
│   ├── Repository/                acesso aos dados (Map em memória + arquivo)
│   ├── Domain/                    Residuo, Usuario, Solicitacao, Bairro, enums e interfaces
│   ├── Estrutura/                 estruturas próprias (No, FilaEncadeada, Grafo)
│   ├── Persistency/               gravação e leitura dos arquivos JSON
│   ├── Exceptions/                AcessoNegadoException
│   ├── Controller/                controllers do Spring (etapa web)
│   └── Util/                      cifras, geração de código, GrafoBairros, sessão, configuração e validações
├── .env.example                   modelo da configuração
└── pom.xml
```

## Arquitetura

O projeto é dividido em camadas:

```text
TelaTerminal  →  Service  →  Repository  →  Persistência (JSON, depois banco)
                    ↘            ↘
                     Domain (entidades, enums e interfaces)
                     Estrutura (fila e grafo, e depois AVL e pilha)
```

- **TelaTerminal** só conversa com o usuário: lê o teclado e mostra resultados.
- **Service** aplica as regras: gerar código, checar permissão de cargo, conferir CPF e e-mail
  repetidos, cifrar senha, organizar a fila de solicitações.
- **Repository** guarda os dados num `Map` em memória e grava no arquivo a cada alteração.
- **Domain** define as entidades. Cada uma valida os próprios dados, e a `Solicitacao` também
  valida as próprias mudanças de status.
- **Estrutura** guarda as estruturas de dados feitas pelo grupo, sem usar as coleções prontas
  do Java.
- A classe **`Instanciador`** cria uma única instância de cada repository e de cada service e
  entrega cada dependência pelo construtor. Assim todo o sistema usa os mesmos dados em
  memória ([ADR-058](docs/ADRs/ADR-058.md)). Quando o Spring entrar, ele assume esse papel.
- Erros são sinalizados com exceções (`IllegalArgumentException`, `AcessoNegadoException`) e
  tratados na tela.

### Cargos

```text
Usuário  <  Coletor  <  Administrador  <  Gerente
```

A lista final ainda está sendo fechada pelo grupo. Só o gerente troca cargos, ninguém troca o
próprio, e o primeiro gerente é criado direto no banco ([ADR-053](docs/ADRs/ADR-053.md)).

## Documentação

- **Grafo dos bairros:** [`docs/GrafoBairros.md`](docs/GrafoBairros.md), com os 15 bairros,
  as 30 arestas marcadas como medidas ou estimadas, o método de estimativa e uma tabela de
  distâncias esperadas para testar o Dijkstra.
- **Requisitos:** [`docs/Requisitos.md`](docs/Requisitos.md), com os requisitos funcionais
  (RF01 a RF10), os não funcionais (RNF01 a RNF05) e as regras de negócio (RN01 a RN12).
- **Decisões de arquitetura:** [`docs/ADRs/`](docs/ADRs/), no formato ADR. Lá está o histórico
  inteiro, da versão 1.0 de 06/10/2026 até as decisões do Projeto Integrador. Quando uma
  decisão muda, o ADR antigo não é apagado: ele é marcado como "Substituída por ADR-XXX".
- Para registrar uma nova decisão, use o [modelo de ADR](docs/ADRs/ADR-template.md). O próximo
  número livre é o **ADR-071**.
