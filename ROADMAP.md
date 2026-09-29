# JustExcavators — Roadmap de Implementação

> **Alvo inicial:** Minecraft 26.3, Fabric, Java 25  
> **Escopo:** versão 1.0 definida em `JUSTEXCAVATORS_IDEA.md`  
> **Última atualização:** 2026-09-29  
> **Estado atual:** Fases 0 a 8 concluídas; próxima etapa é a Fase 9

Este documento é o guia operacional do desenvolvimento. Ele registra decisões,
ordem de execução, critérios de aceite e pontos de retomada para que o trabalho
possa continuar com segurança mesmo após uma troca de contexto.

O documento de visão continua sendo a fonte de verdade para produto e gameplay:

- `JUSTEXCAVATORS_IDEA.md`

O projeto JustHammers disponível no workspace é somente uma referência técnica.
JustExcavators deve continuar independente, sem dependência obrigatória e sem
copiar código GPL diretamente.

## 1. Escopo fechado da versão 1.0

### Ferramentas

- Stone Excavator
- Copper Excavator
- Iron Excavator
- Golden Excavator
- Diamond Excavator
- Netherite Excavator

### Cores

- Deep Excavation Core
- Wide Excavation Core
- Advanced Excavation Core
- Silk Core

### Perfis

| Perfil | Área máxima | Uso principal |
|---|---:|---|
| Basic | `3x3x1` | escavação geral |
| Deep | `3x3x3` | escavação em profundidade |
| Wide | `5x5x1` | limpeza e nivelamento de superfícies |
| Advanced | `5x5x3` | escavação de grande volume |

### Regras essenciais

- Segurar Sneak desativa a escavação em área.
- Somente blocos adequados para shovel podem ser afetados.
- A direção depende da face atingida.
- Deep sempre avança para dentro da face atingida.
- Cada bloco efetivamente quebrado paga seu próprio custo de durabilidade.
- Loot, XP, encantamentos e regras de modo de jogo devem seguir o fluxo vanilla.
- A quebra real é autoritativa no servidor.
- JustHammers é uma integração opcional, nunca uma dependência.

### Fora do escopo

- `5x5x5`
- Smelting Core e Fortune Core
- GUI ou sistema de sockets
- configuração externa
- Forge e NeoForge
- ports para versões anteriores

Os assets de Stone, Copper e Advanced integram a versão 1.0 expandida.

## 2. Decisões arquiteturais

### IDs e estado da ferramenta

Registrar somente uma Excavator por material. Basic, Deep, Wide e Advanced não
devem gerar combinações adicionais de IDs.

Estado previsto da `ItemStack`:

```text
excavation_mode = BASIC | DEEP | WIDE | ADVANCED
enhancements = [SILK]
```

Usar Data Components persistentes e sincronizados, nunca NBT legado.

### Fabricação dos perfis

Fabricar Basic, Deep, Wide e Advanced diretamente na Crafting Table. O modo é
gravado como Data Component no resultado, sem criar IDs adicionais por perfil.

- Basic não usa Core; `core_model.png` é apenas um asset visual, não um item.
- Deep, Wide e Advanced usam seus respectivos Cores no molde da ferramenta.
- A progressão dos Cores é linear: Netherite Shovel -> Deep -> Wide -> Advanced.
- A Smithing Table fica reservada para upgrades de material e enhancements.
- Diamond -> Netherite deve preservar modo, dano, nome e demais componentes.
- Silk é independente e poderá coexistir com qualquer perfil.

### Modelo e texturas

O modelo visual deve ser selecionado pelo componente de modo:

```text
BASIC -> <material>_excavator.png
DEEP  -> <material>_deep_excavator.png
WIDE  -> <material>_wide_excavator.png
ADVANCED -> <material>_advanced_excavator.png
```

Stone, Copper e `advanced_*` integram o conteúdo base; `core_model.png` não
possui item correspondente.

### AOE server-side

Separar completamente cálculo, validação e mutação:

```text
ExcavationAreaCalculator
        -> lista ordenada de posições
ExcavationTargetValidator
        -> posições permitidas
ExcavationHandler
        -> quebra server-side
```

Para maximizar compatibilidade, cada bloco secundário deve passar pelo fluxo
normal de `ServerPlayerGameMode.destroyBlock`, protegido contra recursão.

O AOE só deve começar depois de a quebra do bloco central ter sido concluída
com sucesso. Isso impede que uma região seja escavada quando outro mod ou uma
proteção cancelar o alvo original.

### Silk Core

