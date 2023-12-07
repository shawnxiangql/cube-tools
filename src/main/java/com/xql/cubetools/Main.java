package com.xql.cubetools;


public class Main {
    public static void main(String[] args) {
//        String randomStr = "F' D' R2 F2 U R' B' D2 R B2 R2 L' U2 F2 U2 D2 R D2 L F L2 Rw Uw2";
        String randomStr = "y";
        Cube cube = new Cube(randomStr);
        System.out.println(cube);
    }
}
