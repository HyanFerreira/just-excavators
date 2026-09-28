# Just Excavators

Just Excavators is an independent Fabric mod that brings specialized
area-mining shovels to Minecraft while keeping a predictable, vanilla-style
experience.

Instead of making every upgrade excavate a larger volume, the mod is designed
around distinct excavation profiles:

- Basic: `3x3x1` for general terrain work;
- Deep: `3x3x3` for digging into large volumes;
- Wide: `5x5x1` for clearing and leveling surfaces.

Holding sneak disables area mining so that an Excavator breaks only one block.
Future integration with JustHammers will be optional; Just Excavators will
always work as a standalone mod.

## Status

The project is in its initial development stage and currently targets:

- Minecraft 26.3;
- Fabric Loader 0.19.5;
- Java 25.

The complete product vision and planned scope are documented in
[JUSTEXCAVATORS_IDEA.md](JUSTEXCAVATORS_IDEA.md).

## Development

Build the project with:

```bash
./gradlew build
```

The generated mod artifacts will be available in `build/libs`.