O Silk Core será implementado por último, depois que todos os demais sistemas
estiverem estáveis.

Direção inicial:

- componente próprio registra a origem da habilidade;
- comportamento de loot reutiliza Silk Touch vanilla;
- Fortune e Silk devem permanecer incompatíveis;
- aplicação redundante deve ser rejeitada;
- tooltip deve indicar `Native Silk Touch`/equivalente.

A implementação definitiva depende de um spike técnico isolado. A solução deve
priorizar compatibilidade com loot tables vanilla, mods e datapacks, sem tabelas
manuais de conversão de blocos.

## 3. Estrutura de código proposta

```text
net.hfstack.justexcavators
├── JustExcavators
├── component
│   ├── ExcavatorComponents
│   └── ExcavatorEnhancements
├── excavation
│   ├── ExcavationMode
│   ├── ExcavationAreaCalculator
│   ├── ExcavationTargetValidator
│   └── ExcavationHandler
├── item
│   ├── ExcavatorItem
│   ├── ModItems
│   └── ModCreativeTab
├── registry
│   └── ModTags
├── compat
│   └── justhammers
├── mixin
│   └── ServerPlayerGameModeMixin
├── client
│   └── ExcavatorTooltipHandler
└── datagen
    ├── ModItemModelProvider
    ├── ModRecipeProvider
    ├── ModItemTagProvider
    ├── ModBlockTagProvider
    └── ModLanguageProvider
```

Os nomes podem ser refinados durante a implementação, mas as responsabilidades
devem continuar separadas.

## 4. Fases de implementação

Cada fase só é considerada concluída quando seus critérios de aceite forem
atendidos e o build permanecer verde.

### Fase 0 — Fundação do projeto

Estado: `CONCLUÍDO`

Tarefas:

- [x] Disponibilizar JDK 25 no ambiente de desenvolvimento.
- [x] Substituir `Loom 1.18-SNAPSHOT` por uma versão estável compatível.
- [x] Confirmar Minecraft, Loader, Fabric API e Gradle configurados.
- [x] Adicionar uma licença própria ao projeto.
- [x] Configurar source sets, entrypoint client e mixins.
- [x] Validar `build`, `runClient`, `runServer` e `runDatagen`.
- [x] Manter o CI construindo com Java 25.

Critérios de aceite:

- build limpo e reproduzível;
- cliente e servidor dedicado inicializam sem erros;
- datagen executa sem sobrescrever assets manuais indevidamente.

### Fase 1 — Registries e itens básicos

Estado: `CONCLUÍDO`

Tarefas:

- [x] Registrar Stone, Copper, Iron, Golden, Diamond e Netherite Excavators.
- [x] Registrar Deep, Wide, Advanced e Silk Cores.
- [x] Criar `ExcavatorItem` com propriedades de shovel.
- [x] Aplicar fire resistance à Netherite Excavator.
- [x] Configurar materiais de reparo.
- [x] Criar creative tab do mod.
- [x] Criar tags de itens necessárias.

Tags previstas:

- `justexcavators:excavators`
- `minecraft:shovels`
- tags vanilla de enchantability, durability, mining loot e vanishing
- uma tag convencional de ferramenta, se houver equivalente adequado no 26.3

Critérios de aceite:

- dez itens aparecem no jogo;
- as seis Excavators funcionam como shovels individuais;
- velocidade, drops, reparo e comportamento Netherite estão corretos;
- nenhuma escavação AOE existe ainda.

### Fase 2 — Data Components e apresentação

Estado: `CONCLUÍDO`

Tarefas:

- [x] Criar enum `ExcavationMode` com largura, altura e profundidade.
- [x] Registrar componente persistente e sincronizado para o modo.
- [x] Preparar componente extensível para enhancements.
- [x] Definir Basic como estado padrão.
- [x] Gerar modelos que selecionam textura pelo componente.
- [x] Implementar tooltips do modo, área e Sneak.
- [x] Adicionar traduções `en_us` e `pt_br`.

Critérios de aceite:

- serialização persiste após salvar e recarregar o mundo;
- cliente e servidor observam o mesmo modo;
- modelo e tooltip mudam corretamente entre Basic, Deep, Wide e Advanced;
- comandos que fornecem uma ferramenta sem componente recebem um default seguro.

### Fase 3 — Receitas e progressão de perfil

Estado: `CONCLUÍDO`

Tarefas:

- [x] Definir recipes em cadeia dos Cores Deep, Wide e Advanced.
- [x] Criar recipes diretas de Basic, Deep, Wide e Advanced para Stone, Copper,
  Iron, Gold e Diamond.
