# JustExcavators — Enhancement Cores

> **Status:** implementação funcional concluída; gate automatizado aprovado em 2026-09-30
> **Projeto:** JustExcavators
> **Plataforma inicial:** Fabric
> **Objetivo deste documento:** registrar o comportamento entregue dos Enhancement Cores, suas regras de gameplay, compatibilidades, restrições e ideias futuras.

## Estado entregue

Silk, Collector, Smelting, Filter e Void estão implementados nos dois slots
fixos da Excavator e são administrados pela Enhancement Workbench. O pipeline
server-side cobre o bloco central e o AOE, preserva o fluxo vanilla de quebra e
aplica os efeitos na ordem documentada. A antiga aplicação do Silk pela
Smithing Table, o encantamento persistente e os workarounds de Grindstone foram
removidos; a receita avulsa do Silk Core permanece disponível.

Testes unitários e integrados, build, carregamento de mixins e datagen
idempotente compõem o gate automatizado. Arte final distinta para a Workbench e
os Cores, receitas Survival ainda não decididas e playtests interativos em
multiplayer/servidor dedicado permanecem como acompanhamento externo.

---

# 1. Visão geral

O JustExcavators terá dois tipos principais de progressão:

```text
Excavation Mode
+
Enhancement Core
```

O **Excavation Mode** define a área de escavação:

```text
Base
3×3×1

Deep
3×3×3

Wide
5×5×1

Advanced
5×5×3
```

Já o **Enhancement Core** define uma habilidade especial da ferramenta.

A proposta é que os Enhancement Cores criem diferentes estilos de uso, sem transformar a Excavator em uma ferramenta que faz tudo ao mesmo tempo.

---

# 2. Regra principal dos Enhancement Cores

Cada Excavator poderá possuir:

```text
1 Excavation Mode
+
até 2 Enhancement Cores
```

Exemplos:

```text
Wide Diamond Excavator
+
Silk Core
+
Collector Core
```

```text
Deep Netherite Excavator
+
Collector Core
```

```text
Advanced Iron Excavator
+
Void Core
```

Cada Excavator poderá ter **no máximo dois Enhancement Cores ativos por vez**.

Os dois Cores precisam ser compatíveis entre si. Cores incompatíveis não poderão ser instalados simultaneamente.

Isso mantém:

- balanceamento;
- clareza;
- valor de escolha;
- builds diferentes;
- combinações úteis sem transformar a ferramenta em uma máquina universal.

---

# 3. Enhancement Cores confirmados

A primeira linha conceitual de Enhancement Cores será:

1. Silk Core
2. Collector Core
3. Smelting Core
4. Filter Core
5. Void Core

Esses cinco formam a identidade principal do sistema de melhorias.

---

# 4. Silk Core

## Nome

```text
Silk Core
```

## Função

Concede comportamento equivalente a:

```text
Silk Touch
```

de forma nativa.

A ferramenta poderá obter os drops correspondentes a Silk Touch sem precisar possuir o encantamento.

Exemplos:

- Grass Block;
- Mycelium;
- Podzol;
- outros blocos compatíveis com Silk Touch.

## Regra de implementação

O Silk Core não deve utilizar uma lista fixa de conversões.

Evitar:

```text
Grass → Grass
Podzol → Podzol
Mycelium → Mycelium
```

Preferir utilizar:

- loot tables;
- loot context;
- lógica vanilla de Silk Touch.

Objetivo:

> manter compatibilidade com datapacks e outros mods sempre que possível.

## Compatibilidade com enchantments

### Silk Touch encantado

Caso a ferramenta já possua Silk Touch:

```text
Silk Core
+
Silk Touch
```

é redundante.

O jogo deve impedir essa combinação redundante.

### Fortune

Silk Core e Fortune devem permanecer incompatíveis.

Não permitir:

```text
Silk Core
+
Fortune
```

de forma simultânea.

O Codex deverá definir a solução técnica mais segura.

## Uso ideal

```text
Wide Excavator
+
Silk Core
```

para:

- coletar grandes áreas de Grass;
- preservar blocos;
- terraformar sem perder o bloco original.

---

# 5. Collector Core

