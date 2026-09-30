# JustExcavators — Enhancement Cores

> **Status:** conceito definido para implementação futura no mod
> **Projeto:** JustExcavators
> **Plataforma inicial:** Fabric
> **Objetivo deste documento:** registrar os Enhancement Cores planejados, suas regras de gameplay, compatibilidades, restrições e ideias futuras antes da implementação pelo Codex.

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
| Silk + Filter | Preserva somente os blocos iguais ao bloco central. |
| Collector + Smelting | Processa os drops e envia o resultado ao inventário. |
| Collector + Filter | Envia ao inventário apenas os drops dos blocos filtrados. |
| Smelting + Filter | Processa somente os drops dos blocos iguais ao bloco central. |
| Filter + Void | Remove sem drops somente os blocos iguais ao bloco central. |

## Combinações proibidas

| Combinação | Motivo |
|---|---|
| Silk + Smelting | Um Core preserva o bloco enquanto o outro tenta processar seu drop. |
| Silk + Void | O Void descartaria aquilo que o Silk tentaria preservar. |
| Collector + Void | O Void não produz drops para o Collector recolher. |
| Smelting + Void | O resultado processado seria descartado imediatamente. |
| Dois Cores iguais | O mesmo efeito não poderá ser acumulado. |

Regra conceitual:

```text
Máximo: 2 Enhancement Cores

Silk, Smelting e Void
→ mutuamente exclusivos entre si

Collector e Filter
→ compatíveis entre si

Collector
→ compatível com Silk, Smelting e Filter

Filter
→ compatível com Silk, Collector, Smelting e Void
```

Mesmo quando três Cores seriam logicamente compatíveis, o limite de dois continuará valendo.

Não permitir, por exemplo:

```text
Silk + Collector + Filter
```

## Compatibilidade com enchantments

- Silk Core é incompatível com Fortune;
- Silk Core é redundante com Silk Touch encantado e sua aplicação deve ser impedida;
- Smelting Core é incompatível com Silk Touch encantado;
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

A forma definitiva ainda deve ser definida pelo Codex.

Possibilidades:

- Smithing Table;
- crafting especial;
- recipe customizada.

Requisitos:

- preservar durability;
- preservar name;
- preservar enchantments;
- preservar excavation mode;
- preencher um slot vazio quando a combinação for válida;
- validar incompatibilidades antes da aplicação;
- substituir o Enhancement Core escolhido corretamente quando os dois slots estiverem ocupados;
- impedir duplicação.

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

Quando houver um slot vazio, o novo Core compatível deverá ser adicionado sem remover o Core existente.

Quando os dois slots estiverem ocupados, a aplicação deverá indicar claramente qual Core será substituído. Nenhum Core deverá ser removido de forma ambígua ou silenciosa.

O Core substituído não precisa necessariamente ser devolvido.

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

Conceitualmente:

```text
excavation_mode = WIDE
enhancements = [SILK, COLLECTOR]
```

A representação deverá:

- aceitar zero, um ou dois Cores;
- rejeitar Cores duplicados;
- rejeitar combinações incompatíveis;
- manter uma ordem estável para serialização, tooltip e sincronização em multiplayer.

---

# 16. Enum conceitual

Possível estrutura:

```text
EnhancementType
{
    NONE,
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
1. calcular área
2. validar bloco
3. aplicar Filter, se presente
4. quebrar bloco
5. calcular loot
6. aplicar Silk / Smelting / Void
7. aplicar Collector
8. consumir durabilidade
```

Mais de uma etapa poderá ocorrer na mesma quebra. A ordem acima é obrigatória para que combinações como `Smelting + Collector` e `Silk + Collector` tenham resultado previsível.

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
- rejeição de um terceiro Core;
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

Smelting deverá evitar procurar recipes de forma ineficiente para cada bloco caso exista forma de cache segura.

---

# 20. Ideias futuras — ainda não confirmadas

As ideias abaixo NÃO fazem parte do escopo atual.

São apenas ideias para futuras versões.

## Enhancement Workbench

### Ideia

Adicionar um bloco com interface própria para gerenciar os Enhancement Cores instalados em uma Excavator.

Nome conceitual:

```text
Enhancement Workbench
```

A interface poderia possuir três slots funcionais:

```text
[ Excavator ] [ Core 1 ] [ Core 2 ]
```

Ao inserir uma Excavator, a mesa identificaria os Cores armazenados na ItemStack e os exibiria nos dois slots de melhoria.

Possíveis comportamentos:

- instalar Cores compatíveis;
- remover e devolver Cores instalados;
- substituir um dos dois Cores;
- bloquear combinações incompatíveis;
- permitir o rodízio de builds sem fabricar várias Excavators.

Essa proposta trataria os Cores como módulos reutilizáveis. A interface precisaria manter toda alteração server-side, preservar os dados da ferramenta e impedir duplicação durante inserção, retirada, fechamento da tela ou quebra do bloco.

### Pontos ainda não definidos

- nome e aparência final do bloco;
- receita de fabricação;
- troca gratuita ou com custo de XP/material;
- comportamento ao quebrar a mesa com itens inseridos;
- interface e mensagens para combinações inválidas;
- se a mesa substituirá completamente ou apenas complementará os métodos de aplicação inicialmente considerados.

Status:

```text
IDEIA FUTURA EM AVALIAÇÃO
```

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

## Sistema futuro em avaliação

```text
Enhancement Workbench
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
12. impedir Cores duplicados e a instalação de um terceiro Core;
13. respeitar as incompatibilidades com Fortune e Silk Touch encantado.

---

**Documento preparado para handoff ao Codex.**
