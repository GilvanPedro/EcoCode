# EcoCode — Recicla+

> *Menos lixo e um mundo melhor.*

Sistema de gestão de descarte e reciclagem de **resíduos eletrônicos em Goiânia**. A pessoa
cadastra o que quer descartar, descobre qual ponto de coleta fica mais perto e acompanha o
pedido até a coleta. Do outro lado, a equipe atende os pedidos por uma fila, e o gerente
cuida dos cargos de quem usa o sistema.

O back-end é feito em Java puro. A interface web vem depois, com Spring Boot e Thymeleaf.

| | |
| --- | --- |
| **Equipe** | Gilvan Pedro, Sidney Emanuel, Guilherme Scarcela, João Paulo e Dários Suess |
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
9. [backend_ecocode.Estrutura do projeto](#estrutura-do-projeto)
10. [Arquitetura](#arquitetura)
11. [Documentação](#documentação)

## Equipe

| Integrante |
| --- |
| Gilvan Pedro |
| Sidney Emanuel |
| Guilherme Scarcela |
| João Paulo |
| Dários Suess |

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
| 23/11/2026 (seg) | Planejamento do Projeto Integrador (Dijkstra, AVL, solicitações, log, cargos, Java puro antes do front); grafo de bairros, correção de bugs, `Instanciador`, cadastro aberto, edição opcional, UUID | [048](docs/ADRs/ADR-048.md) a [061](docs/ADRs/ADR-061.md) |
| 24/11/2026 (ter) | Fila encadeada, `Solicitacao` e o service de solicitações, fim do modo offline, troca de cifra proposta, enum `Bairro`, `Grafo<T>` | [062](docs/ADRs/ADR-062.md) a [068](docs/ADRs/ADR-068.md) |
| 25/11/2026 (qua) | Arestas do grafo medidas no Google Maps, status do resíduo, ecopontos da Comurg (19 bairros e 40 arestas), pacotes do `Domain`, bairro obrigatório, classe `Dijkstra<T>` | [069](docs/ADRs/ADR-069.md) a [075](docs/ADRs/ADR-075.md) |
| 26/11/2026 (qui) | Pontos de coleta só do gerente, ponto mais próximo, `ArvoreAVL` nos resíduos, `PilhaEncadeada`, log de auditoria, `LocalDateTime` no JSON | [076](docs/ADRs/ADR-076.md) a [081](docs/ADRs/ADR-081.md) |
| 27/11/2026 (sex) | Dono do resíduo, usuário logado nos métodos com permissão, Cifra Afim de 62 caracteres na senha | [082](docs/ADRs/ADR-082.md) a [084](docs/ADRs/ADR-084.md) |

## Situação atual

O back-end em Java puro está completo e testado pela classe `backend_ecocode.Teste`
(199 testes, 198 passando; detalhes em [`docs/Testes.md`](docs/Testes.md)). Falta a interface
web, o banco de dados e a sessão de 30 dias.

### Pronto

**Usuários**

- Cadastro aberto: qualquer pessoa cria a própria conta, sempre com o cargo `USUARIO`
- Cadastro pelo gerente, já com o cargo escolhido
- Bairro obrigatório no cadastro, escolhido entre os 19 do grafo ([ADR-074](docs/ADRs/ADR-074.md))
- Login por e-mail e senha; sessão lembrada entre execuções ([ADR-047](docs/ADRs/ADR-047.md))
- Edição com campos opcionais: o que não for informado continua como está
- Promoção de cargo pelo CPF: só o gerente promove, e ninguém troca o próprio cargo
- Remoção pelo gerente, que não pode remover a si mesmo
- CPF e e-mail únicos, conferidos no service; formato validado na entidade
- Senha cifrada com a Cifra Afim de 62 caracteres ([ADR-084](docs/ADRs/ADR-084.md))

**Resíduos**

- Cadastro com tipo, quantidade e descrição opcional, código gerado automaticamente
- Cada resíduo tem dono: só o dono edita, exclui ou pede o descarte, e só enquanto está
  disponível ([ADR-082](docs/ADRs/ADR-082.md))
- Status próprio (`DISPONIVEL`, `EM_SOLICITACAO`, `DESCARTADO`) ligado à solicitação
  ([ADR-071](docs/ADRs/ADR-071.md))
- Guardados numa `ArvoreAVL` com o código como chave; a listagem sai pela ordem do código
  ([ADR-078](docs/ADRs/ADR-078.md))
- Na tela, o gerente vê todos os resíduos e os outros cargos veem só os seus

**Solicitações de descarte**

- Status Pendente, Em rota, Concluída e Cancelada, conferidos na própria `Solicitacao`
- Fila das pendentes (`FilaEncadeada`), posição na fila, pegar a próxima e concluir (funcionário
  e gerente)
- O usuário cancela só enquanto está Pendente; o gerente cancela também quando está Em rota

**Pontos de coleta e ponto mais próximo**

- Cadastrar, editar (parcial) e excluir só pelo gerente ([ADR-076](docs/ADRs/ADR-076.md))
- Ponto mais próximo: o Dijkstra roda uma vez a partir do bairro do usuário
  ([ADR-075](docs/ADRs/ADR-075.md), [ADR-077](docs/ADRs/ADR-077.md))

**Log de auditoria** ([ADR-080](docs/ADRs/ADR-080.md))

- 10 ações no enum `AcaoLog`, `RegistroLog` imutável, `PilhaEncadeada` com o mais novo no topo
- Só o gerente consulta

**Estruturas próprias** (pacote `backend_ecocode.Estrutura`)

- `No<T>`, `FilaEncadeada<T>`, `PilhaEncadeada<T>`, `Grafo<T>`, `Dijkstra<T>`, `NoAVL<K, V>` e
  `ArvoreAVL<K, V>`

**Grafo dos bairros** ([documento completo](docs/GrafoBairros.md))

- Enum `Bairro` com 19 bairros: os 15 âncora de Goiânia ([ADR-067](docs/ADRs/ADR-067.md)) e os 4
  bairros dos ecopontos da Comurg que faltavam ([ADR-072](docs/ADRs/ADR-072.md))
- `GrafoBairros` monta o grafo com 40 arestas, todas com a distância de carro do Google Maps
  ([ADR-070](docs/ADRs/ADR-070.md))

### Pendências conhecidas

- A fila perde a ordem de chegada quando o sistema reinicia (ver [`docs/Testes.md`](docs/Testes.md))
- A `Configuracao` ainda lê as chaves da Afim com `mod 26`, e não `mod 62` ([ADR-084](docs/ADRs/ADR-084.md))
- A edição de usuário não grava `EDITOU_USUARIO` no log, e a promoção não guarda o cargo anterior (RN10)

### Planejado

- Banco de dados no lugar dos arquivos JSON, sem modo offline ([ADR-065](docs/ADRs/ADR-065.md))
- Sessão com validade de 30 dias ([ADR-054](docs/ADRs/ADR-054.md))
- Interface web com Spring Boot e Thymeleaf ([ADR-055](docs/ADRs/ADR-055.md))

## Categorias e código do resíduo

O código tem 6 dígitos (com zeros à esquerda) e é gerado de forma que **`código mod 5`** seja igual ao número da
categoria:

| Resto | Categoria | Exemplos de código |
| --- | --- | --- |
| 0 | Computadores e Notebooks | 000005, 000010, 000015 |
| 1 | Celulares e Tablets | 000001, 000006, 000011 |
| 2 | Cabos e Periféricos | 000002, 000007, 000012 |
| 3 | Pilhas e Baterias | 000003, 000008, 000013 |
| 4 | Outros Eletrônicos | 000004, 000009, 000014 |

Só o último código gerado (de qualquer tipo) fica salvo, em `arquivos/codigo.json`. O próximo
código é o menor número depois dele cujo resto da divisão por 5 é o resto do tipo:

```text
candidato = último + 1
distância = (resto do tipo − candidato) mod 5
próximo   = candidato + distância
```

Exemplo, começando sem arquivo: Celular → 000001, Computador → 000005, Computador → 000010,
Bateria → 000013, Outros → 000014, Cabos → 000017. Assim o código também indica a ordem de cadastro,
e a sequência continua entre execuções. Detalhes no [ADR-032](docs/ADRs/ADR-032.md).

Desde o [ADR-061](docs/ADRs/ADR-061.md), o código é um campo único do resíduo, e não mais a
chave do resíduo: o identificador é um `UUID`. A busca pelo usuário continua sendo pelo código,
que também é a chave da `ArvoreAVL` ([ADR-078](docs/ADRs/ADR-078.md)).

## Cifras

As duas cifras são **didáticas**: mostram a aritmética modular estudada na disciplina, mas não
servem para proteger senhas de verdade. Um sistema real usaria um hash de senha, como o BCrypt
([ADR-084](docs/ADRs/ADR-084.md)).

**Cifra Afim** (`Util/CifraAfim`), usada na senha desde o [ADR-084](docs/ADRs/ADR-084.md):

- Alfabeto único de 62 caracteres: A-Z (0 a 25), a-z (26 a 51) e 0-9 (52 a 61)
- Cifrar: `E(x) = (a · x + b) mod 62`
- Decifrar: `D(y) = a⁻¹ · (y − b) mod 62`, com o inverso calculado pelo Euclides Estendido
- `a` precisa ter `mdc(a, 62) = 1`, ou seja, ser ímpar e diferente de 31. Exemplo: `a = 7`,
  `b = 3`, inverso 9
- φ(62) = 30 valores de `a` × 62 valores de `b` = 1.860 chaves
- Espaços, pontuação e acentos não mudam

**Cifra de César** (`Util/CifraCesar`), mantida por causa da Missão 03, não é mais usada na senha:

- Maiúsculas: `E(x) = (x + k) mod 26`; minúsculas no sentido contrário; dígitos `mod 10`
- Tem decifração e força bruta com todas as chaves

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
CIFRA_AFIM_A=7
CIFRA_AFIM_B=3
```

O `.env` não vai para o Git. Sem ele, o programa para avisando qual variável está faltando.

> As chaves da Afim precisam ser as mesmas usadas quando as senhas de `arquivos/usuarios.json`
> foram cifradas; com outras chaves, nenhum login antigo funciona.

### 2. Compilar e rodar

Pela IDE, rode a classe `backend_ecocode.Main`.

Pelo terminal, a partir da raiz do projeto:

```bash
mvn compile
mvn dependency:copy-dependencies
java -cp "target/classes:target/dependency/*" backend_ecocode.Main
```

No Windows, troque `:` por `;` no classpath: `"target/classes;target/dependency/*"`.

### 3. Rodar os testes

```bash
java -cp "target/classes:target/dependency/*" backend_ecocode.Teste
```

O `Teste` grava nos arquivos de `arquivos/`. Antes de rodar, faça uma cópia da pasta se quiser
manter os dados como estavam. O resultado esperado e a lista de testes estão em
[`docs/Testes.md`](docs/Testes.md).

Rode sempre a partir da raiz do projeto: os arquivos de dados são lidos pelo caminho relativo
`arquivos/`. A pasta não vai para o Git e é criada na primeira execução, se não existir.

## backend_ecocode.Estrutura do projeto

```text
.
├── arquivos/                      dados salvos em JSON (fora do Git)
│   ├── rediduos.json              resíduos cadastrados
│   ├── usuarios.json              usuários (senha cifrada)
│   ├── codigo.json                último código gerado
│   ├── solicitacoes.json          solicitações de descarte
│   ├── pontos_coletas.json        pontos de coleta
│   ├── log.json                   log de auditoria
│   └── sessao.json                quem ficou logado e até quando
├── docs/
│   ├── ADRs/                      decisões de arquitetura (ADR-001 a ADR-084)
│   ├── Requisitos.md              requisitos funcionais, não funcionais e regras de negócio
│   ├── Testes.md                  testes do fluxo principal, resultados e bugs encontrados
│   └── GrafoBairros.md            os 19 bairros, as 40 arestas e de onde vêm as distâncias
├── src/main/java/backend_ecocode.Estrutura/       estruturas próprias (No, FilaEncadeada, PilhaEncadeada, Grafo,
│                                  Dijkstra, NoAVL, ArvoreAVL)
├── src/main/java/backend_ecocode/
│   ├── Main.java                  menu do terminal
│   ├── Teste.java                 testes gerais do back-end
│   ├── Instanciador.java          cria e liga repositories e services
│   ├── ServidorApplication.java   ponto de entrada do Spring (para a etapa web)
│   ├── TelaTerminal/              leitura do teclado e exibição
│   ├── Service/                   regras de negócio (usuários, resíduos, solicitações, pontos, log)
│   ├── Repository/                acesso aos dados (Map, AVL ou pilha em memória + arquivo)
│   ├── Domain/Entities/           Usuario, Residuo, Solicitacao, PontoColeta, RegistroLog
│   ├── Domain/Enums/              CargoUsuario, TipoResiduo, StatusResiduo, StatusSolicitacao, Bairro, AcaoLog
│   ├── Domain/Interfaces/         interfaces dos repositories e services
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
                     backend_ecocode.Estrutura (fila, pilha, grafo, Dijkstra e AVL)
```

- **TelaTerminal** só conversa com o usuário: lê o teclado e mostra resultados.
- **Service** aplica as regras: gerar código, checar permissão de cargo, conferir CPF e e-mail
  repetidos, cifrar senha, organizar a fila de solicitações.
- **Repository** guarda os dados em memória (um `Map`, a `ArvoreAVL` nos resíduos ou a
  `PilhaEncadeada` no log) e grava no arquivo a cada alteração.
- **Domain** define as entidades. Cada uma valida os próprios dados, e a `Solicitacao` também
  valida as próprias mudanças de status.
- **backend_ecocode.Estrutura** guarda as estruturas de dados feitas pelo grupo, sem usar as coleções prontas
  do Java.
- A classe **`Instanciador`** cria uma única instância de cada repository e de cada service e
  entrega cada dependência pelo construtor. Assim todo o sistema usa os mesmos dados em
  memória ([ADR-058](docs/ADRs/ADR-058.md)). Quando o Spring entrar, ele assume esse papel.
- Erros são sinalizados com exceções (`IllegalArgumentException`, `AcessoNegadoException`) e
  tratados na tela.

### Cargos

```text
Usuário  <  Funcionário  <  Gerente
```

| Cargo | O que pode fazer além do anterior |
| --- | --- |
| Usuário | Cadastrar e cuidar dos próprios resíduos, pedir descarte, ver o ponto mais próximo |
| Funcionário | Pegar a próxima solicitação da fila e concluir a coleta |
| Gerente | Cadastrar usuários com cargo, promover, remover, gerenciar pontos de coleta, ver todos os resíduos, cancelar qualquer solicitação e consultar o log |

Só o gerente troca cargos, ninguém troca o próprio, e o primeiro gerente é criado direto no
banco ([ADR-053](docs/ADRs/ADR-053.md)). Os métodos com permissão recebem o usuário logado
([ADR-083](docs/ADRs/ADR-083.md)).

## Documentação

- **Grafo dos bairros:** [`docs/GrafoBairros.md`](docs/GrafoBairros.md), com os 19 bairros,
  as 40 arestas medidas no Google Maps e uma tabela de
  distâncias esperadas para testar o Dijkstra.
- **Requisitos:** [`docs/Requisitos.md`](docs/Requisitos.md), com os requisitos funcionais
  (RF01 a RF10), os não funcionais (RNF01 a RNF05) e as regras de negócio (RN01 a RN18).
- **Testes:** [`docs/Testes.md`](docs/Testes.md), com os testes do fluxo principal, o critério de
  aceitação e o resultado de cada um, e os bugs encontrados.
- **Decisões de arquitetura:** [`docs/ADRs/`](docs/ADRs/), no formato ADR. Lá está o histórico
  inteiro, da versão 1.0 de 06/10/2026 até as decisões do Projeto Integrador. Quando uma
  decisão muda, o ADR antigo não é apagado: ele é marcado como "Substituída por ADR-XXX".
- Para registrar uma nova decisão, use o [modelo de ADR](docs/ADRs/ADR-template.md). O próximo
  número livre é o **ADR-085**.
