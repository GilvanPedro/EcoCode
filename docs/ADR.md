# Índice geral das ADRs do EcoCode

Este arquivo junta todas as decisões de arquitetura do EcoCode num lugar só. Cada linha leva ao
ADR completo, na pasta [`ADRs/`](ADRs/).

Um ADR (Architecture Decision Record) registra **uma** decisão: o problema, o que foi decidido,
o que mais foi considerado e o que muda com isso. Funciona como a ata de uma reunião do grupo,
só que para o código: daqui a um mês, quem abrir o projeto entende não só o que foi feito, mas
por que foi feito assim.

| | |
| --- | --- |
| **Total** | 94 ADRs (ADR-001 a ADR-094) |
| **Aceitas** | 75 |
| **Substituídas** | 19 |
| **Próximo número livre** | ADR-095 |
| **Modelo** | [`ADRs/ADR-template.md`](ADRs/ADR-template.md) |

## Sumário

1. [Como ler e escrever um ADR](#como-ler-e-escrever-um-adr)
2. [Linha do tempo](#linha-do-tempo)
3. [ADRs por tema](#adrs-por-tema)
4. [Decisões que mudaram](#decisões-que-mudaram)
5. [Pendências registradas nos ADRs](#pendências-registradas-nos-adrs)

## Como ler e escrever um ADR

Todo ADR segue o mesmo formato: **Contexto** (o problema), **Decisão** (uma frase começando com
"Vamos..."), **Alternativas consideradas** (com prós, contras e por que não), **Consequências**
(positivas, negativas e neutras), **Riscos e pendências** e **Referências**.

As regras do grupo:

- Um ADR registra uma decisão só. Se surgirem duas, são dois ADRs.
- A numeração é sequencial e nunca é reaproveitada.
- Um ADR aceito não é reescrito. Se a decisão mudar, nasce um ADR novo, e o antigo passa a
  "Substituída por ADR-XXX". Quando só uma parte muda, o novo ADR diz "Altera o ADR-XXX" nas
  Referências, e o antigo continua "Aceita".
- O título fica no imperativo ("Usar...", "Guardar...", "Mover...").
- Autores: "Grupo do projeto (Gilvan Pedro, Sidney Emanuel, Guilherme Scarcela, João Paulo e
  Dários Suess)".

Status possíveis: **Proposta** (ainda em discussão), **Aceita**, **Rejeitada**,
**Substituída por ADR-XXX** e **Obsoleta**.

Os ADRs 001 a 047 vieram de outra disciplina, antes do Projeto Integrador, e mantêm as datas
originais. Do 048 em diante são as decisões do PI.

## Linha do tempo

### Versão 1.0 (06/10/2026)

A primeira versão, feita na Missão 02 de outra disciplina: programa de terminal, tudo na classe `Cadastro`, dados numa lista em memória. Estes ADRs foram **reconstruídos a partir do código** depois: o que o código faz foi conferido rodando o programa, e os motivos foram deduzidos do código e do enunciado. Os nomes de classe são os da versão 1.0.

| ADR | Decisão | Status |
| --- | --- | --- |
| [001](ADRs/ADR-001.md) | Construir o módulo como aplicação de terminal em Java com Maven | Aceita |
| [002](ADRs/ADR-002.md) | Concentrar tela, dados e geração de código na classe Cadastro | Substituída por [010](ADRs/ADR-010.md) |
| [003](ADRs/ADR-003.md) | Guardar os resíduos numa ArrayList em memória | Substituída por [011](ADRs/ADR-011.md) |
| [004](ADRs/ADR-004.md) | Gerar o código de forma que código mod 5 caia na categoria do tipo | Aceita |
| [005](ADRs/ADR-005.md) | Representar o código como String de 6 dígitos com zeros à esquerda | Substituída por [013](ADRs/ADR-013.md) |
| [006](ADRs/ADR-006.md) | Receber o tipo do resíduo como texto livre, reconhecido por sinônimos | Substituída por [012](ADRs/ADR-012.md) |
| [007](ADRs/ADR-007.md) | Classificar com uma classe utilitária estática | Substituída por [015](ADRs/ADR-015.md) |
| [008](ADRs/ADR-008.md) | Carregar 12 registros de exemplo no construtor do Cadastro | Substituída por [018](ADRs/ADR-018.md) |

### Refatoração em camadas (22 e 23/10/2026)

O módulo foi separado em tela, service, repository e domínio, com validações, edição e exclusão.

| ADR | Decisão | Status |
| --- | --- | --- |
| [009](ADRs/ADR-009.md) | Adicionar uma descrição opcional aos resíduos | Aceita |
| [010](ADRs/ADR-010.md) | Separar o módulo em camadas (tela, service, repository e domínio) | Aceita |
| [011](ADRs/ADR-011.md) | Guardar os resíduos num TreeMap com o código como chave | Substituída por [061](ADRs/ADR-061.md) |
| [012](ADRs/ADR-012.md) | Representar o tipo do resíduo com o enum TipoResiduo | Aceita |
| [013](ADRs/ADR-013.md) | Gerar o código com 4 dígitos numa classe própria e normalizar o código digitado | Aceita, alterada por [025](ADRs/ADR-025.md), [032](ADRs/ADR-032.md) e [089](ADRs/ADR-089.md) |
| [014](ADRs/ADR-014.md) | Validar no objeto e no service, sinalizando erros com exceções | Aceita, alterada por [030](ADRs/ADR-030.md) |
| [015](ADRs/ADR-015.md) | Calcular a classe pelo código no service, no lugar do Classificador estático | Aceita |
| [016](ADRs/ADR-016.md) | Editar e excluir por código, com confirmação e sem alterar tipo nem código | Aceita |
| [017](ADRs/ADR-017.md) | Filtrar a listagem por categoria no service | Aceita |
| [018](ADRs/ADR-018.md) | Cadastrar os registros iniciais pelo service | Aceita |

### Cifras, usuários e arquivos JSON (23 a 26/10/2026)

Cifra de César e depois Afim, módulo de usuários com cargos, dados salvos em JSON e a classe `Aritmetica`.

| ADR | Decisão | Status |
| --- | --- | --- |
| [019](ADRs/ADR-019.md) | Implementar a Cifra de César sobre a correspondência A = 0 ... Z = 25 | Aceita, alterada por [021](ADRs/ADR-021.md) e [031](ADRs/ADR-031.md) |
| [020](ADRs/ADR-020.md) | Ler a chave da cifra de um arquivo .env com dotenv-java | Aceita, alterada por [033](ADRs/ADR-033.md) e [086](ADRs/ADR-086.md) |
| [021](ADRs/ADR-021.md) | Preservar maiúsculas e minúsculas na cifra, deslocando cada caixa num sentido | Aceita, alterada por [031](ADRs/ADR-031.md) |
| [022](ADRs/ADR-022.md) | Criar o módulo de usuários com CPF como chave e cadastro restrito a gerentes | Substituída por [059](ADRs/ADR-059.md) e [061](ADRs/ADR-061.md) |
| [023](ADRs/ADR-023.md) | Guardar a senha do usuário cifrada com a Cifra de César | Substituída por [066](ADRs/ADR-066.md) e [084](ADRs/ADR-084.md) |
| [024](ADRs/ADR-024.md) | Salvar os resíduos em arquivo JSON, com o Map em memória como fonte principal | Substituída por [085](ADRs/ADR-085.md) |
| [025](ADRs/ADR-025.md) | Gerar o código por sequência de cada tipo (último + 5), salvando o último em arquivo | Substituída por [032](ADRs/ADR-032.md) |
| [026](ADRs/ADR-026.md) | Salvar os usuários em arquivo JSON, reaproveitando a persistência dos resíduos | Substituída por [085](ADRs/ADR-085.md) |
| [027](ADRs/ADR-027.md) | Guardar e buscar o CPF só com dígitos | Aceita |
| [028](ADRs/ADR-028.md) | Reunir as validações do usuário na classe ValidacoesUsuario | Aceita |
| [029](ADRs/ADR-029.md) | Data de nascimento em AAAA-MM-DD no arquivo e em DD/MM/AAAA para o usuário | Aceita |
| [030](ADRs/ADR-030.md) | Reunir as validações do resíduo na classe ValidacoesResiduos | Aceita |
| [031](ADRs/ADR-031.md) | Cifrar também os dígitos e concentrar a conta da cifra num único método | Aceita |
| [032](ADRs/ADR-032.md) | Calcular o próximo código a partir do último gerado, de qualquer tipo | Substituída por [089](ADRs/ADR-089.md) |
| [033](ADRs/ADR-033.md) | Implementar a Cifra Afim com as chaves a e b no .env | Substituída por [084](ADRs/ADR-084.md) |
| [034](ADRs/ADR-034.md) | Reunir MDC, MMC e inverso modular na classe Aritmetica | Aceita |
| [035](ADRs/ADR-035.md) | Calcular o inverso da Cifra Afim pelo Euclides Estendido | Aceita |
| [036](ADRs/ADR-036.md) | Listar os resíduos por ordem de cadastro ou por ordem de código | Substituída por [078](ADRs/ADR-078.md) |
| [037](ADRs/ADR-037.md) | Calcular as quantidades de resíduos no service | Aceita |

### Spring Boot, login e menu do gerente (27 e 28/10/2026)

O Spring entrou no `pom.xml`, todo o código foi para o pacote `backend_ecocode`, e o login passou a ser por e-mail.

| ADR | Decisão | Status |
| --- | --- | --- |
| [038](ADRs/ADR-038.md) | Usar Spring Boot como servidor do back-end | Aceita |
| [039](ADRs/ADR-039.md) | Colocar todo o código Java dentro do pacote raiz backend_ecocode | Aceita, alterada por [073](ADRs/ADR-073.md) e [093](ADRs/ADR-093.md) |
| [040](ADRs/ADR-040.md) | Marcar services e repositórios como componentes do Spring e sincronizar os repositórios | Aceita |
| [041](ADRs/ADR-041.md) | Fazer o front-end em HTML, CSS e JavaScript puros dentro de resources | Substituída por [055](ADRs/ADR-055.md) |
| [042](ADRs/ADR-042.md) | Manter a persistência em arquivos JSON e adiar o banco de dados | Substituída por [085](ADRs/ADR-085.md) |
| [043](ADRs/ADR-043.md) | Fazer o login por e-mail e senha, devolvendo o usuário que entrou | Aceita |
| [044](ADRs/ADR-044.md) | Garantir e-mail único conferindo no service | Aceita |
| [045](ADRs/ADR-045.md) | Mostrar as opções de usuários no menu só para o gerente, numa tela própria | Aceita |
| [046](ADRs/ADR-046.md) | Usar um único Scanner para o terminal e ler os números linha por linha | Aceita |

### Lembrar o login (05/11/2026)

Último ADR antes do Projeto Integrador.

| ADR | Decisão | Status |
| --- | --- | --- |
| [047](ADRs/ADR-047.md) | Lembrar o login entre execuções com um arquivo de sessão | Substituída por [090](ADRs/ADR-090.md) e [091](ADRs/ADR-091.md) |

### PI: segunda, 23/11/2026

Planejamento do Projeto Integrador (Dijkstra, AVL, solicitações, log, cargos, sessão de 30 dias, Java puro antes do front) e as primeiras mudanças: grafo de bairros, correção de bugs, `Instanciador`, cadastro aberto, edição opcional e `UUID`.

| ADR | Decisão | Status |
| --- | --- | --- |
| [048](ADRs/ADR-048.md) | Calcular o ponto de coleta mais próximo com o algoritmo de Dijkstra | Aceita |
| [049](ADRs/ADR-049.md) | Usar uma árvore AVL em memória como índice das buscas, com o banco como fonte dos dados | Aceita, banco entrou no [085](ADRs/ADR-085.md) |
| [050](ADRs/ADR-050.md) | Classificar os resíduos numa lista simples de categorias | Aceita |
| [051](ADRs/ADR-051.md) | Acompanhar as solicitações de descarte por quatro status, com uma fila para as pendentes | Aceita |
| [052](ADRs/ADR-052.md) | Registrar um log de auditoria imutável e consultá-lo como pilha | Aceita |
| [053](ADRs/ADR-053.md) | Organizar os cargos em níveis e deixar só o gerente alterar cargos, pelo CPF | Aceita |
| [054](ADRs/ADR-054.md) | Fazer a sessão de login expirar em 30 dias | Aceita, implementada no [090](ADRs/ADR-090.md) |
| [055](ADRs/ADR-055.md) | Terminar o back-end em Java puro e fazer o front-end depois, com Spring Boot e Thymeleaf | Aceita, em andamento |
| [056](ADRs/ADR-056.md) | Representar Goiânia por um grafo de 15 bairros âncora | Aceita |
| [057](ADRs/ADR-057.md) | Conferir CPF e e-mail repetidos só no service e tratar e-mail inexistente no login | Aceita |
| [058](ADRs/ADR-058.md) | Montar os objetos na classe Instanciador, com injeção de dependência pelo construtor | Aceita |
| [059](ADRs/ADR-059.md) | Separar o cadastro aberto de usuários do cadastro feito pelo gerente, e criar a promoção de cargo | Aceita |
| [060](ADRs/ADR-060.md) | Editar o usuário com campos opcionais, mantendo o valor atual do que não for informado | Aceita |
| [061](ADRs/ADR-061.md) | Identificar Usuario, Residuo e Solicitacao por um UUID gerado pelo próprio sistema | Aceita |

### PI: terça, 24/11/2026

Fila encadeada, `Solicitacao`, fim do modo offline, troca de cifra proposta, enum `Bairro` e `Grafo<T>`.

| ADR | Decisão | Status |
| --- | --- | --- |
| [062](ADRs/ADR-062.md) | Implementar uma fila encadeada genérica no pacote backend_ecocode.Estrutura | Aceita |
| [063](ADRs/ADR-063.md) | Criar a entidade Solicitacao validando as mudanças de status na própria classe | Aceita |
| [064](ADRs/ADR-064.md) | Guardar as solicitações num repository por id e manter a fila e as em rota no service | Aceita |
| [065](ADRs/ADR-065.md) | Descartar o modo offline e guardar os dados só no banco de dados | Aceita, cumprida no [085](ADRs/ADR-085.md) |
| [066](ADRs/ADR-066.md) | Trocar a Cifra de César pela Cifra Afim na senha do usuário | Aceita, implementada no [084](ADRs/ADR-084.md) |
| [067](ADRs/ADR-067.md) | Representar os 15 bairros âncora com o enum Bairro | Aceita |
| [068](ADRs/ADR-068.md) | Implementar um grafo genérico com lista de adjacência no pacote backend_ecocode.Estrutura | Aceita |

### PI: quarta, 25/11/2026

Distâncias do Google Maps, status do resíduo, ecopontos da Comurg, pacotes do `Domain`, bairro obrigatório e a classe `Dijkstra<T>`.

| ADR | Decisão | Status |
| --- | --- | --- |
| [069](ADRs/ADR-069.md) | Montar o grafo com 30 arestas, metade medida no Google Maps e metade estimada | Substituída por [070](ADRs/ADR-070.md) |
| [070](ADRs/ADR-070.md) | Usar só a distância de carro do Google Maps em todas as arestas do grafo | Aceita |
| [071](ADRs/ADR-071.md) | Dar um status ao resíduo e só aceitar solicitação de resíduo disponível | Aceita |
| [072](ADRs/ADR-072.md) | Adicionar ao grafo os bairros dos ecopontos da Comurg | Aceita |
| [073](ADRs/ADR-073.md) | Dividir o pacote Domain em Entities, Enums e Interfaces | Aceita, alterada por [093](ADRs/ADR-093.md) |
| [074](ADRs/ADR-074.md) | Tornar o bairro obrigatório no cadastro do usuário | Aceita |
| [075](ADRs/ADR-075.md) | Implementar o Dijkstra numa classe genérica própria, sem fila de prioridade | Aceita |

### PI: quinta, 26/11/2026

Pontos de coleta só do gerente, ponto mais próximo, `ArvoreAVL` nos resíduos, `PilhaEncadeada` e log de auditoria.

| ADR | Decisão | Status |
| --- | --- | --- |
| [076](ADRs/ADR-076.md) | Deixar só o gerente cadastrar, editar e excluir ponto de coleta | Aceita |
| [077](ADRs/ADR-077.md) | Achar o ponto de coleta mais próximo rodando o Dijkstra uma vez a partir do bairro do usuário | Aceita |
| [078](ADRs/ADR-078.md) | Usar a ArvoreAVL genérica como índice dos resíduos, com o código como chave | Aceita |
| [079](ADRs/ADR-079.md) | Implementar uma pilha encadeada genérica no pacote backend_ecocode.Estrutura | Aceita |
| [080](ADRs/ADR-080.md) | Montar o log de auditoria com o enum AcaoLog, o RegistroLog imutável e a pilha | Aceita |
| [081](ADRs/ADR-081.md) | Gravar data e hora (LocalDateTime) no JSON com um adaptador do Gson | Aceita, só usada no plano B (JSON) |

### PI: sexta, 27/11/2026

Dono do resíduo, usuário logado nos métodos com permissão e Cifra Afim de 62 caracteres. Com isso, a parte de Estrutura de Dados ficou completa.

| ADR | Decisão | Status |
| --- | --- | --- |
| [082](ADRs/ADR-082.md) | Ligar cada resíduo ao seu dono e só deixar o dono alterá-lo | Aceita |
| [083](ADRs/ADR-083.md) | Passar o usuário logado, e não só o cargo, para os métodos que exigem permissão | Aceita |
| [084](ADRs/ADR-084.md) | Usar na senha a Cifra Afim com um alfabeto único de 62 caracteres | Aceita |

### PI: sábado, 28/11/2026

Saída do JSON para o MySQL, sessão de 30 dias, código do resíduo pelo banco e reorganização dos pacotes e dos testes.

| ADR | Decisão | Status |
| --- | --- | --- |
| [085](ADRs/ADR-085.md) | Trocar os arquivos JSON pelo MySQL com JDBC, mantendo a interface Persistencia | Aceita |
| [086](ADRs/ADR-086.md) | Abrir a conexão com o banco numa classe só, lendo o acesso do .env | Aceita |
| [087](ADRs/ADR-087.md) | Modelar o banco EcoCode em seis tabelas ligadas por chaves estrangeiras | Aceita |
| [088](ADRs/ADR-088.md) | Gravar com upsert, apagar os excluídos e só inserir no log | Aceita |
| [089](ADRs/ADR-089.md) | Calcular o último código de cada tipo a partir dos resíduos do banco | Aceita |
| [090](ADRs/ADR-090.md) | Fazer a sessão de 30 dias com um token aleatório e a opção "manter conectado" | Aceita, alterada por [091](ADRs/ADR-091.md) |
| [091](ADRs/ADR-091.md) | Guardar a sessão no banco e só o token no computador | Aceita |
| [092](ADRs/ADR-092.md) | Deixar os bairros e as arestas do grafo no código, sem tabelas no banco | Aceita |
| [093](ADRs/ADR-093.md) | Mover as estruturas de dados e os testes para dentro do pacote backend_ecocode | Aceita |
| [094](ADRs/ADR-094.md) | Testar cada módulo contra o banco com uma classe de teste por módulo | Aceita |

## ADRs por tema

O mesmo conjunto, agrupado pelo assunto. Ajuda a montar o relatório e a apresentação: cada tema conta a história de uma parte do sistema, do começo até hoje.

### Estruturas de dados (a parte de ED do PI)

- [048](ADRs/ADR-048.md): Calcular o ponto de coleta mais próximo com o algoritmo de Dijkstra (Aceita)
- [049](ADRs/ADR-049.md): Usar uma árvore AVL em memória como índice das buscas, com o banco como fonte dos dados (Aceita, banco entrou no [085](ADRs/ADR-085.md))
- [062](ADRs/ADR-062.md): Implementar uma fila encadeada genérica no pacote backend_ecocode.Estrutura (Aceita)
- [068](ADRs/ADR-068.md): Implementar um grafo genérico com lista de adjacência no pacote backend_ecocode.Estrutura (Aceita)
- [075](ADRs/ADR-075.md): Implementar o Dijkstra numa classe genérica própria, sem fila de prioridade (Aceita)
- [077](ADRs/ADR-077.md): Achar o ponto de coleta mais próximo rodando o Dijkstra uma vez a partir do bairro do usuário (Aceita)
- [078](ADRs/ADR-078.md): Usar a ArvoreAVL genérica como índice dos resíduos, com o código como chave (Aceita)
- [079](ADRs/ADR-079.md): Implementar uma pilha encadeada genérica no pacote backend_ecocode.Estrutura (Aceita)
- [080](ADRs/ADR-080.md): Montar o log de auditoria com o enum AcaoLog, o RegistroLog imutável e a pilha (Aceita)

### Persistência e banco de dados

- [024](ADRs/ADR-024.md): Salvar os resíduos em arquivo JSON, com o Map em memória como fonte principal (Substituída por [085](ADRs/ADR-085.md))
- [026](ADRs/ADR-026.md): Salvar os usuários em arquivo JSON, reaproveitando a persistência dos resíduos (Substituída por [085](ADRs/ADR-085.md))
- [042](ADRs/ADR-042.md): Manter a persistência em arquivos JSON e adiar o banco de dados (Substituída por [085](ADRs/ADR-085.md))
- [065](ADRs/ADR-065.md): Descartar o modo offline e guardar os dados só no banco de dados (Aceita, cumprida no [085](ADRs/ADR-085.md))
- [085](ADRs/ADR-085.md): Trocar os arquivos JSON pelo MySQL com JDBC, mantendo a interface Persistencia (Aceita)
- [086](ADRs/ADR-086.md): Abrir a conexão com o banco numa classe só, lendo o acesso do .env (Aceita)
- [087](ADRs/ADR-087.md): Modelar o banco EcoCode em seis tabelas ligadas por chaves estrangeiras (Aceita)
- [088](ADRs/ADR-088.md): Gravar com upsert, apagar os excluídos e só inserir no log (Aceita)
- [092](ADRs/ADR-092.md): Deixar os bairros e as arestas do grafo no código, sem tabelas no banco (Aceita)

### Senha e cifras

- [019](ADRs/ADR-019.md): Implementar a Cifra de César sobre a correspondência A = 0 ... Z = 25 (Aceita, alterada por [021](ADRs/ADR-021.md) e [031](ADRs/ADR-031.md))
- [021](ADRs/ADR-021.md): Preservar maiúsculas e minúsculas na cifra, deslocando cada caixa num sentido (Aceita, alterada por [031](ADRs/ADR-031.md))
- [023](ADRs/ADR-023.md): Guardar a senha do usuário cifrada com a Cifra de César (Substituída por [066](ADRs/ADR-066.md) e [084](ADRs/ADR-084.md))
- [031](ADRs/ADR-031.md): Cifrar também os dígitos e concentrar a conta da cifra num único método (Aceita)
- [033](ADRs/ADR-033.md): Implementar a Cifra Afim com as chaves a e b no .env (Substituída por [084](ADRs/ADR-084.md))
- [034](ADRs/ADR-034.md): Reunir MDC, MMC e inverso modular na classe Aritmetica (Aceita)
- [035](ADRs/ADR-035.md): Calcular o inverso da Cifra Afim pelo Euclides Estendido (Aceita)
- [066](ADRs/ADR-066.md): Trocar a Cifra de César pela Cifra Afim na senha do usuário (Aceita, implementada no [084](ADRs/ADR-084.md))
- [084](ADRs/ADR-084.md): Usar na senha a Cifra Afim com um alfabeto único de 62 caracteres (Aceita)

### Usuários, cargos e permissões

- [022](ADRs/ADR-022.md): Criar o módulo de usuários com CPF como chave e cadastro restrito a gerentes (Substituída por [059](ADRs/ADR-059.md) e [061](ADRs/ADR-061.md))
- [027](ADRs/ADR-027.md): Guardar e buscar o CPF só com dígitos (Aceita)
- [053](ADRs/ADR-053.md): Organizar os cargos em níveis e deixar só o gerente alterar cargos, pelo CPF (Aceita)
- [057](ADRs/ADR-057.md): Conferir CPF e e-mail repetidos só no service e tratar e-mail inexistente no login (Aceita)
- [059](ADRs/ADR-059.md): Separar o cadastro aberto de usuários do cadastro feito pelo gerente, e criar a promoção de cargo (Aceita)
- [060](ADRs/ADR-060.md): Editar o usuário com campos opcionais, mantendo o valor atual do que não for informado (Aceita)
- [074](ADRs/ADR-074.md): Tornar o bairro obrigatório no cadastro do usuário (Aceita)
- [083](ADRs/ADR-083.md): Passar o usuário logado, e não só o cargo, para os métodos que exigem permissão (Aceita)

### Resíduos e código

- [004](ADRs/ADR-004.md): Gerar o código de forma que código mod 5 caia na categoria do tipo (Aceita)
- [012](ADRs/ADR-012.md): Representar o tipo do resíduo com o enum TipoResiduo (Aceita)
- [013](ADRs/ADR-013.md): Gerar o código com 4 dígitos numa classe própria e normalizar o código digitado (Aceita, alterada por [025](ADRs/ADR-025.md), [032](ADRs/ADR-032.md) e [089](ADRs/ADR-089.md))
- [016](ADRs/ADR-016.md): Editar e excluir por código, com confirmação e sem alterar tipo nem código (Aceita)
- [032](ADRs/ADR-032.md): Calcular o próximo código a partir do último gerado, de qualquer tipo (Substituída por [089](ADRs/ADR-089.md))
- [050](ADRs/ADR-050.md): Classificar os resíduos numa lista simples de categorias (Aceita)
- [071](ADRs/ADR-071.md): Dar um status ao resíduo e só aceitar solicitação de resíduo disponível (Aceita)
- [082](ADRs/ADR-082.md): Ligar cada resíduo ao seu dono e só deixar o dono alterá-lo (Aceita)
- [089](ADRs/ADR-089.md): Calcular o último código de cada tipo a partir dos resíduos do banco (Aceita)

### Solicitações e fila de coletas

- [051](ADRs/ADR-051.md): Acompanhar as solicitações de descarte por quatro status, com uma fila para as pendentes (Aceita)
- [063](ADRs/ADR-063.md): Criar a entidade Solicitacao validando as mudanças de status na própria classe (Aceita)
- [064](ADRs/ADR-064.md): Guardar as solicitações num repository por id e manter a fila e as em rota no service (Aceita)

### Grafo de Goiânia e pontos de coleta

- [056](ADRs/ADR-056.md): Representar Goiânia por um grafo de 15 bairros âncora (Aceita)
- [067](ADRs/ADR-067.md): Representar os 15 bairros âncora com o enum Bairro (Aceita)
- [069](ADRs/ADR-069.md): Montar o grafo com 30 arestas, metade medida no Google Maps e metade estimada (Substituída por [070](ADRs/ADR-070.md))
- [070](ADRs/ADR-070.md): Usar só a distância de carro do Google Maps em todas as arestas do grafo (Aceita)
- [072](ADRs/ADR-072.md): Adicionar ao grafo os bairros dos ecopontos da Comurg (Aceita)
- [076](ADRs/ADR-076.md): Deixar só o gerente cadastrar, editar e excluir ponto de coleta (Aceita)

### Log de auditoria

- [052](ADRs/ADR-052.md): Registrar um log de auditoria imutável e consultá-lo como pilha (Aceita)
- [080](ADRs/ADR-080.md): Montar o log de auditoria com o enum AcaoLog, o RegistroLog imutável e a pilha (Aceita)

### Login e sessão

- [043](ADRs/ADR-043.md): Fazer o login por e-mail e senha, devolvendo o usuário que entrou (Aceita)
- [044](ADRs/ADR-044.md): Garantir e-mail único conferindo no service (Aceita)
- [047](ADRs/ADR-047.md): Lembrar o login entre execuções com um arquivo de sessão (Substituída por [090](ADRs/ADR-090.md) e [091](ADRs/ADR-091.md))
- [054](ADRs/ADR-054.md): Fazer a sessão de login expirar em 30 dias (Aceita, implementada no [090](ADRs/ADR-090.md))
- [090](ADRs/ADR-090.md): Fazer a sessão de 30 dias com um token aleatório e a opção "manter conectado" (Aceita, alterada por [091](ADRs/ADR-091.md))
- [091](ADRs/ADR-091.md): Guardar a sessão no banco e só o token no computador (Aceita)

### Organização do código

- [010](ADRs/ADR-010.md): Separar o módulo em camadas (tela, service, repository e domínio) (Aceita)
- [039](ADRs/ADR-039.md): Colocar todo o código Java dentro do pacote raiz backend_ecocode (Aceita, alterada por [073](ADRs/ADR-073.md) e [093](ADRs/ADR-093.md))
- [058](ADRs/ADR-058.md): Montar os objetos na classe Instanciador, com injeção de dependência pelo construtor (Aceita)
- [073](ADRs/ADR-073.md): Dividir o pacote Domain em Entities, Enums e Interfaces (Aceita, alterada por [093](ADRs/ADR-093.md))
- [093](ADRs/ADR-093.md): Mover as estruturas de dados e os testes para dentro do pacote backend_ecocode (Aceita)
- [094](ADRs/ADR-094.md): Testar cada módulo contra o banco com uma classe de teste por módulo (Aceita)

### Interface e Spring

- [038](ADRs/ADR-038.md): Usar Spring Boot como servidor do back-end (Aceita)
- [041](ADRs/ADR-041.md): Fazer o front-end em HTML, CSS e JavaScript puros dentro de resources (Substituída por [055](ADRs/ADR-055.md))
- [045](ADRs/ADR-045.md): Mostrar as opções de usuários no menu só para o gerente, numa tela própria (Aceita)
- [046](ADRs/ADR-046.md): Usar um único Scanner para o terminal e ler os números linha por linha (Aceita)
- [055](ADRs/ADR-055.md): Terminar o back-end em Java puro e fazer o front-end depois, com Spring Boot e Thymeleaf (Aceita, em andamento)

## Decisões que mudaram

Algumas decisões foram trocadas no caminho. A cadeia mostra como cada uma evoluiu:

| Assunto | Caminho |
| --- | --- |
| Onde ficam os dados | Lista em memória ([003](ADRs/ADR-003.md)) → `TreeMap` ([011](ADRs/ADR-011.md)) → JSON ([024](ADRs/ADR-024.md), [026](ADRs/ADR-026.md)) → banco adiado ([042](ADRs/ADR-042.md)) → sem offline ([065](ADRs/ADR-065.md)) → MySQL com JDBC ([085](ADRs/ADR-085.md)) |
| Senha | César ([023](ADRs/ADR-023.md)) → troca proposta ([066](ADRs/ADR-066.md)) → Afim de 62 caracteres ([084](ADRs/ADR-084.md)). O ideal seria hash, e isso está registrado no 084 |
| Código do resíduo | 6 dígitos ([005](ADRs/ADR-005.md)) → 4 dígitos ([013](ADRs/ADR-013.md)) → sequência por tipo em arquivo ([025](ADRs/ADR-025.md)) → último de qualquer tipo ([032](ADRs/ADR-032.md)) → último de cada tipo, calculado pelo banco ([089](ADRs/ADR-089.md)) |
| Sessão | Arquivo com CPF, 7 dias ([047](ADRs/ADR-047.md)) → 30 dias ([054](ADRs/ADR-054.md)) → token aleatório ([090](ADRs/ADR-090.md)) → token no computador e sessão no banco ([091](ADRs/ADR-091.md)) |
| Cadastro de usuário | Só o gerente ([022](ADRs/ADR-022.md)) → cadastro aberto + cadastro do gerente + promoção ([059](ADRs/ADR-059.md)) |
| Identificador | CPF e código como chave ([022](ADRs/ADR-022.md), [011](ADRs/ADR-011.md)) → `UUID` ([061](ADRs/ADR-061.md)) |
| Grafo | 15 bairros ([056](ADRs/ADR-056.md)) → 30 arestas, metade estimada ([069](ADRs/ADR-069.md)) → todas do Google Maps ([070](ADRs/ADR-070.md)) → 19 bairros com os ecopontos ([072](ADRs/ADR-072.md)) |
| Ordem da listagem de resíduos | Cadastro ou código ([036](ADRs/ADR-036.md)) → sempre pelo código, na AVL ([078](ADRs/ADR-078.md)) |
| Pacote das estruturas | `Estrutura` fora do raiz ([073](ADRs/ADR-073.md)) → `backend_ecocode.Estrutura` ([093](ADRs/ADR-093.md)) |
| Front-end | HTML, CSS e JS puros ([041](ADRs/ADR-041.md)) → back em Java puro primeiro, depois Spring + Thymeleaf ([055](ADRs/ADR-055.md)) |

## Pendências registradas nos ADRs

As caixas `[ ]` dos ADRs mais recentes que ainda estão abertas e pesam na entrega:

- [ ] Fazer o `removerExcluidos` no `PersistenciaBancoUsuario`: hoje um usuário removido volta
  depois de reiniciar ([085](ADRs/ADR-085.md), [088](ADRs/ADR-088.md)).
- [ ] Decidir o que acontece ao remover um usuário que tem resíduos, pedidos ou log, por causa
  das chaves estrangeiras ([087](ADRs/ADR-087.md)).
- [ ] Guardar o script SQL do banco no repositório ([087](ADRs/ADR-087.md)).
- [ ] Trocar o `mod 26` por `mod 62` na `Configuracao` e no `.env.example`
  ([084](ADRs/ADR-084.md), [086](ADRs/ADR-086.md)).
- [ ] Corrigir o pacote do `TestePontosColeta` ([093](ADRs/ADR-093.md)).
- [ ] Testar a expiração da sessão e rodar a `Teste` inteira no banco ([094](ADRs/ADR-094.md)).
- [ ] Tirar `CODIGO_PATH` e `SESSAO_PATH` da `Constantes` ([089](ADRs/ADR-089.md),
  [090](ADRs/ADR-090.md)).
- [ ] Registrar no relatório que a cifra na senha é didática e que o correto seria hash
  ([084](ADRs/ADR-084.md)).
- [ ] Ligar o front ao back com Spring e Thymeleaf ([055](ADRs/ADR-055.md)).
