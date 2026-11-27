# Testes do Sistema EcoCode

> Registro dos testes do back-end: o que foi testado, o critério de aceitação de cada teste,
> o resultado obtido e os bugs encontrados no caminho.

| | |
|---|---|
| **Projeto** | EcoCode, Projeto Integrador |
| **Equipe** | Gilvan Pedro, Sidney Emanuel, Guilherme Scarcela, João Paulo e Dários Suess |
| **Última atualização** | 28/11/2026 |
| **Classes de teste** | `backend_ecocode.Testes.Teste` (geral, em banco vazio) e `TesteUsuarios`, `TesteResiduos`, `TesteSolicitacoes`, `TestePontosColeta` (no banco), todas em `src/main/java/backend_ecocode/Testes/` |
| **Resultado histórico anterior à integração web** | No banco (28/11): as 4 classes passaram, e a fila volta na ordem certa depois de reiniciar (B05 corrigido). Na `Teste` (JSON, 27/11): 199 testes, 198 passaram |

## Validação da integração web

O protótipo foi exercitado com Java 25, Spring Boot 4 e MySQL 8.4. O teste geral em um banco exclusivo passou nas 199 verificações. Os quatro testes JDBC por módulo registraram 49 verificações positivas, além das recusas de login esperadas.

| Cenário | Critério de aceitação | Resultado observado |
| --- | --- | --- |
| Descarte no navegador | Após o cadastro e login, registrar e selecionar dois resíduos deve gerar dois pedidos independentes no histórico | O Chromium exibiu dois pedidos e abriu o detalhe do pedido selecionado pelo seu identificador |
| Pedido duplicado | Um resíduo pendente só pode integrar uma solicitação ativa | A API respondeu HTTP 400 e o histórico manteve um único pedido para o resíduo |
| Privacidade do histórico | Outra conta só pode visualizar e cancelar os próprios pedidos | O histórico da outra conta ficou vazio e a tentativa de cancelamento retornou HTTP 403 |
| Cancelamento pelo dono | Cancelar um pedido pendente deve liberar o resíduo | O pedido passou para CANCELADA e o resíduo para DISPONIVEL |
| Fila da equipe | Apenas a equipe pode iniciar e concluir coletas | O usuário recebeu HTTP 403 e a conta de gerência iniciou e concluiu uma coleta |
| Falha de gravação | Uma falha ao inserir o pedido deve reverter a mudança de estado do resíduo | Uma restrição temporária no banco de teste impediu a inserção. O banco e a API mantiveram o resíduo disponível, e a repetição após retirar a restrição teve sucesso |
| Encerrar sessão | Após sair, o navegador precisa autenticar novamente | A consulta de perfil retornou HTTP 401 |

Para repetir o teste automatizado de API, inicie a aplicação em um banco de teste e execute na raiz do repositório. O teste cria duas contas e um resíduo com histórico de descarte.

```bash
ECOCODE_TEST_URL=http://localhost:8080 python3 tests/fluxo_web.py
```

## Sumário

