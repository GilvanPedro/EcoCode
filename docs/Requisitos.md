## Requisitos funcionais

- RF01 Manter e autenticar usuários
- RF02 Manter resíduos eletrônicos (com classificação por categoria)
- RF03 Manter pontos de coleta (funcionário)
- RF04 Consultar ponto de coleta mais próximo
- RF05 Solicitar descarte
- RF06 Acompanhar solicitação de descarte
- RF07 Gerenciar fila de solicitações (funcionário)
- RF08 Registrar log das ações sensíveis (solicitações, pontos de coleta, resíduos, usuários)
- RF09 Consultar log de atividades (funcionário)
- RF10 Alterar cargo de usuário pelo CPF (gerente).

## Requisitos não funcionais

- RNF01 Interface intuitiva
- RNF02 Segurança das informações (senhas com hash, dados locais cifrados, controle de acesso por perfil)
- RNF03 Persistência dos dados em banco de dados
- RNF04 Desempenho: buscas rápidas usando estrutura em memória
- RNF05 Personalização de tema claro e escuro (opcional)

## Regras de negócio

- RN01 O CPF do usuário deve ser válido (dígitos verificadores) e único no sistema.
- RN02 O e-mail deve ter formato válido e ser único no sistema.
- RN03 O código do resíduo é gerado automaticamente pelo sistema, sem edição manual.
- RN04 O usuário só pode cancelar uma solicitação enquanto ela estiver Pendente.
- RN05 Apenas funcionários acessam a fila, o cadastro de pontos de coleta e o log.
- RN06 A sessão de qualquer usuário expira 30 dias após o login, exigindo nova autenticação.
- RN07 Registros de log não podem ser editados nem excluídos.
- RN08 Apenas o gerente pode alterar o cargo de outro usuário.
- RN09 Um usuário não pode alterar o próprio cargo.
- RN10 Toda alteração de cargo é registrada no log.