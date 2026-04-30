package com.example;

import java.util.HashMap;
import java.util.Map;

import com.example.Room.Type;

public class Level {

    public record Pos(int x, int y) {
    }

    private Map<Pos, Room> map;

    private final Player player;

    private Map<Pos, Movable> entities;

    private int depth;

    private void generateMap(int roomAmount) {
        this.map = new HashMap<>();
        this.entities = new HashMap<>();

        Pos entryPos = new Pos(0, 0);
        Room entryRoom = new Room(Type.ENTRY, entryPos);
        boolean exitSet = false;

        this.player.move(entryPos, false);
        entryRoom.playerEnter(this.player.getName());

        map.put(entryPos, entryRoom);

        Pos lastPos = new Pos(0, 0);
        for (int i = 0; i < roomAmount; i++) {

            int tries = 0;
            while (true) {
                Pos lastPosMem = new Pos(lastPos.x, lastPos.y);
                int randXY = (int) (Math.random() * 2);
                int randInDe = (int) (Math.random() * 2);

                lastPos = switch (randXY) {
                    case 0 -> new Pos(randInDe == 0 ? lastPos.x + 1
                            : lastPos.x - 1, lastPos.y);
                    case 1 -> new Pos(lastPos.x, randInDe == 0 ? lastPos.y + 1
                            : lastPos.y - 1);
                    default -> new Pos(-1000, -1000);
                };

                if (++tries > 100) {

                    outer: for (int y = 0; y < i; y++) {
                        for (int x = 0; x < i; x++) {
                            if (map.get(new Pos(lastPos.x + x,
                                    lastPos.y + y)) == null) {
                                lastPos = new Pos(lastPos.x + x,
                                        lastPos.y + y);
                                break outer;
                            } else if (map.get(new Pos(lastPos.x + x,
                                    lastPos.y - y)) == null) {
                                lastPos = new Pos(lastPos.x + y,
                                        lastPos.y - y);
                                break outer;
                            } else if (map.get(new Pos(lastPos.x - x,
                                    lastPos.y + y)) == null) {
                                lastPos = new Pos(lastPos.x - x,
                                        lastPos.y + y);
                                break outer;
                            } else if (map.get(new Pos(lastPos.x - x,
                                    lastPos.y - y)) == null) {
                                lastPos = new Pos(lastPos.x - x,
                                        lastPos.y - y);
                                break outer;
                            }
                        }
                    }
                }
                if (map.get(lastPos) != null || Math.random() > 0.5) {
                    lastPos = lastPosMem;
                    tries++;
                } else {
                    break;
                }

            }
            if (!exitSet && ((i == (roomAmount - 1)) || (i > (roomAmount / 2)
                    && Math.random() < 0.01))) {
                map.put(lastPos, new Room(Type.EXIT, lastPos));
                exitSet = true;
            } else {
                map.put(lastPos, new Room(Type.NORMAL, lastPos));
            }

        }

        double spawnChance = this.depth * this.depth;

        int entityCount = 0;
        int specialRoomCount = 0;

        for (int i = 0; i < this.depth; i++) {
            if (spawnChance > Math.random() * 100) {
                entityCount++;
            }
            if (spawnChance > Math.random() * 100) {
                specialRoomCount++;
            }
        }

        Room[] mapArray = new Room[map.size()];
        Pos[] posArray = new Pos[map.size()];

        int iter = 0;
        int iter2 = 0;

        for (Room room : map.values()) {
            mapArray[iter] = room;
            iter++;
        }

        for (Pos pos : map.keySet()) {
            posArray[iter2] = pos;
            iter2++;
        }

        for (int i = 0; i < entityCount; i++) {
            boolean tryAgain = true;
            int tries = 0;

            while (tryAgain) {

                if (++tries % 10 == 0) {
                    int entityCounter = 0;
                    for (Room room : map.values()) {
                        if (room.getEntityPresent()) {
                            entityCounter++;
                        }
                    }
                    if (map.size() - 2 == entityCounter) {
                        break;
                    }
                }

                int randIndex = (int) (Math.random() * posArray.length);

                if (map.get(posArray[randIndex]).getEntityPresent()
                        && (map.get(posArray[randIndex]).getType()
                        == Type.ENTRY
                        || map.get(posArray[randIndex]).getType()
                        == Type.EXIT)) {
                    tryAgain = true;
                } else {
                    entities.put(posArray[randIndex],
                            new Movable(posArray[randIndex]));
                    map.get(posArray[randIndex]).entityEnter();
                    tryAgain = false;
                }
            }
        }

        for (int i = 0; i < specialRoomCount; i++) {

            boolean tryAgain = true;
            int tries = 0;

            while (tryAgain) {

                if (++tries % 10 == 0) {
                    int specialRoomCounter = 0;
                    for (Room room : map.values()) {
                        if (room.getType() != Type.NORMAL) {
                            specialRoomCounter++;
                        }
                    }
                    if (map.size() == specialRoomCounter) {
                        break;
                    }
                }

                int randIndex = (int) (Math.random() * map.size());
                int randType = (int) (Math.random() * 2);

                tryAgain = switch (mapArray[randIndex].getType()) {
                    case Type.NORMAL -> {
                        mapArray[randIndex].setType(randType == 0
                                ? Type.PLAYGROUND : Type.WORMHOLE);
                        yield false;
                    }
                    default -> true;
                };
            }
        }

    }

