# Testes do Sistema EcoCode

> Registro dos testes do back-end: o que foi testado, o critério de aceitação de cada teste,
> o resultado obtido e os bugs encontrados no caminho.

| | |
|---|---|
| **Projeto** | EcoCode, Projeto Integrador |
| **Equipe** | Gilvan Pedro, Sidney Emanuel, Guilherme Scarcela, João Paulo e Dários Suess |
| **Última atualização** | 27/11/2026 |
| **Classe de teste** | `backend_ecocode.Teste` (`src/main/java/backend_ecocode/Teste.java`) |
| **Resultado da última rodada** | 199 testes: 198 passaram, 1 falhou (bug B05, ainda aberto) |

## Sumário

1. [Como os testes funcionam](#como-os-testes-funcionam)
2. [Como rodar](#como-rodar)
3. [Testes do fluxo principal](#testes-do-fluxo-principal)
4. [Todos os grupos de testes da classe Teste](#todos-os-grupos-de-testes-da-classe-teste)
5. [Bugs encontrados](#bugs-encontrados)
6. [Testes possíveis que ainda não foram feitos](#testes-possíveis-que-ainda-não-foram-feitos)

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

Quatro pessoas de teste são usadas do começo ao fim:

| Pessoa | Cargo | Bairro |
| --- | --- | --- |
| Maria Souza | Usuário | Setor Bueno |
| João Lima | Usuário | Jardim Curitiba |
| Carlos Coletor | Funcionário | Setor Central |
| Ana Gerente | Gerente | Parque Amazônia |

## Como rodar

A partir da raiz do projeto, com o `.env` preenchido:

```bash
mvn compile
mvn dependency:copy-dependencies
java -cp "target/classes:target/dependency/*" backend_ecocode.Teste
```

No Windows, troque `:` por `;`. Pela IDE, basta rodar a classe `backend_ecocode.Teste`.

> **Atenção:** o `Teste` usa os arquivos reais de `arquivos/`. Os usuários de teste criados no
> grupo "Usuários" são removidos no fim, e os 5 ecopontos são reaproveitados se já existirem,
> mas os resíduos e as solicitações de teste ficam salvos. Faça uma cópia da pasta `arquivos/`
> antes, se quiser manter os dados como estavam.

A saída completa da última rodada está em `documentos/codigo/saida-Teste.txt`. O teste foi
rodado três vezes seguidas sobre os mesmos arquivos, sempre com o mesmo resultado.

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
| T32 | A fila continua na ordem de chegada depois de reiniciar | Sucesso | Os 4 pedidos novos ficam na mesma ordem | **Falhou** (bug B05) |

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
| T42 | Reler o `log.json` | Sucesso (RN07) | O topo e a data e hora voltam iguais | Passou |

---

## Todos os grupos de testes da classe Teste

A classe é dividida em grupos, um por parte do sistema, na ordem em que aparecem na saída.

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
| B05 | **A fila perde a ordem de chegada quando o sistema reinicia.** O repository de solicitações guarda tudo num `HashMap`, que não mantém ordem, e o service remonta a fila ordenando pela data. Como a `Solicitacao` guarda só o dia (`LocalDate`), pedidos do mesmo dia voltam em qualquer ordem | Teste T32 | **Aberto.** Sugestão: guardar data e hora (`LocalDateTime`) na `Solicitacao`, ou usar `LinkedHashMap` no repository |

Pontos que não são erro de execução, mas não batem com a documentação:

| ID | O que foi visto | Onde |
| --- | --- | --- |
| P01 | A `Configuracao` ainda aplica `mod 26` nas chaves da Afim e recusa `a` par ou 13. Com o alfabeto de 62, deveria aplicar `mod 62` e recusar par ou 31. Hoje o sistema usa só 312 das 1.860 chaves | `Util/Configuracao`, `.env.example` ([ADR-084](ADRs/ADR-084.md)) |
| P02 | A promoção grava no log o CPF e o cargo novo, mas não o cargo anterior, que a RN10 pede | `GerenciarUsuariosService.promoverUsuario` |
| P03 | `EDITOU_USUARIO` existe no `AcaoLog`, mas a edição de usuário não grava log | `GerenciarUsuariosService.editar` ([ADR-080](ADRs/ADR-080.md)) |
| P04 | A tela de listar resíduos ainda pergunta "ordem de cadastro ou de código", mas com a AVL as duas mostram pelo código | `TelaCadastroResiduoTerminal` ([ADR-078](ADRs/ADR-078.md)) |

---

## Testes possíveis que ainda não foram feitos

| Teste | Por que ainda não | Quando fazer |
| --- | --- | --- |
| Sessão expira em 30 dias (RN06) | A sessão de 30 dias ainda não foi implementada | Junto com a sessão |
| Dados continuam depois de reiniciar, no banco (RNF03) | O banco ainda não entrou; hoje é JSON | Quando o banco entrar ([ADR-065](ADRs/ADR-065.md)) |
| Mensagens claras na interface (RNF01) | A interface web ainda não existe | Na etapa do Thymeleaf |
| Tema claro e escuro (RNF05) | Opcional, depende da interface | Se sobrar tempo |
| Dois funcionários pegando a fila ao mesmo tempo | O back-end roda num processo só, pelo terminal | Quando o servidor Spring entrar |
| Busca com muitos resíduos (desempenho, RNF04) | Com poucos dados a diferença não aparece | Opcional, para mostrar a AVL |
| Bairro sem caminho até nenhum ponto | Os 19 bairros são todos ligados no grafo atual | Se um bairro novo entrar sem arestas |
