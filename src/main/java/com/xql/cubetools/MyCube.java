package com.xql.cubetools;

import static com.xql.cubetools.Color.*;
import static com.xql.cubetools.Corner.*;
import static com.xql.cubetools.Edge.*;
import static com.xql.cubetools.Facelet.*;

public class MyCube {


}


enum Color {
    U, R, F, D, L, B
}

enum Corner {URF, UFL, ULB, UBR, DFR, DLF, DBL, DRB}

enum CornerOrientation {
    O_0, O_1, O_2
}

enum Edge {UR, UF, UL, UB, DR, DF, DL, DB, FR, FL, BL, BR}

enum EdgeOrientation {
    E_0, E_1
}

enum Facelet {U1, U2, U3, U4, U5, U6, U7, U8, U9, R1, R2, R3, R4, R5, R6, R7, R8, R9, F1, F2, F3, F4, F5, F6, F7, F8, F9, D1, D2, D3, D4, D5, D6, D7, D8, D9, L1, L2, L3, L4, L5, L6, L7, L8, L9, B1, B2, B3, B4, B5, B6, B7, B8, B9}

class CoordCube {

    static final short N_TWIST = 2187;// 3^7 possible corner orientations
    static final short N_FLIP = 2048;// 2^11 possible edge flips
    static final short N_SLICE1 = 495;// 12 choose 4 possible positions of FR,FL,BL,BR edges
    static final short N_SLICE2 = 24;// 4! permutations of FR,FL,BL,BR edges in phase2
    static final short N_PARITY = 2; // 2 possible corner parities
    static final short N_URFtoDLF = 20160;// 8!/(8-6)! permutation of URF,UFL,ULB,UBR,DFR,DLF corners
    static final short N_FRtoBR = 11880; // 12!/(12-4)! permutation of FR,FL,BL,BR edges
    static final short N_URtoUL = 1320; // 12!/(12-3)! permutation of UR,UF,UL edges
    static final short N_UBtoDF = 1320; // 12!/(12-3)! permutation of UB,DR,DF edges
    static final short N_URtoDF = 20160; // 8!/(8-6)! permutation of UR,UF,UL,UB,DR,DF edges in phase2

    static final int N_URFtoDLB = 40320;// 8! permutations of the corners
    static final int N_URtoBR = 479001600;// 8! permutations of the corners

    static final short N_MOVE = 18;

    // All coordinates are 0 for a solved cube except for UBtoDF, which is 114
    short twist;
    short flip;
    short parity;
    short FRtoBR;
    short URFtoDLF;
    short URtoUL;
    short UBtoDF;
    int URtoDF;

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Generate a CoordCube from a CubieCube
    CoordCube(CubieCube c) {
        twist = c.getTwist();
        flip = c.getFlip();
        parity = c.cornerParity();
        FRtoBR = c.getFRtoBR();
        URFtoDLF = c.getURFtoDLF();
        URtoUL = c.getURtoUL();
        UBtoDF = c.getUBtoDF();
        URtoDF = c.getURtoDF();// only needed in phase2
    }

    // A move on the coordinate leve
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    void move(int m) {
        twist = twistMove[twist][m];
        flip = flipMove[flip][m];
        parity = parityMove[parity][m];
        FRtoBR = FRtoBR_Move[FRtoBR][m];
        URFtoDLF = URFtoDLF_Move[URFtoDLF][m];
        URtoUL = URtoUL_Move[URtoUL][m];
        UBtoDF = UBtoDF_Move[UBtoDF][m];
        if (URtoUL < 336 && UBtoDF < 336)// updated only if UR,UF,UL,UB,DR,DF
            // are not in UD-slice
            URtoDF = MergeURtoULandUBtoDF[URtoUL][UBtoDF];
    }

    // ******************************************Phase 1 move tables*****************************************************

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Move table for the twists of the corners
    // twist < 2187 in phase 2.
    // twist = 0 in phase 2.
    static short[][] twistMove = new short[N_TWIST][N_MOVE];

