## Gradle
The Gradle wrapper was included, so you can run Gradle tasks using `gradlew.bat` or `./gradlew` commands.
- `--continue`: when using this flag, errors will not stop the tasks from running.
- `--daemon`: thanks to this flag, Gradle daemon will be used to run chosen tasks.
- `--offline`: when using this flag, cached dependency archives will be used.
- `--refresh-dependencies`: this flag forces validation of all dependencies. Useful for snapshot versions.
- `build`: builds sources and archives of every project.
- `cleanEclipse`: removes Eclipse project data.
- `cleanIdea`: removes IntelliJ project data.
- `clean`: removes `build` folders, which store compiled classes and built archives.
- `eclipse`: generates Eclipse project data.
- `headless:run`: starts the headless application. Note: if headless sources were not modified - and the application still creates `ApplicationListener` from `core` project - this task might fail due to no graphics support.
- `html:dist`: compiles GWT sources. The compiled application can be found at `html/build/dist`: you can use any HTTP server to deploy it.
- `html:superDev`: compiles GWT sources and runs the application in SuperDev mode. It will be available at [localhost:8080/html](http://localhost:8080/html). Use only during development.
- `idea`: generates IntelliJ project data.
- `lwjgl3:jar`: builds application's runnable jar, which can be found at `lwjgl3/build/libs`.
- `lwjgl3:run`: starts the application.
- `test`: runs unit tests (if any).


## Akuderia

Akuderia is a 2D sandbox action RPG set in a vast, dangerous fantasy world.

Players begin with almost nothing and are free to explore, gather resources, craft equipment, build structures, discover secrets, fight enemies and challenge increasingly powerful bosses.

The world is persistent rather than being a sequence of isolated runs. Players can build a home, establish bases, create farms, collect resources, construct elaborate structures and progressively transform the world around them.

But unlike a traditional sandbox RPG, combat is designed around mechanical skill rather than simply character statistics.

The Sandbox
World is generated:
- Forests
- Caves
- Mountains
- Dungeons etc

Classes - Not permanent, like terraria, use certain gear together buffs it, and nerfs others
    -> Mage
    -> Melee -> shields -> Parry/block
    -> Ranger
    -> Summoner

Combat System
 -> Movement
 -> Defense
 -> Attack
 -> Damage system
 -> Resources -> Healing ->
 -> Abilities
 -> Animation cancels