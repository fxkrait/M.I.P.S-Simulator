/* Name: Gregory Hablutzel
   Class: TCSS 372 (Machine Organization) Spring 2020 - Menaka
   Assignment: A: Simulator
   Date: 4/24/20
   File: Computer.java
   Description: This class defines the MIPS computer.
   				It allows up to 500 pieces of 32 bit data, 200 instructions.
   				It features the following operations:
   					ADD, AND, ADDI, ANDI, LW, SW
 */
import java.util.Map;
import java.util.TreeMap;

/**
 * Computer class comprises of memory, registers, cc and
 * can execute the instructions based on PC and IR 
 * @author mmuppa
 *
 */
public class Computer {

	private final static int MAX_MEMORY = 2000; // stores 500 pieces of data because PC + 4
	private final static int MAX_INSTRUCTIONS = 800; // stores 200 instructions because PC + 4

	private final static int MAX_REGISTERS = 35;

	private BitString mMemory[];
	private BitString mIR;
	private BitString iMemory[];

	// Maps the registers to their 32 bit values.
	// EX "$t1" -> 2 (a stored value)
	private Map<String, BitString> registerMap;


	// stores the string mapping to the given int value for the integer
	private Map<Integer, String> regIntToStr; // EX: 0 -> "$zero"

	// stores the integer mapping for the given register string.
	private Map<String, Integer> regStrToInt; // reverse map

	// stores the string names for all of the registers
	private String[] regNames = {
			"$zero",
			"$at",
			"$v0", "$v1",
			"$a0", "$a1", "$a2", "$a3",
			"$t0", "$t1", "$t2", "$t3", "$t4", "$t5", "$t6", "$t7",
			"$s0", "$s1", "$s2", "$s3", "$s4", "$s5", "$s6", "$s7",
			"$t8", "$t9",
			"$k0", "$k0",
			"$gp", "$sp", "$fp", "$ra",
			"pc", "hi", "lo"};

	/**
	 * Initializes all the data and instruction memory to 0, registers to 0
	 * PC, IR to 32 bit 0s
	 * Represents the initial state 
	 */
	public Computer() {

		mIR = new BitString();
		mIR.setValue(0);

		// initialize data memory
		mMemory = new BitString[MAX_MEMORY];
		for (int i = 0; i < MAX_MEMORY; i++) {
			mMemory[i] = new BitString();
			mMemory[i].setValue(0);
		}

		// initialize instruction memory
		iMemory = new BitString[MAX_INSTRUCTIONS];
		for (int i = 0; i < MAX_INSTRUCTIONS; i++) {
			iMemory[i] = new BitString();
			iMemory[i].setValue(0);
		}
		regIntToStr = new TreeMap<Integer, String>();
		regStrToInt = new TreeMap<String, Integer>();
		registerMap = new TreeMap<String, BitString>();

		// create the registerMap, regIntToStr map, and regStrToInt map.
		for (int i = 0; i < regNames.length; i++) {
			regIntToStr.put(i, regNames[i]); // "EX: 0 -> "$zero"
			regStrToInt.put(regNames[i], i); // "EX: "$zero" -> 0
			registerMap.put(regNames[i], new BitString()); // create register (EX: $zero)
			registerMap.get(regNames[i]).setValue(0); // initialize register to 0
		}
	}

	public Map<String, BitString> getRegisterMap() {
		return registerMap;
	}

	public BitString[] getMemory() {
		return mMemory;
	}



	/**
	 * Loads a 16 bit word into memory at the given address. 
	 * @param address memory address
	 * @param word data or instruction or address to be loaded into memory
	 */
	public void loadWord(int address, BitString word) {
		if (address < 0 || address >= MAX_INSTRUCTIONS) {
			throw new IllegalArgumentException("Invalid address");
		}
		iMemory[address] = word;
	}


