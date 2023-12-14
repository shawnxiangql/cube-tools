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
 * U R F D L B
 */
public class Cube {

    private static final Slice
            U1 = new Slice(), U2 = new Slice(), U3 = new Slice(), U4 = new Slice(), U5 = new Slice(), U6 = new Slice(), U7 = new Slice(), U8 = new Slice(), U9 = new Slice(),
            R1 = new Slice(), R2 = new Slice(), R3 = new Slice(), R4 = new Slice(), R5 = new Slice(), R6 = new Slice(), R7 = new Slice(), R8 = new Slice(), R9 = new Slice(),
            F1 = new Slice(), F2 = new Slice(), F3 = new Slice(), F4 = new Slice(), F5 = new Slice(), F6 = new Slice(), F7 = new Slice(), F8 = new Slice(), F9 = new Slice(),
            D1 = new Slice(), D2 = new Slice(), D3 = new Slice(), D4 = new Slice(), D5 = new Slice(), D6 = new Slice(), D7 = new Slice(), D8 = new Slice(), D9 = new Slice(),
            L1 = new Slice(), L2 = new Slice(), L3 = new Slice(), L4 = new Slice(), L5 = new Slice(), L6 = new Slice(), L7 = new Slice(), L8 = new Slice(), L9 = new Slice(),
            B1 = new Slice(), B2 = new Slice(), B3 = new Slice(), B4 = new Slice(), B5 = new Slice(), B6 = new Slice(), B7 = new Slice(), B8 = new Slice(), B9 = new Slice();

    public static final Block
            UFL = new Block(U7, F1, L3), ULB = new Block(U1, L1, B3), UBR = new Block(U3, B1, R3), URF = new Block(U9, F3, R1),
            DLF = new Block(D1, L9, F7), DBL = new Block(D7, B9, L7), DRB = new Block(D9, R9, B7), DFR = new Block(D3, F9, R7),
            UF = new Block(U8, F2), UL = new Block(U4, L2), UB = new Block(U2, B2), UR = new Block(U6, R2),
            DF = new Block(D2, F8), DL = new Block(D4, L8), DB = new Block(D8, B8), DR = new Block(D6, R8),
            FR = new Block(F6, R4), FL = new Block(F4, L6), BL = new Block(B6, L4), BR = new Block(B4, R6);

    private static final Slice[] originOrderSlice = {
            U1, U2, U3, U4, U5, U6, U7, U8, U9,
            R1, R2, R3, R4, R5, R6, R7, R8, R9,
            F1, F2, F3, F4, F5, F6, F7, F8, F9,
            D1, D2, D3, D4, D5, D6, D7, D8, D9,
            L1, L2, L3, L4, L5, L6, L7, L8, L9,
            B1, B2, B3, B4, B5, B6, B7, B8, B9,
    };

    private static final Map<Slice, Integer> sliceIndexMap = new HashMap<>() {
        {
            for (int i = 0; i < originOrderSlice.length; i++) {
                put(originOrderSlice[i], i);
            }
        }
    };

