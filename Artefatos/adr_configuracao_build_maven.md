# Architecture Decision Record (ADR): Resoluções de Build e Encapsulamento de Exceções

## Título
**ADR-006**: Configuração Explícita do Compilador Maven e Tratamento Fino de Exceções de Infraestrutura (I/O).

## Status e Data
**Status:** Aceito e Implementado
**Data:** 14 de Março de 2026

## Contexto e Problema
Após a implementação de metodologias funcionais com Java 21 (Streams API, Lambdas) e da delegação estrita de regras de tela com Exceções Customizadas ([ExceptionValidationRegra](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/exceptions/ExceptionValidationRegra.java#16-29)), enfrentamos dois problemas residuais durante o uso local da aplicação:
1. **Falsos Positivos Visuais na IDE:** Leitores de servidor de linguagem (como o do Apache NetBeans ou o Java Extension Pack do VS Code) marcavam as classes com sublinhados vermelhos falsos indicando incompatibilidade. Isto ocorreu pois a diretriz genérica `<maven.compiler.release>` do [pom.xml](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/pom.xml) não comunicava com clareza nativa as `sources` (códigos-fonte) forçando ferramentas legadas a inferirem erradamente "Java 1.5" ou "Java 19" para o Workspace, acusando Lambdas ou uso do operador Diamond `<>` como ilegais.
2. **"Unreported Exception" no Lifecycle do Maven:** Diferente da interface do painel Swing que aceitava exceções implícitas para repassar ao `JOptionPane`, ao rodarmos a régua cega do `mvn clean compile` pelo terminal CLI nativo, os métodos base [contatoExiste()](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/services/IContatoService.java#22-23) em [ContatoRepository](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/repositories/ContatoRepository.java#22-183) reclamavam o vazamento de exceções filhas de verificação de arquivos (Checked Exceptions de I/O) diretamente no escopo do [ContatoValidation](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/validations/ContatoValidation.java#18-90).

## Decisões Arquiteturais e Refatorações Realizadas

### 1. Injeção Explícita do Maven Compiler Plugin
Alterou-se formalmente as propriedades `<properties>` e `<plugins>` do arquivo [pom.xml](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/pom.xml). Em vez de apenas possuir a propriedade de `release:21`, injetamos abertamente a dependência principal do `maven-compiler-plugin` delimitando forçadamente as tags `<source>21</source>` e `<target>21</target>`. 
**Por que?** Esse ajuste informa as camadas visuais do NetBeans/Eclipse sobre o ambiente de execução real e desabilita marcações de sintaxe de edições antigas do Java, devolvendo o *feedback* verde e limpo na IDE do usuário.

### 2. Wrapping (Encapsulamento) de Checked Exceptions 
Ao invés de estragar a Assinatura Limpa dos métodos da interface [IContatoValidation](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/validations/IContatoValidation.java#15-24) com `throws Exception`, foi estabelecido que interações de checagem com a base de dados em disco que geram Checked Exceptions puros devem ser contidas o quanto antes. Ocorreu o *wrapping*:
As chamadas de `this.repository.contatoExiste()` nos métodos [validaRegraInserir](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/validations/IContatoValidation.java#18-19) e [validaRegraAlterar](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/validations/IContatoValidation.java#19-20) ganharam redomas de `try-catch`. 
**Mapeamento:** Se a busca estourar uma falha genérica de I/O, o catch represcinde e a transcreve para um formato rico que o Controller e UI compreendam:
`throw new ExceptionValidationRegra("io", "Erro ao verificar base", "Erro Crítico", "error_icon");`

## Consequências
### Positivas:
* **DX (Developer Experience):** As ferramentas de edição puderam ser sincronizadas ("Resolve Project Problems - Reload POM"). A eliminação de ruídos falsos de lint na IDE devolveu ao programador conforto analítico para continuar as atualizações da classe no futuro.
* **Segurança de Build:** O CI/CD agora obedece rigorosamente a premissa de sintaxe do Java 21 em qualquer máquina que compilar os pacotes JAR.
* **Abstração Limpa de Domínio:** A tela Swing e os Controllers se desvinculam do dever de saber gerenciar erros puros de texto e arquivo I/O. Quaisquer falhas no acesso à memória são repassadas pelas classes `Exceptions` moldadas com visualização amigável perante ao usuário do software (com popups contendo a logo de Erro Crítico).
