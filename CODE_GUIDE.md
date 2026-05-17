# Guia Completo do Código — Segue o Ritmo

> Escrito para quem está a aprender Java. Cada conceito é explicado desde o início,
> sem assumir conhecimento prévio. Lê do início ao fim — cada capítulo prepara o seguinte.

---

## Índice

1. [Conceitos Base de Java](#1-conceitos-base-de-java)
2. [Estrutura do Projeto e Maven](#2-estrutura-do-projeto-e-maven)
3. [O Ponto de Entrada — Main.java](#3-o-ponto-de-entrada--mainjava)
4. [config — GameConfig.java](#4-config--gameconfigjava)
5. [model — Os Dados do Jogo](#5-model--os-dados-do-jogo)
6. [observer — O Sistema de Eventos](#6-observer--o-sistema-de-eventos)
7. [service — A Lógica de Negócio](#7-service--a-lógica-de-negócio)
8. [strategy — As Velocidades do Jogo](#8-strategy--as-velocidades-do-jogo)
9. [factory — Criação de Botões](#9-factory--criação-de-botões)
10. [controller — O Orquestrador](#10-controller--o-orquestrador)
11. [view — A Interface Visual](#11-view--a-interface-visual)
12. [Os Testes Automáticos](#12-os-testes-automáticos)
13. [Padrões de Design Usados](#13-padrões-de-design-usados)
14. [Fluxo Completo do Jogo — Passo a Passo](#14-fluxo-completo-do-jogo--passo-a-passo)

---

## 1. Conceitos Base de Java

### O que é Java?
Java é uma linguagem de programação onde escrevemos código em ficheiros `.java`, que depois
o compilador transforma em bytecode (`.class`). Esse bytecode é executado pela **JVM**
(Java Virtual Machine) — por isso o jogo corre em qualquer sistema operativo que
tenha Java instalado (Windows, macOS, Linux).

### O que é uma Classe?
Uma classe é um **modelo** (blueprint). Define que dados existem e que ações se podem fazer.

```java
// Isto é uma classe chamada "Carro"
public class Carro {
    // Dados (campos / fields)
    private String cor;
    private int velocidade;

    // Ação (método)
    public void acelerar() {
        velocidade += 10;
    }
}
```

### O que é um Package?
Um package é como uma **pasta** que agrupa classes relacionadas. No projeto:
- `com.followtheritm.model` → classes de dados do jogo
- `com.followtheritm.view`  → classes da interface visual
- `com.followtheritm.controller` → classe que controla o jogo

A declaração `package com.followtheritm.model;` no topo de cada ficheiro diz ao Java
a que pasta pertence.

### O que é uma Interface?
Uma interface é um **contrato**: diz quais métodos uma classe DEVE ter, mas não diz como.
É como um manual de instruções que obriga a que a peça tenha certos encaixes.

```java
// O contrato: qualquer coisa que seja "Animal" deve poder falar
public interface Animal {
    String falar();
}

// Implementação: o Cão cumpre o contrato à sua maneira
public class Cao implements Animal {
    public String falar() { return "Woof"; }
}
```

### O que é um Enum?
Um enum é uma **lista fixa de valores**. Usa-se quando algo só pode ser um de N valores
conhecidos antecipadamente.

```java
public enum DiaDaSemana { SEGUNDA, TERCA, QUARTA, QUINTA, SEXTA }
```

### O que é um Record? (Java 16+)
Um record é uma classe especial para guardar dados **imutáveis** (que não mudam depois
de criados). O Java gera automaticamente o construtor, getters, equals e toString.

```java
// Equivale a uma classe com construtor + getter para "nome" e "idade"
public record Pessoa(String nome, int idade) {}

Pessoa p = new Pessoa("Ana", 30);
System.out.println(p.nome()); // → "Ana"
```

### O que é uma Lambda?
Uma lambda é uma **função anónima** — uma ação sem nome que se pode passar como argumento.

```java
// Sem lambda (código antigo):
button.addActionListener(new ActionListener() {
    public void actionPerformed(ActionEvent e) { System.out.println("Clicado!"); }
});

// Com lambda (Java 8+):
button.addActionListener(e -> System.out.println("Clicado!"));
```

---

## 2. Estrutura do Projeto e Maven

### O que é Maven?
Maven é uma **ferramenta de build** — gere as dependências (bibliotecas externas),
compila o código, corre os testes e cria o ficheiro `.jar` final. É o equivalente
Java de ferramentas como npm (JavaScript) ou pip (Python).

### pom.xml — A Receita do Projeto
O ficheiro `pom.xml` é o ficheiro de configuração do Maven. As partes mais importantes:

```xml
<!-- Identidade do projeto -->
<groupId>com.followtheritm</groupId>   <!-- "empresa" / namespace -->
<artifactId>follow-the-ritm</artifactId> <!-- nome do projeto -->
<version>1.0.0</version>

<!-- Qual versão de Java usar -->
<maven.compiler.source>17</maven.compiler.source>

<!-- Dependências externas -->
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>  <!-- biblioteca de testes -->
    <scope>test</scope>  <!-- só usada em testes, não vai para o JAR final -->
</dependency>

<!-- Plugin que cria o "fat JAR" (JAR com tudo incluído) -->
<plugin>
    <artifactId>maven-assembly-plugin</artifactId>
    <!-- finalName: o ficheiro vai chamar-se "simon-says.jar" -->
    <finalName>simon-says</finalName>
</plugin>
```

### Pasta `target/`
Quando corres `mvn package`, o Maven cria a pasta `target/` com:
- `classes/` → bytecode compilado dos ficheiros `.java`
- `simon-says.jar` → o jogo completo num único ficheiro executável
- `surefire-reports/` → resultados dos testes

**Nunca edites nada em `target/`** — é gerado automaticamente e apagado com `mvn clean`.

### Estrutura de Packages
```
src/main/java/com/followtheritm/
├── Main.java               ← Ponto de entrada (o "main")
├── config/
│   └── GameConfig.java     ← Todas as constantes e textos do jogo
├── model/                  ← Os DADOS do jogo (não têm lógica de UI)
│   ├── GameMode.java
│   ├── GameSequence.java
│   ├── GameSession.java
│   ├── GameState.java
│   ├── PlayerInput.java
│   └── SimonColor.java
├── observer/               ← Sistema de eventos (notificações)
│   ├── GameEvent.java
│   └── GameEventListener.java
├── service/                ← Lógica de negócio (regras do jogo + áudio)
│   ├── AudioService.java
│   ├── AudioServiceImpl.java
│   ├── SequenceService.java
│   └── SequenceServiceImpl.java
├── strategy/               ← Velocidades do jogo (algoritmos intercambiáveis)
│   ├── SpeedStrategy.java
│   ├── SlowSpeed.java
│   ├── NormalSpeed.java
│   └── FastSpeed.java
├── factory/
│   └── ColorButtonFactory.java
├── controller/
│   └── SimonController.java ← Orquestra todo o jogo
└── view/                   ← Interface visual (tudo o que o utilizador vê)
    ├── MainFrame.java
    ├── MainMenuPanel.java
    ├── SimonPanel.java
    ├── ControlPanel.java
    ├── ColorButton.java
    ├── RoundIndicator.java
    └── GameOverOverlay.java
```

---

## 3. O Ponto de Entrada — Main.java

```java
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::bootstrap);
    }
}
```

`main` é o método que o Java procura quando arranca o programa.
Mas porquê `SwingUtilities.invokeLater`?

### O EDT — Event Dispatch Thread
O Swing (biblioteca visual do Java) tem uma regra de ouro:
**toda a criação e modificação de componentes visuais deve acontecer numa thread especial
chamada EDT (Event Dispatch Thread).**

Se criares botões ou janelas noutras threads, o programa pode crashar ou ter
comportamentos imprevisíveis. `invokeLater` agenda a execução do código no EDT.

`Main::bootstrap` é uma **method reference** — uma forma curta de escrever
`() -> Main.bootstrap()`. Passa o método `bootstrap` como argumento para `invokeLater`.

### O método `bootstrap`
```java
private static void bootstrap() {
    // 1. Cria os objetos de dados/lógica
    GameSession         session  = new GameSession();
    SequenceServiceImpl seqSvc   = new SequenceServiceImpl();
    AudioServiceImpl    audioSvc = AudioServiceImpl.getInstance();

    // 2. Cria a janela principal
    MainFrame frame = new MainFrame();

    // 3. Cria o controller que liga tudo
    SimonController controller = new SimonController(
            session, seqSvc, audioSvc,
            frame.getSimonPanel(),
            frame.getRoundIndicator(),
            frame.getControlPanel()
    );

    // 4. Liga os botões às ações
    frame.getMainMenuPanel().setOnModeSelected(mode -> {
        controller.setGameMode(mode);
        frame.showGame();
    });
    frame.getControlPanel().setOnMenuPressed(() -> {
        controller.stop();
        frame.showMainMenu();
    });
    frame.getControlPanel().setOnStart(controller::onStartPressed);
    frame.getControlPanel().setOnFullScreenToggle(frame::toggleFullScreen);

    // 5. Arranca em ecrã inteiro
    frame.toggleFullScreen();
}
```

Este método segue um princípio importante: **criar objetos → ligar objetos → arrancar**.
Nenhuma classe cria as outras — é o `Main` que cria tudo e passa as dependências.
Chama-se **Dependency Injection** (injeção de dependências).

---

## 4. config — GameConfig.java

```java
public final class GameConfig {

    private static final GameConfig INSTANCE = new GameConfig();
    private GameConfig() {}
    public static GameConfig getInstance() { return INSTANCE; }

    // Todos os valores do jogo num só sítio:
    public static final int SLOW_INTERVAL_MS   = 2500;
    public static final int NORMAL_INTERVAL_MS = 950;
    public static final int FAST_INTERVAL_MS   = 450;

    public static final String LABEL_START = "▶  Iniciar";
    public static final String LABEL_SLOW  = "🐢  Lento";
    // ...
}
```

### Por que existe esta classe?
Sem ela, o valor `2500` apareceria espalhado por vários ficheiros — se quisesses
mudar a velocidade lenta, tinhas de procurar e editar em 5 sítios diferentes.
Com `GameConfig`, mudas num único lugar e afeta tudo.

### `final class`
`final` numa classe significa que **ninguém pode fazer extends dela** (herdar dela).
É uma proteção: o Singleton não deve ser alterado por subclasses.

### Campos `static final`
- `static` → pertence à classe, não a uma instância específica. Acede-se com
  `GameConfig.LABEL_START`, não com `config.LABEL_START`.
- `final` → não pode ser reatribuído depois de inicializado. É uma constante.

---

## 5. model — Os Dados do Jogo

O package `model` contém apenas dados — sem lógica de UI, sem Swing, sem sons.
Esta separação é o coração do padrão **MVC** (ver capítulo 13).

### GameState.java — A Máquina de Estados

```java
public enum GameState {
    IDLE,           // Jogo parado, à espera de "Iniciar"
    SHOWING,        // O jogo está a mostrar a sequência (botões desativados)
    AWAITING_INPUT, // O jogador tem de repetir a sequência
    SUCCESS,        // O jogador acertou na ronda inteira
    GAME_OVER       // O jogador errou
}
```

O jogo só pode estar num destes 5 estados em cada momento.
Isto evita erros como "o jogador carregou num botão enquanto a sequência estava a ser mostrada".

### GameMode.java

```java
public enum GameMode {
    CLASSIC, // Sequência cresce 1 por ronda (Simon Says clássico)
    RANDOM   // Sequência nova aleatória a cada ronda
}
```

### SimonColor.java — Enum com Dados

Este é um exemplo de enum mais avançado: cada valor tem **campos próprios**.

```java
public enum SimonColor {
    RED   (new Color(130, 18, 18),  new Color(255, 80, 80),  220),
    BLUE  (new Color( 18, 45, 170), new Color( 80, 140, 255), 277),
    GREEN (new Color( 18, 130, 18), new Color( 70, 230, 70),  330),
    YELLOW(new Color(165, 145, 12), new Color(255, 235, 75),  440);

    private final Color baseColor;   // cor escura (botão "apagado")
    private final Color flashColor;  // cor brilhante (botão "aceso")
    private final int frequencyHz;   // frequência do som associado

    SimonColor(Color baseColor, Color flashColor, int frequencyHz) {
        this.baseColor    = baseColor;
        this.flashColor   = flashColor;
        this.frequencyHz  = frequencyHz;
    }

    public Color getBaseColor()  { return baseColor; }
    public Color getFlashColor() { return flashColor; }
    public int   getFrequencyHz(){ return frequencyHz; }
}
```

Cada cor carrega consigo: a cor escura (quando o botão está apagado), a cor brilhante
(quando o botão acende) e a frequência sonora. Assim, quando precisamos de acender
o botão vermelho, basta chamar `SimonColor.RED.getFlashColor()` — não há números
mágicos espalhados pelo código.

As frequências escolhidas (220, 277, 330, 440 Hz) são as notas Lá3, Dó#4, Mi4, Lá4 —
um acorde harmónico que soa agradável.

### GameSequence.java — Record Imutável

```java
public record GameSequence(List<SimonColor> colors) {

    public GameSequence(List<SimonColor> colors) {
        this.colors = List.copyOf(colors); // cria uma cópia — ninguém pode alterar o original
    }

    public int length()        { return colors.size(); }
    public SimonColor get(int i){ return colors.get(i); }
}
```

Um `record` gera automaticamente:
- O construtor `GameSequence(List<SimonColor> colors)`
- O getter `colors()` (acesso à lista)
- `equals()`, `hashCode()`, `toString()`

`List.copyOf(colors)` cria uma lista **imutável** — mesmo que alguém passe uma lista
e depois a altere, o `GameSequence` mantém sempre os seus valores originais.

### PlayerInput.java — Record Imutável

```java
public record PlayerInput(List<SimonColor> pressed) {

    public PlayerInput(List<SimonColor> pressed) {
        this.pressed = List.copyOf(pressed);
    }

    public static PlayerInput empty() {
        return new PlayerInput(List.of()); // lista vazia no início
    }

    public int size() { return pressed.size(); }

    // Retorna um NOVO PlayerInput com a cor adicionada (o original não muda)
    public PlayerInput withAdded(SimonColor color) {
        var newList = new java.util.ArrayList<>(pressed);
        newList.add(color);
        return new PlayerInput(newList);
    }
}
```

`withAdded` não altera `this` — cria e retorna um objeto novo. É o princípio
da **imutabilidade**: objetos não se modificam, criam-se novos. Evita bugs onde
duas partes do código partilham o mesmo objeto e uma altera o que a outra está a usar.

### GameSession.java — O Estado Mutável Central

Esta é a única classe `model` que muda com o tempo. Guarda:
- o número da ronda atual
- o recorde pessoal
- o estado da máquina de estados
- a lista de listeners (observadores)

```java
public class GameSession {

    private int currentRound = 0;
    private int personalBest = 0;
    private GameState state  = GameState.IDLE;

    private final List<GameEventListener> listeners = new ArrayList<>();

    // Qualquer classe pode "subscrever" para ser notificada de eventos
    public void addListener(GameEventListener listener) {
        listeners.add(listener);
    }

    // Disparar um evento notifica todos os subscribers
    private void fire(GameEvent event) {
        listeners.forEach(l -> l.onGameEvent(event, this));
    }

    public void setState(GameState newState) {
        this.state = newState;
        fire(GameEvent.STATE_CHANGED); // notifica todos
    }

    public void startNewRound() {
        currentRound++;
        fire(GameEvent.ROUND_STARTED);
    }

    public void recordSuccess() {
        if (currentRound > personalBest) {
            personalBest = currentRound;
            fire(GameEvent.PERSONAL_BEST_UPDATED);
        }
    }

    public void reset() {
        currentRound = 0;
        state        = GameState.IDLE;
        fire(GameEvent.GAME_RESET);
    }
}
```

---

## 6. observer — O Sistema de Eventos

### O Problema que Resolve
O `GameSession` (dados) precisava de notificar o `ControlPanel` (UI) e o
`RoundIndicator` (UI) quando o estado muda. Mas o `GameSession` não deve
conhecer os componentes visuais — isso criaria dependências em ciclo.

A solução é o **padrão Observer**: quem tem dados apenas dispara eventos;
quem quer saber, subscreve.

### GameEvent.java

```java
public enum GameEvent {
    STATE_CHANGED,          // o GameState mudou
    ROUND_STARTED,          // nova ronda começou
    PERSONAL_BEST_UPDATED,  // novo recorde
    GAME_RESET              // jogo reiniciado
}
```

### GameEventListener.java

```java
@FunctionalInterface
public interface GameEventListener {
    void onGameEvent(GameEvent event, GameSession session);
}
```

`@FunctionalInterface` diz que esta interface tem exatamente **um método**.
Isso permite usá-la como lambda:

```java
// Em vez de criar uma classe anónima:
session.addListener(new GameEventListener() {
    public void onGameEvent(GameEvent event, GameSession s) {
        System.out.println("Evento: " + event);
    }
});

// Pode-se escrever como lambda:
session.addListener((event, s) -> System.out.println("Evento: " + event));
```

No `SimonController`, a subscrição acontece assim:
```java
session.addListener(this::onGameEvent);
// "this::onGameEvent" é method reference — equivale a (event, s) -> this.onGameEvent(event, s)
```

---

## 7. service — A Lógica de Negócio

### SequenceService.java — A Interface

```java
public interface SequenceService {
    GameSequence extend(GameSequence current);      // adiciona 1 cor aleatória
    GameSequence createInitial();                    // cria sequência de 1 cor
    GameSequence createRandom(int length);           // cria sequência totalmente nova
    boolean isInputCorrectSoFar(GameSequence seq, int index, SimonColor pressed);
}
```

Por que existe uma interface **separada** da implementação?

1. **Testabilidade**: nos testes, injetamos um `SequenceServiceImpl` com um
   `Random` com semente fixa — os resultados são sempre os mesmos.
2. **Substituição**: se amanhã quiséssemos usar uma sequência pré-definida para
   treino, criávamos `TrainingSequenceServiceImpl` sem mudar o controller.

### SequenceServiceImpl.java

```java
public class SequenceServiceImpl implements SequenceService {

    private final Random random;
    private static final SimonColor[] VALUES = SimonColor.values(); // [RED, BLUE, GREEN, YELLOW]

    public SequenceServiceImpl() { this.random = new Random(); }

    // Construtor para testes: injeta um Random com semente fixa
    public SequenceServiceImpl(Random random) { this.random = random; }

    @Override
    public GameSequence createInitial() {
        return new GameSequence(List.of(randomColor())); // 1 cor aleatória
    }

    @Override
    public GameSequence extend(GameSequence current) {
        var next = new ArrayList<>(current.colors()); // copia a lista atual
        next.add(randomColor());                       // adiciona 1 nova cor
        return new GameSequence(next);
    }

    @Override
    public GameSequence createRandom(int length) {
        var colors = new ArrayList<SimonColor>(length);
        for (int i = 0; i < length; i++) colors.add(randomColor());
        return new GameSequence(colors);
    }

    @Override
    public boolean isInputCorrectSoFar(GameSequence seq, int index, SimonColor pressed) {
        return seq.get(index) == pressed; // == funciona em enums (são singletons)
    }

    private SimonColor randomColor() {
        return VALUES[random.nextInt(VALUES.length)]; // índice aleatório entre 0 e 3
    }
}
```

### AudioServiceImpl.java — Áudio Programático

O áudio é gerado em tempo real com matemática — não há ficheiros de som.

```java
private byte[] generateSineWave(int frequencyHz, int durationMs) {
    int samples = GameConfig.SAMPLE_RATE * durationMs / 1000;
    byte[] buffer = new byte[samples];

    for (int i = 0; i < samples; i++) {
        // Fórmula de uma onda sinusoidal:
        // sin(2π × frequência × tempo)
        double angle = 2.0 * Math.PI * frequencyHz * i / GameConfig.SAMPLE_RATE;
        double sample = Math.sin(angle);

        // Envelope de fade: os primeiros e últimos 10ms têm volume crescente/decrescente
        // Evita o "clique" áudio que acontece quando o som começa/acaba abruptamente
        int fadeSamples = GameConfig.SAMPLE_RATE / 100; // 10ms
        if (i < fadeSamples)
            sample *= (double) i / fadeSamples;
        else if (i > samples - fadeSamples)
            sample *= (double)(samples - i) / fadeSamples;

        buffer[i] = (byte)(sample * 100); // converte para byte (-127 a 127)
    }
    return buffer;
}
```

O som é tocado numa **thread separada** (background thread) para não bloquear
a interface visual enquanto o som está a ser gerado:

```java
Thread t = new Thread(task, "audio-tone");
t.setDaemon(true); // thread "daemon": termina automaticamente quando o programa fecha
t.start();
```

---

## 8. strategy — As Velocidades do Jogo

### O Problema
A velocidade do jogo tem de poder mudar sem alterar o controller.
Se metéssemos `if (speed == SLOW) delay = 2500;` dentro do controller,
adicionar um novo nível obrigaria a editar o controller.

### SpeedStrategy.java — A Interface

```java
public interface SpeedStrategy {
    int intervalMs();     // pausa entre cada flash da sequência
    int flashDurationMs(); // quanto tempo o botão fica aceso
}
```

### As três implementações

```java
public class SlowSpeed implements SpeedStrategy {
    public int intervalMs()      { return 2500; } // muito devagar
    public int flashDurationMs() { return 1200; }
}

public class NormalSpeed implements SpeedStrategy {
    public int intervalMs()      { return 950; }
    public int flashDurationMs() { return 480; }
}

public class FastSpeed implements SpeedStrategy {
    public int intervalMs()      { return 450; } // rápido
    public int flashDurationMs() { return 250; }
}
```

No controller, a velocidade é usada assim:
```java
private SpeedStrategy speed = new NormalSpeed(); // valor por defeito

// Quando o utilizador muda a velocidade:
speed = new SlowSpeed(); // substitui o objeto, o controller não precisa de saber qual é

// Quando precisa de usar:
showTimer.setDelay(speed.intervalMs()); // chama o método sem saber qual implementação é
```

Isto chama-se **polimorfismo** — o controller só conhece a interface `SpeedStrategy`,
não as implementações concretas.

---

## 9. factory — Criação de Botões

```java
public class ColorButtonFactory {
    public static ColorButton create(SimonColor color) {
        return new ColorButton(color);
    }
}
```

É uma classe simples mas com um propósito: **centralizar a criação de botões**.
Se amanhã quiséssemos adicionar um som de hover a todos os botões, ou um tooltip,
bastaria editar este método — em vez de procurar todos os `new ColorButton(...)`.

`static` no método significa que se chama como `ColorButtonFactory.create(RED)`,
sem precisar de instanciar a factory.

---

## 10. controller — O Orquestrador

`SimonController` é o cérebro do jogo. Liga todos os outros componentes.
Não tem lógica visual (não pinta nada), nem lógica de dados (não calcula sequências).
Apenas **orquestra**: quando acontece X, chama Y.

### Campos principais

```java
private final GameSession      session;         // os dados
private final SequenceService  sequenceService; // regras de sequência
private final AudioService     audioService;    // sons
private final SimonPanel       simonPanel;      // os 4 botões
private final RoundIndicator   roundIndicator;  // marcador de rondas
private final ControlPanel     controlPanel;    // barra inferior

private GameSequence  currentSequence = new GameSequence(List.of()); // sequência atual
private PlayerInput   playerInput     = PlayerInput.empty();          // input atual do jogador
private SpeedStrategy speed           = new NormalSpeed();            // velocidade atual
private GameMode      gameMode        = GameMode.CLASSIC;             // modo atual
private int           showIndex = 0;   // índice durante a reprodução da sequência
private Timer         showTimer;       // temporizador do Swing
```

### O Timer do Swing

O Swing tem um `javax.swing.Timer` que executa código no EDT de forma repetida:

```java
showTimer = new Timer(600, null);
showTimer.addActionListener(e -> playNextFlash()); // a cada "tick", mostra o próximo flash
showTimer.setRepeats(true);
showTimer.start();
```

Nunca se usa `Thread.sleep()` no EDT — bloquearia a interface e o utilizador via
o jogo congelado. O Timer agenda ações no futuro sem bloquear.

### Fluxo de uma ronda (Modo Clássico)

```java
private void startNewRound() {
    // 1. Incrementa o contador de rondas
    playerInput = PlayerInput.empty();
    session.startNewRound();

    // 2. Gera a sequência (modo clássico: adiciona 1 cor)
    currentSequence = currentSequence.length() == 0
            ? sequenceService.createInitial()
            : sequenceService.extend(currentSequence);

    // 3. Muda o estado para SHOWING
    session.setState(GameState.SHOWING);

    // 4. Desativa os botões (jogador não pode clicar durante a demonstração)
    simonPanel.setButtonsEnabled(false);
    simonPanel.getOverlay().dismiss();

    // 5. Agenda a reprodução da sequência
    scheduleSequencePlayback();
}
```

### Como a Sequência é Reproduzida

```java
private void scheduleSequencePlayback() {
    showIndex = 0;
    showTimer = new Timer(600, null);       // começa com 600ms de pausa inicial
    showTimer.addActionListener(e -> playNextFlash());
    showTimer.setRepeats(true);
    showTimer.setInitialDelay(600);
    showTimer.start();
}

private void playNextFlash() {
    if (showIndex >= currentSequence.length()) {
        showTimer.stop();
        transitionToAwaitingInput(); // sequência acabou → jogador joga
        return;
    }
    SimonColor color = currentSequence.get(showIndex++);
    simonPanel.flashButton(color, speed.flashDurationMs()); // acende botão
    audioService.playColorTone(color);                       // toca som
    showTimer.setDelay(speed.intervalMs());                  // ajusta intervalo
}
```

### Quando o Jogador Carrega num Botão

```java
private void onColorPressed(SimonColor color) {
    if (session.getState() != GameState.AWAITING_INPUT) return; // ignorar se não for altura

    int idx = playerInput.size(); // quantas cores já foram pressionadas
    simonPanel.flashButton(color, speed.flashDurationMs()); // feedback visual
    audioService.playColorTone(color);                       // feedback sonoro

    // Verificar se a cor está correta para esta posição
    if (!sequenceService.isInputCorrectSoFar(currentSequence, idx, color)) {
        handleWrongInput(); // ERROU
        return;
    }

    playerInput = playerInput.withAdded(color); // adiciona ao input (cria novo objeto)

    if (playerInput.size() == currentSequence.length()) {
        handleRoundComplete(); // Sequência completa → passou de ronda!
    }
    // caso contrário, espera pela próxima cor do jogador
}
```

---

## 11. view — A Interface Visual

Todas as classes de view usam **Swing**, a biblioteca gráfica do Java.
Os conceitos-chave:
- `JPanel` → área rectangular que pode ser pintada e conter outros componentes
- `JFrame` → a janela principal do sistema operativo
- `paintComponent(Graphics g)` → método chamado automaticamente pelo Swing para redesenhar
- `Graphics2D` → versão avançada do Graphics com suporte a gradientes, formas arredondadas, etc.

### MainFrame.java — A Janela Principal

`MainFrame extends JFrame` — herda todos os comportamentos de uma janela normal e
adiciona funcionalidades específicas do jogo.

O layout usa **CardLayout** — é como uma pilha de cartas onde só uma está visível:

```java
private final CardLayout cardLayout    = new CardLayout();
private final JPanel     cardContainer = new JPanel(cardLayout);

// Carta 1: menu principal
cardContainer.add(mainMenuPanel, "menu");

// Carta 2: ecrã do jogo
cardContainer.add(buildGamePanel(), "game");

// Para mudar de ecrã:
public void showMainMenu() { cardLayout.show(cardContainer, "menu"); }
public void showGame()     { cardLayout.show(cardContainer, "game"); }
```

#### Ecrã Inteiro com GraphicsDevice

```java
public void toggleFullScreen() {
    if (!fullScreen) {
        dispose();                          // fecha a janela atual
        setUndecorated(true);               // remove barra de título e bordas
        graphicsDevice.setFullScreenWindow(this); // pede ao SO ecrã inteiro exclusivo
        fullScreen = true;
    } else {
        graphicsDevice.setFullScreenWindow(null); // sai do ecrã inteiro
        dispose();
        setUndecorated(false);
        setSize(800, 800);
        setLocationRelativeTo(null);        // centra na tela
        setVisible(true);
        fullScreen = false;
    }
}
```

#### SquareWrapper — Manter o Jogo Quadrado

Em ecrãs wide (16:9), o jogo ficaria distorcido numa grelha rectangular.
`SquareWrapper` garante que o painel de jogo é sempre o maior quadrado que cabe:

```java
private static final class SquareWrapper extends JPanel {

    @Override
    public void doLayout() {
        int w    = getWidth();
        int h    = getHeight();
        int side = Math.min(w, h); // o menor dos dois lados
        int x    = (w - side) / 2; // centrar horizontalmente
        int y    = (h - side) / 2; // centrar verticalmente
        getComponent(0).setBounds(x, y, side, side);
    }
}
```

### SimonPanel.java — Os 4 Botões

`SimonPanel extends JLayeredPane` — o `JLayeredPane` é como um canvas com camadas.
Os botões ficam na camada inferior, o overlay (cartão de erro/sucesso) fica na camada
superior e pode tapar tudo sem precisar de truques com z-order.

```java
private static final Integer BUTTON_LAYER  = JLayeredPane.DEFAULT_LAYER;  // camada 0
private static final Integer OVERLAY_LAYER = JLayeredPane.PALETTE_LAYER;  // camada 100
```

O `JLayeredPane` usa layout nulo (null layout) — os filhos não se posicionam
automaticamente. Por isso é preciso o `doLayout()`:

```java
@Override
public void doLayout() {
    int w = getWidth();
    int h = getHeight();
    buttonGrid.setBounds(0, 0, w, h); // a grelha ocupa tudo
    overlay.setBounds(0, 0, w, h);    // o overlay também — fica por cima
}
```

`EnumMap<SimonColor, ColorButton>` é um Map otimizado para chaves do tipo enum.
Permite ir buscar o botão de uma cor diretamente: `buttons.get(SimonColor.RED)`.

### ColorButton.java — O Botão Colorido

Cada botão tem 3 estados visuais:
- **Normal**: gradiente escuro (botão "apagado")
- **Hover**: gradiente brilhante + borda branca espessa
- **Flash**: gradiente radial branco→cor→escuro + borda luminosa animada

```java
public void flash(int durationMs) {
    stopTimers();
    flashing  = true;
    glowAlpha = 1.0f;    // borda começa com opacidade máxima
    repaint();           // pede ao Swing para redesenhar imediatamente

    // Timer a 60fps: reduz gradualmente a opacidade da borda (efeito de fade)
    glowDecayTimer = new Timer(16, e -> {
        glowAlpha = Math.max(0f, glowAlpha - 0.025f);
        repaint();
    });
    glowDecayTimer.start();

    // Timer que desliga o flash após durationMs
    flashOffTimer = new Timer(durationMs, e -> {
        stopTimers();
        flashing  = false;
        glowAlpha = 0f;
        repaint();
    });
    flashOffTimer.setRepeats(false); // dispara só uma vez
    flashOffTimer.start();
}
```

O método `paintComponent` é chamado pelo Swing sempre que o botão precisa de ser
redesenhado. **Nunca se deve chamar diretamente** — usa-se `repaint()` que agenda
a chamada no EDT.

```java
@Override
protected void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D) g.create(); // criar uma cópia para não afetar outros componentes

    // Ativar antialiasing (bordas suaves, não pixeladas)
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    // Forma arredondada (ARC=28 é o raio dos cantos)
    RoundRectangle2D shape = new RoundRectangle2D.Float(4, 4, w-8, h-8, 28, 28);

    if (flashing) {
        // RadialGradientPaint: gradiente circular a partir do centro
        g2.setPaint(new RadialGradientPaint(
            cx, cy, radius,
            new float[]{ 0f,   0.30f,  1f    }, // posições: centro, 30%, borda
            new Color[] { WHITE, flashColor, darkColor }
        ));
    }
    g2.fill(shape);  // preenche a forma
    g2.draw(shape);  // desenha o contorno

    g2.dispose(); // liberta recursos
}
```

### ControlPanel.java — A Barra Inferior

Contém 4 tipos de componentes personalizados:
1. **`PillButton`** — botão com forma de pílula (bordas totalmente arredondadas)
2. **`DifficultyButton`** — botão de seleção de velocidade com cor + pontos + emoji
3. **`FullScreenButton`** — botão com ícones de cantos desenhados programaticamente
4. Separadores verticais

O `DifficultyButton` resolve um problema curioso:
```java
// FontMetrics.stringWidth("🐢  Lento") devolve um valor errado
// porque o emoji tem largura diferente em diferentes plataformas.
// Solução: separar emoji e texto e medir/desenhar cada um separadamente.
String[] parts = label.split("  ", 2); // divide em "🐢" e "Lento"
this.emoji = parts[0].trim();
this.text  = parts[1].trim();
// Depois cada um é centrado individualmente
```

### RoundIndicator.java e GameOverOverlay.java

**`RoundIndicator`**: desenha a pílula com "Ronda X", as estrelas e o recorde.
Todos os elementos são desenhados em `paintComponent` com `Graphics2D` —
sem componentes Swing internos, tudo à mão para controlo total do visual.

**`GameOverOverlay`**: um painel transparente que aparece sobre os botões
com um cartão de sucesso (verde + ✓) ou de erro (vermelho + ✗).

```java
// ATENÇÃO: o método chama-se dismiss(), NÃO hide()!
// hide() seria sobrepor o método deprecated Component.hide() do Java,
// o que causava um StackOverflowError (loop infinito):
// setVisible(false) → Component.show(false) → Component.hide() → o nosso hide() → loop!
public void dismiss() {
    setVisible(false);
}
```

---

## 12. Os Testes Automáticos

Os testes estão em `src/test/java/` e usam **JUnit 5**.
São executados automaticamente com `mvn test` (ou `mvn package`).

### O que é um teste?
Um teste verifica se o código funciona corretamente. Se o comportamento mudar
inesperadamente (por exemplo, ao modificar código), o teste falha e avisa-nos.

```java
@Test
void extend_addsOneColor() {
    // ARRANGE: preparar o cenário
    SequenceService service = new SequenceServiceImpl(new Random(42)); // semente fixa
    GameSequence initial = service.createInitial();

    // ACT: executar a ação a testar
    GameSequence extended = service.extend(initial);

    // ASSERT: verificar o resultado esperado
    assertEquals(initial.length() + 1, extended.length());
}
```

**Porquê `new Random(42)`?**
Com uma semente fixa, o `Random` gera sempre a mesma sequência de números.
Assim o teste é determinístico — corre sempre igual, não depende da sorte.

### Testes do GameSession

```java
@Test
void personalBest_isUpdatedCorrectly() {
    session.reset();
    session.startNewRound(); // ronda 1
    session.recordSuccess();
    session.startNewRound(); // ronda 2
    session.recordSuccess();
    assertEquals(2, session.getPersonalBest());
}

@Test
void removeListener_stopsReceivingEvents() {
    // Verifica que removeListener funciona: depois de remover,
    // o listener não deve receber mais eventos
    var listener = (GameEventListener)(event, s) -> firedEvents.add(event);
    session.addListener(listener);
    session.removeListener(listener);
    int before = firedEvents.size();
    session.setState(GameState.SHOWING);
    assertEquals(before, firedEvents.size()); // nenhum evento novo
}
```

---

## 13. Padrões de Design Usados

Os **padrões de design** são soluções reutilizáveis para problemas comuns de programação.
São "receitas" que programadores experientes desenvolveram ao longo de décadas.

### MVC — Model-View-Controller

O padrão mais importante do projeto. Divide o código em 3 responsabilidades:

```
MODEL          VIEW             CONTROLLER
(dados)        (visual)         (lógica)

GameSession ←→ SimonPanel    ←→ SimonController
GameSequence   ControlPanel
SimonColor     MainFrame
```

- **Model**: apenas dados. Não sabe nada sobre botões ou janelas.
- **View**: apenas visual. Não sabe nada sobre regras do jogo.
- **Controller**: liga os dois. Quando o Model muda, atualiza a View.
  Quando a View recebe input, atualiza o Model.

**Vantagem**: podes mudar completamente o visual sem tocar nas regras do jogo,
e vice-versa.

### Singleton

Garante que existe **apenas uma instância** de uma classe em todo o programa.

```java
public final class GameConfig {
    private static final GameConfig INSTANCE = new GameConfig();
    private GameConfig() {} // construtor privado → ninguém pode fazer "new GameConfig()"
    public static GameConfig getInstance() { return INSTANCE; }
}
```

Usado em: `GameConfig`, `AudioServiceImpl`.

### Observer (Publicador / Subscritor)

Permite que objetos sejam notificados de eventos sem dependências diretas.

```
GameSession ──→ dispara GameEvent.STATE_CHANGED
                     ↓
              notifica todos os listeners:
                     ↓
              SimonController.onGameEvent() → atualiza botões
```

Usado em: `GameSession` + `GameEventListener`.

### Strategy

Permite trocar um **algoritmo** (a velocidade) sem mudar o código que o usa.

```
SpeedStrategy (interface)
    ├── SlowSpeed   → intervalMs() = 2500
    ├── NormalSpeed → intervalMs() = 950
    └── FastSpeed   → intervalMs() = 450

SimonController usa SpeedStrategy — não sabe qual das três está a usar
```

Usado em: `SpeedStrategy` e as 3 implementações.

### Factory

Uma classe dedicada a **criar objetos**, centralizando a lógica de criação.

```java
ColorButtonFactory.create(SimonColor.RED) // → novo ColorButton configurado para vermelho
```

### State

O jogo tem um estado finito e bem definido. A qualquer momento, só pode estar
num estado:

```
IDLE → (Iniciar) → SHOWING → (sequência terminada) → AWAITING_INPUT
                                                            ↓
                                              (correto, sequência completa)
                                                            ↓
                                                        SUCCESS → (nova ronda) → SHOWING
                                                            ↓
                                              (errado)
                                                            ↓
                                                        GAME_OVER → (Iniciar) → SHOWING
```

---

## 14. Fluxo Completo do Jogo — Passo a Passo

Vamos seguir o que acontece desde que o utilizador abre o jogo até ao fim de uma ronda.

### 1. Arranque (`Main.bootstrap`)
1. `GameSession` é criado (estado: `IDLE`, ronda: 0)
2. `SequenceServiceImpl` e `AudioServiceImpl` são criados
3. `MainFrame` é criado com CardLayout (menu + jogo)
4. `SimonController` é criado e subscreve eventos do `GameSession`
5. Callbacks são ligados (botões → métodos do controller)
6. `frame.toggleFullScreen()` → ecrã inteiro, menu visível

### 2. Seleção de Modo (`MainMenuPanel`)
1. Utilizador clica em "Clássico"
2. `onModeSelected.accept(GameMode.CLASSIC)` é chamado (lambda no Main)
3. `controller.setGameMode(GameMode.CLASSIC)` — guarda o modo
4. `frame.showGame()` — CardLayout mostra o ecrã de jogo

### 3. Pressionar "Iniciar" (`SimonController.onStartPressed`)
1. `session.reset()` → ronda=0, estado=IDLE, dispara `GAME_RESET`
2. `startNewRound()` é chamado:
   - `session.startNewRound()` → ronda=1, dispara `ROUND_STARTED`
   - `sequenceService.createInitial()` → sequência com 1 cor aleatória
   - `session.setState(SHOWING)` → dispara `STATE_CHANGED`
   - `simonPanel.setButtonsEnabled(false)` → botões desativados
   - `scheduleSequencePlayback()` → cria Timer

### 4. Reprodução da Sequência (Timer)
1. Timer dispara após 600ms
2. `playNextFlash()` é chamado:
   - `simonPanel.flashButton(RED, 480)` → `ColorButton.flash(480)`:
     - `flashing = true`
     - `glowDecayTimer` começa: a cada 16ms, reduz `glowAlpha`
     - `flashOffTimer` dispara após 480ms: `flashing = false`
   - `audioService.playColorTone(RED)` → thread de áudio gera e toca onda sinusoidal de 220Hz
3. Timer redefine `delay` para `speed.intervalMs()` e volta ao passo 2
4. Quando `showIndex >= sequência.length()`, Timer para e chama `transitionToAwaitingInput()`

### 5. Aguardar Input do Jogador
1. `session.setState(AWAITING_INPUT)` → dispara `STATE_CHANGED`
2. `onGameEvent` no controller:
   - `controlPanel.setStartEnabled(false)` (não pode iniciar nova jogo enquanto joga)
3. `simonPanel.setButtonsEnabled(true)` → botões ativados

### 6. Input do Jogador (`SimonController.onColorPressed`)
1. Jogador toca no botão vermelho
2. `mousePressed` dispara (não `mouseClicked` — mais fiável em touch)
3. `btn.isEnabled()` → verdadeiro → `handler.accept(SimonColor.RED)`
4. `session.getState() == AWAITING_INPUT` → continua
5. `sequenceService.isInputCorrectSoFar(seq, 0, RED)` → `seq.get(0) == RED`
6. Se correto: `playerInput = playerInput.withAdded(RED)` (novo objeto imutável)
7. `playerInput.size() == currentSequence.length()`? Se sim → `handleRoundComplete()`

### 7. Fim de Ronda com Sucesso
1. `session.recordSuccess()` → actualiza recorde se necessário
2. `session.setState(SUCCESS)` → dispara `STATE_CHANGED`
3. `audioService.playSuccess()` → toca acorde C4-E4-G4
4. `simonPanel.getOverlay().showSuccess()` → cartão verde + ✓ aparece sobre os botões
5. Timer de 1000ms: após 1 segundo, chama `startNewRound()` → volta ao passo 3

### 8. Erro do Jogador
1. `isInputCorrectSoFar` retorna `false`
2. `session.setState(GAME_OVER)` → dispara `STATE_CHANGED`
3. `audioService.playError()` → toca som descendente
4. `simonPanel.getOverlay().showError()` → cartão vermelho + ✗
5. `currentSequence` é resetado para vazio
6. Jogador pressiona "Iniciar" → volta ao passo 3 (recomeça do zero)

---

## Resumo dos Conceitos Java Aprendidos

| Conceito | Onde aparece | Para que serve |
|----------|-------------|----------------|
| `enum` com campos | `SimonColor` | Lista fixa de valores com dados associados |
| `record` | `GameSequence`, `PlayerInput` | Contentor de dados imutável |
| `interface` | `SequenceService`, `SpeedStrategy` | Contrato — separa "o quê" do "como" |
| `@FunctionalInterface` + lambda | `GameEventListener` | Funções como argumentos |
| Method reference (`::`) | `Main.java` | Atalho para lambdas que chamam métodos existentes |
| `static final` | `GameConfig` | Constantes partilhadas |
| `EnumMap` | `SimonPanel` | Map otimizado para chaves do tipo enum |
| `javax.swing.Timer` | `SimonController`, `ColorButton` | Temporizador seguro no EDT |
| `Graphics2D` | `ColorButton`, `ControlPanel` | Desenho vetorial com gradientes e formas |
| `JLayeredPane` | `SimonPanel` | Componentes em camadas sobrepostas |
| `CardLayout` | `MainFrame` | Alternar entre ecrãs |
| `Thread` daemon | `AudioServiceImpl` | Tarefa em background que não bloqueia o programa |
| `List.copyOf()` | `GameSequence` | Lista imutável — cópia defensiva |
| `Consumer<T>` | Callbacks da UI | Função que recebe um argumento e não retorna nada |

---

*Este guia cobre 2094 linhas de código Java distribuídas por 25 classes.
Se tiveres dúvidas sobre alguma parte específica, volta a este guia e
segue as referências entre capítulos.*
