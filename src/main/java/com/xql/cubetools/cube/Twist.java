package com.xql.cubetools.cube;

import java.util.List;

public class Twist extends NamedElement {
    private final List<String[]> sliceSwapGroups;

    public Twist(String name, List<String[]> sliceSwapGroups) {
        super(name);
        this.sliceSwapGroups = sliceSwapGroups;
    }

    public List<String[]> getSliceSwapGroups() {
        return sliceSwapGroups;
    }

    public void doTwist(Cube333 cube) {
        sliceSwapGroups.forEach(g -> cube.loopSwap(g[0], g[1], g[2], g[3]));
    }
}