- [x] Não oferecer crafting direto da Netherite Excavator.
- [x] Implementar Diamond -> Netherite usando template e ingot vanilla.
- [x] Preservar modo, nome, dano, encantamentos e componentes no upgrade Netherite.
- [x] Gerar recipe advancements.

Direção inicial para o corpo da Excavator:

```text
MXM
_SM
_S_
```

`M` representa o material, `S` um stick, `_` um espaço vazio e `X` recebe um
stick no Basic ou o Core do perfil. A versão de Stone usa Stone em vez de
Cobblestone.

Critérios de aceite:

- todas as quatro variantes saem da bancada com o componente correto;
- não existe item ou receita de Basic Core;
- upgrade Netherite preserva integralmente o estado da Diamond Excavator.

### Fase 4 — Geometria pura e testes unitários

Estado: `CONCLUÍDO`

Tarefas:

- [x] Implementar `ExcavationAreaCalculator` sem acesso ao mundo.
- [x] Cobrir as seis direções de face.
- [x] Centralizar corretamente áreas 3x3 e 5x5.
- [x] Fazer Deep avançar para dentro da face atingida.
- [x] Definir ordem determinística: profundidade e distância ao centro.
- [x] Adicionar testes unitários para todos os modos e direções.

Critérios de aceite:

- Basic retorna no máximo 9 posições;
- Deep retorna no máximo 27 posições;
- Wide retorna no máximo 25 posições;
- Advanced retorna no máximo 75 posições;
- não há posições duplicadas;
- os testes confirmam chão, teto e as quatro faces laterais;
- o cálculo não depende de estado client-side.

### Fase 5 — Validação de alvos

Estado: `CONCLUÍDO`

Tarefas:

- [x] Validar o bloco central antes de ativar AOE.
- [x] Exigir `#minecraft:mineable/shovel` nos alvos secundários.
- [x] Verificar que a ferramenta é adequada para drops.
- [x] Rejeitar blocos inquebráveis.
- [x] Ignorar block entities por segurança.
- [x] Criar `justexcavators:excavator_no_aoe` para exclusões data-driven.
- [x] Definir e testar a política para fluidos e blocos waterlogged.

Critérios de aceite:

- Dirt, Grass, Sand, Gravel, Clay, Snow, Soul Sand, Soul Soil e Mud funcionam;
- Stone, ores, madeira, chests, furnaces e máquinas não são afetados;
- tags de datapacks conseguem excluir blocos sem alteração de código.

### Fase 6 — Quebra AOE server-side

Estado: `CONCLUÍDO`

Tarefas:

- [x] Capturar com segurança a face da quebra original.
- [x] Executar AOE somente após sucesso do bloco central.
- [x] Chamar o fluxo vanilla de quebra para cada bloco secundário.
- [x] Implementar guarda contra recursão.
- [x] Interromper quando a ferramenta quebrar ou sair da mão principal.
- [x] Implementar Sneak = 1x1.
- [x] Preservar Creative, Adventure e Spectator.
- [x] Avaliar poluição de som e partículas.

Critérios de aceite:

- loot, XP, stats e exaustão funcionam para cada bloco;
- Creative não consome durabilidade;
- Adventure e Spectator não são contornados;
- Sneak sempre quebra somente o alvo central;
- uma quebra secundária nunca inicia outro AOE;
- servidor dedicado produz o mesmo resultado do single-player.

### Fase 7 — Durabilidade e encantamentos vanilla

Estado: `CONCLUÍDO`

Tarefas:

- [x] Confirmar uma tentativa de desgaste por bloco efetivamente quebrado.
- [x] Confirmar Unbreaking individualmente por bloco.
- [x] Confirmar Mending sem lógica especial.
- [x] Confirmar Efficiency no bloco central.
- [x] Confirmar Fortune sem Silk Core.
- [x] Definir multiplicador inicial de durabilidade por material.
- [x] Interromper o AOE de forma segura quando restar pouca durabilidade.

Direção de balanceamento inicial:

- velocidade e enchantability próximas às ferramentas vanilla;
- durabilidade inicial em torno de 3x a shovel correspondente;
- custo real de uma ação continua proporcional aos blocos quebrados.

Critérios de aceite:

- ação completa paga até 9, 27, 25 ou 75 usos antes de Unbreaking;
- blocos ignorados ou protegidos não consomem durabilidade;
- a ferramenta nunca causa quebra infinita após chegar a zero.

