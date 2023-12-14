package com.xql.cubetools;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * <pre>
 *              |************|
 *              |*U1**U2**U3*|
 *              |************|
 *              |*U4**U5**U6*|
 *              |************|
 *              |*U7**U8**U9*|
 *              |************|
 *  ************|************|************|************|
 *  *L1**L2**L3*|*F1**F2**F3*|*R1**R2**R3*|*B1**B2**B3*|
 *  ************|************|************|************|
 *  *L4**L5**L6*|*F4**F5**F6*|*R4**R5**R6*|*B4**B5**B6*|
 *  ************|************|************|************|
 *  *L7**L8**L9*|*F7**F8**F9*|*R7**R8**R9*|*B7**B8**B9*|
 *  ************|************|************|************|
 *              |************|
 *              |*D1**D2**D3*|
 *              |************|
 *              |*D4**D5**D6*|
 *              |************|
 *              |*D7**D8**D9*|
 *              |************|
 * </pre>
 */
public class Cube {
    private final int[] slices = {
            U1, U2, U3, U4, U5, U6, U7, U8, U9,
            R1, R2, R3, R4, R5, R6, R7, R8, R9,
            F1, F2, F3, F4, F5, F6, F7, F8, F9,
            D1, D2, D3, D4, D5, D6, D7, D8, D9,
            L1, L2, L3, L4, L5, L6, L7, L8, L9,
            B1, B2, B3, B4, B5, B6, B7, B8, B9,
    };

    private static final EnumMap<Slice, Integer> si;

    static {
        si = new EnumMap<>(Slice.class);
        for (int i = 0; i < Slice.values().length; i++) {
            si.put(Slice.values()[i], i);
        }
    }

    public Cube() {
    }

    public Cube(String str) {
        this();
        String[] steps = str.split(" ");
        for (String step : steps) {
            Ops.nameMap.get(step).run(this);
        }
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        String[] sides = {"U", "R", "F", "D", "L", "B"};
        for (int i = 0; i < sides.length; i++) {
            s.append(sides[i]).append("\n");
            for (int j = 0; j < 9; j++) {
                s.append(slices[i * 9 + j]).append(" ");
                if (j % 3 == 2) {
                    s.append("\n");
                }
            }
        }
        return s.append("\n").toString();
    }

    /**
     * a -> b -> c -> d -> a
     */
    public Cube loopSwap(int a, int b, int c, int d) {
        int indexA = si.get(a);
        int indexB = si.get(b);
        int indexC = si.get(c);
        int indexD = si.get(d);
        Slice tmp = slices[indexD];
        slices[indexD] = slices[indexC];
        slices[indexC] = slices[indexB];
        slices[indexB] = slices[indexA];
        slices[indexA] = tmp;
        return this;
    }

    private int edgeBuffer = U6;
    private int cornerBuffer = U9;

    private int[] edgeFind = {U8, U4, U2, D2, D4, D8, D6, F4, F6, B6, B4};

    public List<List<String>> getCodes() {
        List<String> edgeCodes = new ArrayList<>();
        List<Slice> edgeFlip = new ArrayList<>();

        Set<Block> blocks = new HashSet<>();
        int current = edgeBuffer;
        Block currentBlock = current.getBlock();
        blocks.add(currentBlock);

        Slice target = getSlice(current);
        Block targetBlock = target.getBlock();

        int findIndex = 0;

        while (blocks.size() < 12) {
            if (!blocks.contains(targetBlock)) {
                current = target;
                currentBlock = targetBlock;
                blocks.add(currentBlock);

                target = getSlice(current);
                targetBlock = target.getBlock();
            } else {
                if (currentBlock == targetBlock) {
                    if (current == target) {
                        // 当前位置已经还原
                    } else {
                        // 当前位置需要翻色
                    }
                }
                // 跳到下一块
                current = edgeFind[findIndex++];
                currentBlock = current.getBlock();
                blocks.add(currentBlock);

                target = getSlice(current);
                targetBlock = target.getBlock();
            }
        }

        List<String> cornerCodes = new ArrayList<>();


        return null;
    }

    private Slice getSlice(Slice position) {
        return slices[si.get(position)];
    }
}


class Slice {
    private static final AtomicInteger index = new AtomicInteger(0);
    private final int id;

