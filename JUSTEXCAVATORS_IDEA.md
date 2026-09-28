# JustExcavators — Documento de Visão e Implementação

> **Status:** conceito definido / ainda não implementado  
> **Plataforma inicial:** Fabric  
> **Versão inicial recomendada:** Minecraft 26.3  
> **Objetivo deste arquivo:** registrar a visão funcional, progressão, itens, regras de gameplay, estratégia de compatibilidade e direção técnica inicial do mod.  
> **Próxima etapa:** o Codex deverá usar este documento como briefing, analisar a arquitetura necessária e elaborar um plano técnico de implementação antes de qualquer desenvolvimento.

---

# 1. Visão geral

**JustExcavators** é um mod de ferramentas de escavação em área para Minecraft.

A proposta é criar para pás aquilo que ferramentas como Hammers representam para picaretas, mas **sem simplesmente copiar a progressão de área do JustHammers**.

O foco do mod será:

- remover grandes quantidades de terra, areia, cascalho e outros blocos apropriados para pá;
- facilitar terraformação;
- acelerar escavações;
- manter comportamento previsível;
- evitar destruição exagerada do terreno;
- oferecer progressão por especialização;
- manter estética e comportamento próximos ao Minecraft vanilla;
- funcionar de maneira independente;
- oferecer integração opcional com JustHammers quando instalado.

A ideia central não é criar uma pá que eventualmente destrói `5×5×5`.

A ideia é criar diferentes **perfis de escavação**, cada um útil em uma situação específica.

---

# 2. Filosofia do mod

O JustExcavators deve seguir alguns princípios centrais.

## 2.1. Simples de entender

O jogador deve olhar para uma Excavator e compreender imediatamente:

> “Essa ferramenta é uma pá que quebra blocos em área.”

O mod não deve exigir sistemas complexos para ser utilizado.

---

## 2.2. Especialização em vez de crescimento infinito

A progressão não deve ser:

```text
3×3×1
↓
3×3×3
↓
5×5×1
↓
5×5×3
↓
5×5×5
```

Esse modelo funciona melhor para Hammers do que para pás.

Uma Excavator `5×5×5` quebraria até:

```text
125 blocos por ação
```

Isso seria excessivo para:

- terra;
- areia;
- cascalho;
- neve;
- Soul Sand;
- outros terrenos facilmente removíveis.

No JustExcavators, a progressão deverá favorecer **ferramentas com funções diferentes**, não apenas ferramentas cada vez maiores.

---

## 2.3. Vanilla+

A ferramenta deve parecer uma extensão natural do Minecraft.

O mod deve evitar, principalmente na versão inicial:

- interfaces complexas;
- máquinas próprias;
- energia;
- novos minérios;
- árvores tecnológicas;
- dezenas de tiers;
- habilidades excessivamente mágicas.

---

## 2.4. Segurança e previsibilidade

Uma ferramenta AOE precisa ser previsível.

O jogador deve conseguir saber:

- qual área será destruída;
- em qual direção;
- quais blocos poderão ser afetados;
- como desligar temporariamente o modo em área.

---

# 3. Independência do JustHammers

Esta é uma decisão central do projeto.

## JustExcavators será um mod independente.

O JustHammers **NÃO será uma dependência obrigatória**.

O jogador poderá utilizar:

```text
JustExcavators
```

sozinho.

Também poderá utilizar:

```text
JustHammers
```

sozinho.

Ou:

```text
JustExcavators
+
JustHammers
```

simultaneamente.

---

# 4. Por que não depender obrigatoriamente do JustHammers

Tornar JustHammers uma dependência obrigatória causaria vários problemas.

## Dependência de versões

Se JustHammers atrasar uma atualização, JustExcavators também ficaria preso.

---

## Dependência de IDs e implementação

Caso o outro mod altere:

- itens;
- registries;
- recipes;
- cores;
- classes;
- APIs internas;

o JustExcavators poderia quebrar.

---

## Usuário obrigado a instalar conteúdo que não deseja

Alguns jogadores poderão querer apenas uma ferramenta de escavação.

Eles não devem ser obrigados a instalar Hammers para isso.

---

## Identidade própria

JustExcavators precisa ter:

- seus próprios itens;
- seus próprios cores;
- sua própria progressão;
- seu próprio balanceamento;
- seu próprio código.

---

# 5. Compatibilidade opcional com JustHammers

Apesar de independente, JustExcavators deverá ser projetado para conviver bem com JustHammers.

A integração deverá ser **opcional**.

O mod não poderá falhar caso JustHammers não esteja instalado.

---

## Possíveis integrações futuras

Quando JustHammers estiver presente:

- determinados Hammer Cores poderão eventualmente ser aceitos como alternativas em recipes compatíveis;
- recipes poderão reconhecer materiais equivalentes;
- tags poderão permitir interoperabilidade;
- tooltips poderão indicar compatibilidade;
- os mods poderão compartilhar uma linguagem visual coerente.

### Importante

Nenhuma dessas integrações deve fazer o JustExcavators depender internamente de classes privadas ou implementação instável do JustHammers.

Preferir:

- tags;
- resource conditions;
- APIs públicas;
- checagem segura da presença do mod;
- módulos de compatibilidade separados.

---

# 6. Loader inicial

Os dois projetos planejados atualmente:

- JustExcavators;
- Divine Armor;

deverão começar utilizando:

## Fabric

O objetivo é primeiro dominar uma plataforma e publicar uma versão estável.

Multi-loader não faz parte da primeira implementação.

---