Validação desta fase: política de interrupção coberta por teste unitário; fluxo
de desgaste e encantamentos confirmado nas APIs vanilla e nas tags geradas.
GameTests e playtest interativo desses cenários permanecem na Fase 11.

### Fase 8 — Datagen, traduções e acabamento do conteúdo base

Estado: `CONCLUÍDO`

Tarefas:

- [x] Gerar recipes e recipe advancements.
- [x] Gerar item models e item definitions.
- [x] Gerar tags.
- [x] Gerar ou manter traduções de forma consistente.
- [x] Criar advancements opcionais da visão, se não atrasarem a fundação.
- [x] Verificar nomes e mapeamento de todas as texturas usadas.
- [x] Atualizar README com gameplay e recipes finais.

Advancements candidatos:

- Bigger Shovel
- Digging Deeper
- Wide Open
- Handle With Care, após a Fase 10

Critérios de aceite:

- datagen não produz diff inesperado em execuções consecutivas;
- todos os dez itens possuem nome, modelo e textura válidos; itens obtidos em
  Survival possuem recipe válida.

### Fase 9 — Compatibilidade e multiplayer

Estado: `EM ANDAMENTO`

Tarefas:

- [x] Iniciar JustExcavators sem JustHammers.
- [x] Iniciar os dois mods juntos.
- [x] Garantir ausência de imports internos do JustHammers.
- [x] Expor tags estáveis para integrações futuras.
- [ ] Testar cancelamento de quebra por callback/evento.
- [ ] Testar pelo menos um mod de claims compatível com 26.3, se disponível.
- [ ] Testar com latência e dois jogadores próximos.
- [ ] Confirmar sincronização de componentes em servidor dedicado.

Critérios de aceite:

- nenhum crash ou conflito de registry com JustHammers;
- cancelar o bloco central cancela todo o AOE;
- cancelar um bloco secundário não cancela os demais e não cobra durabilidade por ele;
- clientes observam o mesmo modo e resultado de quebra.

Integrações de recipes com Hammer Cores não fazem parte da primeira entrega,
a menos que sejam aprovadas explicitamente depois da base estar estável.

### Fase 10 — Silk Core

Estado: `PENDENTE — EXECUTAR POR ÚLTIMO`

Pré-requisito: Fases 0 a 9 concluídas e estáveis.

Tarefas:

- [ ] Fazer spike técnico isolado da integração com loot vanilla.
- [ ] Registrar o enhancement Silk no componente da ferramenta.
- [ ] Implementar aplicação pela Smithing Table.
- [ ] Preservar modo, dano, nome e demais encantamentos.
- [ ] Rejeitar Silk em ferramenta com Fortune.
- [ ] Rejeitar Silk redundante ou Silk Touch já existente.
- [ ] Impedir aplicação posterior de Fortune pela mecânica vanilla.
- [ ] Garantir o mesmo resultado no bloco central e no AOE.
- [ ] Implementar tooltip `Native Silk Touch`.
- [ ] Ocultar apenas a linha redundante de Silk Touch, se isso for seguro.

Casos obrigatórios:

- Grass Block
- Podzol
- Mycelium
- blocos de Snow compatíveis
- blocos adicionados por datapack/mod com loot baseado em Silk Touch
- Basic + Silk
- Deep + Silk
- Wide + Silk
- Diamond + Silk -> Netherite preservando Silk

Critérios de aceite:

- nenhuma tabela fixa `bloco -> drop` no código;
- loot utiliza a semântica vanilla de Silk Touch;
- Fortune nunca produz efeito junto do Silk Core;
- ferramenta não perde ou duplica componentes durante o upgrade;
- comportamento é idêntico em servidor dedicado.

### Fase 11 — Validação de release 1.0

Estado: `PENDENTE`

Tarefas:

- [ ] Executar todos os testes unitários e GameTests.
- [ ] Executar build e datagen limpos.
- [ ] Fazer playtest de Basic, Deep e Wide em todas as faces.
- [ ] Testar terrenos mistos.
- [ ] Testar Sand e Gravel em cascata.
- [ ] Testar todas as combinações de material, modo e Silk.
- [ ] Verificar recipes e progressão em Survival.
- [ ] Revisar performance e logs.
- [ ] Revisar licença, créditos e metadados do mod.
- [ ] Produzir changelog e artefato de release.

Critérios de aceite:

- nenhuma falha conhecida que cause perda indevida de blocos ou itens;
- nenhuma quebra além da área prevista;
- nenhuma dependência obrigatória em JustHammers;
- build de servidor dedicado validado;
- documentação da 1.0 corresponde ao comportamento real.

