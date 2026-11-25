# Grafo dos bairros de Goiânia

> Como o EcoCode representa a cidade para encontrar o ponto de coleta mais próximo.

| | |
| --- | --- |
| **Projeto** | EcoCode, Projeto Integrador |
| **Equipe** | Gilvan Pedro, Sidney Emanuel, Guilherme Scarcela, João Paulo e Dários Suess |
| **Última atualização** | 25/11/2026 |
| **Decisões relacionadas** | [ADR-048](ADRs/ADR-048.md), [ADR-056](ADRs/ADR-056.md), [ADR-067](ADRs/ADR-067.md), [ADR-068](ADRs/ADR-068.md), [ADR-069](ADRs/ADR-069.md), [ADR-070](ADRs/ADR-070.md), [ADR-072](ADRs/ADR-072.md) |

## Sumário

1. [Para que serve](#para-que-serve)
2. [Os 19 bairros](#os-19-bairros)
3. [Os ecopontos](#os-ecopontos)
4. [As 40 arestas](#as-40-arestas)
5. [Como as distâncias são medidas](#como-as-distâncias-são-medidas)
6. [Como está no código](#como-está-no-código)
7. [Distâncias esperadas para testar o Dijkstra](#distâncias-esperadas-para-testar-o-dijkstra)
8. [Limitações](#limitações)

## Para que serve

Para indicar o ponto de coleta mais próximo (RF04), o sistema precisa saber o caminho entre o
bairro do usuário e o bairro de cada ponto. Goiânia tem cerca de 600 bairros, então o grupo
escolheu 15 bairros âncora espalhados pela cidade e ligou só os vizinhos
([ADR-056](ADRs/ADR-056.md)). Depois entraram os 4 bairros dos ecopontos da Comurg que não
estavam entre os âncora ([ADR-072](ADRs/ADR-072.md)). O algoritmo de Dijkstra vai percorrer
esse grafo e achar a menor distância ([ADR-048](ADRs/ADR-048.md)).

- **Vértices:** 19 bairros (15 âncora e 4 dos ecopontos).
- **Arestas:** 40 ligações entre bairros vizinhos.
- **Peso:** distância em quilômetros.
- **Não direcionado:** a distância de A até B é a mesma de B até A.

## Os 19 bairros

O número de ligações é quantas arestas saem de cada bairro. Os 4 últimos são os bairros dos
ecopontos.

| Bairro | Ligações | Vizinhos |
| --- | :---: | --- |
| Setor Central | 6 | Oeste, Leste Universitário, Campinas, Bueno, Marista, Jaó |
| Setor Bueno | 6 | Central, Oeste, Marista, Jardim América, Pedro Ludovico, Parque Amazônia |
| Cidade Jardim | 6 | Jardim Curitiba, Jardim América, Campinas, Campos Dourados, Jardim São José, Parque Eldorado Oeste |
| Setor Campinas | 5 | Central, Oeste, Cidade Jardim, Jardim Curitiba, Jardim Guanabara |
| Setor Marista | 5 | Central, Oeste, Bueno, Jardim Goiás, Pedro Ludovico |
| Parque Amazônia | 5 | Pedro Ludovico, Jardim América, Bueno, Faiçalville, Campos Dourados |
| Setor Oeste | 4 | Central, Campinas, Bueno, Marista |
| Setor Leste Universitário | 4 | Central, Jardim Novo Mundo, Jardim Goiás, Jaó |
| Jardim Goiás | 4 | Marista, Leste Universitário, Pedro Ludovico, Jardim Novo Mundo |
| Setor Pedro Ludovico | 4 | Marista, Parque Amazônia, Bueno, Jardim Goiás |
| Setor Jaó | 4 | Jardim Guanabara, Central, Leste Universitário, Jardim Novo Mundo |
| Jardim América | 4 | Bueno, Parque Amazônia, Cidade Jardim, Faiçalville |
| Jardim Curitiba | 4 | Cidade Jardim, Campinas, Jardim Guanabara, Jardim São José |
| Jardim Novo Mundo | 3 | Leste Universitário, Jaó, Jardim Goiás |
| Jardim Guanabara | 3 | Jaó, Campinas, Jardim Curitiba |
| Setor Faiçalville | 3 | Parque Amazônia, Jardim América, Campos Dourados |
| Residencial Campos Dourados | 4 | Faiçalville, Parque Amazônia, Cidade Jardim, Parque Eldorado Oeste |
| Jardim São José | 3 | Parque Eldorado Oeste, Cidade Jardim, Jardim Curitiba |
| Parque Eldorado Oeste | 3 | Jardim São José, Cidade Jardim, Campos Dourados |

Todo bairro tem pelo menos 3 ligações, e o grafo é conexo: de qualquer bairro se chega a
qualquer outro.

## Os ecopontos

Os pontos de coleta do EcoCode são os 5 ecopontos da Comurg que recebem eletroeletrônicos.
Cada um fica num vértice do grafo.

| Ecoponto | Endereço | Vértice do grafo |
| --- | --- | --- |
| Jardim Guanabara II | Rua GB-5 c/ Rua GB-6 | Jardim Guanabara (já era âncora) |
| Setor Faiçalville | Av. Nadra Bufaiçal c/ Av. Madri | Setor Faiçalville |
| Residencial Campos Dourados | R. São João Del Rei, APM 07 | Residencial Campos Dourados |
| Jardim São José | R. Frei Nazareno Confaloni c/ R. Irmã Maria Bernarda | Jardim São José |
| Parque Eldorado Oeste | Rua Elo 43 | Parque Eldorado Oeste |

Faiçalville e Campos Dourados ficam no sudoeste, abaixo do Jardim América e do Parque
Amazônia. Jardim São José e Parque Eldorado Oeste ficam colados no extremo oeste, perto do
Conjunto Vera Cruz. A ligação Eldorado Oeste–Campos Dourados fecha um caminho por fora, sem
obrigar tudo a passar pelo Cidade Jardim; ela ficou porque deu 19,8 km, abaixo do limite de
20 km combinado na ADR-072.

## As 40 arestas

Todas as distâncias são de carro, medidas pelo Gilvan no Google Maps com o mesmo critério
([ADR-070](ADRs/ADR-070.md)). O peso usado no grafo é o **km**. O tempo é só informativo: muda
com o trânsito e não entra no cálculo.

| # | Bairro A | Bairro B | km | Tempo de carro (min) |
| :---: | --- | --- | ---: | ---: |
| 1 | Setor Central | Setor Oeste | 3,6 | – |
| 2 | Setor Central | Setor Leste Universitário | 2,0 | – |
| 3 | Setor Central | Setor Campinas | 5,8 | – |
| 4 | Setor Central | Setor Bueno | 5,3 | – |
| 5 | Setor Oeste | Setor Campinas | 4,7 | – |
| 6 | Setor Oeste | Setor Bueno | 2,5 | – |
| 7 | Setor Leste Universitário | Jardim Novo Mundo | 4,4 | – |
| 8 | Setor Marista | Setor Bueno | 2,4 | – |
| 9 | Setor Marista | Jardim Goiás | 4,6 | – |
| 10 | Setor Marista | Setor Pedro Ludovico | 2,4 | – |
| 11 | Setor Bueno | Jardim América | 2,4 | – |
| 12 | Setor Pedro Ludovico | Parque Amazônia | 4,8 | – |
| 13 | Jardim América | Parque Amazônia | 4,5 | – |
| 14 | Cidade Jardim | Jardim Curitiba | 14,2 | – |
| 15 | Setor Jaó | Jardim Guanabara | 5,1 | – |
| 16 | Setor Central | Setor Marista | 4,7 | 19 |
| 17 | Setor Central | Setor Jaó | 5,6 | 12 |
| 18 | Setor Oeste | Setor Marista | 3,4 | 14 |
| 19 | Setor Leste Universitário | Jardim Goiás | 2,7 | 7 |
| 20 | Setor Leste Universitário | Setor Jaó | 6,0 | 14 |
| 21 | Setor Bueno | Setor Pedro Ludovico | 3,1 | 13 |
| 22 | Jardim Goiás | Setor Pedro Ludovico | 4,6 | 13 |
| 23 | Setor Bueno | Parque Amazônia | 5,5 | 19 |
| 24 | Jardim América | Cidade Jardim | 5,0 | 13 |
| 25 | Setor Campinas | Cidade Jardim | 4,7 | 16 |
| 26 | Setor Campinas | Jardim Curitiba | 12,1 | 25 |
| 27 | Setor Jaó | Jardim Novo Mundo | 7,4 | 14 |
| 28 | Jardim Goiás | Jardim Novo Mundo | 4,6 | 10 |
| 29 | Jardim Guanabara | Setor Campinas | 12,2 | 26 |
| 30 | Jardim Guanabara | Jardim Curitiba | 16,8 | 29 |
| 31 | Setor Faiçalville | Parque Amazônia | 5,6 | 12 |
| 32 | Setor Faiçalville | Jardim América | 7,0 | 21 |
| 33 | Setor Faiçalville | Residencial Campos Dourados | 9,4 | 13 |
| 34 | Residencial Campos Dourados | Parque Amazônia | 15,3 | 30 |
| 35 | Residencial Campos Dourados | Cidade Jardim | 17,4 | 36 |
| 36 | Jardim São José | Parque Eldorado Oeste | 2,6 | 7 |
| 37 | Jardim São José | Cidade Jardim | 13,9 | 30 |
| 38 | Jardim São José | Jardim Curitiba | 21,2 | 35 |
| 39 | Parque Eldorado Oeste | Cidade Jardim | 12,3 | 26 |
| 40 | Parque Eldorado Oeste | Residencial Campos Dourados | 19,8 | 34 |

Arestas 1 a 30 medidas em 24/11/2026; arestas 31 a 40 (bairros dos ecopontos) medidas em
25/11/2026. Total do grafo: 40 arestas, todas do Google Maps.

**O que mudou em relação às estimativas antigas.** Das 15 arestas que eram estimadas (linha
reta × 1,3, do [ADR-069](ADRs/ADR-069.md)), as maiores diferenças foram Jardim Goiás–Jardim
Novo Mundo (de 8,0 para 4,6 km, porque a ligação é direta pela região da Av. L) e as duas
ligações do Jardim Guanabara com Campinas e Jardim Curitiba (de 8,5 para 12,2 km e de 12,5
para 16,8 km). Foi esse tipo de diferença que motivou o critério único.

## Como as distâncias são medidas

Um critério só, para todas as arestas ([ADR-070](ADRs/ADR-070.md)):

1. Abrir o Google Maps em **Rotas**, no modo **carro**.
2. Digitar origem e destino como "Nome do bairro, Goiânia" (por exemplo, "Setor Bueno,
   Goiânia"), sem endereço específico. O Maps usa o ponto que ele marca para o bairro.
3. Anotar a distância da **primeira rota sugerida**, em km, com uma casa decimal.
4. Medir num sentido só (A até B). O grafo usa o mesmo valor nos dois sentidos.
5. Anotar a data da medição.

**Conferência anterior.** Com a versão mista do grafo, o Gilvan mediu no Google Maps a
distância do Setor Negrão de Lima até vários bairros e comparou com o grafo. A ordem geral
bateu. Vale repetir essa conferência agora que as 40 estão medidas.

## Como está no código

| Classe | Pacote | Papel |
| --- | --- | --- |
| `Bairro` | `Domain` | Enum com os 19 bairros; cada valor tem uma `descricao` para exibição ("Setor Leste Universitário") |
| `Grafo<T>` | `Estrutura` | Grafo genérico não direcionado, guardado como lista de adjacência num `Map<T, Map<T, Double>>` |
| `GrafoBairros` | `Util` | Monta o `Grafo<Bairro>` com as 40 arestas desta página |

```text
Map<T, Map<T, Double>>
 Setor Central  ->  { Setor Oeste: 3.6, Setor Leste Universitário: 2.0, ... }
 Setor Oeste    ->  { Setor Central: 3.6, Setor Campinas: 4.7, ... }
```

- Cada aresta é gravada nos dois sentidos, porque o grafo não é direcionado.
- O `Grafo<T>` recusa **peso negativo** (o Dijkstra não funciona com ele) e **laço** (aresta
  de um bairro para ele mesmo).
- O Dijkstra ainda não foi implementado. As tabelas da próxima seção servem para testar quando ele for.
- Os 4 bairros dos ecopontos entram no enum `Bairro` como `SETOR_FAICALVILLE`,
  `RESIDENCIAL_CAMPOS_DOURADOS`, `JARDIM_SAO_JOSE` e `PARQUE_ELDORADO_OESTE`
  ([ADR-072](ADRs/ADR-072.md)). As linhas estão em `codigo/Bairro-novos.txt`, e as 40
  chamadas `adicionarAresta` em `codigo/arestas-GrafoBairros.txt`.

## Distâncias esperadas para testar o Dijkstra

Menores caminhos calculados com as 40 arestas finais (19 bairros). Quando o Dijkstra estiver
pronto, rode a partir dessas origens e compare. Se o resultado for diferente, há erro no
algoritmo ou em alguma aresta.

> Somar `double` pode dar `16.799999999999997` em vez de `16.8`. No teste, compare com uma
> folga (por exemplo, diferença menor que 0,01) ou arredonde para uma casa.

Os 4 bairros novos não mudaram nenhum caminho entre os 15 bairros antigos: as distâncias entre
eles continuam as mesmas da versão com 30 arestas.

### A partir do Setor Central

| Destino | Menor distância (km) | Caminho |
| --- | ---: | --- |
| Setor Leste Universitário | 2,0 | Central → Leste Universitário |
| Setor Oeste | 3,6 | Central → Oeste |
| Setor Marista | 4,7 | Central → Marista |
| Jardim Goiás | 4,7 | Central → Leste Universitário → Jardim Goiás |
| Setor Bueno | 5,3 | Central → Bueno |
| Setor Jaó | 5,6 | Central → Jaó |
| Setor Campinas | 5,8 | Central → Campinas |
| Jardim Novo Mundo | 6,4 | Central → Leste Universitário → Jardim Novo Mundo |
| Setor Pedro Ludovico | 7,1 | Central → Marista → Pedro Ludovico |
| Jardim América | 7,7 | Central → Bueno → Jardim América |
| Cidade Jardim | 10,5 | Central → Campinas → Cidade Jardim |
| Jardim Guanabara | 10,7 | Central → Jaó → Jardim Guanabara |
| Parque Amazônia | 10,8 | Central → Bueno → Parque Amazônia |
| Setor Faiçalville | 14,7 | Central → Bueno → Jardim América → Faiçalville |
| Jardim Curitiba | 17,9 | Central → Campinas → Jardim Curitiba |
| Parque Eldorado Oeste | 22,8 | Central → Campinas → Cidade Jardim → Eldorado Oeste |
| Residencial Campos Dourados | 24,1 | Central → Bueno → Jardim América → Faiçalville → Campos Dourados |
| Jardim São José | 24,4 | Central → Campinas → Cidade Jardim → Jardim São José |

### A partir do Jardim Curitiba

Bom para testar caminhos longos, com várias arestas seguidas.

| Destino | Menor distância (km) | Caminho |
| --- | ---: | --- |
| Setor Campinas | 12,1 | Curitiba → Campinas |
| Cidade Jardim | 14,2 | Curitiba → Cidade Jardim |
| Jardim Guanabara | 16,8 | Curitiba → Jardim Guanabara |
| Setor Oeste | 16,8 | Curitiba → Campinas → Oeste |
| Setor Central | 17,9 | Curitiba → Campinas → Central |
| Jardim América | 19,2 | Curitiba → Cidade Jardim → Jardim América |
| Setor Bueno | 19,3 | Curitiba → Campinas → Oeste → Bueno |
| Setor Leste Universitário | 19,9 | Curitiba → Campinas → Central → Leste Universitário |
| Setor Marista | 20,2 | Curitiba → Campinas → Oeste → Marista |
| Jardim São José | 21,2 | Curitiba → Jardim São José |
| Setor Jaó | 21,9 | Curitiba → Jardim Guanabara → Jaó |
| Setor Pedro Ludovico | 22,4 | Curitiba → Campinas → Oeste → Bueno → Pedro Ludovico |
| Jardim Goiás | 22,6 | Curitiba → Campinas → Central → Leste Universitário → Jardim Goiás |
| Parque Amazônia | 23,7 | Curitiba → Cidade Jardim → Jardim América → Parque Amazônia |
| Parque Eldorado Oeste | 23,8 | Curitiba → Jardim São José → Eldorado Oeste |
| Jardim Novo Mundo | 24,3 | Curitiba → Campinas → Central → Leste Universitário → Jardim Novo Mundo |
| Setor Faiçalville | 26,2 | Curitiba → Cidade Jardim → Jardim América → Faiçalville |
| Residencial Campos Dourados | 31,6 | Curitiba → Cidade Jardim → Campos Dourados |

### A partir do Jardim São José

Uma origem bem na ponta oeste, para testar o caminho inteiro atravessando a cidade.

| Destino | Menor distância (km) | Caminho |
| --- | ---: | --- |
| Parque Eldorado Oeste | 2,6 | São José → Eldorado Oeste |
| Cidade Jardim | 13,9 | São José → Cidade Jardim |
| Setor Campinas | 18,6 | São José → Cidade Jardim → Campinas |
| Jardim América | 18,9 | São José → Cidade Jardim → Jardim América |
| Jardim Curitiba | 21,2 | São José → Jardim Curitiba |
| Setor Bueno | 21,3 | São José → Cidade Jardim → Jardim América → Bueno |
| Residencial Campos Dourados | 22,4 | São José → Eldorado Oeste → Campos Dourados |
| Setor Oeste | 23,3 | São José → Cidade Jardim → Campinas → Oeste |
| Parque Amazônia | 23,4 | São José → Cidade Jardim → Jardim América → Parque Amazônia |
| Setor Marista | 23,7 | São José → Cidade Jardim → Jardim América → Bueno → Marista |
| Setor Central | 24,4 | São José → Cidade Jardim → Campinas → Central |
| Setor Pedro Ludovico | 24,4 | São José → Cidade Jardim → Jardim América → Bueno → Pedro Ludovico |
| Setor Faiçalville | 25,9 | São José → Cidade Jardim → Jardim América → Faiçalville |
| Setor Leste Universitário | 26,4 | São José → Cidade Jardim → Campinas → Central → Leste Universitário |
| Jardim Goiás | 28,3 | São José → Cidade Jardim → Jardim América → Bueno → Marista → Jardim Goiás |
| Setor Jaó | 30,0 | São José → Cidade Jardim → Campinas → Central → Jaó |
| Jardim Guanabara | 30,8 | São José → Cidade Jardim → Campinas → Jardim Guanabara |
| Jardim Novo Mundo | 30,8 | São José → Cidade Jardim → Campinas → Central → Leste Universitário → Jardim Novo Mundo |

### Ecoponto mais próximo de cada bairro

Para testar o `pontoMaisProximo`: rode o Dijkstra a partir do bairro e escolha o ecoponto
com a menor distância.

| Bairro do usuário | Ecoponto mais próximo | km | 2º mais próximo (km) |
| --- | --- | ---: | --- |
| Setor Central | Jardim Guanabara | 10,7 | Faiçalville (14,7) |
| Setor Oeste | Faiçalville | 11,9 | Jardim Guanabara (14,3) |
| Setor Leste Universitário | Jardim Guanabara | 11,1 | Faiçalville (16,7) |
| Setor Campinas | Jardim Guanabara | 12,2 | Faiçalville (16,6) |
| Setor Bueno | Faiçalville | 9,4 | Jardim Guanabara (16,0) |
| Setor Marista | Faiçalville | 11,8 | Jardim Guanabara (15,4) |
| Jardim Goiás | Jardim Guanabara | 13,8 | Faiçalville (15,0) |
| Setor Pedro Ludovico | Faiçalville | 10,4 | Jardim Guanabara (17,8) |
| Setor Jaó | Jardim Guanabara | 5,1 | Faiçalville (20,3) |
| Jardim Novo Mundo | Jardim Guanabara | 12,5 | Faiçalville (19,6) |
| Jardim América | Faiçalville | 7,0 | Campos Dourados (16,4) |
| Parque Amazônia | Faiçalville | 5,6 | Campos Dourados (15,0) |
| Cidade Jardim | Faiçalville | 12,0 | Parque Eldorado Oeste (12,3) |
| Jardim Curitiba | Jardim Guanabara | 16,8 | Jardim São José (21,2) |
| Jardim Guanabara | Jardim Guanabara | 0,0 | Faiçalville (25,4) |
| Setor Faiçalville | Faiçalville | 0,0 | Campos Dourados (9,4) |
| Residencial Campos Dourados | Campos Dourados | 0,0 | Faiçalville (9,4) |
| Jardim São José | Jardim São José | 0,0 | Parque Eldorado Oeste (2,6) |
| Parque Eldorado Oeste | Parque Eldorado Oeste | 0,0 | Jardim São José (2,6) |

### Casos que pegam erro

- **Sem aresta direta:** de Central até Pedro Ludovico não existe aresta direta, e o
  caminho pelo Marista (4,7 + 2,4 = 7,1) ganha do caminho pelo Bueno (5,3 + 3,1 = 8,4).
- **Aresta direta que perde:** existe aresta Parque Amazônia–Campos Dourados (15,3), mas o
  caminho pelo Faiçalville é menor (5,6 + 9,4 = 15,0). Se o Dijkstra devolver 15,3, ele parou
  cedo demais. O mesmo acontece em Curitiba–Eldorado Oeste: não há aresta direta, e o caminho
  pelo São José (23,8) ganha do caminho pelo Cidade Jardim (14,2 + 12,3 = 26,5).
- **Saída por bairros diferentes:** de Curitiba até Jaó, o caminho
  pelo Guanabara (2 arestas, 21,9) ganha do caminho por Campinas e Central (3 arestas,
  12,1 + 5,8 + 5,6 = 23,5).
- **Empate de distância:** saindo do Central, Marista e Jardim Goiás ficam os dois a 4,7 km.
  Saindo do Curitiba, Guanabara e Oeste ficam os dois a 16,8 km. Saindo do São José, Central e
  Pedro Ludovico ficam a 24,4 km, e Guanabara e Novo Mundo a 30,8 km. A distância precisa
  bater; a ordem entre os empatados pode variar.
- **Ecoponto quase empatado:** no Cidade Jardim, Faiçalville (12,0) ganha do Eldorado Oeste
  (12,3) por só 300 metros. Bom teste para o `pontoMaisProximo`.
- **Caminho com 5 arestas:** de São José até Jardim Goiás e até Novo Mundo.

## Limitações

- A distância é de bairro a bairro, pelo ponto que o Google Maps marca para cada um; a casa
  do usuário pode ficar a alguns quilômetros desse ponto.
- O Google Maps pode sugerir rotas diferentes em outro dia ou horário.
- Quem mora num bairro fora da lista é tratado como se estivesse num dos 19. Ainda está em
  aberto se haverá uma tabela ligando cada bairro de Goiânia ao seu bairro âncora ou se o
  usuário escolhe direto entre os 15 ([ADR-056](ADRs/ADR-056.md)).
- Trocar uma distância só exige mudar o número no `GrafoBairros`.
