# Memory Match Game — Project Structure

```
MemoryMatchGame/
├── app/
│   └── src/
│       └── main/
│           ├── java/com/memorymatch/
│           │   ├── ui/
│           │   │   ├── lobby/
│           │   │   │   └── LobbyActivity.kt          ← Start screen (Start / Exit)
│           │   │   ├── game/
│           │   │   │   ├── GameActivity.kt            ← Main game screen
│           │   │   │   └── CardAdapter.kt             ← RecyclerView adapter for the 4×4 grid
│           │   │   └── aftergame/
│           │   │       └── ResultDialogFragment.kt    ← Win popup (time + flips, Retry / Lobby)
│           │   ├── model/
│           │   │   └── Card.kt                        ← Data class: id, imageRes, isFlipped, isMatched
│           │   ├── viewmodel/
│           │   │   └── GameViewModel.kt               ← Game state, timer, flip logic
│           │   └── utils/
│           │       └── CardDeck.kt                    ← Builds & shuffles the 8-pair deck
│           └── res/
│               ├── layout/
│               │   ├── activity_lobby.xml             ← Lobby UI
│               │   ├── activity_game.xml              ← Game board + HUD
│               │   ├── item_card.xml                  ← Single card cell
│               │   └── dialog_result.xml              ← Win popup layout
│               ├── drawable/
│               │   ├── bg_lobby.xml                   ← 🎨 PLACEHOLDER — swap for your lobby backdrop
│               │   ├── bg_game.xml                    ← 🎨 PLACEHOLDER — swap for your game backdrop
│               │   ├── card_back.xml                  ← 🎨 PLACEHOLDER — swap for your card back art
│               │   └── card_front_XX.xml (×8)         ← 🎨 PLACEHOLDER — swap for your card front art
│               ├── anim/
│               │   ├── flip_out.xml                   ← Card flip-out animation
│               │   └── flip_in.xml                    ← Card flip-in animation
│               ├── menu/
│               │   └── menu_pause.xml                 ← Pause popup menu (Resume / Back to Lobby)
│               ├── font/
│               │   └── (drop your .ttf/.otf here)     ← 🎨 PLACEHOLDER — custom fonts
│               └── values/
│                   ├── strings.xml
│                   ├── colors.xml
│                   ├── themes.xml
│                   └── dimens.xml
└── PROJECT_STRUCTURE.md
```

## Customization Quick-Reference

| What you want to change | File to edit |
|---|---|
| Lobby background | `res/drawable/bg_lobby.xml` → replace with your image/drawable |
| Game background | `res/drawable/bg_game.xml` → replace with your image/drawable |
| Card back design | `res/drawable/card_back.xml` → replace with your art |
| Card front images | `res/drawable/card_front_01..08.xml` → replace with your art |
| Lobby font | `res/values/themes.xml` → `lobbyTitleFont` / `lobbyButtonFont` attrs |
| Game HUD font | `res/values/themes.xml` → `gameHudFont` attr |
| All text labels | `res/values/strings.xml` |
| Colors | `res/values/colors.xml` |
| Spacing / sizes | `res/values/dimens.xml` |