    public Level(Player player, int depth, int roomAmount) {
        this.player = player;
        this.depth = depth;
        generateMap(roomAmount);
    }

    public Map<Pos, Room> getMap() {
        return map;
    }

    public Map<Pos, Movable> getEntities() {
        return entities;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public void enterPlayground(Pos playgroundPos) {
        int rand = (int) (Math.random() * 2);

        this.player.setSanity(
                rand == 0 ? this.player.getSanity() * 2
                        : this.player.getSanity()
                        + (int) (0.2 * Player.MAXSANITY));
        this.player.setSanity(this.player.getSanity() > Player.MAXSANITY
                ? Player.MAXSANITY : this.player.getSanity());
        this.map.get(playgroundPos).setType(Type.NORMAL);
    }

    public void enterWormhole(Pos wormholePos) {
        int iter = 0;
        Pos[] roomPosArr = new Pos[this.map.size()];
        for (Pos pos : this.map.keySet()) {
            roomPosArr[iter] = pos;
            iter++;
        }
        while (true) {
            int randInd = (int) (Math.random() * this.map.size());
            if (this.map.get(roomPosArr[randInd]).getType() == Type.NORMAL) {
                map.get(this.player.getPos()).playerLeave();
                this.player.move(roomPosArr[randInd], false);
                map.get(roomPosArr[randInd]).playerEnter(this.player.getName());
                this.map.get(wormholePos).setType(Type.NORMAL);
                break;
            }
        }

    }

    public void allEntitesMove() {
        int iter = 0;
        Pos[] posArr = new Pos[entities.size()];
        for (Pos pos : entities.keySet()) {
            posArr[iter] = pos;
            iter++;
        }

        for (Pos pos : posArr) {
            while (true) {
                int rand = (int) (Math.random() * 5);
                Pos randPos = new Pos(rand == 0 ? pos.x + 1
                        : rand == 1 ? pos.x - 1 : pos.x,
                        rand == 2 ? pos.y + 1
                                : rand == 3 ? pos.y - 1 : pos.y);
                if (map.get(randPos) != null) {
                    map.get(pos).entityLeave();
                    Movable currEntity = entities.get(pos);
                    currEntity.move(randPos);
                    entities.remove(pos);
                    entities.put(randPos, currEntity);
                    map.get(randPos).entityEnter();
                    break;
                }

            }

        }

    }

    public String playerView() {
        Pos playerPos = this.player.getPos();

        StringBuilder sb = new StringBuilder();

        String noRoom = """
                ██████████████
                ██████████████
                ██████████████
                ██████████████
                ██████████████
                ██████████████
                ██████████████
                """;

        String noSight = """
                ░░░░░░░░░░░░░░
                ░░░░░░░░░░░░░░
                ░░░░░░░░░░░░░░
                ░░░░░░░░░░░░░░
                ░░░░░░░░░░░░░░
                ░░░░░░░░░░░░░░
                ░░░░░░░░░░░░░░
                """;

        for (int y = playerPos.y - 2; y <= playerPos.y + 2; y++) {

            String[] x0 = this.player.getSanity()
                    < (int) (0.5 * Player.MAXSANITY) ? noSight.split("\\R")
                    : this.map.get(new Pos(playerPos.x - 2, y)) == null
                    ? noRoom.split("\\R")
                    : this.map.get(new Pos(playerPos.x - 2, y))
                    .toString().split("\\R");
            String[] x1 = this.player.getSanity()
                    < (int) (0.5 * Player.MAXSANITY)
                    && (y == playerPos.y - 2 || y == playerPos.y + 2)
                    ? noSight.split("\\R")
                    : this.map.get(new Pos(playerPos.x - 1, y)) == null
                    ? noRoom.split("\\R")
                    : this.map.get(new Pos(playerPos.x - 1, y))
                    .toString().split("\\R");
            String[] x2 = this.player.getSanity()
                    < (int) (0.5 * Player.MAXSANITY)
                    && (y == playerPos.y - 2 || y == playerPos.y + 2)
                    ? noSight.split("\\R")
                    : this.map.get(new Pos(playerPos.x, y)) == null
                    ? noRoom.split("\\R")
                    : this.map.get(new Pos(playerPos.x, y))
                    .toString().split("\\R");
            String[] x3 = this.player.getSanity()
                    < (int) (0.5 * Player.MAXSANITY)
                    && (y == playerPos.y - 2 || y == playerPos.y + 2)
                    ? noSight.split("\\R")
                    : this.map.get(new Pos(playerPos.x + 1, y)) == null
                    ? noRoom.split("\\R")
                    : this.map.get(new Pos(playerPos.x + 1, y))
                    .toString().split("\\R");
            String[] x4 = this.player.getSanity()
                    < (int) (0.5 * Player.MAXSANITY) ? noSight.split("\\R")
                    : this.map.get(new Pos(playerPos.x + 2, y)) == null
                    ? noRoom.split("\\R")
                    : this.map.get(new Pos(playerPos.x + 2, y))
                    .toString().split("\\R");

            for (int i = 0; i < x2.length; i++) {
                sb.append(x0[i]);
                sb.append(x1[i]);
                sb.append(x2[i]);
                sb.append(x3[i]);
                sb.append(x4[i]);
                sb.append("\n");
            }
        }

        return sb.toString();
    }

    public boolean isMoveable(Pos pos) {
        return this.map.get(pos) != null;
    }

}
