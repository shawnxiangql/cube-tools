package com.xql.cubetools;


import com.xql.cubetools.search.Search;

public class Main {
    public static void main(String[] args) {
//        String randomStr = "F' D' R2 F2 U R' B' D2 R B2 R2 L' U2 F2 U2 D2 R D2 L F L2 Rw Uw2";
        String randomStr =
//                "y";
                "R L U2 F U' D F2 R2 B2 L U2 F' B' U R2 D F2 U R2 U";
        Cube cube = new Cube(randomStr);
        System.out.println(cube);

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