# 7. Versão inicial recomendada

## Minecraft 26.3 + Fabric

Essa é a versão recomendada para iniciar o desenvolvimento.

Motivos:

- é a versão atual no início do projeto;
- possui suporte oficial atual do Fabric;
- o Fabric já publicou tooling específico para Minecraft 26.3;
- JustHammers já possui versão Fabric para Minecraft 26.3;
- começar na versão atual facilita testes e publicação para jogadores que acompanham releases recentes.

No anúncio oficial do Fabric para 26.3, a recomendação atual é:

```text
Loom 1.17
Gradle 9.6.0
Fabric Loader 0.19.5
```

Esses valores devem ser novamente verificados pelo Codex antes de criar o projeto, pois dependências podem receber novas versões.

---

# 8. Estratégia de suporte a versões

O objetivo de longo prazo é atingir múltiplas versões do Minecraft.

A versão inicial será:

```text
26.3 Fabric
```

Depois que a versão inicial estiver estável, deverão ser avaliados ports para:

```text
26.2
26.1.x
1.21.x
1.20.1
```

A ordem definitiva deverá considerar:

- tamanho do público;
- downloads;
- ecossistema de mods;
- facilidade de manutenção;
- compatibilidade com JustHammers;
- esforço necessário para backport.

---

# 9. Atenção especial à fronteira 26.1

Minecraft 26.1 representou uma mudança importante no ambiente de desenvolvimento Fabric.

Entre as mudanças relevantes:

- Minecraft deixou de ser ofuscado;
- Fabric passou a utilizar oficialmente mappings Mojang;
- Yarn deixou de ser oficialmente suportado;
- houve mudanças grandes no tooling;
- Minecraft 26.1 exige Java 25 para o Gradle JVM.

Portanto:

## não assumir que portar de 26.x para 1.21.x será apenas alterar números de versão.

O código deve ser organizado desde o começo para reduzir o custo de ports.

---

# 10. Estratégia recomendada para multi-versão

Inicialmente:

```text
uma branch principal
→ Minecraft 26.3
```

Após estabilização:

```text
branches específicas por linha do Minecraft
```

Exemplo conceitual:

```text
main / 26.3
26.2
26.1
1.21
1.20.1
```

O Codex deverá analisar se vale utilizar alguma estratégia de código compartilhado no futuro.

Para a primeira versão, evitar complexidade prematura.

---

# 11. Estrutura principal do mod

O mod será dividido conceitualmente em dois sistemas.

## Excavators

As ferramentas físicas.

---

## Cores

Itens responsáveis por definir ou aprimorar o comportamento da ferramenta.

Existirão inicialmente duas categorias:

```text
Excavation Cores
Enhancement Cores
```

---

# 12. Excavation Cores

Os Excavation Cores definem **como a ferramenta escava**.

A versão inicial terá três perfis.

---

# 13. Basic Excavation Core

**Nome provisório:** `Excavation Core`

## Área

```text
3×3×1
```

Total máximo:

```text
9 blocos
```

## Objetivo

Será o modo clássico de uma Excavator.

Ideal para:

- remover superfícies;
- abrir pequenas paredes;
- terraformação;
- limpar areia;
- limpar terra;
- limpar cascalho.

## Papel na progressão

Será o núcleo base necessário para transformar uma pá convencional ou material correspondente em uma Excavator.

A receita definitiva ainda deverá ser definida.

---

# 14. Deep Excavation Core

**Nome provisório:** `Deep Excavation Core`

## Área

```text
3×3×3
```

Total máximo:

```text
27 blocos
```

## Objetivo

Especialização para escavações profundas.

Ideal para:

- abrir volumes de terra;
- escavar montanhas;
- remover grandes massas de solo;
- criar passagens;
- acelerar terraformação vertical.

## Filosofia

Deep não será necessariamente “melhor” que Wide.

Será uma ferramenta especializada.

---

# 15. Wide Excavation Core

**Nome provisório:** `Wide Excavation Core`

## Área

```text
5×5×1
```

Total máximo:

```text
25 blocos
```

## Objetivo

Especialização em superfícies.

Ideal para:

- nivelar terreno;
- remover camadas;
- limpar praias;
- remover grandes áreas de terra;
- trabalhar em superfícies horizontais.

## Limite importante

A versão inicial NÃO deverá oferecer:

```text
5×5×3
```

nem:

```text
5×5×5
```

---

# 16. Branch de especialização

Deep e Wide devem ser tratados conceitualmente como escolhas diferentes.

Fluxo:

```text
Basic Excavator
      ↓
   escolha
   ↙     ↘
Deep     Wide
```

Não:

```text
Basic
↓
Deep
↓
Wide
```

Isso evita que uma versão torne a anterior inútil.

---

# 17. Enhancement Cores

Enhancement Cores não alteram o tamanho da escavação.

Eles adicionam uma habilidade específica à ferramenta.

A versão inicial deverá possuir pelo menos um.

---

# 18. Silk Core

**Nome provisório:** `Silk Core`

## Função

Concede comportamento equivalente a:

```text
Silk Touch
```

de maneira nativa.

A ferramenta poderá obter drops equivalentes ao encantamento Silk Touch mesmo sem possuir o encantamento.

Exemplos relevantes:

- Grass Block;
- Mycelium;
- Podzol;
- outros blocos compatíveis.

---

## Silk Core não é Silk Touch visualmente

A habilidade deverá fazer parte da ferramenta.

Não é necessário adicionar visualmente:

```text
Silk Touch I
```

