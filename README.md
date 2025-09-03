# JC-ENGINE

A lightweight Java + LWJGL 3 3D engine demonstrating a modern OpenGL forward renderer with shadow mapping, normal/displacement mapping, and a small ECS-style component system.

Project highlights
- LWJGL 3 backend with GLFW windowing and input.
- Modern OpenGL Core profile: 4.1 on macOS, up to 4.6 on Windows/Linux when available.
- GLSL 410 core shaders with include support (simple preprocessor in Shader.java).
- Variance Shadow Mapping (VSM) with separable Gaussian blur.
- Debug output via KHR_debug when supported.

System requirements
- Java: JDK 17+ recommended (works with 11+, but use 17 for best LWJGL compatibility).
- GPU/Driver: Core profile with the following baselines
  - macOS: OpenGL 4.1 Core (Apple maximum).
  - Windows/Linux: OpenGL 4.6 Core preferred (falls back to 4.5/4.3 depending on driver). Engine requests 4.6.
- LWJGL: Bundled in lib/lwjgl-release-3.3.6-custom with per-platform natives.

OpenGL/GLSL specifics
- Context: Core profile + forward-compatible; debug context requested where supported.
- Shaders: GLSL 410 core (#version 410 core). Fragment outputs use explicit layout(location=0).
- Depth clamp (GL_DEPTH_CLAMP) is enabled only during shadow map generation to avoid clipping the light frustum.
- No deprecated GLSL built-ins (e.g., gl_FragColor, texture2D, attribute, varying).

Build and run
Option A: IntelliJ IDEA (recommended)
- Open the project folder.
- Ensure Project SDK is set to a JDK 17+.
- Mark lib/lwjgl-release-3.3.6-custom jars on the classpath and configure "VM options" to point to the correct natives directory.
  - Windows VM options:
    -Djava.library.path=lib/lwjgl-release-3.3.6-custom/natives/windows
  - Linux VM options:
    -Djava.library.path=lib/lwjgl-release-3.3.6-custom/natives/linux
  - macOS VM options:
    -XstartOnFirstThread -Djava.library.path=lib/lwjgl-release-3.3.6-custom/natives/macos
- Set the main class to game.TestGame (or your own entry point) and Run.

Option B: Command line
- Windows (PowerShell):
  $env:JAVA_TOOL_OPTIONS="-Djava.library.path=lib/lwjgl-release-3.3.6-custom/natives/windows"
  javac -cp lib/lwjgl-release-3.3.6-custom/*.jar -d out src/**/*.java
  java -cp out;lib/lwjgl-release-3.3.6-custom/* -Djava.library.path=lib/lwjgl-release-3.3.6-custom/natives/windows game.TestGame

- Linux (bash):
  export JAVA_TOOL_OPTIONS="-Djava.library.path=lib/lwjgl-release-3.3.6-custom/natives/linux"
  find src -name "*.java" > sources.txt
  javac -cp "lib/lwjgl-release-3.3.6-custom/*" -d out @sources.txt
  java -cp "out:lib/lwjgl-release-3.3.6-custom/*" -Djava.library.path=lib/lwjgl-release-3.3.6-custom/natives/linux game.TestGame

- macOS (zsh):
  export JAVA_TOOL_OPTIONS="-XstartOnFirstThread -Djava.library.path=lib/lwjgl-release-3.3.6-custom/natives/macos"
  find src -name "*.java" > sources.txt
  javac -cp "lib/lwjgl-release-3.3.6-custom/*" -d out @sources.txt
  java -cp "out:lib/lwjgl-release-3.3.6-custom/*" -XstartOnFirstThread -Djava.library.path=lib/lwjgl-release-3.3.6-custom/natives/macos game.TestGame

Notes
- Includes (e.g., *.glh/*.fsh/*.vsh) are injected into top-level shaders that declare #version 410 core.
- The engine requests an OpenGL debug context; messages will appear on stderr if the driver supports KHR_debug.
- Texture wrap modes use GL_CLAMP_TO_EDGE which is valid in 4.1 core.
- On macOS, -XstartOnFirstThread is required for GLFW.


Notes on GLSL layout qualifiers
- Vertex attributes: layout(location=n) in vertex shaders must match the index used in your VAO setup (glVertexAttribPointer / glEnableVertexAttribArray). In this engine we bind:
  - 0: position, 1: texCoord, 2: normal, 3: tangent (see src/engine/rendering/resources/MeshResource.java). No glBindAttribLocation calls are needed because the shader declares explicit locations.
- Fragment outputs: layout(location=m) maps to GL_COLOR_ATTACHMENTm on the current draw framebuffer. This engine uses layout(location=0) and attaches its color texture to GL_COLOR_ATTACHMENT0 (see src/engine/rendering/framebuffers/Framebuffer.java). If you add multiple render targets (MRT), remember to call glDrawBuffers with the list of attachments you want to write to.
- Uniforms: we do not use explicit uniform locations; Shader.java queries and caches them. Explicit uniform locations are optional in modern GLSL.
- Compute shaders: none are used here. If you add compute, layout qualifiers cover local sizes (layout(local_size_x=..., ...)) and resource bindings (e.g., layout(rgba16f, binding=0) writeonly uniform image2D img). You must bind those on the Java side via glBindImageTexture, glBindBufferBase/Range, etc. This is separate from vertex attribute and fragment output locations.


Showcase scene (high-res)
- New entry point: game.ShowcaseMain launches a 2560x1440 window and loads ShowcaseGame.
- The scene tries to use high-resolution textures and an external OBJ model if present; otherwise it falls back to the built-in bricks assets and cube/cap meshes.
- Camera controls: WASD to move (FreeMove), mouse to look (FreeLook). Ensure the window has focus.

External assets (free/CC0) recommended
- Textures (ambientCG, CC0): https://ambientcg.com/view?id=Bricks047
  Place the following files under res/textures/external/ambientCG
  - Bricks047_2K_Color.jpg
  - Bricks047_2K_NormalGL.jpg
  - Bricks047_2K_Displacement.jpg
  These will be automatically used by ShowcaseGame if present. Any 2K or higher is fine; 2K works well for 1440p.
- Model (Stanford Bunny): https://graphics.stanford.edu/data/3Dscanrep/
  Download/convert an OBJ as bunny.obj and place it under res/models/external/stanford_bunny/bunny.obj
  If not present, the scene falls back to cube.obj.

How it works
- Asset resolution uses game.assets.AssetHelper to check for external files before falling back to bundled ones.
- ShowcaseGame builds a large ground plane using the high-res bricks with normal and displacement mapping and places a hero model in front of the camera.
- Lighting setup: one directional “sun” light (higher shadow map resolution) plus two colored point lights and a spotlight to stress shadows and normal mapping.

Run the showcase
- In your IDE, set main class to game.ShowcaseMain. For macOS, add -XstartOnFirstThread and the correct natives path as described earlier.
- From the command line you can also compile/run as in the Build and run section, replacing the main class with game.ShowcaseMain.

Attribution
- ambientCG assets are CC0 (public domain). See the asset page for details.
- The Stanford Bunny is provided by the Stanford 3D Scanning Repository; review the site for usage terms.
