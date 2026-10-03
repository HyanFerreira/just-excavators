# Just Excavators

**Just Excavators** é um mod independente para Fabric que adiciona pás especializadas em escavação de área ao
Minecraft, mantendo uma experiência previsível e próxima das regras vanilla.

Em vez de transformar cada melhoria apenas em uma área maior, o mod oferece perfis de escavação distintos, seis
materiais de ferramenta e uma Bancada de Aprimoramento dedicada para instalar efeitos adicionais. A direção da área
é definida pela face atingida, enquanto loot, experiência, durabilidade, encantamentos e permissões continuam usando
o fluxo normal do jogo.

---

## Versão Atual

### 1.0.0

Principais funcionalidades:

- Escavadoras de Pedra, Cobre, Ferro, Ouro, Diamante e Netherita.
- Quatro perfis de escavação: Básico, Profundo, Amplo e Avançado.
- Escavação orientada pela face atingida e executada pelo servidor.
- Sneak desativa temporariamente a escavação em área.
- Bancada de Aprimoramento com dois espaços fixos para Núcleos compatíveis.
- Núcleos de Seda, Coletor, Fundição, Filtro e Vazio.
- Progressão própria de fabricação e upgrade de Diamante para Netherita.
- Advancements, tooltips e traduções em inglês e português brasileiro.
- Compatibilidade opcional com JustHammers, sem dependência obrigatória.

---

## Funcionalidades

### Perfis de Escavação

Cada Escavadora possui um perfil que define o formato máximo da área:

| Perfil | Área | Uso principal |
|---|---:|---|
| Básico | `3x3x1` | Escavação geral de terreno |
| Profundo | `3x3x3` | Abertura de volumes em profundidade |
| Amplo | `5x5x1` | Limpeza e nivelamento de superfícies |
| Avançado | `5x5x3` | Escavação de grande volume |

- A face atingida determina a orientação da área.
- Os perfis Profundo e Avançado avançam para dentro da face atingida.
- Segurar Sneak limita a ação ao bloco central.
- Apenas blocos adequados para pás podem ser incluídos na área adicional.

### Escavação Segura e Vanilla

Cada bloco adicional passa pelo fluxo normal de quebra do Minecraft.

- Blocos protegidos, inquebráveis, excluídos, com fluidos ou com block entity são ignorados.
- Loot, experiência, estatísticas e permissões de modo de jogo são preservados.
- Cada bloco efetivamente quebrado paga seu próprio custo de durabilidade.
- Unbreaking, Mending, Efficiency e Fortune continuam seguindo as regras vanilla.
- A escavação em área só começa depois que a quebra do bloco central é confirmada.

### Materiais

As Escavadoras estão disponíveis em Pedra, Cobre, Ferro, Ouro, Diamante e Netherita.

Os perfis são armazenados na própria ferramenta. O upgrade de Diamante para Netherita preserva perfil, dano, nome,
encantamentos e demais componentes.

### Bancada de Aprimoramento

A **Bancada de Aprimoramento** administra dois espaços fixos de Núcleo em cada Escavadora.

- Instala, remove e substitui Núcleos sem recriar a ferramenta.
- Mantém todas as alterações autoritativas no servidor.
- Rejeita Núcleos duplicados, combinações incompatíveis e conflitos com encantamentos.
- Exibe um painel de compatibilidade dentro da interface.
- Inclui um resource pack interno opcional para restaurar a aparência vanilla da interface.

### Núcleos de Aprimoramento

- **Núcleo de Seda:** calcula o loot com a semântica vanilla de Silk Touch sem gravar o encantamento
  permanentemente na ferramenta.
- **Núcleo Coletor:** envia os drops ao inventário do jogador e deixa no mundo os itens que não couberem.
- **Núcleo de Fundição:** processa drops usando as receitas atuais de fornalha. Uma transformação bem-sucedida cobra
  um ponto adicional de durabilidade e não gera experiência de fornalha.
- **Núcleo de Filtro:** restringe os blocos adicionais da área ao mesmo tipo do bloco central.
- **Núcleo do Vazio:** descarta drops e experiência dos blocos quebrados.

### Compatibilidade entre Núcleos