## Nome

```text
Collector Core
```

## Conceito

Ao quebrar blocos com a Excavator:

```text
drops
→ inventário do jogador
```

em vez de todos os itens serem lançados no chão.

## Comportamento

Para cada drop:

```text
se houver espaço no inventário
→ inserir diretamente

se não houver espaço
→ dropar normalmente no mundo
```

O Core nunca deve:

- apagar item por inventário cheio;
- substituir stacks existentes incorretamente;
- ignorar limites de stack.

## Experiência

XP deverá continuar sendo gerado normalmente.

O Collector Core não deve absorver XP para o inventário.

## Por que existe

Ferramentas AOE podem gerar dezenas de entidades de item.

Exemplo:

```text
Advanced Excavator
5×5×3
=
até 75 blocos
```

Em Dirt, Sand ou Gravel, isso pode gerar muitos drops simultaneamente.

Collector Core melhora:

- experiência;
- organização;
- performance;
- utilidade em grandes escavações.

## Uso ideal

```text
Deep Excavator
+
Collector Core
```

para:

- coletar grandes volumes;
- mineração de terra;
- areia;
- gravel;
- recursos relacionados a shovel.

## Possível comportamento futuro

Poderá existir suporte a:

- backpacks;
- inventories externos;
- storage mods;

mas isso NÃO é requisito inicial.

Na primeira versão:

```text
apenas inventário vanilla do jogador.
```

---

# 6. Smelting Core

## Nome

```text
Smelting Core
```

## Conceito

Os drops de blocos quebrados poderão ser automaticamente processados através de receitas de smelting.

Exemplos:

```text
Sand
→ Glass
```

```text
Clay
→ Terracotta
```

Outras transformações dependerão das receitas válidas existentes.

## Regra principal

Não criar conversões hardcoded se for possível evitar.

Preferir:

```text
drop original
↓
procurar receita válida de smelting
↓
usar resultado da receita
```

Isso pode aumentar compatibilidade com:

- datapacks;
- mods;
- receitas customizadas.

## Balanceamento

Smelting Core é mais poderoso que os outros enhancements.

Por isso deverá possuir algum custo adicional.

### Opção recomendada inicial

```text
bloco normal
= 1 de durabilidade

bloco processado por Smelting Core
= custo adicional
```

Exemplo conceitual:

```text
1 bloco smelted
= 2 de durabilidade total
```

O valor exato deverá ser testado.

## Alternativa futura

Consumir combustível do inventário.

Exemplo:

```text
Coal
Charcoal
etc.
```

Porém isso aumenta bastante a complexidade.

Não implementar inicialmente sem necessidade.

## Interação com Collector Core

Smelting Core e Collector Core são compatíveis.

```text
drop original
↓
resultado da receita de smelting
↓
inventário do jogador
```

Caso não exista espaço no inventário, o resultado processado deverá cair normalmente no mundo.

## Uso ideal

```text
Wide Excavator
+
Smelting Core
```

para transformar rapidamente grandes superfícies de Sand em recursos processados.

---

# 7. Filter Core

## Nome

```text
Filter Core
```

## Conceito

O AOE só deverá quebrar blocos compatíveis com o bloco central.

Versão inicial recomendada:

> quebrar somente blocos exatamente iguais ao bloco atingido.

## Exemplo

Área:

```text
D D D S D
D D S S D
D D D D D
```

Onde:

```text
D = Dirt
S = Stone
```

Jogador quebra:

```text
Dirt
```

Com Filter Core:

```text
somente Dirt
```

é afetado.

Stone permanece intacto.

## Objetivo

Evitar destruição acidental durante:

- terraformação;
- escavação próxima de construções;
- terrenos mistos;
- áreas com múltiplos materiais.

## Regra recomendada para a versão inicial

Comparação direta:

```text
targetBlock == centerBlock
```

ou equivalente seguro da API atual.

## Possível evolução futura

Um sistema mais avançado poderia permitir:

```text
mesma tag
```

Exemplo:

```text
Dirt
Coarse Dirt
Rooted Dirt
```

como uma família.

Porém isso deve ser avaliado depois.