	/**
	 * This method will execute all the instructions starting at address 0 
	 * till HALT instruction is encountered. 
	 */
	public void execute() {
		BitString opCodeStr;
		int opCode;
		BitString funcCodeStr;
		int funcCode;

		while (true) {
			// Fetch the instruction
			int pcVal = registerMap.get("pc").getValue();

			mIR = iMemory[pcVal];

			registerMap.get("pc").setValue(pcVal + 4); // PC + 4
			//registerMap.put("PC", new BitString(pcVal + 4)); // PC + 4

			// Decode the instruction's first 6 bits
			// to figure out the opcode
			opCodeStr = mIR.substring(31, 6);
			opCode = opCodeStr.getValue();

			// What instruction is this?
			if (opCode == 0) { // R-Format
				funcCodeStr = mIR.substring(5, 6);
				funcCode = funcCodeStr.getValue();
				if (funcCode == 32) { // ADD
					executeAdd();
				} else if (funcCode == 36) { // AND
					executeAnd();
				} else { // NA
					return; // not valid, just end program.
				}
			} else { // I / J Format
				if (opCode == 8) { // ADDI
					executeAddI();
				} else if (opCode == 12) { // ANDI
					executeAndI();

				} else if (opCode == 35) { // LW
					executeLW();
				} else if (opCode == 43) { // SW
					executeSW();
				} else {
					return; // not valid, just end program.
				}
			}
		}
	}
	
	/**
     * Performs add operation.
	 *  - Register Mode: uses Destination Register [15:11], Source Register [20:16],
	 * 	  	Source Register2 [25:21]. Adds SR + SR2, stores in DR.
     */

    private void executeAdd() {
		BitString sourceOneBS = mIR.substring(20, 5);
		BitString sourceTwoBS = mIR.substring(25, 5);
		BitString destBS = mIR.substring(15, 5);

		int destVal = destBS.getValue();
		String destReg = regIntToStr.get(destVal);
		int sourceOneRegInt = sourceOneBS.getValue();
		String sourceOneRegStr = regIntToStr.get(sourceOneRegInt);

		int sourceTwoRegInt = sourceTwoBS.getValue();
		String sourceTwoRegStr = regIntToStr.get(sourceTwoRegInt);

		BitString add = new BitString();

		char[] s1 = registerMap.get(sourceOneRegStr).getBits();
		char[] s2 = registerMap.get(sourceTwoRegStr).getBits();;
		char[] output = new char[32];
		int carry = 0;
		for (int i = 31; i >= 0; i--) {
			int sum = carry + (s1[i] - '0') + (s2[i] - '0');
			if (sum >= 2) {
				carry = 1;
			} else {
				carry = 0;
			}
			if (sum % 2 == 0) { // 0 + 0 -> 0 carry 0, 1 + 1 -> 0 carry 1  (ignore carry)
				output[i] = '0';
			} else { // 1 + 0 -> 1 carry 0, 1 + 1 + 1-> 1 carry 1
				output[i] = '1';
			}
		}
		add.setBits(output);
		registerMap.put(destReg, add);

    }

	/**
	 * Performs add immediate operation.
	 *  - uses Destination Register [20:16], Source Register [25:21],
	 * 	  	Immediate Bits [15:0]. Adds SR + immediate, stores in DR.
	 */
	private void executeAddI() {

		BitString sourceBS = mIR.substring(25, 5);
		BitString destBS = mIR.substring(20, 5);
		BitString immediateBS = mIR.substring(15, 16);

		int destVal = destBS.getValue();
		String destReg = regIntToStr.get(destVal);

		int sourceRegInt = sourceBS.getValue();
		String sourceReg = regIntToStr.get(sourceRegInt);

		int immVal = immediateBS.getValue2sComp();
		BitString immediate = new BitString();
		immediate.setValue2sComp(immVal);
		BitString add = new BitString();

		char[] s1 = registerMap.get(sourceReg).getBits();
		char[] s2 = immediate.getBits();
		char[] output = new char[32];
		int carry = 0;
		for (int i = 31; i >= 0; i--) {
			int sum = carry + (s1[i] - '0') + (s2[i] - '0');
			if (sum >= 2) {
				carry = 1;
			} else {
				carry = 0;
			}
			if (sum % 2 == 0) { // 0 + 0 -> 0 carry 0, 1 + 1 -> 0 carry 1  (ignore carry)
				output[i] = '0';
			} else { // 1 + 0 -> 1 carry 0, 1 + 1 + 1-> 1 carry 1
				output[i] = '1';
			}
		}
		add.setBits(output);
		registerMap.put(destReg, add);
	}