    private static final Twist
            T_E = new Twist("E", cube -> cube.loopSwap(F4, R4, B4, L4).loopSwap(F5, R5, B5, L5).loopSwap(F6, R6, B6, L6)),
            T_E2 = new Twist("E2", T_E.cubeConsumer.andThen(T_E.cubeConsumer)),
            T_Ep = new Twist("E'", T_E.cubeConsumer.andThen(T_E.cubeConsumer).andThen(T_E.cubeConsumer)),
            T_M = new Twist("M", cube -> cube.loopSwap(F2, D2, B8, U2).loopSwap(F5, D5, B5, U5).loopSwap(F8, D8, B2, U8)),
            T_M2 = new Twist("M2", T_M.cubeConsumer.andThen(T_M.cubeConsumer)),
            T_Mp = new Twist("M'", T_M.cubeConsumer.andThen(T_M.cubeConsumer).andThen(T_M.cubeConsumer)),
            T_S = new Twist("S", cube -> cube.loopSwap(D6, R8, U4, L2).loopSwap(D5, R5, U5, L5).loopSwap(D4, R2, U6, L8)),
            T_S2 = new Twist("S2", T_S.cubeConsumer.andThen(T_S.cubeConsumer)),
            T_Sp = new Twist("S'", T_S.cubeConsumer.andThen(T_S.cubeConsumer).andThen(T_S.cubeConsumer)),
            T_R = new Twist("R", cube -> cube.loopSwap(U3, B7, D3, F3).loopSwap(U6, B4, D6, F6).loopSwap(U9, B1, D9, F9).loopSwap(R1, R3, R9, R7).loopSwap(R2, R6, R8, R4)),
            T_R2 = new Twist("R2", T_R.cubeConsumer.andThen(T_R.cubeConsumer)),
            T_Rp = new Twist("R'", T_R.cubeConsumer.andThen(T_R.cubeConsumer).andThen(T_R.cubeConsumer)),
            T_Rw = new Twist("Rw", T_R.cubeConsumer.andThen(T_Mp.cubeConsumer)),
            T_Rw2 = new Twist("Rw2", T_R2.cubeConsumer.andThen(T_M2.cubeConsumer)),
            T_Rwp = new Twist("Rw'", T_Rp.cubeConsumer.andThen(T_M.cubeConsumer)),
            T_U = new Twist("U", cube -> cube.loopSwap(L1, B1, R1, F1).loopSwap(L2, B2, R2, F2).loopSwap(L3, B3, R3, F3).loopSwap(U1, U3, U9, U7).loopSwap(U2, U6, U8, U4)),
            T_U2 = new Twist("U2", T_U.cubeConsumer.andThen(T_U.cubeConsumer)),
            T_Up = new Twist("U'", T_U.cubeConsumer.andThen(T_U.cubeConsumer).andThen(T_U.cubeConsumer)),
            T_Uw = new Twist("Uw", T_U.cubeConsumer.andThen(T_Ep.cubeConsumer)),
            T_Uw2 = new Twist("Uw2", T_U2.cubeConsumer.andThen(T_E2.cubeConsumer)),
            T_Uwp = new Twist("Uw'", T_Up.cubeConsumer.andThen(T_E.cubeConsumer)),
            T_F = new Twist("F", cube -> cube.loopSwap(U7, R1, D3, L9).loopSwap(U8, R4, D2, L6).loopSwap(U9, R7, D1, L3).loopSwap(F1, F3, F9, F7).loopSwap(F2, F6, F8, F4)),
            T_F2 = new Twist("F2", T_F.cubeConsumer.andThen(T_F.cubeConsumer)),
            T_Fp = new Twist("F'", T_F.cubeConsumer.andThen(T_F.cubeConsumer).andThen(T_F.cubeConsumer)),
            T_Fw = new Twist("Fw", T_F.cubeConsumer.andThen(T_S.cubeConsumer)),
            T_Fw2 = new Twist("Fw2", T_F2.cubeConsumer.andThen(T_S2.cubeConsumer)),
            T_Fwp = new Twist("Fw'", T_Fp.cubeConsumer.andThen(T_Sp.cubeConsumer)),
            T_L = new Twist("L", cube -> cube.loopSwap(U1, F1, D1, B9).loopSwap(U4, F4, D4, B6).loopSwap(U7, F7, D7, B3).loopSwap(L1, L3, L9, L7).loopSwap(L2, L6, L8, L4)),
            T_L2 = new Twist("L2", T_L.cubeConsumer.andThen(T_L.cubeConsumer)),
            T_Lp = new Twist("L'", T_L.cubeConsumer.andThen(T_L.cubeConsumer).andThen(T_L.cubeConsumer)),
            T_D = new Twist("D", cube -> cube.loopSwap(L7, F7, R7, B7).loopSwap(L8, F8, R8, B8).loopSwap(L9, F9, R9, B9).loopSwap(D1, D3, D9, D7).loopSwap(D2, D6, D8, D4)),
            T_D2 = new Twist("D2", T_D.cubeConsumer.andThen(T_D.cubeConsumer)),
            T_Dp = new Twist("D'", T_D.cubeConsumer.andThen(T_D.cubeConsumer).andThen(T_D.cubeConsumer)),
            T_B = new Twist("B", cube -> cube.loopSwap(U1, L7, D9, R3).loopSwap(U2, L4, D8, R6).loopSwap(U3, L1, D7, R9).loopSwap(B1, B3, B9, B7).loopSwap(B2, B6, B8, B4)),
            T_B2 = new Twist("B2", T_B.cubeConsumer.andThen(T_B.cubeConsumer)),
            T_Bp = new Twist("B'", T_B.cubeConsumer.andThen(T_B.cubeConsumer).andThen(T_B.cubeConsumer)),
            T_x = new Twist("x", T_R.cubeConsumer.andThen(T_Mp.cubeConsumer).andThen(T_Lp.cubeConsumer)),
            T_xp = new Twist("x'", T_Rp.cubeConsumer.andThen(T_M.cubeConsumer).andThen(T_L.cubeConsumer)),
            T_x2 = new Twist("x2", T_R2.cubeConsumer.andThen(T_M2.cubeConsumer).andThen(T_L2.cubeConsumer)),
            T_y = new Twist("y", T_U.cubeConsumer.andThen(T_Ep.cubeConsumer).andThen(T_Dp.cubeConsumer)),
            T_yp = new Twist("y'", T_Up.cubeConsumer.andThen(T_E.cubeConsumer).andThen(T_D.cubeConsumer)),
            T_y2 = new Twist("y2", T_U2.cubeConsumer.andThen(T_E2.cubeConsumer).andThen(T_D2.cubeConsumer)),
            T_z = new Twist("z", T_F.cubeConsumer.andThen(T_Sp.cubeConsumer).andThen(T_Bp.cubeConsumer)),
            T_zp = new Twist("z'", T_Fp.cubeConsumer.andThen(T_S.cubeConsumer).andThen(T_B.cubeConsumer)),
            T_z2 = new Twist("z2", T_F2.cubeConsumer.andThen(T_S2.cubeConsumer).andThen(T_B2.cubeConsumer)),;
    private static final Twist[] twists = new Twist[]{
            T_E , T_E2 , T_Ep ,
            T_M , T_M2 , T_Mp ,
            T_S , T_S2 , T_Sp ,
            T_R , T_R2 , T_Rp , T_Rw , T_Rw2, T_Rwp,
            T_U , T_U2 , T_Up , T_Uw , T_Uw2, T_Uwp,
            T_F , T_F2 , T_Fp , T_Fw , T_Fw2, T_Fwp,
            T_L , T_L2 , T_Lp , T_D , T_D2 , T_Dp ,
            T_B , T_B2 , T_Bp ,
            T_x , T_xp , T_x2 ,
            T_y , T_yp , T_y2 ,
            T_z , T_zp , T_z2 ,
    };
    private static final Map<String, Twist> twistMap = new HashMap<>() {{
        for (Twist twist : twists) {
            put(twist.name, twist);
        }
    }};