A primeira versão deve priorizar previsibilidade.

## Uso ideal

```text
Wide Excavator
+
Filter Core
```

para nivelar terreno sem destruir materiais indesejados.

---

# 8. Void Core

## Nome

```text
Void Core
```

## Conceito

Blocos quebrados:

```text
não geram drops
```

Eles são removidos normalmente do mundo.

## Objetivo

Terraformação pesada.

Exemplo:

```text
centenas de stacks de Dirt
```

podem ser inúteis para o jogador.

Void Core permite limpar grandes volumes sem:

- poluir o chão com itens;
- lotar inventário;
- criar entidades desnecessárias.

## Regra crítica

O Void Core precisa ser extremamente claro para o jogador.

Tooltip recomendado:

```text
WARNING: Destroyed blocks will not drop items.
```

ou tradução equivalente.

## Comportamento esperado

```text
bloco quebrado
→ removido
→ nenhum item
```

XP deverá ser avaliado separadamente.

### Recomendação inicial

Se o bloco normalmente forneceria XP:

```text
não gerar XP
```

Isso mantém a lógica:

> tudo que seria obtido do bloco é descartado.

## Sneak

Segurar Shift continuará desativando AOE:

```text
Shift
→ 1×1
```

Porém o Void Core continuará ativo.

Ou seja:

```text
Shift + Void
→ quebra 1 bloco
→ sem drop
```

## Uso ideal

```text
Advanced Excavator
+
Void Core
```

para:

- remover montanhas de Dirt;
- limpar Sand;
- remover Gravel;
- terraformação em massa.

---

# 9. Compatibilidade entre os Enhancement Cores

Cada Excavator poderá possuir no máximo dois Enhancement Cores.

## Combinações permitidas

| Combinação | Resultado |
|---|---|
| Silk + Collector | Preserva os blocos e envia os drops ao inventário. |
| Silk + Smelting | Calcula o loot com Silk Touch e processa os drops que possuírem receita de fornalha. |
| Silk + Filter | Preserva somente os blocos iguais ao bloco central. |
| Collector + Smelting | Processa os drops e envia o resultado ao inventário. |
| Collector + Filter | Envia ao inventário apenas os drops dos blocos filtrados. |
| Smelting + Filter | Processa somente os drops dos blocos iguais ao bloco central. |
| Filter + Void | Remove sem drops somente os blocos iguais ao bloco central. |

## Combinações proibidas

| Combinação | Motivo |
|---|---|
| Silk + Void | O Void descartaria aquilo que o Silk tentaria preservar. |
| Collector + Void | O Void não produz drops para o Collector recolher. |
| Smelting + Void | O resultado processado seria descartado imediatamente. |
| Dois Cores iguais | O mesmo efeito não poderá ser acumulado. |

Regra conceitual:

```text
Máximo: 2 Enhancement Cores

Void
→ compatível apenas com Filter

Collector e Filter
→ compatíveis entre si

Collector
→ compatível com Silk, Smelting e Filter

Filter
→ compatível com Silk, Collector, Smelting e Void

Silk
→ compatível com Collector, Smelting e Filter
```

Mesmo quando três Cores seriam logicamente compatíveis, o limite de dois continuará valendo.

Não permitir, por exemplo:

```text
Silk + Collector + Filter
```

## Compatibilidade com enchantments

- Silk Core é incompatível com Fortune;
- Silk Core é redundante com Silk Touch encantado e sua aplicação deve ser impedida;
- Smelting Core é compatível com Fortune e com Silk Touch encantado;
- Fortune e Silk Touch são inúteis com Void Core e não poderão coexistir com ele.

Essas regras devem ser verificadas nos dois sentidos: ao instalar um Core em uma ferramenta encantada e ao tentar aplicar um encantamento em uma ferramenta que já possui o Core incompatível.

## Por que limitar em dois

Permitir todos os Cores ao mesmo tempo transformaria a Excavator em uma ferramenta universal.

Isso reduziria:

- escolhas;
- especialização;
- valor dos Cores;
- balanceamento.

---

# 10. Builds desejadas

## Construction Excavator

```text
Wide
+
Silk
+
Collector
```