na lista de encantamentos.

O tooltip pode indicar algo como:

```text
Native Silk Touch
```

ou equivalente.

---

# 19. Interação Silk Core × Fortune

Silk Core e Fortune não devem funcionar simultaneamente.

Comportamento desejado:

```text
Silk Core
X
Fortune
```

O Codex deverá definir a melhor forma de impedir combinações inválidas.

Possibilidades:

- bloquear aplicação de Silk Core em ferramenta com Fortune;
- bloquear Fortune em ferramenta com Silk Core;
- remover combinação conflitante;
- utilizar sistema de incompatibilidade semelhante aos encantamentos vanilla.

Priorizar comportamento intuitivo e seguro.

---

# 20. Silk Core e Silk Touch encantado

Caso a ferramenta já possua Silk Touch:

aplicar Silk Core deverá ser considerado redundante.

Idealmente o jogo não deve permitir que o jogador desperdice o Core sem necessidade.

O comportamento final deverá ser definido durante implementação.

---

# 21. Possíveis Enhancement Cores futuros

Não fazem parte obrigatória da versão 1.0.

---

## Smelting Core

Possível comportamento:

```text
Sand
→ Glass
```

```text
Clay
→ Terracotta
```

Deverá ser estudado cuidadosamente para evitar transformar a ferramenta em uma máquina de processamento exageradamente poderosa.

---

## Fortune Core

Poderia oferecer Fortune nativo.

Sua utilidade para blocos de pá é mais limitada.

Não é prioridade para a 1.0.

---

## Preservation / Replanting Core

Ideia futura relacionada a agricultura ou preservação de terreno.

Pode invadir o papel de Hoes.

Não incluir inicialmente.

---

# 22. Excavators da versão inicial

A versão 1.0 deverá incluir quatro materiais.

---

## Iron Excavator

**Material:** ferro

### Papel

Primeira Excavator realmente acessível.

### Características

- durabilidade baseada em ferro;
- velocidade compatível com o tier;
- aceita Excavation Cores;
- aceita Enhancement Cores compatíveis.

### Direção visual

- cabeça larga de pá;
- formato simétrico;
- aparência de ferramenta pesada;
- cores do Iron vanilla;
- leitura visual imediata como uma pá de escavação em área.

---

# 23. Golden Excavator

**Material:** ouro

### Papel

Versão muito rápida e pouco durável.

Deve seguir a filosofia das ferramentas de ouro vanilla.

### Características

- alta velocidade;
- baixa durabilidade;
- tier correspondente;
- aceita cores.

### Direção visual

Mesma linguagem visual da Iron Excavator, utilizando:

- dourado vanilla;
- detalhes coerentes;
- cabeça larga e simétrica.

---

# 24. Diamond Excavator

**Material:** diamante

### Papel

Excavator de alto nível antes da Netherite.

### Características

- alta durabilidade;
- boa velocidade;
- progressão natural;
- base para Netherite.

### Direção visual

- ciano/azul do Diamond;
- cabeça larga;
- aparência refinada;
- manter leitura vanilla.

---

# 25. Netherite Excavator

**Material:** Netherite

### Papel

Excavator endgame da versão inicial.

### Características

- maior durabilidade;
- boa velocidade;
- resistência compatível com itens Netherite;
- comportamento de item Netherite quando aplicável.

### Direção visual

- corpo escuro;
- detalhes em Netherite;
- aparência pesada;
- versão mais refinada das Excavators anteriores.

---

# 26. Wooden e Stone Excavators

Não incluir inicialmente.

Motivos:

- reduzir quantidade de itens;
- evitar ferramentas AOE muito cedo;
- dar importância à progressão até Iron;
- simplificar lançamento inicial.

Podem ser reconsideradas futuramente.

---

# 27. Identidade visual das Excavators

Todas as Excavators devem compartilhar a mesma silhueta básica.

Características desejadas:

- cabeça mais larga do que uma pá vanilla;
- cabeça perfeitamente simétrica;
- aparência robusta;
- proporção coerente com item 16×16;
- cabo reconhecível como ferramenta Minecraft;
- visual vanilla+;
- leitura imediata mesmo em inventário.

As diferenças entre tiers deverão vir principalmente de:

- material;
- detalhes;
- acabamento.

Evitar redesenhar completamente a silhueta em cada tier.

---

# 28. Identidade visual dos Cores

Os Cores devem possuir linguagem visual própria.

O jogador deve conseguir diferenciar rapidamente:

```text
Excavation Core
```

de:

```text
Enhancement Core
```

---

# 29. Visual dos Excavation Cores

Os três Excavation Cores podem compartilhar uma base visual.

Exemplo:

- núcleo central;
- moldura metálica;
- símbolo representando o formato de escavação;
- diferentes cores ou detalhes por tipo.

### Basic

Símbolo sugerido:

```text
3×3 plano
```

### Deep

Símbolo sugerido:

```text
cubo / profundidade
```

### Wide

Símbolo sugerido:

```text
área larga / quadrado expandido
```

---

# 30. Visual do Silk Core

O Silk Core deve ser visualmente diferente dos Excavation Cores.

Possíveis elementos:

- textura suave;
- aparência de tecido/seda;
- branco;
- azul claro;
- brilho sutil;
- linhas curvas;
- símbolo remetendo a delicadeza.

Evitar simplesmente desenhar o livro de Silk Touch.

---

# 31. Quantidade inicial de itens

## Excavators

1. Iron Excavator
2. Golden Excavator
3. Diamond Excavator
4. Netherite Excavator

