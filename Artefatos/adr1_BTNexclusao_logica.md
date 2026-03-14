# Architecture Decision Record (ADR): Exclusão Lógica (Soft Delete) de Contatos

## Título
**ADR-001**: Implementação de Exclusão Lógica (Soft Delete) de Contatos via Processamento Assíncrono com Arquivo Texto.

## Status e Data
**Status:** Aceito
**Data:** 13 de Março de 2026

## Contexto e Problema
O sistema `ads3.contatos-camadas` necessita permitir que o usuário remova contatos da interface da agenda (botão "Excluir" em [Form_Listagem](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/pages/Form_Listagem.java#23-394)). No entanto, a exclusão física direta da linha no arquivo [.txt](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/agenda.txt) apresenta desvantagens:
1. Perda permanente de histórico dos dados cadastrados.
2. Risco de perda acidental sem possibilidade de reversão por parte do usuário.
3. Necessidade de reescrever e deletar blocos de texto a todo instante, gerando processamento obstrutivo de I/O na Thread principal de Interface (EDT - Event Dispatch Thread).

## Decisão Arquitetural e Tecnologias
Foi decidido **não excluir** fisicamente os contatos do meio de persistência (arquivo [agenda.txt](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/agenda.txt)), mas sim a adoção de um padrão de **Exclusão Lógica (Soft Delete)**, onde o registro recebe apenas uma alteração de estado interno. Toda a orquestração do fluxo foi desenhada baseando-se em uma arquitetura em 4 camadas. 

Além disso, para lidar com as limitações de interface, adotou-se o processamento assíncrono (Threads secundárias).

### 1. Camada de Apresentação (View): [Form_Listagem.java](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/pages/Form_Listagem.java)
**Ação:** O evento do botão Recupera a linha selecionada da tabela de grid e pergunta ao usuário (`JOptionPane.showConfirmDialog`) para evitar remoções indesejadas.
**Tecnologia-Chave (`SwingWorker`):** Como a exclusão vai rodar no disco, a chamada do botão [btnExcluirActionPerformed](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/pages/Form_Listagem.java#269-314) delega a execução para uma classe abstrata nativa chamada `SwingWorker`.
*   O trabalho pesado roda na Thread [doInBackground()](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/pages/Form_Listagem.java#363-372).
*   O resultado volta de forma segura para a interface gráfica em [done()](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/pages/Form_Listagem.java#297-311) através de chamadas encapsuladas em `SwingUtilities.invokeLater()`, sem travar a tela (UX suave).

### 2. Camada Controlador: [ContatoController.java](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/controllers/ContatoController.java)
**Ação:** Implementa a Inversão de Controle recebendo as chamadas da View pelo contrato de Interface (`IContatoListaController.inativarPorNome`). Apenas atua como ponte isolando as exceções HTTP ou de interface da regra pura; repassando de imediato para a camada Service (`this.service.excluir(name)`).

### 3. Camada de Regras de Negócios (Service): [ContatoService.java](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/services/ContatoService.java)
**Ação:** Localizamos e sobrecarregamos as regras. A interface inicial pelo nome [excluir(String name)](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/services/ContatoService.java#58-66) invoca o método de busca, e em seguida chama a versão interna [excluir(Contato contato)](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/services/ContatoService.java#58-66).
**Nota de Implementação (Bug Identificado):** O código na linha 54 (no caso de false na validação da existência do contato na hora de excluir) lança uma exceção informando `"Este contato já existe cadastrado"`. O texto dessa Exception deveria refletir "Contato não encontrado para exclusão", mas a arquitetura já protege perfeitamente o Repository de receber um objeto vazio. Em caso de sucesso de validação, chama-se o `this.repository.desativar(contato)`.

### 4. Camada de Persistência (Repository): [ContatoRepository.java](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/repositories/ContatoRepository.java)
**Ação:** Implementa a escrita final usando pacotes do `java.nio`. 
*   O método [desativar(contato)](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/repositories/ContatoRepository.java#48-54) transforma o objeto modificando a coluna de status (csv index 3) de `"ativo"` para `"inativo"` via [toCsvLine(contato, "inativo")](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/repositories/ContatoRepository.java#99-105).
*   Utiliza a verificação linha a linha em memória e grava o novo estado do arquivo utilizando reescrita (`StandardOpenOption.TRUNCATE_EXISTING`). Desta forma, o arquivo [agenda.txt](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/agenda.txt) sempre preserva as três colunas base + quarta coluna indicando a descontinuação do registro, não afetando os Id's e metadados anteriores do repositório de texto.

## Consequências
### Positivas:
*   **Recuperação de Dados:** O sistema retém todos os cadastros historicamente efetuados (permitindo futura funcionalidade de lixeira / recuperação / auditoria).
*   **Interface Fluida:** O uso da thread local de `SwingWorker` garante que em bases de 1.000 ou 1.000.000 de linhas de texto, a janela do Swing não trave ou apresente estado "Não respondendo" (_Not Responding_).
*   **Desacoplamento Rigoroso:** Graças ao Controller as Views desconhecem por absoluto se a exclusão foi um `UPDATE` relacional SQL ou um Replace no arquivo [.txt](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/agenda.txt). 

### Negativas / Pontos de Atenção:
*   A busca iterativa pelo nome a cada tentativa de exclusão ([alteraRegistro](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/repositories/ContatoRepository.java#131-152) dentro de `repository`) exige ler todo o arquivo [agenda.txt](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/agenda.txt) para um `List<String>`. A complexidade nesse caso cresce na ordem O(N) e poderia gerar lentidão para gigabytes de textos, justificando no futuro a migração da respectiva implementação para Banco de Dados usando `java.sql` (JDBC) ou ORM.
