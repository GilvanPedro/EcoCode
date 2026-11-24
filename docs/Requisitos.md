# Requisitos do Sistema EcoCode

> Sistema de gestão de descarte e reciclagem de resíduos eletrônicos em Goiânia.

| | |
|---|---|
| **Projeto** | EcoCode, Projeto Integrador |
| **Equipe** | Gilvan Pedro, Sidney Emanuel, Guilherme Scarcela, João Paulo e Dários Suess |
| **Última atualização** | 24/11/2026 |

## Sumário

1. [Requisitos Funcionais](#requisitos-funcionais)
2. [Requisitos Não Funcionais](#requisitos-não-funcionais)
3. [Regras de Negócio](#regras-de-negócio)

---

## Requisitos Funcionais

_O que o sistema deve fazer._

| Código | Nome |
|:---:|---|
| **RF01** | Manter e autenticar usuários |
| **RF02** | Manter resíduos eletrônicos |
| **RF03** | Manter pontos de coleta |
| **RF04** | Consultar o ponto de coleta mais próximo |
| **RF05** | Solicitar descarte |
| **RF06** | Acompanhar solicitação de descarte |
| **RF07** | Gerenciar fila de solicitações |
| **RF08** | Registrar ações sensíveis em log |
| **RF09** | Consultar log de atividades |
| **RF10** | Alterar cargo de usuário pelo CPF |

### RF01 · Manter e autenticar usuários

O sistema deve permitir o cadastro, a consulta, a atualização e a exclusão dos dados dos usuários. Qualquer pessoa pode se cadastrar como usuário comum, e apenas o gerente pode cadastrar usuários com outro cargo ou excluir contas. No login, o sistema deve verificar as credenciais informadas antes de liberar o acesso às funcionalidades disponíveis para o perfil de cada usuário.

### RF02 · Manter resíduos eletrônicos

O sistema deve permitir cadastrar, consultar e atualizar os registros de resíduos eletrônicos. Cada resíduo deve ser associado a uma categoria e receber um código gerado automaticamente pelo sistema.

### RF03 · Manter pontos de coleta

O sistema deve permitir que funcionários e gerentes cadastrem, consultem e atualizem os dados dos pontos de coleta. Cada ponto deve estar vinculado a um bairro cadastrado, e essas informações devem servir de base para a consulta do ponto mais próximo pelo usuário.

### RF04 · Consultar o ponto de coleta mais próximo

O sistema deve comparar o bairro do usuário com os bairros dos pontos de coleta cadastrados, calculando a menor distância entre eles. Em seguida, deve indicar o ponto mais próximo e exibir suas informações de localização.

### RF05 · Solicitar descarte

O sistema deve permitir que o usuário registre uma solicitação de descarte de resíduos eletrônicos. A solicitação deve ficar vinculada ao usuário que a criou e disponível na fila para atendimento.

### RF06 · Acompanhar solicitação de descarte

O sistema deve permitir que o usuário consulte suas solicitações e veja o status atual de cada uma. Quando uma solicitação estiver Pendente, o usuário também poderá cancelá-la.

### RF07 · Gerenciar fila de solicitações

O sistema deve permitir que funcionários e gerentes visualizem a fila de solicitações de descarte e consultem os detalhes de cada pedido. Eles devem poder atualizar o status da solicitação conforme o andamento do atendimento.

### RF08 · Registrar ações sensíveis em log

O sistema deve registrar as ações sensíveis relacionadas a usuários, resíduos, pontos de coleta e solicitações de descarte. O registro deve permitir identificar qual ação foi realizada, quem a executou e quando ela ocorreu.

### RF09 · Consultar log de atividades

O sistema deve disponibilizar aos funcionários e gerentes uma área de consulta aos registros de atividades. Nessa área, eles podem verificar as ações registradas e identificar os responsáveis por cada operação.

### RF10 · Alterar cargo de usuário pelo CPF

O sistema deve permitir que o gerente localize outro usuário pelo CPF e altere o cargo associado à conta. Antes de concluir a operação, deve verificar a permissão do gerente e impedir alterações no cargo da própria conta.

---

## Requisitos Não Funcionais

_Como o sistema deve se comportar._

| Código | Nome |
|:---:|---|
| **RNF01** | Interface intuitiva |
| **RNF02** | Segurança das informações |
| **RNF03** | Persistência em banco de dados |
| **RNF04** | Desempenho das buscas |
| **RNF05** | Personalização do tema (opcional) |

### RNF01 · Interface intuitiva

A interface deve apresentar menus, botões e campos com nomes claros para facilitar a navegação. Ações como cadastrar um resíduo e acompanhar uma solicitação devem exibir mensagens que indiquem o resultado da operação.

### RNF02 · Segurança das informações

As senhas devem ser armazenadas na forma de hash. O sistema deve verificar o perfil do usuário antes de permitir o acesso a funcionalidades restritas.

### RNF03 · Persistência em banco de dados

O sistema deve armazenar em banco de dados os registros de usuários, resíduos, pontos de coleta, solicitações e logs. Os registros devem permanecer disponíveis para consulta após o encerramento ou a reinicialização do sistema.

### RNF04 · Desempenho das buscas

O sistema deve usar estruturas de dados em memória para agilizar a localização de registros. Os dados usados nas buscas devem refletir as alterações realizadas no sistema para que os resultados permaneçam atualizados.

### RNF05 · Personalização do tema (opcional)

O sistema pode oferecer as opções de tema claro e escuro. Quando disponível, a configuração deve permitir que o usuário alterne entre os temas na própria interface.

---

## Regras de Negócio

_As condições e restrições que valem dentro das funcionalidades._

| Código | Nome |
|:---:|---|
| **RN01** | Validação e unicidade do CPF |
| **RN02** | Validação e unicidade do e-mail |
| **RN03** | Geração automática do código do resíduo |
| **RN04** | Cancelamento de solicitação pelo usuário |
| **RN05** | Acesso restrito a funcionários e gerentes |
| **RN06** | Expiração da sessão |
| **RN07** | Proteção dos registros de log |
| **RN08** | Permissão para alterar cargos |
| **RN09** | Bloqueio de alteração do próprio cargo |
| **RN10** | Registro de alterações de cargo |
| **RN11** | Bairros cadastrados |
| **RN12** | Cancelamento de solicitação pelo gerente |
| **RN13** | Uma solicitação ativa por resíduo |

### RN01 · Validação e unicidade do CPF

O sistema deve validar os dígitos verificadores do CPF informado no cadastro. Se o CPF for inválido ou já estiver associado a outro usuário, o cadastro deve ser impedido.

### RN02 · Validação e unicidade do e-mail

O sistema deve verificar se o e-mail informado possui formato válido. Cada endereço de e-mail só pode estar vinculado a um usuário no sistema.

### RN03 · Geração automática do código do resíduo

Ao cadastrar um resíduo eletrônico, o sistema deve gerar automaticamente o código que identifica o registro. Esse código não pode ser informado ou alterado manualmente.

### RN04 · Cancelamento de solicitação pelo usuário

O usuário só pode cancelar uma solicitação de descarte própria e enquanto ela estiver com o status Pendente. O sistema deve verificar o dono e o status atual antes de aceitar o cancelamento.

### RN05 · Acesso restrito a funcionários e gerentes

A fila de solicitações, o cadastro de pontos de coleta e a consulta ao log são áreas restritas a funcionários e gerentes. O sistema deve verificar o perfil do usuário antes de liberar o acesso a essas áreas.

### RN06 · Expiração da sessão

A sessão de qualquer usuário deve expirar 30 dias após o login. Após esse prazo, o usuário deve se autenticar novamente para acessar as funcionalidades que exigem login.

### RN07 · Proteção dos registros de log

Depois de gravado, um registro de log não pode ser editado nem excluído por nenhum usuário. As informações originais da ação devem ser preservadas para consulta.

### RN08 · Permissão para alterar cargos

Apenas usuários com perfil de gerente podem alterar o cargo de outro usuário. O sistema deve verificar essa permissão sempre que uma alteração de cargo for solicitada.

### RN09 · Bloqueio de alteração do próprio cargo

Nenhum usuário pode alterar o cargo da própria conta, inclusive quem possui o perfil de gerente. O sistema deve bloquear a operação quando a conta que solicita a alteração e a conta selecionada forem a mesma.

### RN10 · Registro de alterações de cargo

Toda alteração de cargo concluída deve gerar um registro no log. O registro deve identificar o gerente responsável, o usuário afetado, o cargo anterior, o novo cargo e a data da alteração.

### RN11 · Bairros cadastrados

O bairro do usuário e do ponto de coleta deve ser escolhido entre os bairros cadastrados no sistema. Esses bairros formam o grafo usado no cálculo da menor distância.

### RN12 · Cancelamento de solicitação pelo gerente

O gerente pode cancelar solicitações com status Pendente ou Em rota. Solicitações já Concluídas ou Canceladas não podem ser canceladas novamente.

### RN13 · Uma solicitação ativa por resíduo

Um resíduo com solicitação de descarte Pendente ou Em rota não pode receber outra solicitação e não aparece na lista de resíduos disponíveis para descarte. Se a solicitação for Cancelada, o resíduo volta para a lista; se for Concluída, ele já foi descartado e não volta.
