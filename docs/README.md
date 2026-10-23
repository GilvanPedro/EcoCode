# Registro de decisões de arquitetura (ADRs) - EcoCode v1.0

Decisões da versão 1.0 do projeto, como está no commit "Adicionando versao 1.0"
(2026-10-06), antes da refatoração em camadas.

Estes ADRs foram **reconstruídos a partir do código**. O que o código faz está descrito
com precisão e foi conferido rodando o programa. Os motivos de cada decisão foram
deduzidos do código e do enunciado da Missão 02: o grupo deve revisar a seção
"Contexto" de cada um e corrigir onde o motivo real tiver sido outro.

| ADR | Decisão | Status |
| --- | --- | --- |
| [001](001-aplicacao-console-java-maven.md) | Aplicação de terminal em Java com Maven, sem dependências | Aceita |
| [002](002-cadastro-centralizado-em-uma-classe.md) | Concentrar tela, dados e geração de código na classe `Cadastro` | Aceita |
| [003](003-armazenamento-em-arraylist.md) | Guardar os resíduos numa `ArrayList` em memória | Aceita |
| [004](004-codigo-gerado-a-partir-do-tipo.md) | Gerar o código de forma que `código mod 5` caia na categoria do tipo | Aceita |
| [005](005-codigo-como-string-de-6-digitos.md) | Representar o código como `String` de 6 dígitos com zeros à esquerda | Aceita |
| [006](006-tipo-como-texto-livre.md) | Receber o tipo do resíduo como texto livre, reconhecido por sinônimos | Aceita |
| [007](007-classificador-estatico.md) | Classificar com uma classe utilitária estática | Aceita |
| [008](008-registros-iniciais-no-construtor.md) | Carregar 12 registros de exemplo no construtor do `Cadastro` | Aceita |

## Estrutura da versão 1.0

```text
src/main/java/
├── Main.java                      menu do terminal
├── Cadastro/Cadastro.java         telas, lista de resíduos, geração de código, seed
├── Classificador/Classificador.java  tipo -> resto, código -> resto -> categoria
└── Resíduo/Residuos.java          entidade (código, tipo, quantidade)
```
