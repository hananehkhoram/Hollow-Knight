# Hollow Knight — Java Recreation

A recreation of parts of **Hollow Knight** built with **Java** and the **LibGDX** framework, developed as a project for an Advanced Programming course. The project follows a strict **MVC** architecture and implements core gameplay mechanics, an enemy system, a boss fight, a HUD, save/load functionality, achievements, and visual effects.

## Features

### Gameplay & AI
- Character movement, jumping, and combat with AABB-based physics for wall/floor collision resolution
- Multi-room navigation through portals
- Enemies with independent AI:
  - **Mosscreep** (`CrawlerModel`) — patrol movement and physics
  - **Tiktik** (`CrawlerModel`) 
  - **Winged Sentry** (`FlyModel`)
  - **Zote** — multi-stage dialogue, knockdown, and vulnerability logic (actually not an enemy.)
  - **Husk HorneHead** (`HuskModel`)
  - **False Knight** boss fight (`BossModel`) with a granular state machine and a two-phase AI

### Maps
- City of Tears
- Green Path

### UI & Rendering
- HUD (`GameHUD`) including a health bar (static mask + glow animation on heal), soul vessel (FrameBuffer-based masking), and Geo counter
- Pause menu (`PauseMenu`) with Resume / Settings / Charms / Save & Quit panels

### Persistence
- Save/load via **SQLite** (JDBC) with a normalized schema (`saves`, `save_charms`, `save_achievements`, `save_defeated_bosses`)
- Achievement system built on `GameStats`

## Architecture

The project follows a strict **MVC** structure:

```
hana.HollowKnight
├── model        // Game logic, entity state, physics (no rendering dependencies)
├── view         // Pure rendering layer (GameView, GameHUD, ...)
└── controller   // Room lifecycle, portal transitions, AI updates, boss fight logic
```

Key principles enforced throughout the codebase:
- `GameView` is render-only; it contains no game logic.
- `GameController` owns room lifecycle, `initRoom` ordering, AI updates, and death/respawn logic.
- `CollisionController` is always constructed **after** all models it references have been created.
- An `EnumMap`-based rendering pattern keeps the view in sync with model state.

## Requirements

- JDK 17 or later
- Gradle (via the included Gradle Wrapper)
- `org.xerial:sqlite-jdbc:3.46.1.3` (fetched automatically by Gradle)

## Running the Project

```bash
git clone https://github.com/hananehkhoram/Hollow-Knight.git
cd Hollow-Knight
./gradlew lwjgl3:run
```

## Tools & Asset Pipeline

- **Maps:** authored in Tiled Editor (`.tmx`), with dedicated object layers for `collisions`, `spawn_points`, `boss_arena`, etc.
- **Sprites:** packed with TexturePacker into `.atlas` + `.png`; animation folders live under `core/assets/Animations/`
- **UI:** styled via `uiskin.json` under `assets/ui/`

## Save Database Schema

Main SQLite tables:
| Table | Description |
|---|---|
| `saves` | General save data (position, health, soul, etc.) |
| `save_charms` | Charms equipped in each save |
| `save_achievements` | Unlocked achievements |
| `save_defeated_bosses` | Defeated bosses |

## Technical Notes / Lessons Learned

- Tiled spawn point coordinates come from the object's custom `x`/`y` properties, not its geometric position.
- LibGDX's TMX loader already flips the Y-axis from Tiled's top-left origin to LibGDX's bottom-left origin automatically — do not apply a manual flip on top of it.
- To prevent physics tunneling on the first frame (caused by a delta-time spike after asset loading), delta time is clamped to a maximum of `1/30f`.
- Wall (horizontal) vs. floor (vertical) collisions are distinguished using minimum-overlap AABB resolution — resolving on the axis with the least overlap.
- `ZoteModel` must not be recreated on every `initRoom` call (including respawns), or dialogue progress will be lost.

## Author

**Hananeh Khoramdashti** — Advanced Programming 
- Department of Computer Engineering, Sharif University of Technology — Spring 2026