- Seda combina com Coletor, Fundição ou Filtro.
- Coletor combina com Fundição ou Filtro.
- Fundição combina com Filtro.
- Vazio combina somente com Filtro.
- Núcleos repetidos não são permitidos.
- Fortune não pode coexistir com o Núcleo de Seda.

### Advancements

A progressão principal acompanha a obtenção e o uso dos perfis Básico, Profundo, Amplo e Avançado. Uma segunda
ramificação apresenta a Bancada de Aprimoramento e os cinco Núcleos, incluindo desafios para instalar um par
compatível e reunir a coleção completa.

---

## Itens e Receitas

### Escavadoras

O perfil Básico usa o material da ferramenta e gravetos:

```text
Material | Graveto | Material
Vazio    | Graveto | Material
Vazio    | Graveto | Vazio
```

Os perfis Profundo, Amplo e Avançado usam o mesmo formato, substituindo o graveto superior pelo Núcleo de Escavação
correspondente. A versão de Pedra usa Pedra, não Pedregulho.

### Núcleos de Escavação

- **Profundo:** seis Pós de Redstone, dois Blocos de Ferro e uma Escavadora de Netherita.
- **Amplo:** seis Blocos de Redstone, dois Blocos de Ouro e um Núcleo de Escavação Profunda.
- **Avançado:** seis Blocos de Redstone, dois Blocos de Diamante e um Núcleo de Escavação Ampla.

### Estrutura de Núcleo

```text
Pepita de Ferro | Barra de Ferro | Pepita de Ferro
Barra de Ferro  | Vazio          | Barra de Ferro
Pepita de Ferro | Barra de Ferro | Pepita de Ferro
```

### Bancada de Aprimoramento

```text
Tábuas                     | Tábuas                     | Tábuas
Tábuas                     | Bigorna                    | Tábuas
Ladrilhos de Ardósia Abissal | Ladrilhos de Ardósia Abissal | Ladrilhos de Ardósia Abissal
```

### Núcleos de Aprimoramento

Cada Núcleo coloca uma Estrutura de Núcleo no centro, cercada por quatro unidades de cada material alternadas:

- **Seda:** Linha e Esmeralda.
- **Coletor:** Pérola do End e Lápis-Lazúli.
- **Fundição:** Creme de Magma e Pó de Blaze.
- **Filtro:** Quartzo e Fragmento de Ametista.
- **Vazio:** Olho do End e Pérola do End.

### Upgrade para Netherita

Use uma Escavadora de Diamante, um Molde de Ferraria de Melhoria de Netherita e uma Barra de Netherita na Bancada de
Ferraria. O perfil e o estado da ferramenta são preservados.

---

## Requisitos

- **Minecraft:** 1.21.1
- **Loader:** Fabric
- **Java:** 25 ou superior
- **Fabric API**

O mod deve ser instalado no cliente e no servidor.

---

## Observações Técnicas

- A quebra em área é executada exclusivamente no servidor.
- Somente blocos da tag `#minecraft:mineable/shovel` são considerados como alvos adicionais.
- Datapacks podem excluir blocos da escavação usando `justexcavators:excavator_no_aoe`.
- Block entities e blocos com fluidos são ignorados por segurança.
- Ferramentas administrativas com combinações inválidas desativam os efeitos dos Núcleos naquela quebra.
- JustHammers é apenas uma integração opcional e não é necessário para carregar ou jogar o mod.

---

## Desenvolvimento

O desenvolvimento requer um JDK Java 21. Certifique-se de que `JAVA_HOME` aponta para ele antes de executar o Gradle.

Build do projeto:

```bash
./gradlew build
```

Geração de dados:

```bash
./gradlew runDatagen
```

Os artefatos são gerados em `build/libs`, enquanto os dados gerados e versionados ficam em `src/main/generated`.

---

## Créditos

Criado por **Hyan Ferreira**.

JustHammers serviu como inspiração de gameplay e referência de compatibilidade. Just Excavators é uma implementação
independente e não copia o código GPL do projeto.

---

## Licença

Este mod está disponível sob a [Licença MIT](LICENSE).

---

## Links

- GitHub: https://github.com/HyanFerreira/just-excavators
- Issues: https://github.com/HyanFerreira/just-excavators/issues
- Perfil do autor: https://github.com/HyanFerreira
