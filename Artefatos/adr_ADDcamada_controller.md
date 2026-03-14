# Architecture Decision Record (ADR): Implementação da Camada Controller e Segregação de Interfaces (ISP)

## Título
**ADR-002**: Adoção de Camada Controller Isolada com Segregação de Interfaces para Desacoplamento de Telas (Views).

## Status e Data
**Status:** Aceito
**Data:** 14 de Março de 2026

## Contexto e Problema
Inicialmente, em aplicações Desktop Java Swing (fase monólito), é comum que toda a lógica de acesso a dados (banco ou txt), validações de consistência e retornos de negócio fiquem atadas diretamente dentro dos eventos dos botões dos `JFrames` (ex: [btnGravarActionPerformed](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/pages/Form_Cadastro.java#209-245), [btnExcluirActionPerformed](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/pages/Form_Listagem.java#269-314)). 

Isso gera várias dificuldades técnicas conhecidas como "_Fat Controller_" (ou "_Smart UI_"):
1.  **Impossibilidade de Reuso:** Se a aplicação futuramente tiver uma interface web ou mobile, todo o código do sistema está preso nas telas do Desktop.
2.  **Difícil Testabilidade (Unit Tests):** Não é possível testar a lógica do negócio sem abrir (renderizar) as janelas Swing gráficas na memória.
3.  **Complexidade e Acoplamento:** As telas (Views) detêm conhecimentos sobre arquivos, senhas, bancos de dados, o que quebra o Princípio da Responsabilidade Única (SRP - Single Responsibility Principle).

## Decisão Arquitetural e Tecnologias
Foi decidido estruturar a aplicação com o padrão arquitetural em Camadas (derivado fortemente do MVC), onde foi desenhado um pacote e uma classe exclusiva responsável pela orquestração do fluxo de telas: o [ContatoController.java](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/controllers/ContatoController.java).

### 1. View Responsável Apenas por Interações UI (Apresentação Pura)
*   **As classes [Form_Listagem](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/pages/Form_Listagem.java#23-394) e [Form_Cadastro](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/pages/Form_Cadastro.java#22-340)** tiveram toda a sua lógica pesada removida. Elas agem apenas "capturando cliques", "lendo valores digitados nos TextFields" e convertendo para o modelo Entidade / DTO ([Contato](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/domains/Contato.java#19-30)).
*   Qualquer decisão de negócio ou salvamento passou a ser "terceirizada" ao instanciar o Controller e acionar seus métodos sob a interface injetada localmente. Os `Forms` passam apenas a coordenar os estados gráficos (ex: habilitar e desabilitar inputs com o método [ajustaTela()](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/pages/Form_Cadastro.java#299-330) ou apresentar caixas do `JOptionPane`).

### 2. A Camada Controller (O Orquestrador)
* A classe **[ContatoController.java](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/controllers/ContatoController.java)** atua como único ponto de contato das Telas. Ela recebe as conversões prontas das Views (objetos simples do tipo [Contato](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/domains/Contato.java#19-30) ou Strings puras) e orquestra com a camada inferior de "Serviços". 
* Dessa forma, quem toma as decisões macro é o Controller, devolvendo para a tela uma Resposta padronizada, um objeto, coleções genéricas ou estourando `Exceptions` legíveis pelo usuário.

### 3. Segregação de Interface (ISP - SOLID Principles)
* Em vez de fornecer à tela de *Cadastro* um mega-controller contendo métodos de listagem de grid (o que representaria perigo e informações dispensáveis), adotou-se a segregação (interfaces especializadas).
* Foram criadas interfaces contratuais sob medida: **[IContatoCadastroController](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/controllers/IContatoCadastroController.java#5-9)** e **[IContatoListaController](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/controllers/IContatoListaController.java#7-14)**.
* O [Form_Cadastro](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/pages/Form_Cadastro.java#22-340) passa a exigir no seu construtor apenas permissões sobre métodos descritos em [IContatoCadastroController](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/controllers/IContatoCadastroController.java#5-9) (como `.criar()` e `.alterar()`).
* O [Form_Listagem](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/pages/Form_Listagem.java#23-394) assume métodos descritos em [IContatoListaController](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/controllers/IContatoListaController.java#7-14) (como `.listarTodos()`, `.inativarPorNome()`).
* **OBS:** Ambas as interfaces são implementadas pela mesma classe concreta [ContatoController](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/controllers/ContatoController.java#10-68), provando a eficiência dos contratos orientados à Objeto do Java.

## Consequências
### Positivas:
*   **Manutenibilidade:** Ao ocorrer problemas de leitura ou banco de dados amanhã, o desenvolvedor não precisa ir consertar arquivos da interface Java Swing visual, indo diretamente nos pacotes limpos de negócio e persistência.
*   **Segurança Tópica das Views:** Reduz-se drasticamente o risco de codificar manipulação acidental de métodos não essenciais em eventos de tela (Visto que um Form de Listagem não herda/acessa o método de Editar).
*   **Inversão de Controle e Injeção de Dependências:** O Controller permite flexibilidade; ao iniciar a tela, o desenvolvedor pode até repassar uma injeção `.mock()` deste controller via parâmetros do Construtor para realizar excelentes de Testes Unitários de Front.

### Negativas / Pontos de Atenção:
*   **Curva de Aprendizado e Sobrecarga de Arquivos (Boilerplate):** Adicionar um fluxo novo requer abrir e modificar a interface, o controller, e as telas, aumentando a quantidade de arquivos (Boilerplate code).
*   A classe [ContatoController](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/controllers/ContatoController.java#10-68) corre o risco de crescer em número excessivo de linhas (Tornado-se uma *God Class*) caso não seja dividida à medida que as regras de domínio do projeto tomem escala maior.
