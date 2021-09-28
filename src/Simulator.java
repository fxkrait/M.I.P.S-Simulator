/* Name: Gregory Hablutzel
   Class: TCSS 372 (Machine Organization) Spring 2020 - Menaka
   Assignment: A: Simulator
   Date: 4/24/20
   File: Simulator.java
   Description: This class loads a given program, and runs the computer on it.
   				 This specific program adds 6 to $t6.
 */
public class Simulator {

	public static void main(String[] args) {

		Computer comp;

		/************************************** */
		/** The next two variables - program and programSize - */
		/** allow someone using the simulator (such as a grader) */
		/** to decide what program will be simulated. */
		/** The simulation must load and execute */
		/** instructions found in the "program" array. */
		/** For grading purposes, it must be possible for me to */
		/**
		 * - paste in a different set of binary strings to replace the existing
		 * ones
		 */
		/** - recompile your program without further changes */
		/** and see the simulator load and execute the new program. */
		/** Your grade will depend largely on how well that works. */
		/************************************** */

		String program[] = {
				"00100001001011100000000000000010" //addi $t6, $t1,  2
				 };
		comp = new Computer();

		/* load the instructions in the "program" array */
		for (int i=0; i<program.length; i++) {
			BitString instr = new BitString();
			instr.setBits(program[i].toCharArray());
			comp.loadWord(i, instr);
		}
		comp.display();

		/* execute the program */
		/* During execution, the only output to the screen should be */
		/* the result of executing OUT. */
		comp.execute();

		/* shows final configuration of computer */
		comp.display();
	}
}
