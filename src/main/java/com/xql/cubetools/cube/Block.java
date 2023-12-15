package com.xql.cubetools.cube;

public class Block extends NamedElement {
    private final String[] sliceNames;

    public Block(String name, String... sliceNames) {
        super(name);
        this.sliceNames = sliceNames;
    }

    public String[] getSliceNames() {
        return sliceNames;
    }

}
