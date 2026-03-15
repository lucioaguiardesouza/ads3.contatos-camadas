# Plano de Refatoração (Padrões 2026)

Este plano detalha a implementação das melhorias sugeridas no relatório de validação técnica para alinhar o projeto aos padrões de mercado de 2026.

## Proposed Changes

### [Componente: Visão (Pages)]
Centralização da estética e simplificação do código das janelas.

#### [NEW] [BasePage.java](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/pages/BasePage.java)
Criação de uma classe base que estende `JFrame` para:
- Executar `ThemeManager.setupTheme()` no construtor.
- Prover métodos utilitários para aplicar estilos FlatLaf.
- Centralizar o comportamento de `setLocationRelativeTo(null)`.

#### [MODIFY] [Form_Cadastro.java](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/pages/Form_Cadastro.java)
- Alterar para estender `BasePage`.
- Remover chamadas redundantes ao [ThemeManager](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/util/ThemeManager.java#13-74).
- Substituir `SwingWorker` por Virtual Threads para persistência assíncrona.

#### [MODIFY] [Form_Listagem.java](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/pages/Form_Listagem.java)
- Alterar para estender `BasePage`.
- Remover chamadas redundantes ao [ThemeManager](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/util/ThemeManager.java#13-74).
- Substituir `SwingWorker` por Virtual Threads para carregamento de dados.

---

### [Componente: Domínio e Infraestrutura (Exceptions & Repositories)]
Robustez no tratamento de erros e eficiência no I/O.

#### [MODIFY] Interfaces (`IContatoController`, `IContatoService`, `IContatoRepository`)
- Substituir `throws Exception` por `throws CoreException` (ou sub-exceções específicas).

#### [MODIFY] [ContatoRepository.java](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/repositories/ContatoRepository.java)
- Padronizar o lançamento de exceções.
- Garantir que métodos de leitura/escrita não silenciem erros críticos.

---

## Verification Plan

### Automated Tests
- Verificar se o projeto compila sem erros de tipo após a mudança das interfaces.
- Testar a execução manual para garantir que o tema ainda é aplicado corretamente via `BasePage`.

### Manual Verification
- Inserir um contato e verificar se o feedback de erro/sucesso continua funcional (agora via Virtual Threads).
- Listar contatos e verificar a fluidez da UI.
