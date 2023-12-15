package com.xql.cubetools;


import com.xql.cubetools.cube.Cube333;
import com.xql.cubetools.search.Search;

public class Main {
    public static void main(String[] args) {
//        String randomStr = "F' D' R2 F2 U R' B' D2 R B2 R2 L' U2 F2 U2 D2 R D2 L F L2 Rw Uw2";
        String randomStr =
//                "U M' U2 M U";
                "y";
//                "R L U2 F U' D F2 R2 B2 L U2 F' B' U R2 D F2 U R2 U";
        Cube333 cube = new Cube333(randomStr);
        System.out.println(cube);

//        String chiChu = cube.getChiChu();
//
//        System.out.println(chiChu);

//        String s = Search.solution(
//                "UBULURUFU" +
//                        "RURFRBRDR" +
//                        "FUFLFRFDF" +
//                        "DFDLDRDBD" +
//                        "LULBLFLDL" +
//                        "BUBRBLBDB", 25, 10, true
//        );
//        System.out.println(s);
    }
}