## Excavation Cores

5. Excavation Core
6. Deep Excavation Core
7. Wide Excavation Core

## Enhancement Cores

8. Silk Core

### Total inicial

```text
8 itens principais
```

---

# 32. Progressão conceitual

Uma possibilidade inicial:

```text
Shovel / materiais
↓
Basic Excavator
↓
Excavation Core
↓
especialização
   ↙      ↘
Deep     Wide
```

O sistema exato de recipes ainda deverá ser definido.

---

# 33. Como os Cores deverão ser aplicados

A mecânica exata ainda está aberta.

O comportamento desejado é:

- cada Excavator possui exatamente um perfil de escavação;
- Basic, Deep e Wide são mutuamente exclusivos;
- Silk é uma melhoria independente;
- Silk pode coexistir com qualquer perfil de escavação.

Exemplos válidos:

```text
Basic Diamond Excavator
Basic Diamond Excavator + Silk
Deep Diamond Excavator
Deep Diamond Excavator + Silk
Wide Diamond Excavator
Wide Diamond Excavator + Silk
```

---

# 34. Evitar explosão de IDs

Não é desejável criar um item registrado diferente para cada combinação possível.

Exemplo que deve ser evitado:

```text
diamond_excavator
diamond_deep_excavator
diamond_wide_excavator
diamond_silk_excavator
diamond_deep_silk_excavator
diamond_wide_silk_excavator
...
```

Isso crescerá rapidamente.

---

# 35. Direção técnica recomendada para Cores

Preferir:

```text
1 item de Excavator por material
+
dados/componentes da ItemStack
```

O estado da ferramenta poderá armazenar conceitualmente:

```text
excavation_mode = BASIC | DEEP | WIDE
enhancements = [SILK]
```

Minecraft 26.3 possui forte uso de Item Components.

O Codex deverá analisar a forma correta de representar esses dados utilizando APIs atuais.

### Importante

Não implementar NBT legado por hábito sem antes verificar o sistema atual de componentes.

---

# 36. Aplicação de Cores — recomendação conceitual

Preferência atual:

## sistema simples de upgrade

Não adicionar uma máquina própria.

Possibilidades:

- crafting;
- recipe customizada;
- Smithing Table;
- outra interface vanilla existente.

O Codex deverá comparar as alternativas.

Critérios:

- preservar encantamentos;
- preservar durabilidade;
- preservar nome customizado;
- preservar componentes;
- evitar duplicação;
- ser intuitivo.

---

# 37. Troca de perfil

Ainda é uma questão aberta se um Core aplicado poderá ser removido ou substituído.

Possíveis modelos:

## Modelo A — permanente

Aplicou Deep:

```text
Basic → Deep
```

Não volta.

Vantagem:

- simples;
- dá valor ao Core.

---

## Modelo B — substituível

Aplicar Wide em uma ferramenta Deep troca:

```text
Deep → Wide
```

Possivelmente sem devolver o Core anterior.

Vantagem:

- menos ferramentas duplicadas.

---

## Modelo C — cores encaixáveis/removíveis

Muito mais flexível.

Porém exigiria:

- UI;
- sistema de slots;
- armazenamento;
- mais complexidade.

### Recomendação inicial

Preferir A ou B.

Não criar sistema de sockets complexo na 1.0.

---

# 38. Geometria da escavação

A direção do AOE deverá depender da face do bloco atingida.

Esse comportamento é essencial para uma ferramenta de terraformação.

---

# 39. Hit na face superior ou inferior

Exemplo:

jogador olha para o chão.

A escavação deve ocorrer horizontalmente.

Basic:

```text
XXX
XXX
XXX
```

Uma camada.

Wide:

```text
XXXXX
XXXXX
XXXXX
XXXXX
XXXXX
```

Uma camada.

Deep:

```text
3×3
por
3 blocos de profundidade
```

---

# 40. Hit em face lateral

Ao atingir a lateral de uma parede:

Basic deverá criar:

```text
XXX
XXX
XXX
```

verticalmente.

Deep deverá aprofundar essa área:

```text
3×3×3
```

para dentro da parede.

Wide:

```text
5×5×1
```

na face da parede.

---

# 41. Regra de profundidade

A profundidade deve sempre seguir para dentro do bloco atingido.

Nunca deverá avançar aleatoriamente para trás do jogador.

O cálculo precisa utilizar:

- face atingida;
- direção do hit;
- posição do bloco central.

---

# 42. Modo de precisão

Todas as Excavators deverão possuir uma forma imediata de desligar a quebra em área.

## Regra desejada

```text
Segurar Shift / Sneak
→ quebrar apenas 1 bloco
```

Sem AOE.

---

# 43. Por que não criar Precision Core

Não é necessário.

Precision é uma necessidade básica de segurança da ferramenta.

Deve estar disponível em todas as Excavators sem consumir um Core.

---

# 44. Quais blocos podem ser quebrados

A Excavator deve afetar somente blocos considerados adequados para uma pá.

Preferir comportamento data-driven através de tags.

Conceitualmente:

```text
mineable/shovel
```

ou equivalente atual da versão.

---

# 45. Exemplos esperados

Entre os blocos afetados, quando apropriado:

- Dirt;
- Grass Block;
- Coarse Dirt;
- Rooted Dirt;
- Sand;
- Red Sand;
- Gravel;
- Clay;
- Snow;
- Snow Block;
- Soul Sand;
- Soul Soil;
- Mud;
- outros blocos marcados como adequados para shovel.

