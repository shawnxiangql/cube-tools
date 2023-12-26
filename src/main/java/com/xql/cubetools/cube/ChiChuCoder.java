package com.xql.cubetools.cube;

import java.util.*;

public class ChiChuCoder {

    public static final Map<String, String> codes = new HashMap<>();

    static {
        String[] codeStr = ("" +
                "D,E,G,C,U,G,A,A,J," +
                "E,D,C,X,L,T,Q,L,M," +
                "B,B,L,S,F,Q,N,J,Y," +
                "K,H,I,R,R,Z,Z,P,S," +
                "H,F,F,Y,B,W,T,N,P," +
                "W,I,X,K,D,O,O,M,R").split(",");
        for (int i = 0; i < Cube333.Const.orderedSlices.size(); i++) {
            codes.put(Cube333.Const.orderedSlices.get(i).getName(), codeStr[i]);
        }
    }

    public String edgeBuffer = "U6";
    private Block edgeBufferBlock = Cube333.Const.sliceBlockMap.get(edgeBuffer);
    public String cornerBuffer = "U9";
    public String[] edgeFindOrder = "U8,U4,U2,D2,D4,D8,D6,F4,F6,B6,B4".split(",");
    public String[] cornerFindOrder = "".split(",");

    public String getChiChu(Cube333 cube) {
        List<String> edgeSliceOrder = new ArrayList<>();
        List<String> edgeSliceFlips = new ArrayList<>();

        Set<String> visitedBlockNames = new HashSet<>();
        Slice current = Cube333.Const.sliceMap.get(edgeBuffer);
        Block currentBlock = Cube333.Const.sliceBlockMap.get(current.getName());
        visitedBlockNames.add(currentBlock.getName());// buffer block put into visited

        Slice target = cube.getSlices()[Cube333.Const.sliceIndexMap.get(current.getName())];
        Block targetBlock = Cube333.Const.sliceBlockMap.get(target.getName());

        Block startBlock = edgeBufferBlock;
        int findIndex = 0;

        while (visitedBlockNames.size() < Cube333.Const.blockMap.size()) {
            if (visitedBlockNames.contains(targetBlock.getName())) { // 目标块已经看过了
                if (currentBlock.getName().equals(targetBlock.getName())) { // 同一块,已还原或需翻色
                    if (current.getName().equals(target.getName())) {
                        // 当前位置已经还原
                    } else {
                        // 当前位置需要翻色
                        edgeSliceFlips.add(toCode(current.getName()) + "<=>" + toCode(target.getName()));
                    }
                } else { // 不同块，循环结束
                    // 不是缓冲块，加进序列中
                    if (!targetBlock.getName().equals(edgeBufferBlock.getName()))
                        edgeSliceOrder.add(toCode(target.getName()));
                }
                // 跳到下一个没有看过的查找块
                while (findIndex < edgeFindOrder.length) {
                    current = Cube333.Const.sliceMap.get(edgeFindOrder[findIndex++]);
                    currentBlock = Cube333.Const.sliceBlockMap.get(current.getName());
                    target = cube.getSlices()[Cube333.Const.sliceIndexMap.get(current.getName())];
                    targetBlock = Cube333.Const.sliceBlockMap.get(target.getName());
                    if (visitedBlockNames.contains(currentBlock.getName())) {
                        // 看过这个块了
                        continue;
                    }
                    if (currentBlock.getName().equals(targetBlock.getName())) {
                        // 找到的块是翻色或已经复原
                        if (!current.getName().equals(target.getName()))
                            edgeSliceFlips.add(toCode(current.getName()) + "<=>" + toCode(target.getName()));
                        continue;
                    }
                    // 找到新的块, 借用作为起点
                    startBlock = currentBlock;
                    edgeSliceOrder.add(toCode(current.getName()));
                    // 借用的块还要再回来, 暂时不记录
                    // visitedBlockNames.add(currentBlock.getName());
                    break;
                }
                if (findIndex == edgeFindOrder.length) {
                    // 找完了，退出
                    break;
                }
            } else { // 目标块没记录

                if (targetBlock.getName().equals(startBlock.getName())) {
                    // 回到了借用块, 重新借用新的块
                    edgeSliceOrder.add(toCode(target.getName()));
                    visitedBlockNames.add(targetBlock.getName());

                    while (findIndex < edgeFindOrder.length) {
                        current = Cube333.Const.sliceMap.get(edgeFindOrder[findIndex++]);
                        currentBlock = Cube333.Const.sliceBlockMap.get(current.getName());
                        target = cube.getSlices()[Cube333.Const.sliceIndexMap.get(current.getName())];
                        targetBlock = Cube333.Const.sliceBlockMap.get(target.getName());
                        if (visitedBlockNames.contains(currentBlock.getName())) {
                            // 看过这个块了
                            continue;
                        }
                        if (currentBlock.getName().equals(targetBlock.getName())) {
                            // 找到的块是翻色或已经复原
                            if (!current.getName().equals(target.getName()))
                                edgeSliceFlips.add(toCode(current.getName()) + "<=>" + toCode(target.getName()));
                            continue;
                        }
                        // 找到新的块, 借用作为起点
                        startBlock = currentBlock;
                        edgeSliceOrder.add(toCode(current.getName()));
                        // 借用的块还要再回来, 暂时不记录
                        // visitedBlockNames.add(currentBlock.getName());
                        break;
                    }
                    if (findIndex == edgeFindOrder.length) {
                        // 找完了，退出
                        break;
                    }

                } else {
                    edgeSliceOrder.add(toCode(target.getName()));
                    current = target;
                    currentBlock = targetBlock;
                    visitedBlockNames.add(currentBlock.getName());
                    target = cube.getSlices()[Cube333.Const.sliceIndexMap.get(current.getName())];
                    targetBlock = Cube333.Const.sliceBlockMap.get(target.getName());
                }
            }
        }


        return toString(edgeSliceOrder) + ";" + String.join(" ", edgeSliceFlips) + "\n";
    }

    private static String toString(List<String> edgeSliceOrder) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < edgeSliceOrder.size(); i++) {
            if (i > 0 && i % 2 == 0) builder.append(" ");
            builder.append(edgeSliceOrder.get(i));
        }
        return builder.toString();
    }

    private String toCode(String sliceName) {
        return codes.get(sliceName);
    }
}