	/**
	 * Performs AND immediate operation.
	 *  - uses Destination Register [20:16], Source Register [25:21],
	 * 	  	Immediate Bits [15:0]. ANDs SR and immediate, stores in DR.
	 */
	private void executeAndI() {

		BitString sourceBS = mIR.substring(25, 5);
		BitString destBS = mIR.substring(20, 5);
		BitString immediateBS = mIR.substring(15, 16);

		int destVal = destBS.getValue();
		String destReg = regIntToStr.get(destVal);

		int sourceRegInt = sourceBS.getValue();
		String sourceReg = regIntToStr.get(sourceRegInt);
		int sourceRegVal = registerMap.get(sourceReg).getValue2sComp();

		int immVal = immediateBS.getValue2sComp(); // get immediate bits to be 32 bits long
		BitString immediate = new BitString();
		immediate.setValue2sComp(immVal);

		BitString sourceOneValBS = registerMap.get(sourceReg).copy();
		BitString sourceTwoValBS = immediate;
		BitString and = new BitString();
		char[] s1 = sourceOneValBS.getBits();
		char[] s2 = sourceTwoValBS.getBits();
		char[] output = new char[32];
		for (int i = 0; i <= 31; i++) { // traverse bits
			char val = '0';
			if (s1[i] == '1' && s2[i] == '1') {
				val = '1';
			}
			output[i] = val;
		}
		and.setBits(output);
		registerMap.put(destReg, and);
	}


	/**
	 * Performs AND operation.
	 *  - uses Destination Register [15:11], Source Register [20:16],
	 * 	  	Source Register2 [25:21]. Ands SR and SR2, stores in DR.
	 */
	private void executeAnd() {

		BitString sourceOneBS = mIR.substring(20, 5);
		BitString sourceTwoBS = mIR.substring(25, 5);
		BitString destBS = mIR.substring(15, 5);

		int destVal = destBS.getValue();
		String destReg = regIntToStr.get(destVal);
		int sourceOneRegInt = sourceOneBS.getValue();
		String sourceOneRegStr = regIntToStr.get(sourceOneRegInt);

		int sourceTwoRegInt = sourceTwoBS.getValue();
		String sourceTwoRegStr = regIntToStr.get(sourceTwoRegInt);


		BitString sourceOneValBS = registerMap.get(sourceOneRegStr).copy();
		BitString sourceTwoValBS = registerMap.get(sourceTwoRegStr).copy();
		BitString and = new BitString();
		char[] s1 = sourceOneValBS.getBits();
		char[] s2 = sourceTwoValBS.getBits();
		char[] output = new char[32];
		for (int i = 0; i <= 31; i++) { // traverse bits
			char val = '0';
			if (s1[i] == '1' && s2[i] == '1') {
				val = '1';
			}
			output[i] = val;
		}
		and.setBits(output);

		registerMap.put(destReg, and);
	}

	/**
	 * Performs Store Word (32 bits) operation.
	 *  - uses Source Register [20:16], memory address [15:0].
	 *  Stores value at source register into memory address.
	 */
	private void executeSW() {

		BitString sourceBS = mIR.substring(20, 5);
		BitString destBS = mIR.substring(15, 16);

		int destVal = destBS.getValue();

		int sourceRegInt = sourceBS.getValue();
		String sourceRegStr = regIntToStr.get(sourceRegInt);

		BitString output = new BitString();
		output = registerMap.get(sourceRegStr).copy();
		mMemory[destVal] = output;
	}


	/**
	 * Performs Load Word (32 bits) operation.
	 *  - uses Destination Register [20:16], memory address [15:0].
	 *  load value at memory address into source register.
	 */
	private void executeLW() {

		BitString regBS = mIR.substring(20, 5);
		BitString memBS = mIR.substring(15, 16);

		int memVal = memBS.getValue();

		int regInt = regBS.getValue();
		String regStr = regIntToStr.get(regInt);
		registerMap.put(regStr, mMemory[memVal]);
	}

	/**
	 * Displays the computer's state
	 */
	public void display() {
		System.out.print("\nPC ");
		registerMap.get("pc").display("");
		System.out.print("   ");

		System.out.print("IR ");
		mIR.display("");
		System.out.print("   ");
//
//		System.out.print("CC ");
//		mCC.display(true);
//		System.out.println("   ");

		for (int i = 0; i < MAX_REGISTERS; i++) {
			String reg = regIntToStr.get(i);

			System.out.print(reg + " ");
			registerMap.get(reg).display("");
			if (i % 3 == 2) {
				System.out.println();
			} else {
				System.out.print("   ");
			}
		}
		System.out.println();

		for (int i = 0; i < MAX_MEMORY; i++) {
			System.out.printf("%3d ", i);
			mMemory[i].display("");
			if (i % 3 == 2) {
				System.out.println();
			} else {
				System.out.print("   ");
			}
		}
		System.out.println();

	}
}