    public Slice() {
        this.id = index.getAndIncrement();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Slice slice = (Slice) o;
        return id == slice.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    Slice U1 = new Slice(), U2 = new Slice(), U3 = new Slice(), U4 = new Slice(), U5 = new Slice(), U6 = new Slice(), U7 = new Slice(), U8 = new Slice(), U9 = new Slice(),
            R1 = new Slice(), R2 = new Slice(), R3 = new Slice(), R4 = new Slice(), R5 = new Slice(), R6 = new Slice(), R7 = new Slice(), R8 = new Slice(), R9 = new Slice(),
            F1 = new Slice(), F2 = new Slice(), F3 = new Slice(), F4 = new Slice(), F5 = new Slice(), F6 = new Slice(), F7 = new Slice(), F8 = new Slice(), F9 = new Slice(),
            D1 = new Slice(), D2 = new Slice(), D3 = new Slice(), D4 = new Slice(), D5 = new Slice(), D6 = new Slice(), D7 = new Slice(), D8 = new Slice(), D9 = new Slice(),
            L1 = new Slice(), L2 = new Slice(), L3 = new Slice(), L4 = new Slice(), L5 = new Slice(), L6 = new Slice(), L7 = new Slice(), L8 = new Slice(), L9 = new Slice(),
            B1 = new Slice(), B2 = new Slice(), B3 = new Slice(), B4 = new Slice(), B5 = new Slice(), B6 = new Slice(), B7 = new Slice(), B8 = new Slice(), B9 = new Slice();

}

class Block {
    private final Slice[] slices;

    private Block(Slice... slices) {
        this.slices = slices;
    }

    //    UFL = {U7, F1, L3},
//    ULB = {U1, L1, B3},
//    UBR = {U3, B1, R3},
//    URF = {U9, F3, R1},
//    DLF = {D1, L9, F7},
//    DBL = {D7, B9, L7},
//    DRB = {D9, R9, B7},
//    DFR = {D3, F9, R7},
//    UF = {U8, F2},
//    UL = {U4, L2},
//    UB = {U2, B2},
//    UR = {U6, R2},
//    DF = {D2, F8},
//    DL = {D4, L8},
//    DB = {D8, B8},
//    DR = {D6, R8},
//    FR = {F6, R4},
//    FL = {F4, L6},
//    BL = {B6, L4},
//    BR = {B4, R6};
    public static final Block
            UFL = new Block(Slice.U7, Slice.F1, Slice.L3),
            ULB = new Block(Slice.U1, Slice.L1, Slice.B3),
            UBR = new Block(Slice.U3, Slice.B1, Slice.R3),
            URF = new Block(Slice.U9, Slice.F3, Slice.R1),
            DLF = new Block(Slice.D1, Slice.L9, Slice.F7),
            DBL = new Block(Slice.D7, Slice.B9, Slice.L7),
            DRB = new Block(Slice.D9, Slice.R9, Slice.B7),
            DFR = new Block(Slice.D3, Slice.F9, Slice.R7),
            UF = new Block(Slice.U8, Slice.F2),
            UL = new Block(Slice.U4, Slice.L2),
            UB = new Block(Slice.U2, Slice.B2),
            UR = new Block(Slice.U6, Slice.R2),
            DF = new Block(Slice.D2, Slice.F8),
            DL = new Block(Slice.D4, Slice.L8),
            DB = new Block(Slice.D8, Slice.B8),
            DR = new Block(Slice.D6, Slice.R8),
            FR = new Block(Slice.F6, Slice.R4),
            FL = new Block(Slice.F4, Slice.L6),
            BL = new Block(Slice.B6, Slice.L4),
            BR = new Block(Slice.B4, Slice.R6);

}

interface Const {
    // slices

    // blocks
//    int UFL = 1, ULB = 2, UBR = 3, URF = 4, DLF = 5, DBL = 6, DRB = 7, DFR = 8,
//            UF = 1, UL = 2, UB = 3, UR = 4, DF = 5, DL = 6, DB = 7, DR = 8, FR = 9, FL = 10, BL = 11, BR = 12;

