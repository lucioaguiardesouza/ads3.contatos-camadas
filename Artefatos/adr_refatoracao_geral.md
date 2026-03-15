# Architecture Decision Record (ADR): Refatorações Arquiteturais (Streams, IoC, Regex e Exceptions)

## Título
**ADR-005**: Aplicação de Padrões Modernos Java (IoC, Streams API, Custom Exceptions) e Correção DRY de Responsabilidades.

## Status e Data
**Status:** Aceito e Implementado
**Data:** 14 de Março de 2026

## Contexto e Problema
A base do projeto `ads3.contatos-camadas` apresentava bons conceitos embrionários de Model-View-Controller em formato Desktop, mas conservava "smells" e características de legado do Java 6/7.
Especificamente, notou-se:
1.  **Fuga de Domínio (Lançamentos puros de Exception):** Classes estourando apenas `throw new Exception` bloqueando o Controller de mostrar popups bonitos para regras de negócios (apenas apresentava "Erro inesperado").
2.  **Repetição (DRY - Don't Repeat Yourself):** O `ContatoController.buscarPorNome` refez na mão toda a listagem num array manual igual ao do Service.
3.  **Abuso de Memória e Processamento (Imperativo O(N)):** O Java 21 é embarcado no projeto, mas classes vitais de `repository` rodavam `Files.readAllLines` e métodos do [Service](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/services/ContatoService.java#20-123) filtravam contatos abrindo loops sequenciais de `for (Contato c: list)`.
4.  **Insegurança de Inputs:** Os e-mails e telefones salvos aceitavam apenas Strings cruas, sem regex.

## Decisões Arquiteturais e Refatorações Realizadas

### 1. Robustecimento de Regras (Regex, Null-Safety)
Na classe [ContatoValidation](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/validations/ContatoValidation.java#18-78), que antes constava com métodos vazios estourando `UnsupportedOperationException`, foram aplicadas Expressões Regulares compiladas (Pattern) para E-MAIL e TELEFONE. 
*   **Mail:** Adotou-se o padrão `^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$`. 
*   **Telefone:** Implementamos exclusão de hífens via `replaceAll()` e garantia de DDI+DDD através da pattern de dígitos puros de 10-11 números.

### 2. Adoção Impositiva das Exceções Customizadas 
O projeto já desfrutava da presença inteligente das classes formadas do tipo [CoreException](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/exceptions/CoreException.java#16-30) (como o [ValidationException](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/exceptions/ValidationException.java#3-7) e o belo [BusinessException](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/exceptions/BusinessException.java#3-7)). 
*   **Regras:** Retiramos o padrão `throws Exception` do Service e do Validation. Agora uma Falha no E-Mail levanta uma [ExceptionValidationCampo](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/exceptions/ExceptionValidationCampo.java#16-30) rica portando Título, Ícone e Mensagem.
*   **Integração na Tela:** A classe Swing da Visão [Form_Cadastro.java](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/pages/Form_Cadastro.java) repassa de volta diretamente para o JOptionPane instanciar os títulos configurados, avisando ao Usuário sobre "Campos Inválidos" ou "Regras Quebradas".

### 3. Implementação da Injeção de Dependências (DIP / IoC) no Controlador
Foi adicionado ao [ContatoController](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/controllers/ContatoController.java#9-65) a opção de instanciar não apenas a classe dura, mas receber um [IContatoService](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/services/IContatoService.java#14-27) através de parâmetros construtores, validando a letra "D" do SOLID (Inverter o controle e depender de abstrações).

### 4. Transição Radical para a Streams API e NIO 2 Lazy Eval
Substituimos a grande maioria de blocos iterativos (estruturas [for(...)](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/pages/Form_Cadastro.java#154-157) clássicas), adotando os recursos modernos baseados no processamento funcional em tubo `Stream`:
*   *Memory Leak e Lentidão Sanada:* No [ContatoRepository](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/repositories/ContatoRepository.java#22-183), trocamos o uso predatório e OOM propenso do `readAllLines()` para a construção performática Lazy de `java.nio` via `Files.lines()`. Em arquivos com 1 GB de texto, o programa carregará gradualmente em Buffer apenas o index filtrado via Streams API (`.filter(...)`) com total fechamento limpo através do _try-with-resources_.
*   *Service Elegante:* Filtros de nomes incompletos por `contains()` rodam de forma encadeada via Pipeline lambdas (`filter().findFirst().orElse(null)`), gerando expressividade.
*   *Deslocamento DRY:* No controller o método [buscarPorNome](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/services/ContatoService.java#77-87) deletou 10 linhas de loops obsoletos repassando a lógica nua (`return this.service.listarPorNome(name)`).

## Consequências
### Positivas:
*   Redução drástica da "pegada de código" (Lines of Code) e verbosidade nas Listas do [ContatoService](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/services/ContatoService.java#20-123).
*   Feedback Rico em UI: O usuário na cadeira final percebe os popups informando exatamente *O que, Quando, e Qual Campo* deu erro, graças as Views lerem as exceções corretas sem abortar o formulário.
*   As proteções de memória em Streams provém sustentabilidade empresarial para a aplicação lidar com arquivos e fluxos enormes de IO sem engasgos do *Garbage Collector*.
