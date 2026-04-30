package com.example;

import com.example.Level.Pos;

public class Room {

    private Type type;
    private String playerPresent = "";
    private boolean entityPresent = false;
    private final Pos pos;

    /**
     * Room Type.
     */
    public enum Type {
        /**
         * Normal Room nothing special.
         */
        NORMAL,

        /**
         * Entry of the Level.
         */
        ENTRY,

        /**
         * Exit of the Level.
         */
        EXIT,

        /**
         * Sanity Gainer Special Room.
         */
        PLAYGROUND,

        /**
         * Random Teleportation Special Room.
         */
        WORMHOLE
    }

    public Room(Type type, Pos pos) {
        this.type = type;
        this.pos = pos;
    }

    public boolean getPlayerPresent() {
        return !this.playerPresent.isEmpty();
    }

    public void playerEnter(String playerName) {
        this.playerPresent = playerName.length() > 5 
            ? playerName.substring(0, 6) : playerName;
    }

    public void playerLeave() {
        this.playerPresent = "";
    }

    public boolean getEntityPresent() {
        return this.entityPresent;
    }

    public void entityEnter() {
        this.entityPresent = true;
    }

    public void entityLeave() {
        this.entityPresent = false;
    }

    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Pos getPos() {
        return this.pos;
    }

    @Override
    public String toString() {
        String entityAndPlayer = """
│            │
│   Entity   │
│            │
│   %-6s   │
│            │
└────────────┘
                """;
        String player = """
│            │
│            │
│   %-6s   │
│            │
│            │
└────────────┘
                """;
        String entity = """
│            │
│            │
│   Entity   │
│            │
│            │
└────────────┘
                """;
        String nothing = """
│            │
│            │
│            │
│            │
│            │
└────────────┘
                """;
        return switch (this.type) {
            case NORMAL -> {
                if (getPlayerPresent() && this.entityPresent) {
                    yield "┌────────────┐\n" 
                        + String.format(entityAndPlayer, this.playerPresent);
                } else if (getPlayerPresent()) {
                    yield "┌────────────┐\n" 
                        + String.format(player, this.playerPresent);
                } else if (this.entityPresent) {
                    yield "┌────────────┐\n" 
                        + entity;
                } else {
                    yield "┌────────────┐\n" 
                        + nothing;
                }
            }
            case ENTRY -> {
                if (getPlayerPresent() && this.entityPresent) {
                    yield "┌─ENTRY──────┐\n" 
                        + String.format(entityAndPlayer, this.playerPresent);
                } else if (getPlayerPresent()) {
                    yield "┌─ENTRY──────┐\n" 
                        + String.format(player, this.playerPresent);
                } else if (this.entityPresent) {
                    yield "┌─ENTRY──────┐\n" 
                        + entity;
                } else {
                    yield "┌─ENTRY──────┐\n" 
                        + nothing;
                }
            }
            case EXIT -> {
                if (getPlayerPresent() && this.entityPresent) {
                    yield "┌─EXIT───────┐\n" 
                        + String.format(entityAndPlayer, this.playerPresent);
                } else if (getPlayerPresent()) {
                    yield "┌─EXIT───────┐\n" 
                        + String.format(player, this.playerPresent);
                } else if (this.entityPresent) {
                    yield "┌─EXIT───────┐\n" 
                        + entity;
                } else {
                    yield "┌─EXIT───────┐\n" 
                        + nothing;
                }
            }
            case PLAYGROUND -> {
                if (getPlayerPresent() && this.entityPresent) {
                    yield "┌─PLAYGROUND─┐\n" 
                        + String.format(entityAndPlayer, this.playerPresent);
                } else if (getPlayerPresent()) {
                    yield "┌─PLAYGROUND─┐\n" 
                        + String.format(player, this.playerPresent);
                } else if (this.entityPresent) {
                    yield "┌─PLAYGROUND─┐\n" 
                        + entity;
                } else {
                    yield "┌─PLAYGROUND─┐\n" 
                        + nothing;
                }
            }
            case WORMHOLE -> {
                if (getPlayerPresent() && this.entityPresent) {
                    yield "┌─WORMHOLE───┐\n" 
                        + String.format(entityAndPlayer, this.playerPresent);
                } else if (getPlayerPresent()) {
                    yield "┌─WORMHOLE───┐\n" 
                        + String.format(player, this.playerPresent);
                } else if (this.entityPresent) {
                    yield "┌─WORMHOLE───┐\n" 
                        + entity;
                } else {
                    yield "┌─WORMHOLE───┐\n" 
                        + nothing;
                }
            }
            default -> "┌─ERROR──────┐\n"
                    + nothing;
        };
    }

}