    Map<Integer, Integer> blockMap = new HashMap<>() {
        {

            put(U7, UFL);
            put(F1, UFL);
            put(L3, UFL);
            put(U1, ULB);
            put(L1, ULB);
            put(B3, ULB);
            put(U3, UBR);
            put(B1, UBR);
            put(R3, UBR);
            put(U9, URF);
            put(F3, URF);
            put(R1, URF);
            put(D1, DLF);
            put(L9, DLF);
            put(F7, DLF);
            put(D7, DBL);
            put(B9, DBL);
            put(L7, DBL);
            put(D9, DRB);
            put(R9, DRB);
            put(B7, DRB);
            put(D3, DFR);
            put(F9, DFR);
            put(R7, DFR);
            put(U8, UF);
            put(F2, UF);
            put(U4, UL);
            put(L2, UL);
            put(U2, UB);
            put(B2, UB);
            put(U6, UR);
            put(R2, UR);
            put(D2, DF);
            put(F8, DF);
        }
    };
}


enum Ops {
    OP_E("E", cube -> cube
            .loopSwap(F4, R4, B4, L4)
            .loopSwap(F5, R5, B5, L5)
            .loopSwap(F6, R6, B6, L6)),
    OP_E2("E2", OP_E.cubeConsumer.andThen(OP_E.cubeConsumer)),
    OP_Ep("E'", OP_E.cubeConsumer.andThen(OP_E.cubeConsumer).andThen(OP_E.cubeConsumer)),

    OP_M("M", cube -> cube
            .loopSwap(F2, D2, B8, U2)
            .loopSwap(F5, D5, B5, U5)
            .loopSwap(F8, D8, B2, U8)),
    OP_M2("M2", OP_M.cubeConsumer.andThen(OP_M.cubeConsumer)),
    OP_Mp("M'", OP_M.cubeConsumer.andThen(OP_M.cubeConsumer).andThen(OP_M.cubeConsumer)),

    OP_S("S", cube -> cube
            .loopSwap(D6, R8, U4, L2)
            .loopSwap(D5, R5, U5, L5)
            .loopSwap(D4, R2, U6, L8)),
    OP_S2("S2", OP_S.cubeConsumer.andThen(OP_S.cubeConsumer)),
    OP_Sp("S'", OP_S.cubeConsumer.andThen(OP_S.cubeConsumer).andThen(OP_S.cubeConsumer)),

    OP_R("R", cube -> cube
            .loopSwap(U3, B7, D3, F3)
            .loopSwap(U6, B4, D6, F6)
            .loopSwap(U9, B1, D9, F9)
            .loopSwap(R1, R3, R9, R7)
            .loopSwap(R2, R6, R8, R4)),
    OP_R2("R2", OP_R.cubeConsumer.andThen(OP_R.cubeConsumer)),
    OP_Rp("R'", OP_R.cubeConsumer.andThen(OP_R.cubeConsumer).andThen(OP_R.cubeConsumer)),
    OP_Rw("Rw", OP_R.cubeConsumer.andThen(OP_Mp.cubeConsumer)),
    OP_Rw2("Rw2", OP_R2.cubeConsumer.andThen(OP_M2.cubeConsumer)),
    OP_Rwp("Rw'", OP_Rp.cubeConsumer.andThen(OP_M.cubeConsumer)),

    OP_U("U", cube -> cube
            .loopSwap(L1, B1, R1, F1)
            .loopSwap(L2, B2, R2, F2)
            .loopSwap(L3, B3, R3, F3)
            .loopSwap(U1, U3, U9, U7)
            .loopSwap(U2, U6, U8, U4)),
    OP_U2("U2", OP_U.cubeConsumer.andThen(OP_U.cubeConsumer)),
    OP_Up("U'", OP_U.cubeConsumer.andThen(OP_U.cubeConsumer).andThen(OP_U.cubeConsumer)),
    OP_Uw("Uw", OP_U.cubeConsumer.andThen(OP_Ep.cubeConsumer)),
    OP_Uw2("Uw2", OP_U2.cubeConsumer.andThen(OP_E2.cubeConsumer)),
    OP_Uwp("Uw'", OP_Up.cubeConsumer.andThen(OP_E.cubeConsumer)),

