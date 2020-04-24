package io.parkersmith.swmc.show.fountains;

import io.parkersmith.swmc.show.Main;

import java.util.ArrayList;
import java.util.List;

public class FountainManager {
    private List<Fountain> fountains = new ArrayList();

    public FountainManager() {
    }

    public void disable() {
        this.fountains.forEach(Fountain::stop);
        this.fountains.clear();
        Main.fallingBlocks.forEach((block) -> {
            block.remove();
        });
        Main.fallingBlocks.clear();
    }

    public Fountain getFountainByName(String name) {
        return (Fountain)this.fountains.stream().filter((fountain) -> {
            return fountain.getName().equals(name);
        }).findFirst().orElse((Fountain) null);
    }

    public void addFountain(Fountain fountain) {
        this.fountains.add(fountain);
    }

    public void removeFountain(Fountain fountain) {
        this.fountains.remove(fountain);
    }

    public List<Fountain> getFountains() {
        return this.fountains;
    }
}