    static {
        CubieCube a = new CubieCube();
        for (short i = 0; i < N_TWIST; i++) {
            a.setTwist(i);
            for (int j = 0; j < 6; j++) {
                for (int k = 0; k < 3; k++) {
                    a.cornerMultiply(CubieCube.moveCube[j]);
                    twistMove[i][3 * j + k] = a.getTwist();
                }
                a.cornerMultiply(CubieCube.moveCube[j]);// 4. faceturn restores
                // a
            }
        }
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Move table for the flips of the edges
    // flip < 2048 in phase 1
    // flip = 0 in phase 2.
    static short[][] flipMove = new short[N_FLIP][N_MOVE];

    static {
        CubieCube a = new CubieCube();
        for (short i = 0; i < N_FLIP; i++) {
            a.setFlip(i);
            for (int j = 0; j < 6; j++) {
                for (int k = 0; k < 3; k++) {
                    a.edgeMultiply(CubieCube.moveCube[j]);
                    flipMove[i][3 * j + k] = a.getFlip();
                }
                a.edgeMultiply(CubieCube.moveCube[j]);
                // a
            }
        }
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Parity of the corner permutation. This is the same as the parity for the edge permutation of a valid cube.
    // parity has values 0 and 1
    static short[][] parityMove = {{1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1},
            {0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0}};

    // ***********************************Phase 1 and 2 movetable********************************************************

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Move table for the four UD-slice edges FR, FL, Bl and BR
    // FRtoBRMove < 11880 in phase 1
    // FRtoBRMove < 24 in phase 2
    // FRtoBRMove = 0 for solved cube
    static short[][] FRtoBR_Move = new short[N_FRtoBR][N_MOVE];

    static {
        CubieCube a = new CubieCube();
        for (short i = 0; i < N_FRtoBR; i++) {
            a.setFRtoBR(i);
            for (int j = 0; j < 6; j++) {
                for (int k = 0; k < 3; k++) {
                    a.edgeMultiply(CubieCube.moveCube[j]);
                    FRtoBR_Move[i][3 * j + k] = a.getFRtoBR();
                }
                a.edgeMultiply(CubieCube.moveCube[j]);
            }
        }
    }

    // *******************************************Phase 1 and 2 movetable************************************************

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Move table for permutation of six corners. The positions of the DBL and DRB corners are determined by the parity.
    // URFtoDLF < 20160 in phase 1
    // URFtoDLF < 20160 in phase 2
    // URFtoDLF = 0 for solved cube.
    static short[][] URFtoDLF_Move = new short[N_URFtoDLF][N_MOVE];

    static {
        CubieCube a = new CubieCube();
        for (short i = 0; i < N_URFtoDLF; i++) {
            a.setURFtoDLF(i);
            for (int j = 0; j < 6; j++) {
                for (int k = 0; k < 3; k++) {
                    a.cornerMultiply(CubieCube.moveCube[j]);
                    URFtoDLF_Move[i][3 * j + k] = a.getURFtoDLF();
                }
                a.cornerMultiply(CubieCube.moveCube[j]);
            }
        }
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Move table for the permutation of six U-face and D-face edges in phase2. The positions of the DL and DB edges are
    // determined by the parity.
    // URtoDF < 665280 in phase 1
    // URtoDF < 20160 in phase 2
    // URtoDF = 0 for solved cube.
    static short[][] URtoDF_Move = new short[N_URtoDF][N_MOVE];

    static {
        CubieCube a = new CubieCube();
        for (short i = 0; i < N_URtoDF; i++) {
            a.setURtoDF(i);
            for (int j = 0; j < 6; j++) {
                for (int k = 0; k < 3; k++) {
                    a.edgeMultiply(CubieCube.moveCube[j]);
                    URtoDF_Move[i][3 * j + k] = (short) a.getURtoDF();
                    // Table values are only valid for phase 2 moves!
                    // For phase 1 moves, casting to short is not possible.
                }
                a.edgeMultiply(CubieCube.moveCube[j]);
            }
        }
    }

    // **************************helper move tables to compute URtoDF for the beginning of phase2************************

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Move table for the three edges UR,UF and UL in phase1.
    static short[][] URtoUL_Move = new short[N_URtoUL][N_MOVE];

    static {
        CubieCube a = new CubieCube();
        for (short i = 0; i < N_URtoUL; i++) {
            a.setURtoUL(i);
            for (int j = 0; j < 6; j++) {
                for (int k = 0; k < 3; k++) {
                    a.edgeMultiply(CubieCube.moveCube[j]);
                    URtoUL_Move[i][3 * j + k] = a.getURtoUL();
                }
                a.edgeMultiply(CubieCube.moveCube[j]);
            }
        }
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Move table for the three edges UB,DR and DF in phase1.
    static short[][] UBtoDF_Move = new short[N_UBtoDF][N_MOVE];

    static {
        CubieCube a = new CubieCube();
        for (short i = 0; i < N_UBtoDF; i++) {
            a.setUBtoDF(i);
            for (int j = 0; j < 6; j++) {
                for (int k = 0; k < 3; k++) {
                    a.edgeMultiply(CubieCube.moveCube[j]);
                    UBtoDF_Move[i][3 * j + k] = a.getUBtoDF();
                }
                a.edgeMultiply(CubieCube.moveCube[j]);
            }
        }
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Table to merge the coordinates of the UR,UF,UL and UB,DR,DF edges at the beginning of phase2
    static short[][] MergeURtoULandUBtoDF = new short[336][336];

    static {
        // for i, j <336 the six edges UR,UF,UL,UB,DR,DF are not in the
        // UD-slice and the index is <20160
        for (short uRtoUL = 0; uRtoUL < 336; uRtoUL++) {
            for (short uBtoDF = 0; uBtoDF < 336; uBtoDF++) {
                MergeURtoULandUBtoDF[uRtoUL][uBtoDF] = (short) CubieCube.getURtoDF(uRtoUL, uBtoDF);
            }
        }
    }

    // ****************************************Pruning tables for the search*********************************************

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Pruning table for the permutation of the corners and the UD-slice edges in phase2.
    // The pruning table entries give a lower estimation for the number of moves to reach the solved cube.
    static byte[] Slice_URFtoDLF_Parity_Prun = new byte[N_SLICE2 * N_URFtoDLF * N_PARITY / 2];

    static {
        for (int i = 0; i < N_SLICE2 * N_URFtoDLF * N_PARITY / 2; i++)
            Slice_URFtoDLF_Parity_Prun[i] = -1;
        int depth = 0;
        setPruning(Slice_URFtoDLF_Parity_Prun, 0, (byte) 0);
        int done = 1;
        while (done != N_SLICE2 * N_URFtoDLF * N_PARITY) {
            for (int i = 0; i < N_SLICE2 * N_URFtoDLF * N_PARITY; i++) {
                int parity = i % 2;
                int URFtoDLF = (i / 2) / N_SLICE2;
                int slice = (i / 2) % N_SLICE2;
                if (getPruning(Slice_URFtoDLF_Parity_Prun, i) == depth) {
                    for (int j = 0; j < 18; j++) {
                        switch (j) {
                            case 3:
                            case 5:
                            case 6:
                            case 8:
                            case 12:
                            case 14:
                            case 15:
                            case 17:
                                continue;
                            default:
                                int newSlice = FRtoBR_Move[slice][j];
                                int newURFtoDLF = URFtoDLF_Move[URFtoDLF][j];
                                int newParity = parityMove[parity][j];
                                if (getPruning(Slice_URFtoDLF_Parity_Prun, (N_SLICE2 * newURFtoDLF + newSlice) * 2 + newParity) == 0x0f) {
                                    setPruning(Slice_URFtoDLF_Parity_Prun, (N_SLICE2 * newURFtoDLF + newSlice) * 2 + newParity,
                                            (byte) (depth + 1));
                                    done++;
                                }
                        }
                    }
                }
            }
            depth++;
        }
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Pruning table for the permutation of the edges in phase2.
    // The pruning table entries give a lower estimation for the number of moves to reach the solved cube.
    static byte[] Slice_URtoDF_Parity_Prun = new byte[N_SLICE2 * N_URtoDF * N_PARITY / 2];

    static {
        for (int i = 0; i < N_SLICE2 * N_URtoDF * N_PARITY / 2; i++)
            Slice_URtoDF_Parity_Prun[i] = -1;
        int depth = 0;
        setPruning(Slice_URtoDF_Parity_Prun, 0, (byte) 0);
        int done = 1;
        while (done != N_SLICE2 * N_URtoDF * N_PARITY) {
            for (int i = 0; i < N_SLICE2 * N_URtoDF * N_PARITY; i++) {
                int parity = i % 2;
                int URtoDF = (i / 2) / N_SLICE2;
                int slice = (i / 2) % N_SLICE2;
                if (getPruning(Slice_URtoDF_Parity_Prun, i) == depth) {
                    for (int j = 0; j < 18; j++) {
                        switch (j) {
                            case 3:
                            case 5:
                            case 6:
                            case 8:
                            case 12:
                            case 14:
                            case 15:
                            case 17:
                                continue;
                            default:
                                int newSlice = FRtoBR_Move[slice][j];
                                int newURtoDF = URtoDF_Move[URtoDF][j];
                                int newParity = parityMove[parity][j];
                                if (getPruning(Slice_URtoDF_Parity_Prun, (N_SLICE2 * newURtoDF + newSlice) * 2 + newParity) == 0x0f) {
                                    setPruning(Slice_URtoDF_Parity_Prun, (N_SLICE2 * newURtoDF + newSlice) * 2 + newParity,
                                            (byte) (depth + 1));
                                    done++;
                                }
                        }
                    }
                }
            }
            depth++;
        }
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Pruning table for the twist of the corners and the position (not permutation) of the UD-slice edges in phase1
    // The pruning table entries give a lower estimation for the number of moves to reach the H-subgroup.
    static byte[] Slice_Twist_Prun = new byte[N_SLICE1 * N_TWIST / 2 + 1];

    static {
        for (int i = 0; i < N_SLICE1 * N_TWIST / 2 + 1; i++)
            Slice_Twist_Prun[i] = -1;
        int depth = 0;
        setPruning(Slice_Twist_Prun, 0, (byte) 0);
        int done = 1;
        while (done != N_SLICE1 * N_TWIST) {
            for (int i = 0; i < N_SLICE1 * N_TWIST; i++) {
                int twist = i / N_SLICE1, slice = i % N_SLICE1;
                if (getPruning(Slice_Twist_Prun, i) == depth) {
                    for (int j = 0; j < 18; j++) {
                        int newSlice = FRtoBR_Move[slice * 24][j] / 24;
                        int newTwist = twistMove[twist][j];
                        if (getPruning(Slice_Twist_Prun, N_SLICE1 * newTwist + newSlice) == 0x0f) {
                            setPruning(Slice_Twist_Prun, N_SLICE1 * newTwist + newSlice, (byte) (depth + 1));
                            done++;
                        }
                    }
                }
            }
            depth++;
        }
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Pruning table for the flip of the edges and the position (not permutation) of the UD-slice edges in phase1
    // The pruning table entries give a lower estimation for the number of moves to reach the H-subgroup.
    static byte[] Slice_Flip_Prun = new byte[N_SLICE1 * N_FLIP / 2];

    static {
        for (int i = 0; i < N_SLICE1 * N_FLIP / 2; i++)
            Slice_Flip_Prun[i] = -1;
        int depth = 0;
        setPruning(Slice_Flip_Prun, 0, (byte) 0);
        int done = 1;
        while (done != N_SLICE1 * N_FLIP) {
            for (int i = 0; i < N_SLICE1 * N_FLIP; i++) {
                int flip = i / N_SLICE1, slice = i % N_SLICE1;
                if (getPruning(Slice_Flip_Prun, i) == depth) {
                    for (int j = 0; j < 18; j++) {
                        int newSlice = FRtoBR_Move[slice * 24][j] / 24;
                        int newFlip = flipMove[flip][j];
                        if (getPruning(Slice_Flip_Prun, N_SLICE1 * newFlip + newSlice) == 0x0f) {
                            setPruning(Slice_Flip_Prun, N_SLICE1 * newFlip + newSlice, (byte) (depth + 1));
                            done++;
                        }
                    }
                }
            }
            depth++;
        }
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Set pruning value in table. Two values are stored in one byte.
    static void setPruning(byte[] table, int index, byte value) {
        if ((index & 1) == 0)
            table[index / 2] &= 0xf0 | value;
        else
            table[index / 2] &= 0x0f | (value << 4);
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Extract pruning value
    static byte getPruning(byte[] table, int index) {
        if ((index & 1) == 0)
            return (byte) (table[index / 2] & 0x0f);
        else
            return (byte) ((table[index / 2] & 0xf0) >>> 4);
    }
}

class CubieCube {

    // initialize to Id-Cube

    // corner permutation
    Corner[] cp = {URF, UFL, ULB, UBR, DFR, DLF, DBL, DRB};

    // corner orientation
    byte[] co = {0, 0, 0, 0, 0, 0, 0, 0};

    // edge permutation
    Edge[] ep = {UR, UF, UL, UB, DR, DF, DL, DB, FR, FL, BL, BR};

    // edge orientation
    byte[] eo = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    // ************************************** Moves on the cubie level ***************************************************

    private static Corner[] cpU = {UBR, URF, UFL, ULB, DFR, DLF, DBL, DRB};
    private static byte[] coU = {0, 0, 0, 0, 0, 0, 0, 0};
    private static Edge[] epU = {UB, UR, UF, UL, DR, DF, DL, DB, FR, FL, BL, BR};
    private static byte[] eoU = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    private static Corner[] cpR = {DFR, UFL, ULB, URF, DRB, DLF, DBL, UBR};
    private static byte[] coR = {2, 0, 0, 1, 1, 0, 0, 2};
    private static Edge[] epR = {FR, UF, UL, UB, BR, DF, DL, DB, DR, FL, BL, UR};
    private static byte[] eoR = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    private static Corner[] cpF = {UFL, DLF, ULB, UBR, URF, DFR, DBL, DRB};
    private static byte[] coF = {1, 2, 0, 0, 2, 1, 0, 0};
    private static Edge[] epF = {UR, FL, UL, UB, DR, FR, DL, DB, UF, DF, BL, BR};
    private static byte[] eoF = {0, 1, 0, 0, 0, 1, 0, 0, 1, 1, 0, 0};

    private static Corner[] cpD = {URF, UFL, ULB, UBR, DLF, DBL, DRB, DFR};
    private static byte[] coD = {0, 0, 0, 0, 0, 0, 0, 0};
    private static Edge[] epD = {UR, UF, UL, UB, DF, DL, DB, DR, FR, FL, BL, BR};
    private static byte[] eoD = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    private static Corner[] cpL = {URF, ULB, DBL, UBR, DFR, UFL, DLF, DRB};
    private static byte[] coL = {0, 1, 2, 0, 0, 2, 1, 0};
    private static Edge[] epL = {UR, UF, BL, UB, DR, DF, FL, DB, FR, UL, DL, BR};
    private static byte[] eoL = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    private static Corner[] cpB = {URF, UFL, UBR, DRB, DFR, DLF, ULB, DBL};
    private static byte[] coB = {0, 0, 1, 2, 0, 0, 2, 1};
    private static Edge[] epB = {UR, UF, UL, BR, DR, DF, DL, BL, FR, FL, UB, DB};
    private static byte[] eoB = {0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 1};

    // this CubieCube array represents the 6 basic cube moves
    static CubieCube[] moveCube = new CubieCube[6];

    static {
        moveCube[0] = new CubieCube();
        moveCube[0].cp = cpU;
        moveCube[0].co = coU;
        moveCube[0].ep = epU;
        moveCube[0].eo = eoU;

        moveCube[1] = new CubieCube();
        moveCube[1].cp = cpR;
        moveCube[1].co = coR;
        moveCube[1].ep = epR;
        moveCube[1].eo = eoR;

        moveCube[2] = new CubieCube();
        moveCube[2].cp = cpF;
        moveCube[2].co = coF;
        moveCube[2].ep = epF;
        moveCube[2].eo = eoF;

        moveCube[3] = new CubieCube();
        moveCube[3].cp = cpD;
        moveCube[3].co = coD;
        moveCube[3].ep = epD;
        moveCube[3].eo = eoD;

        moveCube[4] = new CubieCube();
        moveCube[4].cp = cpL;
        moveCube[4].co = coL;
        moveCube[4].ep = epL;
        moveCube[4].eo = eoL;

        moveCube[5] = new CubieCube();
        moveCube[5].cp = cpB;
        moveCube[5].co = coB;
        moveCube[5].ep = epB;
        moveCube[5].eo = eoB;

    }

    CubieCube() {

    }

    ;

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    CubieCube(Corner[] cp, byte[] co, Edge[] ep, byte[] eo) {
        this();
        for (int i = 0; i < 8; i++) {
            this.cp[i] = cp[i];
            this.co[i] = co[i];
        }
        for (int i = 0; i < 12; i++) {
            this.ep[i] = ep[i];
            this.eo[i] = eo[i];
        }
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // n choose k
    static int Cnk(int n, int k) {
        int i, j, s;
        if (n < k)
            return 0;
        if (k > n / 2)
            k = n - k;
        for (s = 1, i = n, j = 1; i != n - k; i--, j++) {
            s *= i;
            s /= j;
        }
        return s;
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    static void rotateLeft(Corner[] arr, int l, int r)
    // Left rotation of all array elements between l and r
    {
        Corner temp = arr[l];
        for (int i = l; i < r; i++)
            arr[i] = arr[i + 1];
        arr[r] = temp;
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    static void rotateRight(Corner[] arr, int l, int r)
    // Right rotation of all array elements between l and r
    {
        Corner temp = arr[r];
        for (int i = r; i > l; i--)
            arr[i] = arr[i - 1];
        arr[l] = temp;
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    static void rotateLeft(Edge[] arr, int l, int r)
    // Left rotation of all array elements between l and r
    {
        Edge temp = arr[l];
        for (int i = l; i < r; i++)
            arr[i] = arr[i + 1];
        arr[r] = temp;
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    static void rotateRight(Edge[] arr, int l, int r)
    // Right rotation of all array elements between l and r
    {
        Edge temp = arr[r];
        for (int i = r; i > l; i--)
            arr[i] = arr[i - 1];
        arr[l] = temp;
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // return cube in facelet representation
    FaceCube toFaceCube() {
        FaceCube fcRet = new FaceCube();
        for (Corner c : Corner.values()) {
            int i = c.ordinal();
            int j = cp[i].ordinal();// cornercubie with index j is at
            // cornerposition with index i
            byte ori = co[i];// Orientation of this cubie
            for (int n = 0; n < 3; n++)
                fcRet.f[FaceCube.cornerFacelet[i][(n + ori) % 3].ordinal()] = FaceCube.cornerColor[j][n];
        }
        for (Edge e : Edge.values()) {
            int i = e.ordinal();
            int j = ep[i].ordinal();// edgecubie with index j is at edgeposition
            // with index i
            byte ori = eo[i];// Orientation of this cubie
            for (int n = 0; n < 2; n++)
                fcRet.f[FaceCube.edgeFacelet[i][(n + ori) % 2].ordinal()] = FaceCube.edgeColor[j][n];
        }
        return fcRet;
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Multiply this CubieCube with another cubiecube b, restricted to the corners.<br>
    // Because we also describe reflections of the whole cube by permutations, we get a complication with the corners. The
    // orientations of mirrored corners are described by the numbers 3, 4 and 5. The composition of the orientations
    // cannot
    // be computed by addition modulo three in the cyclic group C3 any more. Instead the rules below give an addition in
    // the dihedral group D3 with 6 elements.<br>
    //
    // NOTE: Because we do not use symmetry reductions and hence no mirrored cubes in this simple implementation of the
    // Two-Phase-Algorithm, some code is not necessary here.
    //
    void cornerMultiply(CubieCube b) {
        Corner[] cPerm = new Corner[8];
        byte[] cOri = new byte[8];
        for (Corner corn : Corner.values()) {
            cPerm[corn.ordinal()] = cp[b.cp[corn.ordinal()].ordinal()];

            byte oriA = co[b.cp[corn.ordinal()].ordinal()];
            byte oriB = b.co[corn.ordinal()];
            byte ori = 0;
            ;
            if (oriA < 3 && oriB < 3) // if both cubes are regular cubes...
            {
                ori = (byte) (oriA + oriB); // just do an addition modulo 3 here
                if (ori >= 3)
                    ori -= 3; // the composition is a regular cube

                // +++++++++++++++++++++not used in this implementation +++++++++++++++++++++++++++++++++++
            } else if (oriA < 3 && oriB >= 3) // if cube b is in a mirrored
            // state...
            {
                ori = (byte) (oriA + oriB);
                if (ori >= 6)
                    ori -= 3; // the composition is a mirrored cube
            } else if (oriA >= 3 && oriB < 3) // if cube a is an a mirrored
            // state...
            {
                ori = (byte) (oriA - oriB);
                if (ori < 3)
                    ori += 3; // the composition is a mirrored cube
            } else if (oriA >= 3 && oriB >= 3) // if both cubes are in mirrored
            // states...
            {
                ori = (byte) (oriA - oriB);
                if (ori < 0)
                    ori += 3; // the composition is a regular cube
                // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            }
            cOri[corn.ordinal()] = ori;
        }
        for (Corner c : Corner.values()) {
            cp[c.ordinal()] = cPerm[c.ordinal()];
            co[c.ordinal()] = cOri[c.ordinal()];
        }
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Multiply this CubieCube with another cubiecube b, restricted to the edges.
    void edgeMultiply(CubieCube b) {
        Edge[] ePerm = new Edge[12];
        byte[] eOri = new byte[12];
        for (Edge edge : Edge.values()) {
            ePerm[edge.ordinal()] = ep[b.ep[edge.ordinal()].ordinal()];
            eOri[edge.ordinal()] = (byte) ((b.eo[edge.ordinal()] + eo[b.ep[edge.ordinal()].ordinal()]) % 2);
        }
        for (Edge e : Edge.values()) {
            ep[e.ordinal()] = ePerm[e.ordinal()];
            eo[e.ordinal()] = eOri[e.ordinal()];
        }
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Multiply this CubieCube with another CubieCube b.
    void multiply(CubieCube b) {
        cornerMultiply(b);
        // edgeMultiply(b);
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Compute the inverse CubieCube
    void invCubieCube(CubieCube c) {
        for (Edge edge : Edge.values())
            c.ep[ep[edge.ordinal()].ordinal()] = edge;
        for (Edge edge : Edge.values())
            c.eo[edge.ordinal()] = eo[c.ep[edge.ordinal()].ordinal()];
        for (Corner corn : Corner.values())
            c.cp[cp[corn.ordinal()].ordinal()] = corn;
        for (Corner corn : Corner.values()) {
            byte ori = co[c.cp[corn.ordinal()].ordinal()];
            if (ori >= 3)// Just for completeness. We do not invert mirrored
                // cubes in the program.
                c.co[corn.ordinal()] = ori;
            else {// the standard case
                c.co[corn.ordinal()] = (byte) -ori;
                if (c.co[corn.ordinal()] < 0)
                    c.co[corn.ordinal()] += 3;
            }
        }
    }

    // ********************************************* Get and set coordinates *********************************************

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // return the twist of the 8 corners. 0 <= twist < 3^7
    short getTwist() {
        short ret = 0;
        for (int i = URF.ordinal(); i < DRB.ordinal(); i++)
            ret = (short) (3 * ret + co[i]);
        return ret;
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    void setTwist(short twist) {
        int twistParity = 0;
        for (int i = DRB.ordinal() - 1; i >= URF.ordinal(); i--) {
            twistParity += co[i] = (byte) (twist % 3);
            twist /= 3;
        }
        co[DRB.ordinal()] = (byte) ((3 - twistParity % 3) % 3);
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // return the flip of the 12 edges. 0<= flip < 2^11
    short getFlip() {
        short ret = 0;
        for (int i = UR.ordinal(); i < BR.ordinal(); i++)
            ret = (short) (2 * ret + eo[i]);
        return ret;
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    void setFlip(short flip) {
        int flipParity = 0;
        for (int i = BR.ordinal() - 1; i >= UR.ordinal(); i--) {
            flipParity += eo[i] = (byte) (flip % 2);
            flip /= 2;
        }
        eo[BR.ordinal()] = (byte) ((2 - flipParity % 2) % 2);
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Parity of the corner permutation
    short cornerParity() {
        int s = 0;
        for (int i = DRB.ordinal(); i >= URF.ordinal() + 1; i--)
            for (int j = i - 1; j >= URF.ordinal(); j--)
                if (cp[j].ordinal() > cp[i].ordinal())
                    s++;
        return (short) (s % 2);
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Parity of the edges permutation. Parity of corners and edges are the same if the cube is solvable.
    short edgeParity() {
        int s = 0;
        for (int i = BR.ordinal(); i >= UR.ordinal() + 1; i--)
            for (int j = i - 1; j >= UR.ordinal(); j--)
                if (ep[j].ordinal() > ep[i].ordinal())
                    s++;
        return (short) (s % 2);
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // permutation of the UD-slice edges FR,FL,BL and BR
    short getFRtoBR() {
        int a = 0, x = 0;
        Edge[] edge4 = new Edge[4];
        // compute the index a < (12 choose 4) and the permutation array perm.
        for (int j = BR.ordinal(); j >= UR.ordinal(); j--)
            if (FR.ordinal() <= ep[j].ordinal() && ep[j].ordinal() <= BR.ordinal()) {
                a += Cnk(11 - j, x + 1);
                edge4[3 - x++] = ep[j];
            }

        int b = 0;
        for (int j = 3; j > 0; j--)// compute the index b < 4! for the
        // permutation in perm
        {
            int k = 0;
            while (edge4[j].ordinal() != j + 8) {
                rotateLeft(edge4, 0, j);
                k++;
            }
            b = (j + 1) * b + k;
        }
        return (short) (24 * a + b);
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    void setFRtoBR(short idx) {
        int x;
        Edge[] sliceEdge = {FR, FL, BL, BR};
        Edge[] otherEdge = {UR, UF, UL, UB, DR, DF, DL, DB};
        int b = idx % 24; // Permutation
        int a = idx / 24; // Combination
        for (Edge e : Edge.values())
            ep[e.ordinal()] = DB;// Use UR to invalidate all edges

        for (int j = 1, k; j < 4; j++)// generate permutation from index b
        {
            k = b % (j + 1);
            b /= j + 1;
            while (k-- > 0)
                rotateRight(sliceEdge, 0, j);
        }

        x = 3;// generate combination and set slice edges
        for (int j = UR.ordinal(); j <= BR.ordinal(); j++)
            if (a - Cnk(11 - j, x + 1) >= 0) {
                ep[j] = sliceEdge[3 - x];
                a -= Cnk(11 - j, x-- + 1);
            }
        x = 0; // set the remaining edges UR..DB
        for (int j = UR.ordinal(); j <= BR.ordinal(); j++)
            if (ep[j] == DB)
                ep[j] = otherEdge[x++];

    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Permutation of all corners except DBL and DRB
    short getURFtoDLF() {
        int a = 0, x = 0;
        Corner[] corner6 = new Corner[6];
        // compute the index a < (8 choose 6) and the corner permutation.
        for (int j = URF.ordinal(); j <= DRB.ordinal(); j++)
            if (cp[j].ordinal() <= DLF.ordinal()) {
                a += Cnk(j, x + 1);
                corner6[x++] = cp[j];
            }

        int b = 0;
        for (int j = 5; j > 0; j--)// compute the index b < 6! for the
        // permutation in corner6
        {
            int k = 0;
            while (corner6[j].ordinal() != j) {
                rotateLeft(corner6, 0, j);
                k++;
            }
            b = (j + 1) * b + k;
        }
        return (short) (720 * a + b);
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    void setURFtoDLF(short idx) {
        int x;
        Corner[] corner6 = {URF, UFL, ULB, UBR, DFR, DLF};
        Corner[] otherCorner = {DBL, DRB};
        int b = idx % 720; // Permutation
        int a = idx / 720; // Combination
        for (Corner c : Corner.values())
            cp[c.ordinal()] = DRB;// Use DRB to invalidate all corners

        for (int j = 1, k; j < 6; j++)// generate permutation from index b
        {
            k = b % (j + 1);
            b /= j + 1;
            while (k-- > 0)
                rotateRight(corner6, 0, j);
        }
        x = 5;// generate combination and set corners
        for (int j = DRB.ordinal(); j >= 0; j--)
            if (a - Cnk(j, x + 1) >= 0) {
                cp[j] = corner6[x];
                a -= Cnk(j, x-- + 1);
            }
        x = 0;
        for (int j = URF.ordinal(); j <= DRB.ordinal(); j++)
            if (cp[j] == DRB)
                cp[j] = otherCorner[x++];
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Permutation of the six edges UR,UF,UL,UB,DR,DF.
    int getURtoDF() {
        int a = 0, x = 0;
        Edge[] edge6 = new Edge[6];
        // compute the index a < (12 choose 6) and the edge permutation.
        for (int j = UR.ordinal(); j <= BR.ordinal(); j++)
            if (ep[j].ordinal() <= DF.ordinal()) {
                a += Cnk(j, x + 1);
                edge6[x++] = ep[j];
            }

        int b = 0;
        for (int j = 5; j > 0; j--)// compute the index b < 6! for the
        // permutation in edge6
        {
            int k = 0;
            while (edge6[j].ordinal() != j) {
                rotateLeft(edge6, 0, j);
                k++;
            }
            b = (j + 1) * b + k;
        }
        return 720 * a + b;
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    void setURtoDF(int idx) {
        int x;
        Edge[] edge6 = {UR, UF, UL, UB, DR, DF};
        Edge[] otherEdge = {DL, DB, FR, FL, BL, BR};
        int b = idx % 720; // Permutation
        int a = idx / 720; // Combination
        for (Edge e : Edge.values())
            ep[e.ordinal()] = BR;// Use BR to invalidate all edges

        for (int j = 1, k; j < 6; j++)// generate permutation from index b
        {
            k = b % (j + 1);
            b /= j + 1;
            while (k-- > 0)
                rotateRight(edge6, 0, j);
        }
        x = 5;// generate combination and set edges
        for (int j = BR.ordinal(); j >= 0; j--)
            if (a - Cnk(j, x + 1) >= 0) {
                ep[j] = edge6[x];
                a -= Cnk(j, x-- + 1);
            }
        x = 0; // set the remaining edges DL..BR
        for (int j = UR.ordinal(); j <= BR.ordinal(); j++)
            if (ep[j] == BR)
                ep[j] = otherEdge[x++];
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Permutation of the six edges UR,UF,UL,UB,DR,DF
    public static int getURtoDF(short idx1, short idx2) {
        CubieCube a = new CubieCube();
        CubieCube b = new CubieCube();
        a.setURtoUL(idx1);
        b.setUBtoDF(idx2);
        for (int i = 0; i < 8; i++) {
            if (a.ep[i] != BR)
                if (b.ep[i] != BR)// collision
                    return -1;
                else
                    b.ep[i] = a.ep[i];
        }
        return b.getURtoDF();
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Permutation of the three edges UR,UF,UL
    short getURtoUL() {
        int a = 0, x = 0;
        Edge[] edge3 = new Edge[3];
        // compute the index a < (12 choose 3) and the edge permutation.
        for (int j = UR.ordinal(); j <= BR.ordinal(); j++)
            if (ep[j].ordinal() <= UL.ordinal()) {
                a += Cnk(j, x + 1);
                edge3[x++] = ep[j];
            }

        int b = 0;
        for (int j = 2; j > 0; j--)// compute the index b < 3! for the
        // permutation in edge3
        {
            int k = 0;
            while (edge3[j].ordinal() != j) {
                rotateLeft(edge3, 0, j);
                k++;
            }
            b = (j + 1) * b + k;
        }
        return (short) (6 * a + b);
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    void setURtoUL(short idx) {
        int x;
        Edge[] edge3 = {UR, UF, UL};
        int b = idx % 6; // Permutation
        int a = idx / 6; // Combination
        for (Edge e : Edge.values())
            ep[e.ordinal()] = BR;// Use BR to invalidate all edges

        for (int j = 1, k; j < 3; j++)// generate permutation from index b
        {
            k = b % (j + 1);
            b /= j + 1;
            while (k-- > 0)
                rotateRight(edge3, 0, j);
        }
        x = 2;// generate combination and set edges
        for (int j = BR.ordinal(); j >= 0; j--)
            if (a - Cnk(j, x + 1) >= 0) {
                ep[j] = edge3[x];
                a -= Cnk(j, x-- + 1);
            }
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Permutation of the three edges UB,DR,DF
    short getUBtoDF() {
        int a = 0, x = 0;
        Edge[] edge3 = new Edge[3];
        // compute the index a < (12 choose 3) and the edge permutation.
        for (int j = UR.ordinal(); j <= BR.ordinal(); j++)
            if (UB.ordinal() <= ep[j].ordinal() && ep[j].ordinal() <= DF.ordinal()) {
                a += Cnk(j, x + 1);
                edge3[x++] = ep[j];
            }

        int b = 0;
        for (int j = 2; j > 0; j--)// compute the index b < 3! for the
        // permutation in edge3
        {
            int k = 0;
            while (edge3[j].ordinal() != UB.ordinal() + j) {
                rotateLeft(edge3, 0, j);
                k++;
            }
            b = (j + 1) * b + k;
        }
        return (short) (6 * a + b);
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    void setUBtoDF(short idx) {
        int x;
        Edge[] edge3 = {UB, DR, DF};
        int b = idx % 6; // Permutation
        int a = idx / 6; // Combination
        for (Edge e : Edge.values())
            ep[e.ordinal()] = BR;// Use BR to invalidate all edges

        for (int j = 1, k; j < 3; j++)// generate permutation from index b
        {
            k = b % (j + 1);
            b /= j + 1;
            while (k-- > 0)
                rotateRight(edge3, 0, j);
        }
        x = 2;// generate combination and set edges
        for (int j = BR.ordinal(); j >= 0; j--)
            if (a - Cnk(j, x + 1) >= 0) {
                ep[j] = edge3[x];
                a -= Cnk(j, x-- + 1);
            }
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    int getURFtoDLB() {
        Corner[] perm = new Corner[8];
        int b = 0;
        for (int i = 0; i < 8; i++)
            perm[i] = cp[i];
        for (int j = 7; j > 0; j--)// compute the index b < 8! for the permutation in perm
        {
            int k = 0;
            while (perm[j].ordinal() != j) {
                rotateLeft(perm, 0, j);
                k++;
            }
            b = (j + 1) * b + k;
        }
        return b;
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    void setURFtoDLB(int idx) {
        Corner[] perm = {URF, UFL, ULB, UBR, DFR, DLF, DBL, DRB};
        int k;
        for (int j = 1; j < 8; j++) {
            k = idx % (j + 1);
            idx /= j + 1;
            while (k-- > 0)
                rotateRight(perm, 0, j);
        }
        int x = 7;// set corners
        for (int j = 7; j >= 0; j--)
            cp[j] = perm[x--];
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    int getURtoBR() {
        Edge[] perm = new Edge[12];
        int b = 0;
        for (int i = 0; i < 12; i++)
            perm[i] = ep[i];
        for (int j = 11; j > 0; j--)// compute the index b < 12! for the permutation in perm
        {
            int k = 0;
            while (perm[j].ordinal() != j) {
                rotateLeft(perm, 0, j);
                k++;
            }
            b = (j + 1) * b + k;
        }
        return b;
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    void setURtoBR(int idx) {
        Edge[] perm = {UR, UF, UL, UB, DR, DF, DL, DB, FR, FL, BL, BR};
        int k;
        for (int j = 1; j < 12; j++) {
            k = idx % (j + 1);
            idx /= j + 1;
            while (k-- > 0)
                rotateRight(perm, 0, j);
        }
        int x = 11;// set edges
        for (int j = 11; j >= 0; j--)
            ep[j] = perm[x--];
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Check a cubiecube for solvability. Return the error code.
    // 0: Cube is solvable
    // -2: Not all 12 edges exist exactly once
    // -3: Flip error: One edge has to be flipped
    // -4: Not all corners exist exactly once
    // -5: Twist error: One corner has to be twisted
    // -6: Parity error: Two corners ore two edges have to be exchanged
    int verify() {
        int sum = 0;
        int[] edgeCount = new int[12];
        for (Edge e : Edge.values())
            edgeCount[ep[e.ordinal()].ordinal()]++;
        for (int i = 0; i < 12; i++)
            if (edgeCount[i] != 1)
                return -2;

        for (int i = 0; i < 12; i++)
            sum += eo[i];
        if (sum % 2 != 0)
            return -3;

        int[] cornerCount = new int[8];
        for (Corner c : Corner.values())
            cornerCount[cp[c.ordinal()].ordinal()]++;
        for (int i = 0; i < 8; i++)
            if (cornerCount[i] != 1)
                return -4;// missing corners

        sum = 0;
        for (int i = 0; i < 8; i++)
            sum += co[i];
        if (sum % 3 != 0)
            return -5;// twisted corner

        if ((edgeParity() ^ cornerParity()) != 0)
            return -6;// parity error

        return 0;// cube ok
    }
}

class FaceCube {
    public Color[] f = new Color[54];

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Map the corner positions to facelet positions. cornerFacelet[URF.ordinal()][0] e.g. gives the position of the
    // facelet in the URF corner position, which defines the orientation.<br>
    // cornerFacelet[URF.ordinal()][1] and cornerFacelet[URF.ordinal()][2] give the position of the other two facelets
    // of the URF corner (clockwise).
    final static Facelet[][] cornerFacelet = {{U9, R1, F3}, {U7, F1, L3}, {U1, L1, B3}, {U3, B1, R3},
            {D3, F9, R7}, {D1, L9, F7}, {D7, B9, L7}, {D9, R9, B7}};

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Map the edge positions to facelet positions. edgeFacelet[UR.ordinal()][0] e.g. gives the position of the facelet in
    // the UR edge position, which defines the orientation.<br>
    // edgeFacelet[UR.ordinal()][1] gives the position of the other facelet
    final static Facelet[][] edgeFacelet = {{U6, R2}, {U8, F2}, {U4, L2}, {U2, B2}, {D6, R8}, {D2, F8},
            {D4, L8}, {D8, B8}, {F6, R4}, {F4, L6}, {B6, L4}, {B4, R6}};

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Map the corner positions to facelet colors.
    final static Color[][] cornerColor = {{U, R, F}, {U, F, L}, {U, L, B}, {U, B, R}, {D, F, R}, {D, L, F},
            {D, B, L}, {D, R, B}};

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Map the edge positions to facelet colors.
    final static Color[][] edgeColor = {{U, R}, {U, F}, {U, L}, {U, B}, {D, R}, {D, F}, {D, L}, {D, B},
            {F, R}, {F, L}, {B, L}, {B, R}};

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    FaceCube() {
        String s = "UUUUUUUUURRRRRRRRRFFFFFFFFFDDDDDDDDDLLLLLLLLLBBBBBBBBB";
        for (int i = 0; i < 54; i++)
            f[i] = Color.valueOf(s.substring(i, i + 1));

    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Construct a facelet cube from a string
    FaceCube(String cubeString) {
        for (int i = 0; i < cubeString.length(); i++)
            f[i] = Color.valueOf(cubeString.substring(i, i + 1));
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Gives string representation of a facelet cube
    String to_String() {
        String s = "";
        for (int i = 0; i < 54; i++)
            s += f[i].toString();
        return s;
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Gives CubieCube representation of a faceletcube
    CubieCube toCubieCube() {
        byte ori;
        CubieCube ccRet = new CubieCube();
        for (int i = 0; i < 8; i++)
            ccRet.cp[i] = URF;// invalidate corners
        for (int i = 0; i < 12; i++)
            ccRet.ep[i] = UR;// and edges
        Color col1, col2;
        for (Corner i : Corner.values()) {
            // get the colors of the cubie at corner i, starting with U/D
            for (ori = 0; ori < 3; ori++)
                if (f[cornerFacelet[i.ordinal()][ori].ordinal()] == U || f[cornerFacelet[i.ordinal()][ori].ordinal()] == D)
                    break;
            col1 = f[cornerFacelet[i.ordinal()][(ori + 1) % 3].ordinal()];
            col2 = f[cornerFacelet[i.ordinal()][(ori + 2) % 3].ordinal()];

            for (Corner j : Corner.values()) {
                if (col1 == cornerColor[j.ordinal()][1] && col2 == cornerColor[j.ordinal()][2]) {
                    // in cornerposition i we have cornercubie j
                    ccRet.cp[i.ordinal()] = j;
                    ccRet.co[i.ordinal()] = (byte) (ori % 3);
                    break;
                }
            }
        }
        for (Edge i : Edge.values())
            for (Edge j : Edge.values()) {
                if (f[edgeFacelet[i.ordinal()][0].ordinal()] == edgeColor[j.ordinal()][0]
                        && f[edgeFacelet[i.ordinal()][1].ordinal()] == edgeColor[j.ordinal()][1]) {
                    ccRet.ep[i.ordinal()] = j;
                    ccRet.eo[i.ordinal()] = 0;
                    break;
                }
                if (f[edgeFacelet[i.ordinal()][0].ordinal()] == edgeColor[j.ordinal()][1]
                        && f[edgeFacelet[i.ordinal()][1].ordinal()] == edgeColor[j.ordinal()][0]) {
                    ccRet.ep[i.ordinal()] = j;
                    ccRet.eo[i.ordinal()] = 1;
                    break;
                }
            }
        return ccRet;
    }

    ;
}
