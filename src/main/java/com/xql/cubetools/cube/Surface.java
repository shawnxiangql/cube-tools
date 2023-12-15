package com.xql.cubetools.cube;

public class Surface extends NamedElement {
    private String[] sliceNames;

    public Surface(String name, String... sliceNames) {
        super(name);
        this.sliceNames = sliceNames;
    }

}
