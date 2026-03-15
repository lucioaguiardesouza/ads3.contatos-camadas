# Architecture Decision Record (ADR): Correções Pós-Implementação da Fase 2

## Título
**ADR-008**: Resolução de Conflitos de Compilação e Ajustes de Tipagem DTO / Factory.

## Status e Data
**Status:** Aceito e Implementado
**Data:** 14 de Março de 2026

## Contexto e Problema
Após a implementação inicial do padrão *Factory* ([DependencyFactory](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/DependencyFactory.java#17-58)) e *Data Transfer Objects* ([ContatoCadastroDTO](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/dtos/ContatoCadastroDTO.java#10-16)) referentes à Fase 2, o compilador do `Maven` reportou falhas no processo de compilação (`BUILD FAILURE`). Esses erros ocorreram devido a inconsistências de assinaturas entre as interfaces e implementações, tipagens incorretas herdadas do padrão antigo na camada de view e erros sintáticos residuais. 

A IDE apresentou incontáveis linhas vermelhas (`String cannot be resolved`, etc.) principalmente devido a interrupções no reconhecimento do JDK 21 provocadas pelo estado quebrado do código, somado ao cache desatualizado.

## Decisões Arquiteturais e Refatorações Realizadas

Realizamos uma varredura nas classes acusadas pelo console do Maven e aplicamos as seguintes correções:

### 1. Injeção de Dependência no Serviço ([ContatoService](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/services/ContatoService.java#20-128))
A [DependencyFactory](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/DependencyFactory.java#17-58) tentava instanciar o [ContatoService](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/services/ContatoService.java#20-128) passando as dependências do repositório e validação via construtor, porém a classe de serviço possuía apenas o construtor vazio genérico (que criava as classes com `new`).
*   **Ação:** Criamos o construtor `public ContatoService(IContatoRepository repository, IContatoValidation validation)` para permitir que o IoC Container (a Factory) injetasse as instâncias concretas corretamente.

### 2. Conversão de Tipos na UI ([Form_Cadastro](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/pages/Form_Cadastro.java#25-368))
O botão de "Gravar" ainda estava instanciando o objeto de domínio (Model) [Contato](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/domains/Contato.java#19-30) bruto ao invés do novo envelope protegido.
*   **Ação:** Alteramos a variável `dto` para utilizar de fato o [ContatoCadastroDTO](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/dtos/ContatoCadastroDTO.java#10-16) puro: `ContatoCadastroDTO dto = new ContatoCadastroDTO(nomeNovo, email, telefone);`, respeitando o isolamento estrito de Frontend x Backend.

### 3. Ajuste de Assinatura na Controladora ([ContatoController](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/controllers/ContatoController.java#10-68))
O método de edição [alterar](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/services/IContatoService.java#16-17) recebia dois parâmetros (`originalName` e `dto`), porém repassava incorretamente a exata mesma quantidade de parâmetros para a camada de Serviço Subjacente (`this.service.alterar(originalName, contato)`), violando as interfaces do Serviço que esperam estritamente o novo objeto único.
*   **Ação:** Limpeza do envio; agora o Controller invoca corretamente `this.service.alterar(contato);`.

### 4. Limpeza Sintática (SwingWorker)
Durante a alteração para requisições assíncronas em background (evitando o travamento da Janela Gráfica), chaves `}` extras de fechamento de blocos corromperam o corpo da classe GUI.
*   **Ação:** Eliminamos o bloco fantasma restaurando a hierarquia perfeita e o Lifecycle do [doInBackground()](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/pages/Form_Listagem.java#364-373).

## Consequências
### Positivas:
*   **Build 100% Funcional:** O Maven validou todas as classes do projeto retornando verde (`BUILD SUCCESS`).
*   **Alinhamento de Contratos:** Todas as interfaces [IContatoService](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/services/IContatoService.java#14-27) e [IContatoCadastroController](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/controllers/IContatoCadastroController.java#6-10) agora possuem suas implementações plenamente convergentes nos Mapeamentos DTO -> Domínio.
*   **Restabelecimento da IDE:** Com a eliminação dos gargalos de compilação cruciais, as ferramentas de Lints das IDEs conseguirão reconstruir a Build Path do Java 21, eliminando alarmes falsos subjacentes.
