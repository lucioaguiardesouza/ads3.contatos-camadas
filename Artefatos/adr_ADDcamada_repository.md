# Architecture Decision Record (ADR): Implementação da Camada Repository (Data Access Object / Port)

## Título
**ADR-004**: Encapsulamento de Acesso a Dados e Persistência Física em Arquivos TXT Utilizando o Padrão Repository e Java NIO.

## Status e Data
**Status:** Aceito
**Data:** 14 de Março de 2026

## Contexto e Problema
Toda aplicação CRUD precisa salvar e ler os dados. Pelo projeto tratar-se de uma fase didática que não utiliza Banco de Dados Relacionais ou Drivers (ex: JDBC, Hibernate, Spring Data), a persistência precisou ser codificada na mão utilizando I/O em Java (lendo e escrevendo blocos de texto no armazenamento local do usuário sob o arquivo fictício [agenda.txt](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/agenda.txt)).

Se lógicas como: "Como abrir um arquivo?", "Que codificação (encoding) usar?", ou "Como separar as colunas por Ponto-e-Vírgula?" residissem na camada *Service* ou nas *Views*, uma eventual troca de tecnologia para um Banco de Dados SQL de verdade exigiria reescrever o sistema quase do zero, quebrando o paradigma de Arquitetura em Camadas. 

## Decisão Arquitetural
Para isolar radicalmente o *"Lugar Onde Salvo"* e o *"Como Salvo"*, foi implementado o Padrão Repository (similar ao antigo *DAO - Data Access Object*).
Foram criadas a interface `IContatoRepository` e a classe concreta [ContatoRepository](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/repositories/ContatoRepository.java#22-186). Elas não conhecem NADA de Regras de Negócio, Validações ou Apresentação Gráfica. Conhecem apenas CRUD: [insereRegistro](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/repositories/ContatoRepository.java#120-130), [alteraRegistro](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/repositories/ContatoRepository.java#131-152), [linhasAtivas](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/repositories/ContatoRepository.java#153-170). 

### 1. Inversão de Dependências na Persistência
O [Service](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/services/ContatoService.java#20-125) injeta o contrato da interface ([IContatoRepository.java](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/repositories/IContatoRepository.java)). Assim, quando a aplicação migrar do arquivo [.txt](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/agenda.txt) para um Banco MySQL, basta construir o `ContatoRepositorySQL implements IContatoRepository`, alterando o driver sem quebrar as outras camadas do sistema. E foi por conta disso que os métodos do Contract devolvem instâncias de `List<Contato>` limpas, e não Arrays de Strings impuros vindas do arquivo.

### 2. Tratamento Interno da Biblioteca Java NIO (Non-blocking I/O)
A adoção da biblioteca mais recente e inteligente do Java: a árvore NIO 2 (`java.nio.file.Files`).
* **Tratamento da Fila IO:** Métodos como [insereRegistro](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/repositories/ContatoRepository.java#120-130) abrem, inserem o texto e fecham as _Stream Writers_ do sistema operacional em microsegundos com `StandardOpenOption.APPEND`, utilizando a constante de quebra de diretório cross-OS (`System.lineSeparator()`).
* **Confiabilidade Linear e UTF-8:** A escrita sempre carrega o charset `StandardCharsets.UTF_8` fixado na chamada de `Files.write(...)`. Previne a "corrupção" dos assentos e cedilhas na Agenda por falta de sincronia (onde usuários abrem em Editores Linux em EXT4 ou no Bloco de Notas Win10 / NTFS). 

### 3. Implementação Personalizada de Busca em Disco e Update
Em vez de utilizar soluções arcaicas do antigo pacote `java.io`, a arquitetura utiliza:
1. [linhasAtivas()](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/repositories/ContatoRepository.java#153-170): Filtra através do índice `;` e verifica no offset a posição `[3]` para deduzir o status _ativo_ ou _inativo_.
2. [alteraRegistro](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/repositories/ContatoRepository.java#131-152): Localizado na camada mais primitiva, carrega todas as linhas à memoria com o NIO 2, varre o cache comparando o array com a linha antiga da edição do Controller, e executa uma **sobrescrita em lote (Truncate)** limpa (`StandardOpenOption.TRUNCATE_EXISTING`).

## Consequências
### Positivas:
*   **Encapsulamento Total:** Para o sistema (Controllers, Windows, ou Services), é impossível diferenciar e notar que os dados estão sendo guardados num formato CSV primário (separado por `;`) de texto, ou num Banco em Cloud; os detalhes sujos e técnicos ficam retidos nas entranhas do [Repository](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/repositories/ContatoRepository.java#22-186). 
*   **Extrema Simplicidade Testável:** Uma rotina de teste automatizado pode instanciar o [ContatoRepository(Paths.get("teste_mock.txt"))](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/repositories/ContatoRepository.java#22-186) no repositório Mock e forjar leituras diretas em segundos.

### Negativas / Pontos de Atenção:
*   A leitura inteira para a Memória (`Files.readAllLines`) pode ser desastrosa por consumo excessivo de RAM (OOM / *Out Of Memory Exception*) sob longos laços O(N) caso o arquivo de TXT escale a níveis milionários de agenda de contatos (+ 500 Mil a 1 Milhão de linhas de registros). A arquitetura exigirá modificação para a API de Streams `Files.lines()`.
*   A classe gerencia instintivamente as "Constraints" (`.contatoExiste()`). Em um banco lógico e maduro esta validação de concorrência ou colisão seria feita de modo assíncrono antes da finalização direto pelo Driver, porém no limite técnico do disco C: o sistema varre exaustivamente as linhas tentando casar Nomes Iguias.
