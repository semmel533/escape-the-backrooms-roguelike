package com.example;

import com.example.Level.Pos;

public class Player extends Movable {

    /**
     * All possible Moves.
     */
    public enum MOVEDECISION {
        /**
         * Valid Move.
         */
        VALID,

        /**
         * Invalid Move.
         */
        INVALID,

        /**
         * Quit the Game.
         */
        QUIT,

        /**
         * Entered the Level Exit.
         */
        EXIT,

        /**
         * Ran out of Sanity.
         */
        GAMEOVER
    }

    /**
     * Maximum and Entry Player Sanity.
     */
    public static final int MAXSANITY = 1000;
    private final String name;
    private int sanity;

    public Player(String name, Pos pos) {
        super(pos);
        this.name = name;
        this.sanity = MAXSANITY;
    }

    public void move(Pos pos, boolean consumeSanity) {
        super.move(pos);
        if (consumeSanity) {
            this.sanity--;
        }
    }

    public String getName() {
        return this.name;
    }

    public int getSanity() {
        return this.sanity;
    }

    public void setSanity(int sanity) {
        this.sanity = sanity;
    }

    public void sanityRefresh() {
        this.sanity = MAXSANITY;
    }

    public void entityEncounter() {
        double rand = Math.random();
        this.sanity = rand < 0.01 ? 0 : rand < 0.2 ? this.sanity / 2 
            : this.sanity - (int) (0.1 * MAXSANITY);
    }

    @Override
    public String toString() {
        String mem = "";
        mem += "PLAYER: " + this.name + "\n";
        mem += "SANITY: ";
        double sanityPercentage = (double) this.sanity / MAXSANITY;
        int sanityLeftChars = (int) (sanityPercentage * 62);
        for (int o = 0; o < 1; o++) {
            for (int i = 0; i < sanityLeftChars; i++) {
                mem += "█";
            }
            for (int i = 0; i < 62 - (Math.max(sanityLeftChars, 0)); i++) {
                mem += "░";
            }
            mem += "\n";
        }
        return mem;
    }

}