    OP_F("F", cube -> cube
            .loopSwap(U7, R1, D3, L9)
            .loopSwap(U8, R4, D2, L6)
            .loopSwap(U9, R7, D1, L3)
            .loopSwap(F1, F3, F9, F7)
            .loopSwap(F2, F6, F8, F4)),
    OP_F2("F2", OP_F.cubeConsumer.andThen(OP_F.cubeConsumer)),
    OP_Fp("F'", OP_F.cubeConsumer.andThen(OP_F.cubeConsumer).andThen(OP_F.cubeConsumer)),
    OP_Fw("Fw", OP_F.cubeConsumer.andThen(OP_S.cubeConsumer)),
    OP_Fw2("Fw2", OP_F2.cubeConsumer.andThen(OP_S2.cubeConsumer)),
    OP_Fwp("Fw'", OP_Fp.cubeConsumer.andThen(OP_Sp.cubeConsumer)),

    OP_L("L", cube -> cube
            .loopSwap(U1, F1, D1, B9)
            .loopSwap(U4, F4, D4, B6)
            .loopSwap(U7, F7, D7, B3)
            .loopSwap(L1, L3, L9, L7)
            .loopSwap(L2, L6, L8, L4)),
    OP_L2("L2", OP_L.cubeConsumer.andThen(OP_L.cubeConsumer)),
    OP_Lp("L'", OP_L.cubeConsumer.andThen(OP_L.cubeConsumer).andThen(OP_L.cubeConsumer)),

    OP_D("D", cube -> cube
            .loopSwap(L7, F7, R7, B7)
            .loopSwap(L8, F8, R8, B8)
            .loopSwap(L9, F9, R9, B9)
            .loopSwap(D1, D3, D9, D7)
            .loopSwap(D2, D6, D8, D4)),
    OP_D2("D2", OP_D.cubeConsumer.andThen(OP_D.cubeConsumer)),
    OP_Dp("D'", OP_D.cubeConsumer.andThen(OP_D.cubeConsumer).andThen(OP_D.cubeConsumer)),

    OP_B("B", cube -> cube
            .loopSwap(U1, L7, D9, R3)
            .loopSwap(U2, L4, D8, R6)
            .loopSwap(U3, L1, D7, R9)
            .loopSwap(B1, B3, B9, B7)
            .loopSwap(B2, B6, B8, B4)),
    OP_B2("B2", OP_B.cubeConsumer.andThen(OP_B.cubeConsumer)),
    OP_Bp("B'", OP_B.cubeConsumer.andThen(OP_B.cubeConsumer).andThen(OP_B.cubeConsumer)),

    OP_x("x", OP_R.cubeConsumer.andThen(OP_Mp.cubeConsumer).andThen(OP_Lp.cubeConsumer)),
    OP_xp("x'", OP_Rp.cubeConsumer.andThen(OP_M.cubeConsumer).andThen(OP_L.cubeConsumer)),
    OP_x2("x2", OP_R2.cubeConsumer.andThen(OP_M2.cubeConsumer).andThen(OP_L2.cubeConsumer)),

    OP_y("y", OP_U.cubeConsumer.andThen(OP_Ep.cubeConsumer).andThen(OP_Dp.cubeConsumer)),
    OP_yp("y'", OP_Up.cubeConsumer.andThen(OP_E.cubeConsumer).andThen(OP_D.cubeConsumer)),
    OP_y2("y2", OP_U2.cubeConsumer.andThen(OP_E2.cubeConsumer).andThen(OP_D2.cubeConsumer)),

    OP_z("z", OP_F.cubeConsumer.andThen(OP_Sp.cubeConsumer).andThen(OP_Bp.cubeConsumer)),
    OP_zp("z'", OP_Fp.cubeConsumer.andThen(OP_S.cubeConsumer).andThen(OP_B.cubeConsumer)),
    OP_z2("z2", OP_F2.cubeConsumer.andThen(OP_S2.cubeConsumer).andThen(OP_B2.cubeConsumer)),

    ;
    private final String name;
    private final Consumer<Cube> cubeConsumer;
    public static final Map<String, Ops> nameMap;

    static {
        nameMap = new LinkedHashMap<>();
        for (Ops value : Ops.values()) {
            nameMap.put(value.name, value);
        }
    }

    Ops(String name, Consumer<Cube> cubeConsumer) {
        this.name = name;
        this.cubeConsumer = cubeConsumer;
    }

    public void run(Cube cube) {
        cubeConsumer.accept(cube);
    }
}

