package com.xql.cubetools.cube;

import java.util.*;
import java.util.function.Function;

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
public class Cube333 {

    private Slice[] slices = Const.sliceOrders.toArray(new Slice[0]);

    public Cube333() {
    }

    // example: R U R' U'
    public Cube333(String twists) {
        this();
        String[] twistNames = twists.trim().split("\\s+");
        for (String twistName : twistNames) Const.twistMap.get(twistName.trim()).doTwist(this);
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
    public void loopSwap(String a, String b, String c, String d) {
        int indexA = Const.sliceIndexMap.get(a);
        int indexB = Const.sliceIndexMap.get(b);
        int indexC = Const.sliceIndexMap.get(c);
        int indexD = Const.sliceIndexMap.get(d);
        Slice tmp = slices[indexD];
        slices[indexD] = slices[indexC];
        slices[indexC] = slices[indexB];
        slices[indexB] = slices[indexA];
        slices[indexA] = tmp;
    }

    public String edgeBuffer = "U6";
    public String cornerBuffer = "U9";
    public String[] edgeFindOrder = "U8,U4,U2,D2,D4,D8,D6,F4,F6,B6,B4".split(",");
    public String[] cornerFindOrder = "".split(",");

    public String getChiChu() {
        List<String> edgeSliceOrder = new ArrayList<>();
        List<String> edgeSliceFlips = new ArrayList<>();

        Set<String> visitedBlockNamess = new HashSet<>();
        Slice current = Const.sliceMap.get(edgeBuffer);
        Block currentBlock = Const.sliceBlockMap.get(current.getName());
        visitedBlockNamess.add(currentBlock.getName());// buffer block put into visited

        Slice target = slices[Const.sliceIndexMap.get(current.getName())];
        Block targetBlock = Const.sliceBlockMap.get(target.getName());

        int findIndex = 0;

        while (visitedBlockNamess.size() < 12) {
            if (visitedBlockNamess.contains(targetBlock.getName())) { // 目标块已经看过了
                if (currentBlock.getName().equals(targetBlock.getName())) { // 同一块,已还原或需翻色
                    if (current.getName().equals(target.getName())) {
                        // 当前位置已经还原
                    } else {
                        // 当前位置需要翻色
                        edgeSliceFlips.add(current.getName() + "," + target.getName());
                    }
                } else { // 不同块，循环结束
                    edgeSliceOrder.add(target.getName());
                }
                // 跳到下一个没有看过的查找块
                do {
                    current = Const.sliceMap.get(edgeFindOrder[findIndex]);
                    currentBlock = Const.sliceBlockMap.get(current.getName());
                } while (visitedBlockNamess.contains(currentBlock.getName()) && findIndex++ < edgeFindOrder.length);

                if (visitedBlockNamess.contains(currentBlock.getName())) {
                    // 所有块都看过,结束
                    break;
                } else { // 找到了下一块，继续
                    visitedBlockNamess.add(currentBlock.getName());
                    target = slices[Const.sliceIndexMap.get(current.getName())];
                    targetBlock = Const.sliceBlockMap.get(target.getName());
                }
            } else { // 目标块没看过,记录当前块，走到下一块
                edgeSliceOrder.add(current.getName());

                current = target;
                currentBlock = targetBlock;
                visitedBlockNamess.add(currentBlock.getName());
                target = slices[Const.sliceIndexMap.get(current.getName())];
                targetBlock = Const.sliceBlockMap.get(target.getName());
            }
        }


        return String.join(" ", edgeSliceOrder) + ";" + String.join(" ", edgeSliceFlips) + "\n";
    }

    public static class Const {
        public static final List<Slice> sliceOrders = new ArrayList<>();
        public static final Map<String, Slice> sliceMap = new HashMap<>();
        public static final Map<String, Integer> sliceIndexMap = new HashMap<>();
        public static final Map<String, Block> blockMap = new HashMap<>();
        public static final Map<String, Block> sliceBlockMap = new HashMap<>();
        public static final Map<String, Twist> twistMap = new HashMap<>();
        public static final Map<String, Surface> surfaceMap = new HashMap<>();
        public static final Map<String, Surface> sliceSurfaceMap = new HashMap<>();

        static {
            batchParse(Const::parseSlice, ",", " " +
                    "U1,U2,U3,U4,U5,U6,U7,U8,U9," +
                    "R1,R2,R3,R4,R5,R6,R7,R8,R9," +
                    "F1,F2,F3,F4,F5,F6,F7,F8,F9," +
                    "D1,D2,D3,D4,D5,D6,D7,D8,D9," +
                    "L1,L2,L3,L4,L5,L6,L7,L8,L9," +
                    "B1,B2,B3,B4,B5,B6,B7,B8,B9");

            batchParse(Const::parseBlock, ";\\s?", " " +
                    "UFL:U7,F1,L3;ULB:U1,L1,B3;UBR:U3,B1,R3;URF:U9,F3,R1;" +
                    "DLF:D1,L9,F7;DBL:D7,B9,L7;DRB:D9,R9,B7;DFR:D3,F9,R7;" +
                    "UF:U8,F2;UL:U4,L2;UB:U2,B2;UR:U6,R2;" +
                    "DF:D2,F8;DL:D4,L8;DB:D8,B8;DR:D6,R8;" +
                    "FR:F6,R4;FL:F4,L6;BL:B6,L4;BR:B4,R6");

            batchParse(Const::parseTwist, "\\n", " " +
                    "E:F4,R4,B4,L4;F5,R5,B5,L5;F6,R6,B6,L6\n" + "E2:E,E\n" + "E':E,E,E\n" +
                    "M:F2,D2,B8,U2;F5,D5,B5,U5;F8,D8,B2,U8\n" + "M2:M,M\n" + "M':M,M,M\n" +
                    "S:D6,R8,U4,L2;D5,R5,U5,L5;D4,R2,U6,L8\n" + "S2:S,S\n" + "S':S,S,S\n" +
                    "R:U3,B7,D3,F3;U6,B4,D6,F6;U9,B1,D9,F9;R1,R3,R9,R7;R2,R6,R8,R4\n" +
                    "R2:R,R\n" + "R':R,R,R\n" + "Rw:R,M'\n" + "Rw2:R2,M2\n" + "Rwp:R',M\n" +
                    "U:L1,B1,R1,F1;L2,B2,R2,F2;L3,B3,R3,F3;U1,U3,U9,U7;U2,U6,U8,U4\n" +
                    "U2:U,U\n" + "U':U,U,U\n" + "Uw:U,E'\n" + "Uw2:U2,E2\n" + "Uwp:U',E\n" +
                    "F:U7,R1,D3,L9;U8,R4,D2,L6;U9,R7,D1,L3;F1,F3,F9,F7;F2,F6,F8,F4\n" +
                    "F2:F,F\n" + "F':F,F,F\n" + "Fw:F,S'\n" + "Fw2:F2,S2\n" + "Fwp:F',S\n" +
                    "L:U1,F1,D1,B9;U4,F4,D4,B6;U7,F7,D7,B3;L1,L3,L9,L7;L2,L6,L8,L4\n" + "L2:L,L\n" + "L':L,L,L\n" +
                    "D:L7,F7,R7,B7;L8,F8,R8,B8;L9,F9,R9,B9;D1,D3,D9,D7;D2,D6,D8,D4\n" + "D2:D,D\n" + "D':D,D,D\n" +
                    "B:U1,L7,D9,R3;U2,L4,D8,R6;U3,L1,D7,R9;B1,B3,B9,B7;B2,B6,B8,B4\n" + "B2:B,B\n" + "B':B,B,B\n" +
                    "x:R,M',L'\n" + "x2:R2,M2,L2\n" + "x':R',M,L\n" +
                    "y:U,E',D'\n" + "y2:U2,E2,D2\n" + "y':U',E,D\n" +
                    "z:F,S',B'\n" + "z2:F2,S2,B2\n" + "z':F',S,B"
            );

            batchParse(Const::parseSurface, ";", " " +
                    "U:U1,U2,U3,U4,U5,U6,U7,U8,U9;" +
                    "F:F1,F2,F3,F4,F5,F6,F7,F8,F9;" +
                    "R:R1,R2,R3,R4,R5,R6,R7,R8,R9;" +
                    "L:L1,L2,L3,L4,L5,L6,L7,L8,L9;" +
                    "D:D1,D2,D3,D4,D5,D6,D7,D8,D9;" +
                    "B:B1,B2,B3,B4,B5,B6,B7,B8,B9"
            );
        }

        private static <T> void batchParse(Function<String, T> builder, String regex, String str) {
            for (String s : str.split(regex)) {
                builder.apply(s);
            }
        }

        private static Surface parseSurface(String str) {
            int i = str.indexOf(":");
            String name = str.substring(0, i);
            String[] sliceNames = str.substring(i + 1).split(",");
            Surface surface = new Surface(name, sliceNames);
            surfaceMap.put(name, surface);
            for (String sliceName : sliceNames) sliceSurfaceMap.put(sliceName, surface);
            return surface;
        }

        private static Twist parseTwist(String str) {
            int i = str.indexOf(":");
            String name = str.substring(0, i);
            List<String[]> sliceSwapGroups = new ArrayList<>();
            Twist twist = new Twist(name, sliceSwapGroups);

            String[] group = str.substring(i + 1).split(";");
            for (String g : group) {
                int j = g.indexOf(",");
                if (j == -1) {
                    sliceSwapGroups.addAll(twistMap.get(g.trim()).getSliceSwapGroups());
                } else {
                    sliceSwapGroups.add(g.split(",\\s?"));
                }
            }
            twistMap.put(name, twist);
            return twist;
        }

        private static Block parseBlock(String str) {
            int i = str.indexOf(":");
            Block block = new Block(str.substring(0, i).trim(), str.substring(i + 1).trim().split(","));
            blockMap.put(block.getName(), block);
            for (String sliceName : block.getSliceNames()) {
                sliceBlockMap.put(sliceName, block);
            }
            return block;
        }

        private static int sliceIndex = 0;

        private static Slice parseSlice(String str) {
            Slice slice = new Slice(str);
            sliceMap.put(str, slice);
            sliceIndexMap.put(str, sliceIndex++);
            sliceOrders.add(slice);
            return slice;
        }
    }
}

