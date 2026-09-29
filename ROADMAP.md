# JustExcavators — Roadmap de Implementação

> **Alvo inicial:** Minecraft 26.3, Fabric, Java 25  
> **Escopo:** versão 1.0 definida em `JUSTEXCAVATORS_IDEA.md`  
> **Última atualização:** 2026-09-29  
> **Estado atual:** Fases 0 a 7 concluídas; próxima etapa é a Fase 8

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

- Iron Excavator
- Golden Excavator
- Diamond Excavator
- Netherite Excavator

### Cores

- Excavation Core
- Deep Excavation Core
- Wide Excavation Core
- Silk Core

### Perfis

| Perfil | Área máxima | Uso principal |
|---|---:|---|
| Basic | `3x3x1` | escavação geral |
| Deep | `3x3x3` | escavação em profundidade |
| Wide | `5x5x1` | limpeza e nivelamento de superfícies |

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

- Stone e Copper Excavators
- `5x5x3` e `5x5x5`
- Smelting Core e Fortune Core
- preview visual avançado
- GUI ou sistema de sockets
- configuração externa
- Forge e NeoForge
- ports para versões anteriores

As texturas Stone, Copper e `advanced_*` já existentes devem ser preservadas,
mas não utilizadas como autorização para ampliar a versão 1.0.

## 2. Decisões arquiteturais

### IDs e estado da ferramenta

Registrar somente uma Excavator por material. Basic, Deep, Wide e Silk não
devem gerar combinações adicionais de IDs.

Estado previsto da `ItemStack`:

```text
excavation_mode = BASIC | DEEP | WIDE
enhancements = [SILK]
```

Usar Data Components persistentes e sincronizados, nunca NBT legado.

### Aplicação dos Cores

Usar uma interface vanilla, preferencialmente a Smithing Table.

- Basic, Deep e Wide são mutuamente exclusivos.
- Aplicar outro Excavation Core substitui o perfil atual.
- O Core anterior não é devolvido.
- Aplicar o mesmo perfil novamente deve ser rejeitado para evitar desperdício.
- Silk é independente e pode coexistir com qualquer perfil.
- Todos os dados não relacionados ao upgrade devem ser preservados.

### Modelo e texturas

O modelo visual deve ser selecionado pelo componente de modo:

```text
BASIC -> <material>_excavator.png
DEEP  -> <material>_deep_excavator.png
WIDE  -> <material>_wide_excavator.png
```

Arquivos `advanced_*`, Stone e Copper ficam reservados. Antes da fase de assets,
normalizar a correspondência entre `core_model.png`,
`advanced_excavation_core.png` e o nome definitivo do Basic Excavation Core.

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
├── recipe
│   └── CoreUpgradeRecipe
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

- [x] Registrar Iron, Golden, Diamond e Netherite Excavators.
- [x] Registrar os quatro Cores.
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

- oito itens aparecem no jogo;
- as quatro Excavators funcionam como shovels individuais;
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
- modelo e tooltip mudam corretamente entre Basic, Deep e Wide;
- comandos que fornecem uma ferramenta sem componente recebem um default seguro.

### Fase 3 — Receitas e troca de perfil

Estado: `CONCLUÍDO`

Tarefas:

- [x] Definir recipes provisórias dos três Excavation Cores.
- [x] Criar recipes das Excavators Iron, Gold e Diamond com Basic Core.
- [x] Não oferecer crafting direto da Netherite Excavator.
- [x] Implementar upgrades Basic, Deep e Wide pela Smithing Table.
- [x] Preservar nome, dano, encantamentos e componentes.
- [x] Rejeitar aplicação do mesmo modo atual.
- [x] Implementar Diamond -> Netherite usando template e ingot vanilla.
- [x] Gerar recipe advancements.

Direção inicial para o corpo da Excavator:

```text
M C M
M S M
  S
```

`M` representa o material, `C` o Basic Core e `S` um stick. Custos finais devem
ser refinados em playtest, sem alterar a arquitetura.

Critérios de aceite:

- troca Basic <-> Deep <-> Wide funciona em todos os materiais;
- nenhum upgrade duplica itens ou componentes;
- aplicar o mesmo Core não consome recursos;
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

- ação completa paga até 9, 27 ou 25 usos antes de Unbreaking;
- blocos ignorados ou protegidos não consomem durabilidade;
- a ferramenta nunca causa quebra infinita após chegar a zero.

Validação desta fase: política de interrupção coberta por teste unitário; fluxo
de desgaste e encantamentos confirmado nas APIs vanilla e nas tags geradas.
GameTests e playtest interativo desses cenários permanecem na Fase 11.

### Fase 8 — Datagen, traduções e acabamento do conteúdo base

Estado: `PENDENTE`

Tarefas:

- [ ] Gerar recipes e recipe advancements.
- [ ] Gerar item models e item definitions.
- [ ] Gerar tags.
- [ ] Gerar ou manter traduções de forma consistente.
- [ ] Criar advancements opcionais da visão, se não atrasarem a fundação.
- [ ] Verificar nomes e mapeamento de todas as texturas usadas.
- [ ] Atualizar README com gameplay e recipes finais.

Advancements candidatos:

- Bigger Shovel
- Digging Deeper
- Wide Open
- Handle With Care, após a Fase 10

Critérios de aceite:

- datagen não produz diff inesperado em execuções consecutivas;
- todos os oito itens possuem nome, modelo, textura e recipe válidos;
- não existem referências a Stone, Copper ou Advanced na distribuição 1.0.

### Fase 9 — Compatibilidade e multiplayer

Estado: `PENDENTE`

Tarefas:

- [ ] Iniciar JustExcavators sem JustHammers.
- [ ] Iniciar os dois mods juntos.
- [ ] Garantir ausência de imports internos do JustHammers.
- [ ] Expor tags estáveis para integrações futuras.
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
- regras de substituição de perfil.

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

Há assets para Stone, Copper e Advanced, enquanto a visão da 1.0 fecha quatro
materiais e três perfis. Preservar os arquivos, mas não registrar conteúdo extra.

### Licença do JustHammers

JustHammers declara GPL-3.0-only. Estudar comportamento é permitido, mas copiar
implementação pode impor obrigações de licença ao projeto inteiro. Escrever uma
implementação original e registrar créditos de inspiração quando apropriado.

## 7. Registro de progresso

Atualizar esta seção ao concluir cada etapa.

| Fase | Estado | Observações |
|---|---|---|
| 0 — Fundação | Concluído | MIT; build, client, server e datagen validados |
| 1 — Registries | Concluído | Oito itens, creative tab e tags geradas; durabilidade inicial 3x vanilla |
| 2 — Components | Concluído | Modo persistente/sincronizado, modelos dinâmicos, tooltips e traduções |
| 3 — Receitas | Concluído | Dez receitas/advancements; perfis substituíveis e upgrade Netherite |
| 4 — Geometria | Concluído | Seis faces testadas; áreas centralizadas e ordenadas centro-para-fora |
| 5 — Validação | Concluído | Política testada; block entities, fluidos e tag de exclusão protegidos |
| 6 — AOE server-side | Concluído | Fluxo vanilla por bloco, guarda de recursão e proteções preservadas |
| 7 — Durabilidade | Concluído | Desgaste vanilla por bloco, encantamentos preservados e último ponto protegido |
| 8 — Conteúdo/datagen | Pendente | |
| 9 — Compatibilidade | Pendente | |
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

Iniciar a **Fase 8 — Datagen, traduções e acabamento do conteúdo base**. Nenhuma lógica de
Silk Core deve ser implementada antes da conclusão das Fases 0 a 9.
