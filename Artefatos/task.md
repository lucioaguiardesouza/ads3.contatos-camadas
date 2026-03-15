# Fase 1: Refatorações Arquiteturais e Novo ADR (Finalizada)

- [x] Implementar Injeção de Dependências em ContatoController e ContatoService.
- [x] Aplicar DRY no ContatoController.buscarPorNome delegando para o Service.
- [x] Otimizar laços no ContatoService utilizando Java 21 Streams API (`.stream()`).
- [x] Finalizar métodos vazios em ContatoValidation ([validaCampo](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/validations/ContatoValidation.java#30-47), [validaRegraAlterar](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/validations/IContatoValidation.java#19-20), etc).
- [x] Implementar Regex em ContatoValidation (E-mail e Telefone).
- [x] Fazer fallback de `throws Exception` para classes customizadas ([ValidationException](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/exceptions/ValidationException.java#3-7), [BusinessException](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/exceptions/BusinessException.java#3-7)).
- [x] Otimizar uso de memória no ContatoRepository usando `Files.lines()` em vez de `Files.readAllLines()`.
- [x] Gerar documento ADR 005 e ADR 006 relatando refatorações.

# Fase 2: Evolução Arquitetural Sênior (Atual)

- [x] Criar classe gerenciadora [DependencyFactory](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/DependencyFactory.java#17-58) (Inversão de Controle - IoC) no pacote raiz para instanciar Controllers fechados.
- [x] Criar o pacote `dtos` contendo [ContatoCadastroDTO](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/dtos/ContatoCadastroDTO.java#10-16) imutável (Records se aplicável).
- [x] Mudar assinatura em [IContatoCadastroController](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/controllers/IContatoCadastroController.java#6-10) para que a tela não forneça instâncias diretas do Domínio [Contato](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/domains/Contato.java#19-30).
- [x] Adequar [Form_Cadastro](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/pages/Form_Cadastro.java#24-367) transferindo seus inputs de UI para [ContatoCadastroDTO](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/dtos/ContatoCadastroDTO.java#10-16).
- [x] Ocultar acoplamento do `new ContatoController(...)` invocando a camada de UI através de [DependencyFactory](file:///home/lucio/Documents/GitHub/ads3.contatos-camadas/src/main/java/com/br/senai/ads3/agenda_fatesg/DependencyFactory.java#17-58).
- [x] Atualizar testes com a IDE e gerar Walktrough & ADR final da Fase 2.
