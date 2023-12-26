package com.xql.cubetools;


import com.xql.cubetools.cube.ChiChuCoder;
import com.xql.cubetools.cube.Cube333;
import com.xql.cubetools.search.Search;

public class Main {
    public static void main(String[] args) {
//        String randomStr = "F' D' R2 F2 U R' B' D2 R B2 R2 L' U2 F2 U2 D2 R D2 L F L2 Rw Uw2";
        String randomStr =
//                "U M' U2 M U";
//                "y";
                "R L F U2 R2 U' D' F2 R' F B U L2 B2 D2 R2 D' L2 D B2 D"; // 棱全翻色
//                "U2 L F L U B2 D F2 U2 L' U2 B2 R2 U2 R2 D2 B2 F L'";
//                "F2 B R D' L' B' U F D2 B' L2 B2 U' B2 D' R2 F2 U2 F2 U R2";
        Cube333 cube = new Cube333(randomStr);
//        Cube333 cube = new Cube333();
        System.out.println(cube);
        ChiChuCoder coder = new ChiChuCoder();
        String chiChu = coder.getChiChu(cube);

        System.out.println(chiChu);

        String s = Search.solution(
                "UBULURUFU" +
                        "RURFRBRDR" +
                        "FUFLFRFDF" +
                        "DFDLDRDBD" +
                        "LULBLFLDL" +
                        "BUBRBLBDB", 25, 10, true
        );
        System.out.println(s);
    }
}
