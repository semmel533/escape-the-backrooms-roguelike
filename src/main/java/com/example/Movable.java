package com.example;

import com.example.Level.Pos;

public class Movable {
    private Pos pos;

    public Movable(Pos pos) {
        this.pos = pos;
    }

    public void move(Pos newPos) {
        this.pos = newPos;
    }

    public Pos getPos() {
        return this.pos;
    }

}