O Codex deverá confirmar as tags atuais em 26.3.

---

# 46. Não quebrar blocos aleatórios

Uma AOE iniciada em Dirt não deve destruir indiscriminadamente:

- Stone;
- Ores;
- Chests;
- Furnaces;
- máquinas de mods;
- madeira;
- construções.

Mesmo que estejam dentro da área.

A regra principal deverá ser:

> somente blocos validamente mineráveis pela Excavator.

---

# 47. Bloco central

O jogador deverá conseguir iniciar AOE apenas quando o bloco central for válido para a ferramenta.

Caso o bloco central não seja adequado:

- comportamento vanilla da ferramenta;
- ou nenhuma quebra AOE.

O Codex deverá escolher o comportamento mais consistente.

---

# 48. Drops

Cada bloco destruído pela AOE deverá gerar seus drops normalmente.

O sistema deverá respeitar:

- loot tables;
- enchantments;
- Silk Core;
- Fortune quando aplicável;
- mods que alterem drops, sempre que possível.

Evitar recriar drops manualmente.

---

# 49. Experiência

Se algum bloco válido gerar experiência, a AOE deverá preservar esse comportamento.

Não descartar XP silenciosamente.

---

# 50. Durabilidade

A ferramenta deve pagar pelo número de blocos quebrados.

Recomendação inicial:

```text
1 bloco quebrado
=
1 uso de durabilidade
```

Exemplo:

3×3 completo:

```text
até 9 de durabilidade
```

3×3×3 completo:

```text
até 27
```

5×5×1 completo:

```text
até 25
```

---

# 51. Por que cobrar por bloco

Sem esse custo:

```text
1 clique
=
25 blocos
=
1 durabilidade
```

seria extremamente eficiente e reduziria muito o valor de progressão.

A durabilidade por bloco ajuda a equilibrar ferramentas AOE.

---

# 52. Unbreaking

Unbreaking deverá continuar funcionando.

O Codex deverá avaliar a forma correta de aplicar o encantamento durante AOE.

Idealmente o comportamento deve ser consistente com o sistema vanilla de durabilidade.

---

# 53. Mending

Mending deve funcionar normalmente.

Nenhuma implementação especial deverá ser criada se não for necessária.

---

# 54. Efficiency

Efficiency deve aumentar a velocidade da ferramenta normalmente.

A velocidade percebida do AOE precisa continuar ligada à quebra do bloco central.

---

# 55. Fortune

Fortune deverá funcionar normalmente quando:

- aplicável ao bloco;
- a ferramenta não possuir Silk Core;
- a ferramenta não possuir Silk Touch.

---

# 56. Silk Touch

Silk Touch convencional continuará suportado.

A aplicação do encantamento deve gerar os mesmos resultados esperados de uma pá vanilla.

Silk Core deverá reproduzir semanticamente esse comportamento.

---

# 57. Comportamento do Silk Core

O Silk Core não deve implementar uma tabela fixa como:

```text
Grass → Grass
Podzol → Podzol
...
```

Preferir utilizar o sistema vanilla de loot.

O objetivo é que blocos modificados por datapacks ou mods continuem tendo maior chance de funcionar corretamente.

O Codex deverá encontrar a forma mais segura de fazer o loot considerar o comportamento de Silk Touch.

---

# 58. Quebra server-side

A quebra real dos blocos deverá ser autoritativa no servidor.

Evitar lógica principal somente client-side.

Isso é necessário para:

- multiplayer;
- segurança;
- sincronização;
- servidores dedicados.

---

# 59. Preview de área

Um preview visual da área que será destruída seria útil.

Exemplo:

- outline;
- partículas;
- highlight.

Porém não é obrigatório para a versão 1.0.

Pode ser avaliado para uma atualização posterior.

---

# 60. Proteções de terreno

Ferramentas AOE podem causar problemas em servidores com:

- claims;
- regiões protegidas;
- plugins;
- mods de proteção.

O sistema deve respeitar eventos de quebra sempre que possível.

Evitar simplesmente utilizar métodos de remoção de bloco que ignorem completamente hooks externos.

Esse ponto deve ser destacado pelo Codex como risco de compatibilidade.

---

# 61. Blocos com gravidade

Sand e Gravel continuarão obedecendo à física vanilla.

O mod não precisa impedir quedas em cascata.

Essa interação faz parte do comportamento esperado.

---

# 62. Velocidade e balanceamento

Material deverá controlar principalmente:

- mining speed;
- durability;
- mining level;
- enchantability;
- propriedades do item.

Core deverá controlar:

- área;
- comportamento especial.

Resumo:

> **Material = quão boa é a ferramenta.**

> **Core = o que a ferramenta faz.**

---

# 63. Progressão de materiais

A progressão segue a lógica vanilla:

```text
Iron
↓
Diamond
↓
Netherite
```

Gold existe como opção de alta velocidade e baixa durabilidade.

---

# 64. Upgrade Diamond → Netherite

A Netherite Excavator deverá preferencialmente seguir a lógica vanilla de Smithing.

Exemplo conceitual:

```text
Diamond Excavator
+
Netherite Upgrade Template
+
Netherite Ingot
=
Netherite Excavator
```

O upgrade deve preservar:

- Core;
- Silk enhancement;
- enchantments;
- nome;
- componentes;
- durabilidade relativa, conforme comportamento vanilla.

---

# 65. Recipes

As recipes definitivas ainda não estão fechadas.

Devem ser propostas pelo Codex levando em conta:

