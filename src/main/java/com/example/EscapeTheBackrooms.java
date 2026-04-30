package com.example;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import com.example.Level.Pos;
import com.example.Room.Type;

public class EscapeTheBackrooms implements AutoCloseable {
    Scanner sc = new Scanner(System.in);
    Terminal terminal;
    private Player player;
    private Level currentLevel;
    private String notification = "Eine beunruhigende Stille umgibt dich...";
    private org.jline.terminal.Attributes originalAttributes;

    @Override
    public void close() {
        if (terminal != null) {
            try {
                if (originalAttributes != null) {
                    terminal.setAttributes(originalAttributes);
                }
                terminal.flush();
                terminal.close();
            } catch (IOException ignored) {
            }
        }
        sc.close();
        System.out.println();
    }

    public void jumpscare() {
        final int width = 70;
        final int height = 42;
        final char[] chars = { '░', '█', ' ' };
        final Random rand = new Random();
        long startTime = System.currentTimeMillis();

        try {
            File soundFile = new File("class.wav");
            AudioInputStream audioIn = AudioSystem
                    .getAudioInputStream(soundFile);

            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);

            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        while (true) {
            if (System.currentTimeMillis() - startTime > 4000) {
                break;
            }
                
            StringBuilder frame = new StringBuilder();

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    frame.append(chars[rand.nextInt(chars.length)]);
                }
                frame.append('\n');
            }

            System.out.print("\033[H\033[2J\033[3J");
            System.out.flush();
            System.out.print(frame);
            try {
                Thread.sleep(60);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public static void main(String[] args) {
        try (var backrooms = new EscapeTheBackrooms()) {
            backrooms.start();
        } catch (IOException e) {
            System.out.println("IOException!");
        }
    }

    public Player.MOVEDECISION playerMove() throws IOException {

        int ch = this.terminal.reader().read();

        Pos newPos = switch (ch) {
            case 'w' -> new Pos(player.getPos().x(), player.getPos().y() - 1);
            case 'a' -> new Pos(player.getPos().x() - 1, player.getPos().y());
            case 's' -> new Pos(player.getPos().x(), player.getPos().y() + 1);
            case 'd' -> new Pos(player.getPos().x() + 1, player.getPos().y());
            case 'q' -> {
                this.notification = "Spiel beendet.";
                yield new Pos(-10000, -10000);
            }
            default -> new Pos(-10000, -10000);
        };
        
        Player.MOVEDECISION md = switch (ch) {
            case 'w', 'a', 's', 'd' -> Player.MOVEDECISION.VALID;
            case 'q' -> Player.MOVEDECISION.QUIT;
            default -> Player.MOVEDECISION.INVALID;
        };
        if (md == Player.MOVEDECISION.VALID
                && currentLevel.getMap().get(newPos) != null) {

            currentLevel.allEntitesMove();
            currentLevel.getMap().get(player.getPos()).playerLeave();
            player.move(newPos, true);
            currentLevel.getMap().get(newPos)
                    .playerEnter(this.player.getName());

            if (currentLevel.getMap().get(newPos).getEntityPresent()) {
                this.player.entityEncounter();
            }

            if (player.getSanity() <= 0) {
                this.notification = "GAMEOVER!";
                md = Player.MOVEDECISION.GAMEOVER;
                jumpscare();
                return md;
            }

            md = switch (currentLevel.getMap().get(newPos).getType()) {
                case Type.EXIT -> {
                    this.notification =
                            "Das war nur ein Ausgang von vielen...";
                    yield Player.MOVEDECISION.EXIT;
                }
                case Type.PLAYGROUND -> {
                    this.currentLevel.enterPlayground(newPos);
                    this.notification =
                            "Hier scheint ein Spielplatz zu sein?";
                    yield Player.MOVEDECISION.VALID;
                }
                case Type.WORMHOLE -> {
                    this.currentLevel.enterWormhole(newPos);
                    this.notification =
                            "Raum und Zeit haben sich verschoben!";
                    yield Player.MOVEDECISION.VALID;
                }
                default -> {
                    this.notification = 
                            "Eine beunruhigende Stille umgibt dich...";
                    yield Player.MOVEDECISION.VALID;
                }
            };
            
            if (currentLevel.getMap().get(newPos).getEntityPresent()) {
                this.notification +=
                        "\nEine dir unbekannte Spezies ist anwesend. " 
                        + "Verhalte dich unauffälig.";
            }

        }
        return md;
    }

    public void renderGameView() {
        System.out.print("\033[H\033[2J\033[3J");
        System.out.flush();
        System.out.println(this.player);
        System.out.println(currentLevel.playerView());
        System.out.println("DEPTH: " + this.currentLevel.getDepth());
        System.out.println("NOTIFICATION: " + this.notification);
    }

    public void start() throws IOException {
        String title =
     """
     ,---.     .---.   ,--,   .--. ,---. ,---.    _______.-. .-.,---.
     | .-'    ( .-._).' .')  / /\\ \\| .-.\\| .-'   |__   __| | | || .-'
     | `-.   (_) \\   |  |(_)/ /__\\ \\ |-' ) `-.     )| |  | `-' || `-.
     | .-'   _  \\ \\  \\  \\   |  __  | |--'| .-'    (_) |  | .-. || .-'
     |  `--.( `-'  )  \\  `-.| |  |)| |   |  `--.    | |  | | |)||  `--.
     /( __.' `----'    \\____\\_|  (_)(    /( __.'    `-'  /(  (_)/( __.'
    (__)                          (__)  (__)            (__)   (__)
     ,---.    .--.   ,--, ,-. .-.,---.   .---.  .---.           .---.
     | .-.\\  / /\\ \\.' .') | |/ / | .-.\\ / .-. )/ .-. )|\\    /| ( .-._)
     | |-' \\/ /__\\ \\  |(_)| | /  | `-'/ | | |(_) | |(_)(\\  / |(_) \\
     | |--. \\  __  \\  \\   | | \\  |   (  | | | || | | |(_)\\/  |_  \\ \\
     | |`-' / |  |)|\\  `-.| |) \\ | |\\ \\ \\ `-' /\\ `-' /| \\  / ( `-'  )
     /( `--'|_|  (_) \\____\\((_)-'|_| \\)\\ )---'  )---' | |\\/| |`----'
    (__)                  (_)        (__|_)    (_)    '-'  '-'
    """;
        System.out.println("\n" + title);
        System.out.print("Wie lautet dein Name? ");
        this.player = new Player(sc.nextLine(), new Pos(0, 0));
        this.terminal = TerminalBuilder.terminal();
        this.originalAttributes = terminal.getAttributes();
        terminal.enterRawMode();
        this.currentLevel = new Level(player, 1, 1000);

        renderGameView();
        Player.MOVEDECISION md;

        do {

            md = playerMove();

            if (md == Player.MOVEDECISION.EXIT) {
                this.player = new Player(this.player.getName(),
                        new Pos(0, 0));
                this.currentLevel = new Level(player,
                        this.currentLevel.getDepth() + 1, 100);
            }
            if (md != Player.MOVEDECISION.GAMEOVER) {
                renderGameView();
            }

        } while (md != Player.MOVEDECISION.QUIT
                && md != Player.MOVEDECISION.GAMEOVER);
    }
}