1. [Como os testes funcionam](#como-os-testes-funcionam)
2. [Como rodar](#como-rodar)
3. [Testes no banco de dados](#testes-no-banco-de-dados)
4. [Testes do fluxo principal](#testes-do-fluxo-principal)
5. [Todos os grupos de testes da classe Teste](#todos-os-grupos-de-testes-da-classe-teste)
6. [Bugs encontrados](#bugs-encontrados)
7. [Testes possíveis que ainda não foram feitos](#testes-possíveis-que-ainda-não-foram-feitos)

---

## Como os testes funcionam

O PI pede o registro dos testes do fluxo principal, com pelo menos um cenário de sucesso e um
cenário de rejeição por regra de negócio, e com o critério de aceitação e o resultado de cada
teste. Para isso o grupo escreveu a classe `Teste`, um `main` que monta o sistema inteiro pelo
`Instanciador` (os mesmos services que a tela usa) e chama cada função como um usuário faria.

Ela usa três ajudantes:

| Ajudante | O que confere | Exemplo |
| --- | --- | --- |
| `conferir(nome, condição)` | A condição é verdadeira | "solicitação nasce PENDENTE" |
| `esperaErro(nome, ação)` | A ação lança `IllegalArgumentException` ou `IllegalStateException` | "quantidade 0 dá erro" |
| `esperaAcessoNegado(nome, ação)` | A ação lança `AcessoNegadoException` | "usuário não cadastra ponto" |

Cada teste imprime `[OK]` ou `[FALHA]` com o nome, e no fim aparece o total. Um teste de
rejeição só passa se o sistema **recusar** a ação: se a ação funcionar, o teste falha.

Na classe `Teste`, quatro pessoas de teste são usadas do começo ao fim:

| Pessoa | Cargo | Bairro |
| --- | --- | --- |
| Maria Souza | Usuário | Setor Bueno |
| João Lima | Usuário | Jardim Curitiba |
| Carlos Coletor | Funcionário | Setor Central |
| Ana Gerente | Gerente | Parque Amazônia |

## Como rodar

A partir da raiz do projeto, com o MySQL rodando, o banco `EcoCode` criado e o `.env`
preenchido (`DB_URL`, `DB_USUARIO`, `DB_SENHA` e as chaves da Cifra Afim):

```bash
mvn compile
mvn dependency:copy-dependencies

# testes no banco, nesta ordem (cada um usa os dados do anterior)
java -cp "target/classes:target/dependency/*" backend_ecocode.Testes.TesteUsuarios
java -cp "target/classes:target/dependency/*" backend_ecocode.Testes.TesteResiduos
java -cp "target/classes:target/dependency/*" backend_ecocode.Testes.TesteSolicitacoes
java -cp "target/classes:target/dependency/*" backend_ecocode.TestePontosColeta

# teste geral: use um segundo banco vazio com o esquema de docs/banco/EcoCode.sql
# ajuste DB_URL para esse banco antes de executar
java -cp "target/classes:target/dependency/*" backend_ecocode.Testes.Teste
```

No Windows, troque `:` por `;`. Pela IDE, basta rodar cada classe.

> **Atenção:** o `TestePontosColeta` ainda declara `package backend_ecocode;` (ponto P05), por
> isso o nome dele no comando não tem `.Testes`. Quando o pacote for corrigido, o comando
> passa a ser `backend_ecocode.Testes.TestePontosColeta`.

> **Os testes gravam no banco de verdade.** As pessoas de teste e os ecopontos são
> reaproveitados se já existirem, mas cada rodada do `TesteSolicitacoes` deixa quatro resíduos
> e quatro pedidos novos. Se quiser começar do zero, recrie o banco com o script antes.

A rodada histórica em JSON antecede a integração web. No MySQL, a classe geral usa as seis tabelas do esquema versionado.

---

## Testes no banco de dados

Em 28/11 os dados passaram dos arquivos JSON para o MySQL ([ADR-085](ADRs/ADR-085.md)). Para
conferir a troca, o grupo escreveu uma classe de teste por módulo
([ADR-094](ADRs/ADR-094.md)). Elas usam o mesmo `Instanciador` da tela, então gravam e leem do
banco como o sistema de verdade.

As pessoas de teste ficam salvas no banco e são usadas pelos outros testes:

| Pessoa | CPF | Cargo | Bairro |
| --- | --- | --- | --- |
| Ana Ribeiro | 390.533.447-05 | Gerente | Parque Amazônia |
| Carlos Mendes | 111.444.777-35 | Funcionário | Setor Central |
| Maria Souza | 529.982.247-25 | Usuário | Setor Bueno |
| João Lima | 123.456.789-09 | Usuário | Jardim Curitiba |
| Beatriz Alves | 714.602.380-01 | Usuário | Jardim São José |

### TesteUsuarios (RF01, RN01, RN02, RN16, RNF02, RNF03)

| ID | Cenário | Tipo | Critério de aceitação | Resultado |
| --- | --- | --- | --- | --- |
| BD01 | Cadastrar a gerente Ana e o funcionário Carlos pelo cadastro do gerente | Sucesso | Os dois aparecem no banco com o cargo certo | Passou |
| BD02 | Cadastro aberto de Maria, João e Bia | Sucesso | Os três aparecem no banco com cargo `USUARIO` | Passou |
| BD03 | Ler os usuários do banco | Sucesso (RNF02) | A senha salva aparece cifrada, nunca em texto puro | Passou |
| BD04 | Login da Maria e da Ana com a senha certa | Sucesso | As duas entram | Passou |
| BD05 | Login com senha errada e com e-mail que não existe | Rejeição | O sistema recusa, sem erro de execução | Passou |
| BD06 | Cadastrar CPF repetido, e-mail repetido e sem bairro | Rejeição (RN01, RN02, RN16) | O sistema recusa com mensagem | Passou |

### TesteResiduos (RF02, RN03, RN14)

| ID | Cenário | Tipo | Critério de aceitação | Resultado |
| --- | --- | --- | --- | --- |
| BD07 | Cadastrar 5 resíduos de Maria, João e Bia | Sucesso | Cada um recebe código e dono, e aparece no banco | Passou |
| BD08 | Maria edita o próprio notebook | Sucesso | Quantidade e descrição mudam no banco | Passou |
| BD09 | João exclui o próprio celular | Sucesso | A linha some da tabela `residuos` | Passou |
| BD10 | João edita e Bia exclui um resíduo da Maria | Rejeição (RN14) | O sistema recusa | Passou |
| BD11 | Quantidade zero, sem tipo e sem dono | Rejeição | O sistema recusa | Passou |
| BD12 | Listar o que ficou no banco com o nome do dono | Sucesso | Cada resíduo mostra código, tipo, quantidade, status e dono | Passou |

### TesteSolicitacoes (RF05 a RF07, RN04, RN05, RN12, RN13)

| ID | Cenário | Tipo | Critério de aceitação | Resultado |
| --- | --- | --- | --- | --- |
| BD13 | Maria, João e Bia pedem o descarte, com um segundo entre os pedidos | Sucesso | Os pedidos são gravados com data e hora | Passou |
| BD14 | Posição de cada pedido na fila | Sucesso | Maria antes de João, João antes de Bia | Passou |
| BD15 | Bia cancela o próprio pedido pendente | Sucesso | Vira `CANCELADA` e o resíduo volta a `DISPONIVEL` | Passou |
| BD16 | **Reiniciar o programa** (segundo `Instanciador`) e ver a fila | Sucesso (RNF03) | As posições continuam na mesma ordem | Passou (confirma a correção do bug B05) |
| BD17 | Carlos pega a próxima depois de reiniciar e conclui | Sucesso | Pega a mais antiga que estava na fila, e ela vira `CONCLUIDA` | Passou |
| BD18 | Maria (usuária) tenta pegar solicitação | Rejeição (RN05) | O sistema recusa | Passou |
| BD19 | João cancela pedido da Bia; Maria pede resíduo do João | Rejeição (RN04, RN14) | O sistema recusa | Passou |
| BD20 | Pedir o mesmo resíduo duas vezes; cancelar pedido já cancelado | Rejeição (RN13) | O sistema recusa | Passou |
| BD21 | Ana (gerente) cancela o pedido da Bia | Sucesso (RN12) | Vira `CANCELADA` | Passou |

### TestePontosColeta (RF03, RF04, RN11, RN17)

| ID | Cenário | Tipo | Critério de aceitação | Resultado |
| --- | --- | --- | --- | --- |
| BD22 | Ana cadastra os 5 ecopontos da Comurg | Sucesso | Os 5 aparecem no banco, sem duplicar se já existirem | Passou |
| BD23 | Ana cria, edita e remove um ponto temporário | Sucesso | Nome e bairro mudam; depois a linha some da tabela | Passou |
| BD24 | Carlos cadastra ponto; Maria edita e exclui ponto | Rejeição (RN17) | `AcessoNegadoException` | Passou |
| BD25 | Ponto sem nome e sem bairro | Rejeição | O sistema recusa | Passou |
| BD26 | Ponto mais próximo das 5 pessoas, com os dados lidos do banco | Sucesso | Maria e Ana: Setor Faiçalville; João e Carlos: Jardim Guanabara II; Bia: Jardim São José (mesmo bairro) | Passou |

Os IDs desta seção começam com **BD** (banco de dados), para não confundir com os bugs B01 a B06.

---

## Testes do fluxo principal

O fluxo principal do PI vai do cadastro e da classificação do resíduo até a consulta do ponto
de coleta, o registro da solicitação e o acompanhamento da situação dela. Cada linha abaixo é
um teste da classe `Teste`, com o nome que aparece na saída.

### Cadastro e classificação do resíduo (RF02, RN03, RN14)

| ID | Cenário | Tipo | Critério de aceitação | Resultado |
| --- | --- | --- | --- | --- |
| T01 | Maria cadastra um notebook | Sucesso | O resíduo recebe `UUID`, código gerado e começa `DISPONIVEL` | Passou |
| T02 | Código do notebook | Sucesso | `código mod 5 = 0`, a categoria de Computadores | Passou |
| T03 | Código do celular | Sucesso | `código mod 5 = 1`, a categoria de Celulares | Passou |
| T04 | Cadastrar com quantidade 0 ou negativa | Rejeição | O sistema recusa com mensagem | Passou |
| T05 | Cadastrar sem tipo ou sem dono | Rejeição | O sistema recusa com mensagem | Passou |
| T06 | João tenta editar e excluir o resíduo da Maria | Rejeição (RN14) | `AcessoNegadoException` | Passou |
| T07 | Maria edita a quantidade dos cabos para 7 | Sucesso | A quantidade salva passa a ser 7 | Passou |

### Consulta do ponto de coleta (RF03, RF04, RN11, RN16, RN17)

| ID | Cenário | Tipo | Critério de aceitação | Resultado |
| --- | --- | --- | --- | --- |
| T08 | Gerente cadastra os 5 ecopontos da Comurg | Sucesso | Os 5 aparecem na listagem | Passou |
| T09 | Usuário e funcionário tentam cadastrar ponto | Rejeição (RN17) | `AcessoNegadoException` | Passou |
| T10 | Gerente edita só o endereço | Sucesso | Endereço muda; nome e bairro continuam | Passou |
| T11 | Cadastrar usuário sem bairro | Rejeição (RN16) | O sistema recusa | Passou |
| T12 | Ponto mais próximo da Maria (Setor Bueno) | Sucesso | Ecoponto Setor Faiçalville | Passou |
| T13 | Ponto mais próximo do João (Jardim Curitiba) | Sucesso | Ecoponto Jardim Guanabara II | Passou |
| T14 | Ponto mais próximo do Carlos (Setor Central) | Sucesso | Ecoponto Jardim Guanabara II | Passou |
| T15 | Ponto mais próximo da Ana (Parque Amazônia) | Sucesso | Ecoponto Setor Faiçalville | Passou |

Os resultados esperados de T12 a T15 vêm da tabela de `docs/GrafoBairros.md`, calculada à mão
antes de o Dijkstra existir.

### Registro da solicitação (RF05, RN13, RN14)

| ID | Cenário | Tipo | Critério de aceitação | Resultado |
| --- | --- | --- | --- | --- |
| T16 | Maria pede o descarte do notebook | Sucesso | Solicitação `PENDENTE`; resíduo vira `EM_SOLICITACAO` e some da lista de disponíveis | Passou |
| T17 | Maria pede o mesmo notebook de novo | Rejeição (RN13) | O sistema recusa | Passou |
| T18 | João pede o descarte de um resíduo da Maria | Rejeição (RN14) | `AcessoNegadoException` | Passou |
| T19 | Maria tenta editar o notebook que está em solicitação | Rejeição (RN15) | O sistema recusa | Passou |
| T20 | Posição na fila | Sucesso | O pedido do celular fica depois do pedido do notebook | Passou |

### Acompanhamento da situação (RF06, RF07, RN04, RN05, RN12, RN13)

| ID | Cenário | Tipo | Critério de aceitação | Resultado |
| --- | --- | --- | --- | --- |
| T21 | Maria cancela o próprio pedido pendente | Sucesso | Vira `CANCELADA`; o resíduo volta a `DISPONIVEL` | Passou |
| T22 | João tenta cancelar o pedido da Maria | Rejeição (RN04) | `AcessoNegadoException` | Passou |
| T23 | Gerente cancela o pedido de outra pessoa | Sucesso (RN12) | Vira `CANCELADA`; o resíduo volta a `DISPONIVEL` | Passou |
| T24 | Resíduo cancelado é pedido de novo | Sucesso (RN13) | Nova solicitação `PENDENTE` | Passou |
| T25 | Usuário comum tenta pegar da fila ou concluir | Rejeição (RN05) | `AcessoNegadoException` | Passou |
| T26 | Funcionário pega o pedido do notebook | Sucesso | Vira `EM_ROTA` | Passou |
| T27 | Funcionário conclui o pedido | Sucesso | Vira `CONCLUIDA`; o notebook vira `DESCARTADO` e não volta à lista | Passou |
| T28 | Concluir um pedido que não está em rota, ou concluir duas vezes | Rejeição | O sistema recusa | Passou |
| T29 | Gerente tenta cancelar um pedido concluído | Rejeição (RN12) | O sistema recusa | Passou |
| T30 | Usuário tenta cancelar um pedido em rota | Rejeição (RN04) | O sistema recusa | Passou |
| T31 | Gerente cancela um pedido em rota | Sucesso (RN12) | Vira `CANCELADA`; o resíduo volta a `DISPONIVEL` | Passou |
| T32 | A fila continua na ordem de chegada depois de reiniciar | Sucesso | Os 4 pedidos novos ficam na mesma ordem | Falhou no JSON (bug B05); **passou no banco** (BD16) |

### Usuários, cargos e log (RF01, RF08 a RF10, RN01, RN02, RN07 a RN09, RN18)

| ID | Cenário | Tipo | Critério de aceitação | Resultado |
| --- | --- | --- | --- | --- |
| T33 | Cadastro aberto | Sucesso | Cargo `USUARIO`; CPF só com dígitos; senha não fica em texto puro | Passou |
| T34 | CPF ou e-mail repetido, CPF inválido, e-mail inválido | Rejeição (RN01, RN02) | O sistema recusa | Passou |
| T35 | Login com senha certa, errada e e-mail inexistente | Sucesso e rejeição | Só a senha certa entra | Passou |
| T36 | Trocar a senha | Sucesso | A nova entra e a antiga não entra mais | Passou |
| T37 | Usuário comum tenta cadastrar funcionário | Rejeição | `AcessoNegadoException` | Passou |
| T38 | Gerente promove pelo CPF | Sucesso (RN08) | O cargo muda | Passou |
| T39 | Gerente tenta mudar o próprio cargo ou se remover | Rejeição (RN09, RN18) | O sistema recusa | Passou |
| T40 | Ações do gerente aparecem no log | Sucesso (RF08) | O mais novo no topo, com quem, quando, ação e alvo | Passou |
| T41 | Usuário comum ou funcionário tenta ver o log | Rejeição (RF09) | `AcessoNegadoException` | Passou |
| T42 | Reler o `log.json` | Sucesso (RN07) | O topo e a data e hora voltam iguais | Passou (no JSON) |

---

## Todos os grupos de testes da classe Teste

A classe é dividida em grupos, um por parte do sistema, na ordem em que aparecem na saída.
Os resultados abaixo são da rodada de 27/11, ainda com os arquivos JSON. A `Teste` ainda não
foi rodada inteira sobre o banco.

| Grupo na saída | O que testa | Testes | Resultado |
| --- | --- | --- | --- |
| `=== RESÍDUOS ===` | Cadastro, UUID, código e categoria, buscas, validações, dono do resíduo, edição | 32 | Todos passaram |
| `=== SOLICITAÇÃO + RN13 ===` | Solicitar, status do resíduo, cancelar pelo dono e pelo gerente, fila, rota, conclusão | 33 | Todos passaram |
| `=== PONTOS DE COLETA ===` | CRUD só do gerente, edição parcial, validações, excluir | 24 | Todos passaram |
| `=== GRAFO ===` | Distâncias do Dijkstra no grafo de Goiânia | 4 | Todos passaram |
| `=== PONTO MAIS PRÓXIMO ===` | `pontoMaisProximo` para as 4 pessoas de teste | 5 | Todos passaram |
| `=== FILA DEPOIS DE REINICIAR ===` | Ordem da fila depois de ler o JSON de novo | 1 | **Falhou** (B05) |
| `=== USUÁRIOS ===` | Cadastro aberto e pelo gerente, login, edição, troca de senha, promoção, remoção | 28 | Todos passaram |
| `=== LOG DE AUDITORIA ===` | Ações registradas, ordem da pilha, quem pode ver, reler o JSON | 17 | Todos passaram |
| `=== CIFRA AFIM ===` | Cifrar e decifrar volta o original, dígitos cifrados, mdc, inverso, 1.860 chaves | 21 | Todos passaram |
| `=== ÁRVORE AVL ===` | 1.000 inserções em ordem, altura, busca, chave repetida, remoções, ordem | 15 | Todos passaram |
| `=== PILHA E FILA ===` | LIFO da pilha, FIFO da fila, estruturas vazias | 11 | Todos passaram |
| `=== DIJKSTRA NUM GRAFO PEQUENO ===` | Caminho mais curto, vértice inalcançável, origem inválida, 19 bairros alcançáveis | 8 | Todos passaram |
| **Total** | | **199** | **198 passaram, 1 falhou** |

Alguns destaques que valem para a apresentação:

- **AVL:** 1.000 chaves inseridas em ordem crescente, que é o pior caso de uma árvore sem
  balanceamento (altura 1.000). A AVL ficou com altura de no máximo 14, perto de log₂(1.000) ≈ 10.
- **Dijkstra:** no grafo A-B (1), B-C (2) e A-C (5), o caminho de A até C dá 3, passando por B,
  e não 5 pela aresta direta.
- **Cifra Afim:** "Senha2026", "EcoCode", "abcXYZ019" e outras voltam iguais depois de cifrar e
  decifrar. O teste "a mesma senha gera sempre o mesmo texto" passa, e isso mostra por que a
  cifra não é um hash.

---

## Bugs encontrados

| ID | Bug | Como apareceu | Situação |
| --- | --- | --- | --- |
| B01 | `NullPointerException` ao carregar resíduos antigos do JSON, salvos antes de o resíduo ter `UUID` e dono | Ao iniciar o sistema com o arquivo antigo | Corrigido: os resíduos antigos foram apagados e cadastrados de novo ([ADR-082](ADRs/ADR-082.md)) |
| B02 | Concluir uma solicitação que não estava em rota não era recusado como devia | Teste "concluir sem estar em rota dá erro" | Corrigido: o `concluir` procura só nas em rota e lança `IllegalArgumentException` |
| B03 | O próprio teste usava a busca errada para achar o resíduo pelo `UUID` | Testes de resíduo falhando sem o sistema estar errado | Corrigido no `Teste`: agora usa `buscarPorId` |
| B04 | O Gson não gravava `LocalDateTime`, e o log não era salvo | Primeira gravação do `log.json` | Corrigido com um adaptador no Gson ([ADR-081](ADRs/ADR-081.md)) |
| B05 | A fila perdia a ordem de chegada quando o sistema reiniciava. O repository de solicitações guarda tudo num `HashMap`, que não mantém ordem, e o service remonta a fila ordenando pela data. Como a `Solicitacao` guardava só o dia (`LocalDate`), pedidos do mesmo dia voltavam em qualquer ordem | Teste T32 | Corrigido: a `Solicitacao` guarda data e hora (`LocalDateTime`), gravada como `DATETIME` no banco ([ADR-087](ADRs/ADR-087.md)). Conferido no teste BD16 |
| B06 | **Usuário removido volta depois de reiniciar.** O `PersistenciaBancoUsuario` só faz upsert e não tem `removerExcluidos`, então a linha continua no banco | Leitura do código, ao escrever o ADR-088 | **Aberto.** Fazer o `removerExcluidos` e decidir o que fazer com as chaves estrangeiras ([ADR-088](ADRs/ADR-088.md)) |

Pontos que não são erro de execução, mas não batem com a documentação:

| ID | O que foi visto | Onde |
| --- | --- | --- |
| P01 | A `Configuracao` ainda aplica `mod 26` nas chaves da Afim e recusa `a` par ou 13. Com o alfabeto de 62, deveria aplicar `mod 62` e recusar par ou 31. Hoje o sistema usa só 312 das 1.860 chaves | `Util/Configuracao`, `.env.example` ([ADR-084](ADRs/ADR-084.md)) |
| P02 | ~~A promoção não guardava o cargo anterior~~. Corrigido: o log agora grava `CPF: CARGO_ANTERIOR -> CARGO_NOVO` | `GerenciarUsuariosService.promoverUsuario` |
| P03 | `EDITOU_USUARIO` existe no `AcaoLog`, mas a edição de usuário não grava log | `GerenciarUsuariosService.editar` ([ADR-080](ADRs/ADR-080.md)) |
| P04 | A tela de listar resíduos ainda pergunta "ordem de cadastro ou de código", mas com a AVL as duas mostram pelo código | `TelaCadastroResiduoTerminal` ([ADR-078](ADRs/ADR-078.md)) |
| P05 | O `TestePontosColeta` está na pasta `Testes/`, mas declara `package backend_ecocode;` | `Testes/TestePontosColeta` ([ADR-093](ADRs/ADR-093.md)) |
| P06 | As constantes `CODIGO_PATH` e `SESSAO_PATH` continuam na `Constantes`, mas não são mais usadas | `Util/Constantes` ([ADR-089](ADRs/ADR-089.md), [ADR-090](ADRs/ADR-090.md)) |

---

## Testes possíveis que ainda não foram feitos

| Teste | Por que ainda não | Quando fazer |
| --- | --- | --- |
| Sessão expira em 30 dias (RN06) | A sessão foi feita ([ADR-090](ADRs/ADR-090.md)), mas o teste precisa de uma sessão com data vencida | Antes da entrega |
| "Sair" apaga a sessão no banco e o `token.txt` | Só foi testado na mão, pelo terminal | Junto com o teste da expiração |
| Rodar a `Teste` inteira sobre o banco | Os testes por módulo vieram primeiro | Antes da entrega, para atualizar o total |
| Remover usuário e reiniciar | Hoje o usuário volta (bug B06) | Depois de corrigir o B06 |
| Excluir um resíduo que já teve solicitação cancelada | Depende da chave estrangeira do banco ([ADR-087](ADRs/ADR-087.md)) | Antes da entrega |
| Mensagens claras na interface (RNF01) | A interface web ainda não existe | Na etapa do Thymeleaf |
| Tema claro e escuro (RNF05) | Opcional, depende da interface | Se sobrar tempo |
| Dois funcionários pegando a fila ao mesmo tempo | O back-end roda num processo só, pelo terminal | Quando o servidor Spring entrar |
| Busca com muitos resíduos (desempenho, RNF04) | Com poucos dados a diferença não aparece | Opcional, para mostrar a AVL |
| Bairro sem caminho até nenhum ponto | Os 19 bairros são todos ligados no grafo atual | Se um bairro novo entrar sem arestas |