Objetivo:

- Grass;
- Podzol;
- Mycelium;
- blocos preservados;
- coleta direta no inventário.

## Resource Excavator

```text
Deep
+
Collector
+
Filter
```

Objetivo:

- coleta eficiente;
- redução de drops no chão;
- coleta seletiva do material desejado.

## Processing Excavator

```text
Wide
+
Smelting
+
Collector
```

Objetivo:

- processamento automático;
- envio do resultado ao inventário.

## Precision Excavator

```text
Wide
+
Filter
+
Silk
```

Objetivo:

- escavação controlada;
- preservação dos blocos selecionados.

## Terraforming Excavator

```text
Advanced
+
Void
+
Filter
```

Objetivo:

- destruição rápida de terreno sem drops;
- proteção dos materiais diferentes do bloco central.

---

# 11. Identidade visual dos Enhancement Cores

Todos os Enhancement Cores devem pertencer à mesma família visual.

Porém cada um precisa ser reconhecível.

## Silk Core — direção visual

Palavras-chave:

- delicado;
- seda;
- suavidade;
- preservação.

Possíveis cores:

- branco;
- azul claro;
- ciano;
- lilás suave.

Símbolos possíveis:

- fio;
- tecido;
- espiral suave;
- teia estilizada.

## Collector Core — direção visual

Palavras-chave:

- atração;
- coleta;
- magnetismo;
- inventário.

Possíveis cores:

- azul;
- roxo;
- ciano.

Símbolos possíveis:

- ímã;
- setas entrando no centro;
- funil;
- partículas convergindo.

## Smelting Core — direção visual

Palavras-chave:

- calor;
- forja;
- forno;
- fusão.

Possíveis cores:

- laranja;
- amarelo;
- vermelho;
- carvão escuro.

Símbolos possíveis:

- chama;
- forno;
- núcleo incandescente.

## Filter Core — direção visual

Palavras-chave:

- seleção;
- separação;
- precisão.

Possíveis cores:

- verde;
- azul;
- branco.

Símbolos possíveis:

- funil;
- filtro;
- grade;
- blocos separados;
- check central.

## Void Core — direção visual

Palavras-chave:

- vazio;
- destruição;
- ausência;
- anulação.

Possíveis cores:

- preto;
- roxo escuro;
- violeta;
- azul profundo.

Símbolos possíveis:

- buraco;
- centro negro;
- espiral;
- espaço vazio;
- item sendo consumido.

O Void Core deve parecer mais perigoso que os demais.

---

# 12. Tooltips

## Silk Core

```text
Preserves blocks as if mined with Silk Touch.
```

## Collector Core

```text
Sends block drops directly to your inventory.
```

## Smelting Core

```text
Automatically smelts compatible block drops.
```

## Filter Core

```text
Area mining only affects blocks matching the target block.
```

## Void Core

```text
Destroyed blocks do not drop items.
```

Adicionar aviso visual ao Void.

## Exibição dos Cores instalados

A tooltip da Excavator deverá listar os dois slots de forma clara e em ordem estável.

Exemplo:

```text
Enhancement Cores (2/2):
- Silk Core
- Collector Core
```

Slots vazios poderão ser omitidos ou exibidos como `Empty`, desde que a interface indique o limite de dois Cores.

---

# 13. Aplicação do Core

Os Enhancement Cores serão administrados exclusivamente através da:

```text
Enhancement Workbench
Mesa de Trabalho de Aprimoramento
```

A mesa terá três slots funcionais:

```text
       [ Excavator ]

[ Core slot 1 ] [ Core slot 2 ]
```

O inventário e a hotbar do jogador também serão exibidos.

Requisitos:

- preservar durability;
- preservar name;
- preservar enchantments;
- preservar excavation mode;
- preencher um slot vazio quando a combinação for válida;
- validar incompatibilidades antes da aplicação;
- projetar nos dois slots os Cores armazenados na Excavator;
- permitir remover e devolver Cores instalados;
- substituir explicitamente o Core do slot clicado;
- manter toda alteração autoritativa no servidor;
- impedir duplicação.

