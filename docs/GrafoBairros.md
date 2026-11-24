# Grafo dos bairros de Goiânia

> Como o EcoCode representa a cidade para encontrar o ponto de coleta mais próximo.

| | |
| --- | --- |
| **Projeto** | EcoCode, Projeto Integrador |
| **Equipe** | Gilvan Pedro, Sidney Emanuel, Guilherme Scarcela, João Paulo e Dários Suess |
| **Última atualização** | 24/11/2026 |
| **Decisões relacionadas** | [ADR-048](ADRs/ADR-048.md), [ADR-056](ADRs/ADR-056.md), [ADR-067](ADRs/ADR-067.md), [ADR-068](ADRs/ADR-068.md), [ADR-069](ADRs/ADR-069.md), [ADR-070](ADRs/ADR-070.md) |

## Sumário

1. [Para que serve](#para-que-serve)
2. [Os 15 bairros](#os-15-bairros)
3. [As 30 arestas](#as-30-arestas)
4. [Como as distâncias são medidas](#como-as-distâncias-são-medidas)
5. [Como está no código](#como-está-no-código)
6. [Distâncias esperadas para testar o Dijkstra](#distâncias-esperadas-para-testar-o-dijkstra)
7. [Limitações](#limitações)

## Para que serve

Para indicar o ponto de coleta mais próximo (RF04), o sistema precisa saber o caminho entre o
bairro do usuário e o bairro de cada ponto. Goiânia tem cerca de 600 bairros, então o grupo
escolheu 15 bairros âncora espalhados pela cidade e ligou só os vizinhos
([ADR-056](ADRs/ADR-056.md)). O algoritmo de Dijkstra vai percorrer esse grafo e achar a menor
distância ([ADR-048](ADRs/ADR-048.md)).

- **Vértices:** os 15 bairros.
- **Arestas:** 30 ligações entre bairros vizinhos.
- **Peso:** distância em quilômetros.
- **Não direcionado:** a distância de A até B é a mesma de B até A.

## Os 15 bairros

O número de ligações é quantas arestas saem de cada bairro.

| Bairro | Ligações | Vizinhos |
| --- | :---: | --- |
| Setor Central | 6 | Oeste, Leste Universitário, Campinas, Bueno, Marista, Jaó |
| Setor Bueno | 6 | Central, Oeste, Marista, Jardim América, Pedro Ludovico, Parque Amazônia |
| Setor Campinas | 5 | Central, Oeste, Cidade Jardim, Jardim Curitiba, Jardim Guanabara |
| Setor Marista | 5 | Central, Oeste, Bueno, Jardim Goiás, Pedro Ludovico |
| Setor Oeste | 4 | Central, Campinas, Bueno, Marista |
| Setor Leste Universitário | 4 | Central, Jardim Novo Mundo, Jardim Goiás, Jaó |
| Jardim Goiás | 4 | Marista, Leste Universitário, Pedro Ludovico, Jardim Novo Mundo |
| Setor Pedro Ludovico | 4 | Marista, Parque Amazônia, Bueno, Jardim Goiás |
| Setor Jaó | 4 | Jardim Guanabara, Central, Leste Universitário, Jardim Novo Mundo |
| Jardim Novo Mundo | 3 | Leste Universitário, Jaó, Jardim Goiás |
| Jardim América | 3 | Bueno, Parque Amazônia, Cidade Jardim |
| Parque Amazônia | 3 | Pedro Ludovico, Jardim América, Bueno |
| Cidade Jardim | 3 | Jardim Curitiba, Jardim América, Campinas |
| Jardim Curitiba | 3 | Cidade Jardim, Campinas, Jardim Guanabara |
| Jardim Guanabara | 3 | Jaó, Campinas, Jardim Curitiba |

Todo bairro tem pelo menos 3 ligações, e o grafo é conexo: de qualquer bairro se chega a
qualquer outro.

## As 30 arestas

Todas as distâncias são de carro, medidas pelo Gilvan no Google Maps com o mesmo critério
([ADR-070](ADRs/ADR-070.md)). O peso usado no grafo é o **km**. O tempo, anotado nas últimas
15 medições, é só informativo: muda com o trânsito e não entra no cálculo.

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

Medições concluídas em 24/11/2026. Total do grafo: 30 arestas, todas do Google Maps.

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
bateu. Vale repetir essa conferência agora que as 30 estão medidas.

## Como está no código

| Classe | Pacote | Papel |
| --- | --- | --- |
| `Bairro` | `Domain` | Enum com os 15 bairros; cada valor tem uma `descricao` para exibição ("Setor Leste Universitário") |
| `Grafo<T>` | `Estrutura` | Grafo genérico não direcionado, guardado como lista de adjacência num `Map<T, Map<T, Double>>` |
| `GrafoBairros` | `Util` | Monta o `Grafo<Bairro>` com as 30 arestas desta página |

```text
Map<T, Map<T, Double>>
 Setor Central  ->  { Setor Oeste: 3.6, Setor Leste Universitário: 2.0, ... }
 Setor Oeste    ->  { Setor Central: 3.6, Setor Campinas: 4.7, ... }
```

- Cada aresta é gravada nos dois sentidos, porque o grafo não é direcionado.
- O `Grafo<T>` recusa **peso negativo** (o Dijkstra não funciona com ele) e **laço** (aresta
  de um bairro para ele mesmo).
- O Dijkstra ainda não foi implementado. As tabelas da próxima seção servem para testar quando ele for.

## Distâncias esperadas para testar o Dijkstra

Menores caminhos calculados com as 30 arestas finais. Quando o Dijkstra estiver pronto, rode a
partir dessas duas origens e compare. Se o resultado for diferente, há erro no algoritmo ou
em alguma aresta.

> Somar `double` pode dar `16.799999999999997` em vez de `16.8`. No teste, compare com uma
> folga (por exemplo, diferença menor que 0,01) ou arredonde para uma casa.

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
| Jardim Curitiba | 17,9 | Central → Campinas → Jardim Curitiba |

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
| Setor Jaó | 21,9 | Curitiba → Jardim Guanabara → Jaó |
| Setor Pedro Ludovico | 22,4 | Curitiba → Campinas → Oeste → Bueno → Pedro Ludovico |
| Jardim Goiás | 22,6 | Curitiba → Campinas → Central → Leste Universitário → Jardim Goiás |
| Parque Amazônia | 23,7 | Curitiba → Cidade Jardim → Jardim América → Parque Amazônia |
| Jardim Novo Mundo | 24,3 | Curitiba → Campinas → Central → Leste Universitário → Jardim Novo Mundo |

### Casos que pegam erro

- **Sem aresta direta:** de Central até Pedro Ludovico não existe aresta direta, e o
  caminho pelo Marista (4,7 + 2,4 = 7,1) ganha do caminho pelo Bueno (5,3 + 3,1 = 8,4).
- **Saída por bairros diferentes:** de Curitiba até Jaó, o caminho
  pelo Guanabara (2 arestas, 21,9) ganha do caminho por Campinas e Central (3 arestas,
  12,1 + 5,8 + 5,6 = 23,5).
- **Empate de distância:** saindo do Central, Marista e Jardim Goiás ficam os dois a 4,7 km.
  Saindo do Curitiba, Guanabara e Oeste ficam os dois a 16,8 km. A distância precisa bater;
  a ordem entre os empatados pode variar.
- **Caminho com 4 arestas:** de Curitiba até Pedro Ludovico, Jardim Goiás e Novo Mundo.

## Limitações

- A distância é de bairro a bairro, pelo ponto que o Google Maps marca para cada um; a casa
  do usuário pode ficar a alguns quilômetros desse ponto.
- O Google Maps pode sugerir rotas diferentes em outro dia ou horário.
- Quem mora num bairro fora da lista é tratado como se estivesse num dos 15. Ainda está em
  aberto se haverá uma tabela ligando cada bairro de Goiânia ao seu bairro âncora ou se o
  usuário escolhe direto entre os 15 ([ADR-056](ADRs/ADR-056.md)).
- Trocar uma distância só exige mudar o número no `GrafoBairros`.