- custo;
- progressão;
- simplicidade;
- compatibilidade vanilla;
- valor dos Cores.

---

# 66. Filosofia das recipes

Evitar recipes excessivamente caras apenas para criar grind.

O custo deve refletir:

- economia de tempo oferecida pela ferramenta;
- tamanho da área;
- tier;
- força dos upgrades.

---

# 67. Basic Core

Deve ser relativamente acessível.

O jogador não precisa chegar ao End para usar uma Excavator 3×3.

---

# 68. Deep Core

Deve ser mais caro que Basic.

Representa aumento de volume:

```text
9
→
27 blocos máximos
```

---

# 69. Wide Core

Deve possuir custo semelhante ao Deep.

Ele não é necessariamente superior.

Ele é uma especialização alternativa.

---

# 70. Silk Core

Deve exigir materiais relacionados a encantamento ou delicadeza.

Possíveis referências:

- Lapis;
- Amethyst;
- String;
- Ender-related items;
- experiência/encantamento.

A receita final ainda não foi definida.

---

# 71. Compatibilidade com JustHammers — arquitetura

Criar um pacote/módulo separado.

Exemplo conceitual:

```text
compat/
└── justhammers/
```

O código principal não deve importar classes do JustHammers sem proteção.

---

# 72. Detecção opcional

Conceitualmente:

```text
if JustHammers is loaded
→ registrar integrações
else
→ continuar normalmente
```

A implementação exata deverá utilizar APIs Fabric atuais.

---

# 73. Recipes opcionais

Se houver recipes específicas de compatibilidade:

elas só devem existir quando JustHammers estiver presente.

O Codex deverá analisar:

- Fabric resource conditions;
- tags;
- datapacks;
- recipe conditions disponíveis em 26.3.

---

# 74. Hammer Cores externos

Não reutilizar os Hammer Cores do JustHammers como base obrigatória.

Nossos Cores existirão sempre.

No futuro, um Hammer Core equivalente poderá ser aceito como alternativa se:

- fizer sentido conceitual;
- não quebrar balanceamento;
- a integração for estável.

---

# 75. Licença e referência

JustExcavators será inspirado na filosofia de ferramentas AOE popularizada por mods como JustHammers.

Caso código do JustHammers seja estudado:

- respeitar sua licença;
- não copiar implementação sem analisar obrigações;
- preferir implementação própria;
- creditar inspiração quando apropriado.

O JustHammers atualmente declara licença GPL-3.0-only.

O Codex deverá verificar licenças novamente antes de reutilizar qualquer código.

---

# 76. Nome do mod

Nome de trabalho:

```text
JustExcavators
```

O nome comunica bem a proposta.

Porém o projeto deverá deixar claro que:

- não é oficial do JustHammers;
- não é mantido necessariamente pelo mesmo autor;
- funciona de forma independente.

A descrição pública poderá mencionar inspiração no conceito de ferramentas AOE.

---

# 77. Tooltips

Cada Excavator deve informar claramente seu perfil.

Exemplo:

```text
Excavation Area: 3×3×1
```

ou:

```text
Mode: Wide
Area: 5×5×1
```

Caso tenha Silk Core:

```text
Enhancement: Native Silk Touch
```

---

# 78. Sneak tooltip

Pode haver uma linha curta:

```text
Hold Shift to disable area mining
```

O texto poderá aparecer sempre ou apenas com tooltip avançado.

---

# 79. Traduções

A 1.0 deverá possuir pelo menos:

```text
en_us
pt_br
```

Inglês como idioma principal de publicação.

Português brasileiro como tradução oficial desde o lançamento.

---

# 80. Advancements

Não são essenciais, mas podem dar personalidade ao mod.

Sugestões:

---

## Bigger Shovel

Obter a primeira Excavator.

---

## Digging Deeper

Aplicar um Deep Excavation Core.

---

## Wide Open

Aplicar um Wide Excavation Core.

---

## Handle With Care

Aplicar um Silk Core.

---

# 81. Configuração

Configuração não é obrigatória na primeira versão.

Não adicionar biblioteca de config apenas por adicionar.

Caso surja necessidade real, possibilidades futuras:

- habilitar/desabilitar determinados modos;
- alterar consumo de durabilidade;
- bloquear 5×5;
- controlar comportamento de Sneak;
- whitelist/blacklist de blocos.

Primeiro preferir tags e datapacks quando possível.

---

# 82. Estrutura técnica conceitual

O Codex deverá propor a estrutura definitiva.

Uma separação possível:

```text
justexcavators/
├── JustExcavators
├── item/
│   ├── ExcavatorItem
│   └── ModItems
├── excavation/
│   ├── ExcavationMode
│   ├── ExcavationAreaCalculator
│   └── ExcavationHandler
├── component/
│   ├── ExcavatorComponents
│   └── EnhancementData
├── core/
│   ├── ExcavationCore
│   └── EnhancementCore
├── compat/
│   └── justhammers/
├── registry/
├── datagen/
└── client/
```

Isso é apenas direção conceitual.

O Codex deverá adequar às convenções atuais do Fabric 26.3.

---

# 83. Separar cálculo de área da quebra

Essa separação é importante.

Exemplo:

```text
ExcavationAreaCalculator
```

deve determinar:

```text
quais posições seriam afetadas
```

sem necessariamente quebrá-las.

Depois:

```text
ExcavationHandler
```

valida e executa.

Isso facilita:

- testes;
- preview futuro;
- diferentes modos;
- manutenção.

---

# 84. Enum de modos

Conceitualmente:

