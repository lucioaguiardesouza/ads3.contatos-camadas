# Roadmap de Evolução: Agenda Contatos 2026+

O projeto atualmente segue excelentes práticas (Arquitetura em Camadas, Virtual Threads para non-blocking I/O, Tratamento de Exceções Padronizado e Design Moderno). Para elevar o nível para uma aplicação de porte comercial, aqui estão algumas sugestões:

## 1. Persistência e Dados
- [ ] **Migração para Banco de Dados**: Substituir o [agenda.txt](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/agenda.txt) por um banco embutido como **H2** ou **SQLite**. Isso garante integridade referencial e buscas muito mais rápidas.
- [ ] **JPA/Hibernate**: Implementar um ORM para gerenciar as entidades, reduzindo o código de conversão manual de CSV/Objeto.
- [ ] **Backup Automático**: Criar uma rotina para exportar os contatos em formato **JSON** ou **VCF (vCard)** para importação em celulares.

## 2. Interface e Experiência do Usuário (UI/UX)
- [ ] **Fotos nos Contatos**: Permitir o upload de uma imagem de perfil, armazenando-a em uma pasta `/uploads` e exibindo um avatar redondo na listagem.
- [ ] **Busca Incremental com Highlight**: Destacar o texto encontrado na tabela conforme o usuário digita no filtro.
- [ ] **Dashboard Estatístico**: Uma tela inicial com gráficos (usando JFreeChart ou FlatLaf extras) mostrando, por exemplo, a distribuição de contatos por DDD ou provedor de e-mail.
- [ ] **Modo Light/Dark Toggle**: Adicionar um botão na [BasePage](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/pages/BasePage.java#11-49) para alternar entre temas claros e escuros em tempo real.

## 3. Arquitetura e Engenharia
- [ ] **Injeção de Dependência (DI)**: Utilizar um framework leve como **Guice** ou **Dagger 2** (ou até Spring Context) para gerenciar as instâncias, removendo a necessidade de [DependencyFactory](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/DependencyFactory.java#19-67) estático.
- [ ] **Validação com Bean Validation**: Usar anotações como `@NotBlank` e `@Email` nos DTOs para automatizar as regras de negócio de forma declarativa.
- [ ] **Logs Profissionais**: Substituir `System.out` pelo **SLF4J com Logback**, salvando logs de erro em arquivos rotativos para depuração futura.

## 4. Qualidade e DevOps
- [ ] **Testes de Unidade e Integração**: Cobrir a camada [Service](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/services/ContatoService.java#21-129) e [Repository](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/repositories/ContatoRepository.java#23-186) com **JUnit 5** e **Mockito**, garantindo que novas mudanças não quebrem o sistema.
- [ ] **Native Image (GraalVM)**: Configurar o projeto para gerar um executável nativo (.exe ou binário Linux). Isso faria a aplicação abrir instantaneamente e consumir 5x menos memória.
- [ ] **Pipeline CI/CD**: Configurar **GitHub Actions** para compilar e testar o projeto automaticamente em cada *push*.

## 5. Funcionalidades Extras
- [ ] **Sincronização em Nuvem**: Pequena integração com Firebase ou uma API REST própria para manter os contatos sincronizados.
- [ ] **Internacionalização (i18n)**: Suporte a múltiplos idiomas (Português, Inglês, Espanhol) usando arquivos de propriedades.

---
> [!IMPORTANT]
> O código atual é uma base extremamente sólida. Essas melhorias transformariam uma ferramenta utilitária em um software profissional escalável.
