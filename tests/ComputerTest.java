/* Name: Gregory Hablutzel
   Class: TCSS 372 (Machine Organization) Spring 2020 - Menaka
   Assignment: A: Simulator
   Date: 4/24/20
   File: ComputerTest.java
   Description: This class contains the JUNIT 5 test cases for the computer class.
                It features test for the following operations:
   					ADD, AND, ADDI, ANDI, LW, SW
 */

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ComputerTest {

    /** Test for ADDI operation.
     * Performs: addi $t6, $t1,  2
     *  Checks if value in $t6 is 2
     */
    @Test
    public void testAddI() {
        Computer computer = new Computer();
        BitString AndIInstr = new BitString();
        // addi $t6, $t1,  2
        AndIInstr.setBits("00100001001011100000000000000010".toCharArray());
        computer.loadWord(0, AndIInstr);
        computer.execute();
        assertEquals(2, computer.getRegisterMap().get("$t6").getValue());
    }

    /** Test for ADDI operation.
     * Performs:  addi $t6, $t1,  2
     *             addi $t5, $t6, 5
     *  Checks if value in $t5 is 7
     */
    @Test
    public void testAddISourceRegHasPosValue() {
        Computer computer = new Computer();
        BitString AddIInstr = new BitString();
        // addi $t6, $t1,  2
        AddIInstr.setBits("00100001001011100000000000000010".toCharArray());
        computer.loadWord(0, AddIInstr);
        BitString AddIInstr2 = new BitString();;
        // addi $t5, $t6, 5
        AddIInstr2.setBits("00100001110011010000000000000101".toCharArray());
        computer.loadWord(4, AddIInstr2);
        computer.execute();
        assertEquals(7, computer.getRegisterMap().get("$t5").getValue2sComp());
    }

    /** Tests for ADDI operation.
     * Performs:  addi $t6, $t1,  2
     *             addi $t5, $t6, -5
     *  Checks if value in $t5 is -3
     */
    @Test
    public void testAddISourceRegHasNegValue() {
        Computer computer = new Computer();
        BitString AddIInstr = new BitString();
        // addi $t6, $t1,  2
        AddIInstr.setBits("00100001001011100000000000000010".toCharArray());
        computer.loadWord(0, AddIInstr);
        BitString AddIInstr2 = new BitString();
        // addi $t5, $t6, -5
         AddIInstr2.setBits("00100001110011011111111111111011".toCharArray());
        computer.loadWord(4, AddIInstr2);
        computer.execute();
        assertEquals(-3, computer.getRegisterMap().get("$t5").getValue2sComp());
    }

    /** Tests for ADD operation.
     * Performs:  addi $t6, $t1,  2
     *             addi $t5, $t6, 5
     *             add $t4, $t5, $t6
     *  Checks if value in $t4 is 9
     */
    @Test
    public void testAdd() {

        String program[] = {
                "00100001001011100000000000000010",  // addi $t6, $t1, 2  : t6 set to 2
                "00100001110011010000000000000101", // addi $t5, $t6, 5   : t5 set to 7
                "00000001101011100110000000100000" // add $t4, $t5, $t6   : t4 set to 9
               };
        Computer computer = new Computer();

        /* load the instructions in the "program" array */
        for (int i=0; i<program.length; i++) {
            BitString instr = new BitString();
            instr.setBits(program[i].toCharArray());
            computer.loadWord(i*4, instr);
        }
        computer.execute();
        assertEquals(9, computer.getRegisterMap().get("$t4").getValue());
    }

    /** Tests for ADD operation.
     * Performs:  addi $t6, $t1,  2
     *             addi $t5, $t6, -5
     *             add $t4, $t5, $t6
     *  Checks if value in $t4 is -1
     */
    @Test
    public void testAddNegVal() {

        String program[] = {
                "00100001001011100000000000000010",  // addi $t6, $t1, 2  : t6 set to 2
                "00100001110011011111111111111011", // addi $t5, $t6, -5 : t5 set to -3
                "00000001101011100110000000100000" // add $t4, $t5, $t6   : t4 set to -1
        };
        Computer computer = new Computer();

        /* load the instructions in the "program" array */
        for (int i=0; i<program.length; i++) {
            BitString instr = new BitString();
            instr.setBits(program[i].toCharArray());
            computer.loadWord(i*4, instr);
        }
        computer.execute();
        assertEquals(-1, computer.getRegisterMap().get("$t4").getValue());
    }


    /** Tests for ANDI operation.
     * Performs:  addi $t6, $t1,  2
     *             andi $t5, $t6, 0
     *  Checks if value in $t5 is 0
     */
    @Test
    public void testAndIZero() {

        String program[] = {
                "00100001001011100000000000000010",  // addi $t6, $t1, 2  : t6 set to 2
                "00110001110011010000000000000000" // andi $t5, $t6, 0   : t5 set to 0
        };
        Computer computer = new Computer();

        /* load the instructions in the "program" array */
        for (int i=0; i<program.length; i++) {
            BitString instr = new BitString();
            instr.setBits(program[i].toCharArray());
            computer.loadWord(i*4, instr);
        }
        computer.execute();
        assertEquals(0, computer.getRegisterMap().get("$t5").getValue());
    }

    /** Tests for ANDI operation.
     * Performs:  addi $t6, $t1,  2
     *             andi $t5, $t6, 3
     *  Checks if value in $t5 is 2
     */
    @Test
    public void testAndIGreater() {

        String program[] = {
                "00100001001011100000000000000010",  // addi $t6, $t1, 2  : t6 set to 2
                "00110001110011010000000000000011" // andi $t5, $t6, 3   : t5 set to 2
        };
        Computer computer = new Computer();

        /* load the instructions in the "program" array */
        for (int i=0; i<program.length; i++) {
            BitString instr = new BitString();
            instr.setBits(program[i].toCharArray());
            computer.loadWord(i*4, instr);
        }
        computer.execute();
        assertEquals(2, computer.getRegisterMap().get("$t5").getValue());
    }

    /** Tests for ANDI operation.
     * Performs:  addi $t6, $t1,  2
     *             andi $t5, $t6, 2
     *  Checks if value in $t5 is 2
     */
    @Test
    public void testAndISame() {

        String program[] = {
                "00100001001011100000000000000010",  // addi $t6, $t1, 2  : t6 set to 2
                "00110001110011010000000000000010" // andi $t5, $t6, 2   : t5 set to 2
        };
        Computer computer = new Computer();

        /* load the instructions in the "program" array */
        for (int i=0; i<program.length; i++) {
            BitString instr = new BitString();
            instr.setBits(program[i].toCharArray());
            computer.loadWord(i*4, instr);
        }
        computer.execute();
        assertEquals(2, computer.getRegisterMap().get("$t5").getValue());
    }

    /** Tests for ANDI operation.
     * Performs:  addi $t6, $t1,  2
     *             andi $t5, $t6, -1
     *  Checks if value in $t5 is 2
     */
    @Test
    public void testAndINegOne() {

        String program[] = {
                "00100001001011100000000000000010",  // addi $t6, $t1, 2  : t6 set to 2
                "00110001110011011111111111111111" // andi $t5, $t6, -1   : t5 set to 2
        };
        Computer computer = new Computer();

        /* load the instructions in the "program" array */
        for (int i=0; i<program.length; i++) {
            BitString instr = new BitString();
            instr.setBits(program[i].toCharArray());
            computer.loadWord(i*4, instr);
        }
        computer.execute();
        assertEquals(2, computer.getRegisterMap().get("$t5").getValue());
    }

    /** Tests for AND operation.
     * Performs:  addi $t6, $t1,  2
     *             andi $t5, $t6, 5
     *             and $t4, $t5, $t6
     *  Checks if value in $t4 is 2
     */
    @Test
    public void testAnd() {

        String program[] = {
                "00100001001011100000000000000010",  // addi $t6, $t1, 2  : t6 set to 2
                "00100001110011010000000000000101", // addi $t5, $t6, 5   : t5 set to 7
                "00000001101011100110000000100100" // and $t4, $t5, $t6  : t4 set to 2
        };
        Computer computer = new Computer();

        /* load the instructions in the "program" array */
        for (int i=0; i<program.length; i++) {
            BitString instr = new BitString();
            instr.setBits(program[i].toCharArray());
            computer.loadWord(i*4, instr);
        }
        computer.execute();
        assertEquals(2, computer.getRegisterMap().get("$t4").getValue());
    }

    /** Tests for SW operation.
     * Performs:  addi $t6, $t1,  2
     *             andi $t5, $t6, 5
     *             sw $t5, 0
     *  Checks if value in memory address 0 is 7
     */
    @Test
    public void testSW() {

        String program[] = {
                "00100001001011100000000000000010",  // addi $t6, $t1, 2  : t6 set to 2
                "00100001110011010000000000000101", // addi $t5, $t6, 5   : t5 set to 7
                "10101100000011010000000000000000" // sw $t5, 0  : store 7 into mem[0]
        };
        Computer computer = new Computer();

        /* load the instructions in the "program" array */
        for (int i=0; i<program.length; i++) {
            BitString instr = new BitString();
            instr.setBits(program[i].toCharArray());
            computer.loadWord(i*4, instr);
        }
        computer.execute();
        assertEquals(7,  computer.getMemory()[0].getValue());
    }

    /** Tests for LW operation.
     * Performs:  addi $t6, $t1,  2
     *             andi $t5, $t6, 5
     *             sw $t5, 0
     *             lw $t4, 0
     *  Checks if value in $t4 is 7
     */
    @Test
    public void testLW() {

        String program[] = {
                "00100001001011100000000000000010",  // addi $t6, $t1, 2  : t6 set to 2
                "00100001110011010000000000000101", // addi $t5, $t6, 5   : t5 set to 7
                "10101100000011010000000000000000", // sw $t5, 0  : store 7 into mem[0]
                "10001100000011000000000000000000" // lw $t4, 0   : load 7 into $t4
        };
        Computer computer = new Computer();

        /* load the instructions in the "program" array */
        for (int i=0; i<program.length; i++) {
            BitString instr = new BitString();
            instr.setBits(program[i].toCharArray());
            computer.loadWord(i*4, instr);
        }
        computer.execute();
        assertEquals(7,  computer.getRegisterMap().get("$t4").getValue());
    }


}