A mesa será uma workstation temporária, sem inventário persistente no bloco. Ao
fechar a interface, a Excavator e itens reais no cursor retornam ao inventário;
se não houver espaço, caem junto ao jogador. Os Cores ainda exibidos permanecem
armazenados na Excavator.

---

# 14. Troca de Enhancement Core

Recomendação atual:

## permitir substituição

Exemplo:

```text
Silk + Collector Excavator
+
Filter Core
→
Silk + Filter Excavator
```

Quando houver um slot vazio, o novo Core compatível poderá ser colocado no slot
1 ou no slot 2 sem mover automaticamente o Core já instalado.

Quando os dois slots estiverem ocupados, o jogador deverá clicar diretamente no
Core que deseja substituir enquanto segura o novo Core. A troca só ocorrerá se
a combinação resultante for válida.

O Core substituído será devolvido ao cursor ou ao inventário do jogador.

Shift + clique instala no primeiro slot vazio compatível. Se os dois slots
estiverem ocupados, o atalho rejeita a operação e nunca escolhe uma substituição
automaticamente.

Isso evita obrigar o jogador a criar várias cópias da mesma ferramenta.

---

# 15. Representação interna

Não criar um item separado para cada combinação.

Evitar:

```text
diamond_wide_silk_excavator
diamond_wide_collector_excavator
diamond_wide_void_excavator
...
```

Preferir:

```text
diamond_excavator
+
Item Component
```

Conceitualmente, os dois slots são posições opcionais fixas:

```text
excavation_mode = WIDE
slot_1 = SILK
slot_2 = COLLECTOR
```

A representação deverá:

- aceitar zero, um ou dois Cores em posições independentes;
- preservar `slot_2` quando `slot_1` estiver vazio;
- rejeitar Cores duplicados;
- rejeitar combinações incompatíveis;
- manter uma ordem estável para serialização, tooltip e sincronização em multiplayer.

---

# 16. Enum conceitual

Possível estrutura:

```text
EnhancementType
{
    SILK,
    COLLECTOR,
    SMELTING,
    FILTER,
    VOID
}
```

O Codex deverá adaptar às APIs atuais.

---

# 17. Ordem de processamento

A implementação deverá possuir uma ordem clara.

Exemplo conceitual:

```text
1. capturar ferramenta, Cores, bloco central e contexto
2. validar permissões, proteção, ferramenta e bloco
3. calcular área
4. aplicar Filter somente aos blocos adicionais
5. montar o loot context efetivo, incluindo Silk ou Fortune
6. calcular loot através da loot table
7. aplicar Smelting ou Void
8. aplicar Collector ou gerar os drops no mundo
9. gerar o XP permitido
10. consumir durabilidade
```

Silk participa do cálculo da loot table; não é uma transformação aplicada depois
que os drops normais já foram calculados. A ordem acima torna previsíveis
combinações como `Silk + Smelting`, `Smelting + Collector` e `Silk + Collector`.

A arquitetura deve centralizar a validação e evitar lógica duplicada.

---

# 18. Casos de teste

## Silk

Testar:

- Grass Block;
- Podzol;
- Mycelium;
- blocos com loot table customizada.

## Collector

Testar:

- inventário vazio;
- inventário parcialmente cheio;
- inventário completamente cheio;
- stacks parciais;
- múltiplos drops.

## Smelting

Testar:

- Sand;
- Clay;
- bloco sem recipe;
- recipe alterada por datapack;
- consumo extra de durabilidade.

## Filter

Testar:

- área homogênea;
- área com Dirt + Stone;
- área com Sand + Red Sand;
- orientação horizontal;
- orientação vertical.

## Void

Testar:

- nenhum drop;
- nenhum XP, se essa regra for confirmada;
- Shift 1×1;
- AOE completo.

## Compatibilidade e slots

Testar:

- instalação do primeiro e do segundo Core;
- rejeição de Shift + clique quando os dois slots estiverem ocupados;
- todas as combinações permitidas;
- todas as combinações proibidas;
- rejeição de dois Cores iguais;
- substituição explícita de um Core com os dois slots ocupados;
- preservação dos demais dados da ItemStack durante instalação e substituição;
- incompatibilidades com Fortune e Silk Touch encantado.