## 5. Estratégia de testes

### Testes unitários

- geometria dos três modos nas seis direções;
- contagem, centralização e ausência de duplicatas;
- ordenação determinística;
- codec e sincronização dos componentes;
- codecs e componentes dos quatro perfis.

### GameTests

- áreas com Dirt, Stone, Chest e Sand misturados;
- Sneak em todos os modos;
- consumo de durabilidade;
- interrupção por ferramenta quebrada;
- Creative e Adventure;
- cancelamento de bloco central e secundário;
- Diamond -> Netherite preservando dados;
- casos de Silk da Fase 10.

### Testes manuais

- chão, teto e quatro paredes;
- servidor dedicado;
- dois jogadores simultâneos;
- blocos com gravidade;
- mods de claims/proteção;
- coexistência com JustHammers.

## 6. Riscos registrados

### Mixins e mudança de versão

O hook de `ServerPlayerGameMode` é sensível à versão. Manter apenas um mixin
pequeno e coberto por testes. Toda tentativa de port deve revalidar seu alvo.

### Claims e proteções

Chamar `destroyBlock` para cada posição preserva o máximo possível do fluxo
vanilla e de callbacks Fabric, mas alguns mods podem depender do pacote original
do cliente. Compatibilidade perfeita não deve ser presumida sem testes.

### Silk Core

É o ponto de maior risco porque loot tables verificam o estado real da ferramenta.
Por isso foi movido para a última fase funcional e terá um spike próprio.

### Assets divergentes do escopo

Stone, Copper e Advanced foram incorporados ao escopo após playtest. Revalidar
sempre suas recipes, durabilidade e modelos junto aos demais tiers.

### Licença do JustHammers

JustHammers declara GPL-3.0-only. Estudar comportamento é permitido, mas copiar
implementação pode impor obrigações de licença ao projeto inteiro. Escrever uma
implementação original e registrar créditos de inspiração quando apropriado.

## 7. Registro de progresso

Atualizar esta seção ao concluir cada etapa.

| Fase | Estado | Observações |
|---|---|---|
| 0 — Fundação | Concluído | MIT; build, client, server e datagen validados |
| 1 — Registries | Concluído | Dez itens, creative tab e tags geradas; durabilidade inicial 3x vanilla |
| 2 — Components | Concluído | Modo persistente/sincronizado, modelos dinâmicos, tooltips e traduções |
| 3 — Receitas | Concluído | Perfis fabricados diretamente; Cores em cadeia; ferraria reservada ao upgrade Netherite |
| 4 — Geometria | Concluído | Seis faces testadas; áreas centralizadas e ordenadas centro-para-fora |
| 5 — Validação | Concluído | Política testada; block entities, fluidos e tag de exclusão protegidos |
| 6 — AOE server-side | Concluído | Fluxo vanilla por bloco, guarda de recursão e proteções preservadas |
| 7 — Durabilidade | Concluído | Desgaste vanilla por bloco, encantamentos preservados e último ponto protegido |
| 8 — Conteúdo/datagen | Concluído | Datagen idempotente; `core_model.png` mantido apenas como asset; Bigger Shovel, Digging Deeper e Wide Open adicionados; Silk continua reservado à Fase 10 |
| 9 — Compatibilidade | Em andamento | Inicialização standalone e com JustHammers 26.3.0.1 + Nanite Library 26.3.0.6 confirmada; testes de callbacks, claims e multiplayer aguardam mundo de servidor com EULA aceita |
| 10 — Silk Core | Pendente, por último | |
| 11 — Release | Pendente | |

Estados permitidos:

```text
PENDENTE
EM ANDAMENTO
BLOQUEADO
CONCLUÍDO
```

## 8. Checklist para retomada de contexto

Ao iniciar uma nova sessão de desenvolvimento:

1. Ler `JUSTEXCAVATORS_IDEA.md` para visão e restrições.
2. Ler este `ROADMAP.md`.
3. Consultar a tabela de progresso acima.
4. Verificar `git status` e preservar alterações existentes.
5. Rodar os testes da última fase concluída.
6. Trabalhar apenas na próxima fase incompleta.
7. Atualizar este documento ao concluir ou alterar uma decisão.

## 9. Próximo passo

Iniciar a **Fase 9 — Compatibilidade e multiplayer**. Nenhuma lógica de Silk
Core deve ser implementada antes da conclusão das Fases 0 a 9.
