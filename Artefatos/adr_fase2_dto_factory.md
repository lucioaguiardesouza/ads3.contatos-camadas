# Architecture Decision Record (ADR): Fase 2 - DTOs e Inversão de Controle (IoC)

## Título
**ADR-007**: Desacoplamento da Camada de View com [DependencyFactory](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/DependencyFactory.java#17-58) e `Data Transfer Objects (DTO)`.

## Status e Data
**Status:** Aceito e Implementado
**Data:** 14 de Março de 2026

## Contexto e Problema
Apesar da implementação anterior da Arquitetura em Camadas (Controller, Service, Repository), os formulários Swing ([Form_Cadastro](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/pages/Form_Cadastro.java#24-367), [Form_Listagem](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/pages/Form_Listagem.java#23-395)) ainda sofriam de alto acoplamento:
1.  **Acoplamento Físico:** A View precisava instanciar suas dependências explicitamente com `new ContatoController()`. Isso impossibilitava a injeção em massa, testes mockados e sobrecarregava as telas com decisões de infraestrutura.
2.  **Vazamento de Domínio:** O [Form_Cadastro](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/pages/Form_Cadastro.java#24-367) injetava diretamente os inputs do usuário dentro da Entidade Mestra do Domínio ([Contato](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/domains/Contato.java#19-30)), expondo as fraquezas da Entidade às imperfeições dos inputs de texto brutos que vinham das textboxes visuais, burlando os limites das camadas.

## Decisões Arquiteturais e Refatorações Realizadas

### 1. Inversão de Controle (IoC) via DependencyFactory
Criamos a classe base [DependencyFactory](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/DependencyFactory.java#17-58) na raiz do projeto. Esta atua como um repositório Singleton e provedor centralizado de Injeção de Dependências.
As Views não instanciam mais seus controladores. Em vez disso, pedem ao roteador central suas interfaces: `DependencyFactory.getContatoCadastroController();`.
**Por que?** Esse isolamento imita a espinha dorsal de frameworks pesados como *Spring Framework*, tirando o peso do Roteamento e Inicialização (Wiring) da camada gráfica.

### 2. Adoção de Java Records para DTOs
Isolamos o trânsito de dados entre a UI e a Controladora de Formulários criando o pacote `dtos` e o objeto [ContatoCadastroDTO](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/dtos/ContatoCadastroDTO.java#10-16) usando o novo recurso Imutável `record` do Java 14+.
As textboxes recolhem as `Strings` da tela e empacotam neste form simplório, enviando-o limpo pelo barramento `contatoController.criar()`.
**Por que?** Isso impede a violação dos objetos da regra de negócio (DDD - Domain Driven Design). É o Controller que realiza o *Parsing* traduzindo o pacote neutro (DTO) para o Objeto Rico (Entidade [Contato](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/domains/Contato.java#19-30)) antes de passá-lo ao [Service](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/services/ContatoService.java#20-123).

## Consequências
### Positivas:
*   **Encapsulamento Total:** Telas de Swing agora desconhecem qual Banco de Dados, Validadores ou Serviços elas usam. Só conversam com Interfaces de Contrato.
*   **Expansibilidade:** Facilita muito a adição no futuro de `Senha`, `Checkbox de Aceite`, dados que muitas vezes existem na Tela (e portanto ficarão contidos no DTO) e que não necessariamente fazem parte das Regras do Contato que descerá para o Arquivo TXT.
*   **Qualidade Sênior:** Transformamos o paradigma do projeto de *Código de Script Procedural* para *Engenharia OO de Classes Enxutas (SOLID)*.
