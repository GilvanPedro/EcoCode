# Documentação do EcoCode

Esta pasta guarda tudo o que explica o EcoCode por fora do código: o que o sistema precisa
fazer, por que ele foi feito do jeito que foi, como foi testado e de onde vieram os dados do
mapa de Goiânia. O README da raiz do projeto conta o que o sistema é e como rodar. Aqui fica o
resto.

| | |
| --- | --- |
| **Projeto** | EcoCode, Projeto Integrador (Grupo 05) |
| **Equipe** | Gilvan Pedro, Sidney Emanuel, Guilherme Scarcela, João Paulo e Dários Suess |
| **Última atualização** | 28/11/2026 |
| **Entrega** | 05/12/2026 |
| **Apresentação** | 07/12/2026 |

## O que tem aqui

| Arquivo ou pasta | O que é | Quando abrir |
| --- | --- | --- |
| [`Requisitos.md`](Requisitos.md) | Requisitos funcionais (RF01 a RF10), não funcionais (RNF01 a RNF05) e regras de negócio (RN01 a RN18) | Para saber o que o sistema tem que fazer e qual regra cada parte cumpre |
| [`ADR.md`](ADR.md) | Índice geral das decisões de arquitetura: linha do tempo, temas, decisões que mudaram e pendências | Para achar uma decisão ou montar a parte de arquitetura do relatório |
| [`ADRs/`](ADRs/) | Os 94 ADRs, um arquivo por decisão, e o modelo [`ADR-template.md`](ADRs/ADR-template.md) | Para ler uma decisão inteira, com as alternativas e as consequências |
| [`Testes.md`](Testes.md) | Testes do fluxo principal e do banco, com critério de aceitação, resultado, bugs e pendências | Para o entregável de testes e para conferir o que já foi validado |
| [`GrafoBairros.md`](GrafoBairros.md) | Os 19 bairros, as 40 arestas com a distância de carro do Google Maps e as distâncias esperadas do Dijkstra | Para explicar o ponto de coleta mais próximo |
| [`Referencias/`](Referencias/) | Enunciado do PI e materiais de apoio em PDF | Para conferir o que a disciplina pede |
| [`Desing.md`](Desing.md) | Reservado para o relatório de Design de Software | Ainda vazio |
| [`Diagramas.md`](Diagramas.md) | Reservado para os diagramas (classes, banco, casos de uso) | Ainda vazio |

## Por onde começar

Depende de quem está lendo:

- **Quem vai corrigir ou avaliar:** comece pelo [README da raiz](../README.md), depois
  [`Requisitos.md`](Requisitos.md) e [`Testes.md`](Testes.md). Os ADRs explicam o porquê de
  cada escolha.
- **Quem vai mexer no código:** leia a parte de arquitetura do [README da raiz](../README.md) e
  os ADRs do tema que vai mexer, pelo [índice por tema](ADR.md#adrs-por-tema).
- **Quem vai apresentar:** a [linha do tempo das ADRs](ADR.md#linha-do-tempo) conta a história do
  projeto, e os destaques do [`Testes.md`](Testes.md) (AVL, Dijkstra, Cifra Afim, fila depois de
  reiniciar) são bons exemplos para mostrar ao vivo.

## Como os documentos se ligam

```text
Requisitos.md  ──  o que o sistema precisa fazer (RF, RNF, RN)
      │
      ├──>  ADRs/  ──  como e por que cada parte foi feita     (citam os RF, RNF e RN)
      │       └──>  ADR.md   índice de todas
      │
      ├──>  Testes.md  ──  prova de que cada regra funciona    (cada teste cita o RF ou RN)
      │
      └──>  GrafoBairros.md  ──  os dados do grafo usados no ponto mais próximo (RF04)
```

Um exemplo do caminho completo: a regra RN13 diz que um resíduo só pode estar numa solicitação
aberta por vez. O [ADR-071](ADRs/ADR-071.md) explica como isso virou o status do resíduo, e os
testes T16, T17 e BD20 do [`Testes.md`](Testes.md) mostram o sistema recusando o pedido
repetido.

## Situação dos documentos

| Documento | Situação |
| --- | --- |
| Requisitos | Completo, revisado em 25/11 |
| ADRs | Atualizados até o ADR-094 (28/11). Próximo número livre: ADR-095 |
| Testes | Atualizado em 28/11, com os testes no banco. Falta rodar a `Teste` inteira sobre o banco |
| Grafo dos bairros | Completo (19 bairros, 40 arestas) |
| Design de Software | A fazer |
| Diagramas | A fazer: diagrama de classes, diagrama do banco (as 6 tabelas do [ADR-087](ADRs/ADR-087.md)) e casos de uso |
| Script do banco | Ainda não está no repositório. Sugestão: `docs/banco/EcoCode.sql` |

## Regras para escrever aqui

- Português, em frases simples, como se estivesse explicando para um colega.
- Datas em DD/MM/AAAA no texto e AAAA-MM-DD no campo "Data" dos ADRs.
- Nome dos integrantes sempre completo: Gilvan Pedro, Sidney Emanuel, Guilherme Scarcela, João
  Paulo e Dários Suess.
- Decisão nova vira ADR novo, a partir do [modelo](ADRs/ADR-template.md). ADR aceito não é
  reescrito: o antigo passa a "Substituída por ADR-XXX". Depois, acrescente a linha no
  [`ADR.md`](ADR.md).
- Teste novo entra no [`Testes.md`](Testes.md) com cenário, tipo (sucesso ou rejeição),
  critério de aceitação e resultado.
- Ao citar uma regra, use o código dela (RF04, RN13), para dar para procurar.