---

# 19. Performance

Collector e Void podem melhorar performance em grandes AOE:

- menos entidades de item;
- menos drops físicos.

Smelting consulta o Recipe Manager atual por unidade, sem cache entre reloads de
datapack, garantindo que receitas alteradas sejam observadas imediatamente.

---

# 20. Ideias futuras — ainda não confirmadas

As ideias abaixo NÃO fazem parte do escopo atual.

São apenas ideias para futuras versões.

## Fortune Core

### Ideia

Fortune nativo.

Possivelmente:

```text
Fortune III
```

### Problema

A utilidade em uma ferramenta especializada em shovel é limitada.

Exemplo principal:

```text
Gravel → Flint
```

Pode não justificar um Core inteiro.

Status:

```text
IDEIA FUTURA
```

## Reinforcement Core

### Ideia

Reduzir custo de durabilidade do AOE.

Exemplo:

```text
chance de determinados blocos não consumirem durability
```

### Problema

Pode se sobrepor demais a:

```text
Unbreaking
```

Status:

```text
IDEIA FUTURA
```

## Stability Core

### Ideia

Interagir com blocos com gravidade.

Possibilidades:

- reduzir cascatas de Sand/Gravel;
- quebrar camada de forma mais estável;
- controlar atualizações temporariamente.

### Problema

Pode gerar:

- bugs;
- comportamento estranho;
- conflitos com física vanilla.

Status:

```text
IDEIA EXPERIMENTAL
```

## Replanting / Preservation Core

### Ideia

Replantar automaticamente elementos removidos.

Porém isso começa a invadir o papel de:

```text
Hoe
```

Pode fazer mais sentido em outro mod/ferramenta.

Status:

```text
IDEIA FUTURA
```

## Speed Core

### Ideia

Aumentar velocidade.

### Decisão atual

Não recomendado.

Motivo:

```text
Efficiency
```

já resolve essa função.

Evitar criar Core que apenas reproduz um enchantment sem agregar identidade.

---

# 21. Filosofia futura

Novos Enhancement Cores devem obedecer à seguinte pergunta:

> “Isso cria uma nova forma interessante de usar a Excavator?”

Se a resposta for apenas:

```text
faz mais rápido
```

ou:

```text
faz mais dano
```

provavelmente não é um bom Enhancement Core.

---

# 22. Escopo recomendado

## Confirmados

```text
Enhancement Workbench
Silk Core
Collector Core
Smelting Core
Filter Core
Void Core
```

## Futuro

```text
Fortune Core
Reinforcement Core
Stability Core
Replanting / Preservation Core
```

## Não recomendado atualmente

```text
Speed Core
```

---

# 23. Resumo final

Os Enhancement Cores passam a ser uma das principais identidades do JustExcavators.

Eles não aumentam simplesmente o tamanho da área.

Eles alteram **como a Excavator interage com o mundo**.

```text
Silk
→ preservar

Collector
→ coletar

Smelting
→ processar

Filter
→ controlar

Void
→ destruir
```

A combinação:

```text
Excavation Mode
+
até 2 Enhancement Cores compatíveis
```

permite que dois jogadores utilizando a mesma Excavator tenham ferramentas com funções completamente diferentes.

Essa especialização é intencional e deve ser preservada.

---

# 24. Instruções para o Codex

Ao planejar a implementação:

1. não criar um item por combinação;
2. usar dados/componentes na ItemStack;
3. permitir no máximo dois Enhancement Cores ativos;
4. preservar compatibilidade com loot tables;
5. manter lógica server-side;
6. considerar multiplayer;
7. evitar hardcode de drops sempre que possível;
8. permitir expansão futura;
9. criar testes individuais para cada Core;
10. manter Silk, Collector, Smelting, Filter e Void como os Enhancement Cores principais definidos neste documento;
11. centralizar a validação das combinações permitidas e proibidas;
12. impedir Cores duplicados e substituições implícitas via Shift + clique;
13. respeitar as incompatibilidades com Fortune e Silk Touch encantado.

---

**Documento preparado para handoff ao Codex.**