```text
BASIC
DEEP
WIDE
```

Cada modo pode expor:

- largura;
- altura;
- profundidade;
- nome;
- tooltip.

Evitar espalhar números mágicos como `3`, `5` e `27` pelo código.

---

# 85. Enhancement system

Projetar desde o começo para aceitar novas melhorias.

Inicialmente:

```text
SILK
```

Futuramente:

```text
SMELTING
FORTUNE
...
```

Não implementar funcionalidades futuras agora.

Apenas evitar arquitetura que impossibilite expansão.

---

# 86. Data generation

Utilizar datagen quando apropriado para:

- recipes;
- models;
- item tags;
- language data quando conveniente;
- advancements;
- loot-related data.

Evitar JSON duplicado manualmente quando a geração trouxer benefício claro.

---

# 87. Assets

Cada item deverá possuir textura 16×16 inicialmente.

Arquivos esperados:

```text
iron_excavator.png
golden_excavator.png
diamond_excavator.png
netherite_excavator.png

excavation_core.png
deep_excavation_core.png
wide_excavation_core.png
silk_core.png
```

Nomes definitivos podem mudar.

---

# 88. Testes prioritários

O Codex deverá criar estratégia de testes cobrindo pelo menos:

---

## Área Basic

- chão;
- teto;
- parede norte;
- parede sul;
- parede leste;
- parede oeste.

---

## Área Deep

Confirmar que profundidade avança para dentro da face atingida.

---

## Área Wide

Confirmar centralização correta do `5×5×1`.

---

## Sneak

Confirmar:

```text
Shift
→ 1 bloco
```

em todos os modos.

---

## Blocos misturados

Área contendo:

```text
Dirt
Stone
Chest
Sand
```

Somente blocos válidos devem ser afetados.

---

## Durabilidade

Confirmar consumo por bloco efetivamente quebrado.

---

## Unbreaking

Confirmar comportamento correto.

---

## Silk Core

Testar:

- Grass Block;
- Podzol;
- Mycelium;
- outros casos relevantes.

---

## Silk × Fortune

Confirmar incompatibilidade.

---

## Multiplayer

Testar em servidor dedicado.

---

## Netherite upgrade

Confirmar preservação de:

- perfil;
- enhancements;
- enchantments;
- nome;
- dados do item.

---

## Compatibilidade sem JustHammers

JustExcavators deve iniciar normalmente.

---

## Compatibilidade com JustHammers

Ambos devem carregar juntos sem conflitos.

---

# 89. Performance

Uma ação poderá avaliar até:

```text
27 blocos
```

na versão inicial.

Isso é pequeno, mas a implementação ainda deve evitar:

- loops desnecessários;
- chamadas duplicadas;
- atualizações de mundo em excesso;
- lógica client/server duplicada.

---

# 90. Segurança contra recursão

Ao quebrar blocos adicionais programaticamente:

garantir que cada quebra secundária não dispare novamente outra escavação AOE.

Sem proteção, uma ação poderia provocar:

```text
AOE
→ cada bloco inicia outro AOE
→ cadeia enorme
```

Esse risco deve ser considerado explicitamente.

---

# 91. Ordem de quebra

O Codex deverá avaliar se existe benefício em uma ordem específica.

Possíveis critérios:

- bloco central primeiro;
- distância do centro;
- camada por camada.

O comportamento deve ser determinístico.

---

# 92. Som e partículas

Cada bloco pode utilizar comportamento vanilla de quebra.

Evitar reproduzir efeitos 25 vezes de forma exagerada caso gere poluição visual ou sonora.

Avaliar durante testes.

---

# 93. Creative Mode

Em Creative:

- AOE pode funcionar;
- não deve consumir durabilidade;
- comportamento deve permanecer previsível.

O Codex deverá confirmar expectativa vanilla.

---

# 94. Adventure Mode

Respeitar regras vanilla de quebra.

Não permitir que AOE contorne restrições de Adventure.

---

# 95. Spectator

Nenhuma escavação.

---

# 96. Escopo da versão 1.0

A versão 1.0 deverá focar somente no essencial.

## Itens

```text
Iron Excavator
Golden Excavator
Diamond Excavator
Netherite Excavator

Excavation Core
Deep Excavation Core
Wide Excavation Core
Silk Core
```

## Sistemas

```text
3×3×1
3×3×3
5×5×1

Sneak = 1×1

durabilidade por bloco
encantamentos vanilla
Silk Core
Diamond → Netherite
tooltips
recipes
translations
datagen
multiplayer
compatibilidade opcional segura com JustHammers
```

---

# 97. Fora do escopo da 1.0

Não implementar inicialmente:

- 5×5×3;
- 5×5×5;
- Smelting Core;
- Fortune Core;
- sistema de sockets com GUI;
- preview avançado;
- máquinas;
- energia;
- minério próprio;
- Wooden Excavator;
- Stone Excavator;
- Forge;
- NeoForge;
- ports para versões antigas;
- integração obrigatória com JustHammers.

---

# 98. Roadmap conceitual

## 1.0

Fundação do mod.

```text
Fabric 26.3
Excavators
Basic / Deep / Wide
Silk Core
```

---

## 1.x

Possíveis melhorias:

- preview de área;
- configs;
- novos Enhancement Cores;
- melhorias de compatibilidade;
- refinamento de recipes.

---

## Ports

Após estabilidade:

```text
26.2
26.1.x
1.21.x
1.20.1
```

A ordem deverá ser baseada em demanda.

---

## Multi-loader

Somente depois de estabilizar Fabric.

