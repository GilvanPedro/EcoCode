## Requisitos funcionais

- RF01 Manter e autenticar usuários
- RF02 Manter resíduos eletrônicos (com classificação por categoria)
- RF03 Manter pontos de coleta (funcionário)
- RF04 Consultar ponto de coleta mais próximo
- RF05 Solicitar descarte
- RF06 Acompanhar solicitação de descarte
- RF07 Gerenciar fila de solicitações (funcionário)

## Requisitos não funcionais

- RNF01 Interface intuitiva
- RNF02 Segurança das informações (senha criptografada, controle de acesso por perfil)
- RNF03 Persistência dos dados em banco de dados
- RNF04 Disponibilidade: funcionar sem conexão com o banco e sincronizar depois
- RNF05 Desempenho: buscas rápidas usando estrutura em memória
- RNF06 Personalização de tema claro e escuro (opcional)

## Regras de negócio

- RN01 O CPF do usuário deve ser válido (dígitos verificadores) e único no sistema.
- RN02 O e-mail deve ter formato válido e ser único no sistema.
- RN03 O código do resíduo é gerado automaticamente pelo sistema, sem edição manual.
- RN04 O usuário só pode cancelar uma solicitação enquanto ela estiver Pendente.
- RN05 Apenas funcionários acessam a fila e o cadastro de pontos de coleta.