    private Slice[] slices = Arrays.copyOf(originOrderSlice, originOrderSlice.length);

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
    public Cube loopSwap(Slice a, Slice b, Slice c, Slice d) {
        int indexA = sliceIndexMap.get(a);
        int indexB = sliceIndexMap.get(b);
        int indexC = sliceIndexMap.get(c);
        int indexD = sliceIndexMap.get(d);
        Slice tmp = slices[indexD];
        slices[indexD] = slices[indexC];
        slices[indexC] = slices[indexB];
        slices[indexB] = slices[indexA];
        slices[indexA] = tmp;
        return this;
    }

    private Slice edgeBuffer = U6;
    private Slice cornerBuffer = U9;

    private Slice[] edgeFind = {U8, U4, U2, D2, D4, D8, D6, F4, F6, B6, B4};

    public List<List<String>> getCodes() {
        List<String> edgeCodes = new ArrayList<>();
        List<Slice> edgeFlip = new ArrayList<>();

        Set<Block> blocks = new HashSet<>();
        Slice current = edgeBuffer;
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
        return slices[sliceIndexMap.get(position)];
    }

    public static class Slice {
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
    }

    public static class Block {
        private final Slice[] slices;

        public Block(Slice... slices) {
            this.slices = slices;
        }

    }

    public static class Twist {
        private String name;
        private Consumer<Cube> cubeConsumer;

        public Twist(String name, Consumer<Cube> cubeConsumer) {
            this.name = name;
            this.cubeConsumer = cubeConsumer;
        }

        public void doTwist(Cube cube) {
            cubeConsumer.accept(cube);
        }
    }
}


enum Ops {
    private final String name;
    private final Consumer<Cube> cubeTwistOperation;
    public static final Map<String, Ops> nameMap;

    static {
        nameMap = new LinkedHashMap<>();
        for (Ops value : Ops.values()) {
            nameMap.put(value.name, value);
        }
    }

    Ops(String name, Consumer<Cube> cubeTwistOperation) {
        this.name = name;
        this.cubeTwistOperation = cubeTwistOperation;
    }

    public void run(Cube cube) {
        cubeTwistOperation.accept(cube);
    }
}