Avaliar:

```text
NeoForge
Forge
```

Não iniciar arquitetura multiplataforma complexa antes de possuir uma versão funcional.

---

# 99. Relação futura com Divine Armor

JustExcavators e Divine Armor serão projetos separados.

Ambos começam:

```text
Fabric
```

e devem seguir uma filosofia semelhante de qualidade e suporte.

Futuramente pode existir:

```text
Divine Excavator
```

como integração temática.

Isso NÃO faz parte do escopo atual.

---

# 100. Objetivo de experiência

O jogador deve sentir:

```text
“Quero limpar essa área de terra.”
→ Basic
```

```text
“Quero remover uma grande camada da superfície.”
→ Wide
```

```text
“Quero realmente cavar para dentro.”
→ Deep
```

```text
“Quero preservar os blocos.”
→ Silk
```

A ferramenta ideal depende da tarefa.

---

# 101. Principal diferencial

O diferencial do JustExcavators não será:

> “Nossa pá quebra mais blocos que todas as outras.”

Será:

> **“Nossa pá permite escolher a forma certa de escavar.”**

---

# 102. Instruções para o Codex

Este documento representa o briefing funcional e criativo do JustExcavators.

Antes de escrever código, o Codex deverá:

1. analisar todos os requisitos;
2. confirmar APIs atuais do Fabric para Minecraft 26.3;
3. verificar versões atuais de Fabric Loader, Fabric API, Loom e Gradle;
4. propor arquitetura;
5. definir estratégia para Item Components;
6. definir como Cores serão aplicados;
7. definir como recipes preservarão dados da ferramenta;
8. definir cálculo de área;
9. definir execução segura de AOE;
10. definir tratamento de drops;
11. definir Silk Core;
12. definir durabilidade;
13. definir estratégia de compatibilidade opcional com JustHammers;
14. identificar riscos de multiplayer;
15. identificar riscos de claims/proteções;
16. propor testes;
17. dividir implementação em etapas pequenas;
18. manter o escopo da 1.0.

---

# 103. O Codex NÃO deve

- transformar JustHammers em dependência obrigatória;
- copiar 5×5×5 apenas porque existe no JustHammers;
- criar dezenas de combinações como itens separados;
- adicionar novos minérios;
- criar GUI complexa sem necessidade;
- implementar Forge/NeoForge antes da versão Fabric estar estável;
- adicionar Enhancement Cores fora do escopo sem justificativa;
- ignorar multiplayer;
- quebrar blocos diretamente sem considerar eventos e compatibilidade;
- usar APIs antigas sem verificar Fabric 26.3;
- iniciar implementação sem primeiro produzir um plano.

---

# 104. Decisões já tomadas

Estas decisões devem ser tratadas como parte da visão atual.

```text
Mod independente
Fabric primeiro
Minecraft 26.3 como alvo inicial
JustHammers opcional
Cores próprios
Basic = 3×3×1
Deep = 3×3×3
Wide = 5×5×1
Sem 5×5×5
Silk Core na primeira versão
Shift = 1×1
Iron / Gold / Diamond / Netherite
Arquitetura preparada para multi-versão
```

---

# 105. Questões ainda abertas

O Codex poderá propor alternativas para:

## Recipes

- crafting da Excavator;
- crafting dos Cores;
- custo dos Cores.

## Aplicação de Cores

- crafting;
- Smithing Table;
- custom recipe;
- substituição de Core.

## Dados

- Item Components específicos;
- serialização;
- sincronização.

## Compatibilidade

- melhor forma de recipes condicionais com JustHammers;
- tags compartilhadas.

## Balanceamento

- velocidade;
- durabilidade base;
- custo de recipes;
- enchantability.

---

# 106. Referências técnicas atuais

## Fabric — Minecraft 26.3

Anúncio oficial:

https://www.fabricmc.net/2026/09/15/263.html

No momento da definição deste documento:

```text
Loom 1.17
Gradle 9.6.0
Fabric Loader 0.19.5
```

Verificar novamente antes de iniciar.

---

## Fabric — Minecraft 26.1

Referência importante sobre a mudança de tooling/mappings:

https://www.fabricmc.net/2026/03/14/261.html

---

## JustHammers

Página de referência:

https://modrinth.com/mod/just-hammers

JustHammers possui atualmente suporte a Minecraft 26.3 em Fabric, além de diversas versões anteriores.

---

# 107. Resumo final

JustExcavators será um mod Fabric independente focado em escavação AOE.

Ele não tentará competir através da maior área possível.

Sua identidade será baseada em:

```text
Basic
→ 3×3×1

Deep
→ 3×3×3

Wide
→ 5×5×1
```

Esses perfis serão complementados por Enhancement Cores, começando por:

```text
Silk Core
→ Silk Touch nativo
```

A ferramenta terá:

```text
Shift = quebra normal 1×1
```

e respeitará:

- drops;
- encantamentos;
- durabilidade;
- multiplayer;
- blocos apropriados para shovel.

JustHammers será uma integração opcional, nunca uma dependência obrigatória.

A primeira versão será desenvolvida para:

```text
Minecraft 26.3
Fabric
```

e, após estabilização, o projeto deverá ser portado para outras versões relevantes para ampliar o público.

A filosofia principal do mod é:

> **Material define quão boa é a ferramenta. Core define o que ela faz.**

E o principal diferencial será:

> **Não escavar o máximo possível, mas escavar da forma certa para cada trabalho.**

---

**Documento preparado para handoff ao Codex.**
