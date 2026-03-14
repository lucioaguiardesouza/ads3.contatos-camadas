# Architecture Decision Record (ADR): Implementação da Camada Service (Regras de Negócio)

## Título
**ADR-003**: Adoção de Camada Service Isolada para Encapsulamento de Regras de Negócio e Orquestração do Domínio.

## Status e Data
**Status:** Aceito
**Data:** 14 de Março de 2026

## Contexto e Problema
Com a remoção das lógicas da Interface Gráfica (View) para a camada Controller (ver `ADR-002`), o sistema evitava o problema de "_Smart UI_". Porém, as validações de campos (`nome` não nulo, formatação do `telefone`, validação se o usuário já existia) correram o risco de se aglomerarem dentro dos métodos do Controlador ([ContatoController](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/controllers/ContatoController.java#10-68)).
Se as regras de negócio ficassem atadas ao Controller, a manutenção e futuras reutilizações seriam comprometidas caso novas telas fossem adicionadas ou se a aplicação migrasse de plataforma UI. Além disso, o próprio Controller faria acesso direto ao banco de dados/arquivo de persistência. 

## Decisão Arquitetural
Foi decidido estruturar e delimitar um novo pacote `services`, em conjunto com a injeção da interface `IContatoService` sendo implementada pela classe principal **[ContatoService](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/services/ContatoService.java#20-125)**.

### 1. Responsabilidade Única (O "Cérebro" do Sistema)
*   A classe [ContatoService](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/services/ContatoService.java#20-125) tornou-se o coração lógico da aplicação. Nenhuma regra puramente comportamental ou estrutural dos dados (Domínio [Contato](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/domains/Contato.java#19-30)) reside fora dela ou de seus pacotes adjuntos (como o `validations`).
*   **Orquestração:** É papel do Service fazer o meio-campo entre o "O que o Controller pediu?" e "Como o Repository vai Salvar?". 

### 2. Adoção da Facade Oculta (Validations)
*   Para evitar que o Service em si se inflasse demais e se tornasse ilegível, optou-se também por adotar a injeção do objeto `IContatoValidation` via construtor (implementado no [ContatoValidation](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/validations/ContatoValidation.java#14-54)).
*   Metodologia adotada no `.inserir()` do Service: 
    1. Ação A: Validação das "propriedades puras" do campo (`validation.validaCampo(contato)`). Ex: Formatação de Email.
    2. Ação B: Validação do "domínio de negócio" (`validation.validaRegraInserir(contato)`). Ex: Este contato específico já existe na lista do repositório?
    3. Ação C: Execução da lógica final (Aprovação ou envio ao `Repository.inserir()`).

### 3. Independência de Infraestrutura Tecnológica
*   O pacote Service foi codificado para ser uma **Camada Pura Java (`POJO - Plain Old Java Objects`)**. A classe desconhece absolutamente se foi chamada via Java Desktop Swing (Eventos Mouse), Requisição Web HTTP (API REST/Spring) ou CLI (Console). 
*   Também desconhece as minúcias técnicas se debaixo dos panos existe um banco Postgres, MongoDB ou um `arquivo.txt`. O [ContatoService](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/services/ContatoService.java#20-125) lida apenas com objetos em memória Listas (`List<Contato>`).

## Consequências
### Positivas:
*   **Isolamento Tático:** Extremamente fácil de escrever testes unitários velozes focados nas Regras de Negócio sem necessitar rodar toda a aplicação (`JUnit` no Model, Validation e Service com exclusividade).
*   **Fácil Manutenção & Extensibilidade:** Se amanhã as leis da LGPD forçarem a exclusão sumária e física do contato excluído, apenas o [ContatoService](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/services/ContatoService.java#20-125) (ao chamar `.excluir()`) e a respectiva regra vão sofrer alteração. 

### Negativas / Pontos de Atenção:
*   Na atual implementação didática, o Controller e o Service não retornam mensagens detalhadas do [ContatoValidation](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/validations/ContatoValidation.java#14-54) nos métodos que deveriam englobar exceções de domínio; e sim retornam em booleano (`return this.repository.inserir(contato)`), transferindo em alguns momentos parte irrisória da decisão para a UI do Controller.
