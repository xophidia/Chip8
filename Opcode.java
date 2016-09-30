package chip8;

/**
 * Fondation - Emulateur CHIP-8 
 *
 * @author xophidia@gmail.com & Kryll@free.fr
 */

public class Opcode {

	private short value;
	private int index = -1;
	private String name="";
	@SuppressWarnings("unused")
	private byte n4, n3, n2, n1;
	
	public int getIndex() {
		return this.index;
	}

	public Opcode(short opcode) {
		n4 = (byte) ((opcode & 0xF000) >> 12);
		n3 = (byte) ((opcode & 0x0F00) >> 8);
		n2 = (byte) ((opcode & 0x00F0) >> 4);
		n1 = (byte) (opcode & 0x000F);

		this.value = opcode;

		switch (n4) {
		case 0x0:
			switch (getNNN()) {
			case 0x0e0:
				name = "00E0";
				index = 0;
				break;
			case 0x0ee:
				name = "00EE";
				index = 1;
				break;
			default:
				name = "0NNN";
				index = 2;
				break;
			}
			break;

		case 0x1:
			this.name = "1NNN";
			this.index = 3;
			break;
		case 0x2:
			this.name = "2NNN";
			this.index = 4;
			break;
		case 0x3:
			this.name = "3XNN";
			this.index = 5;
			break;
		case 0x4:
			this.name = "4XNN";
			this.index = 6;
			break;
		case 0x5:
			this.name = "5XY0";
			this.index = 7;
			break;
		case 0x6:
			this.name = "6XNN";
			this.index = 8;
			break;
		case 0x7:
			this.name = "7XNN";
			this.index = 9;
			break;
		case 0x8:
			switch (n1) {
			case 0x0:
				this.name = "8XY0";
				this.index = 10;
				break;
			case 0x1:
				this.name = "8XY1";
				this.index = 11;
				break;
			case 0x2:
				this.name = "8XY2";
				this.index = 12;
				break;
			case 0x3:
				this.name = "8XY3";
				this.index = 13;
				break;
			case 0x4:
				this.name = "8XY4";
				this.index = 14;
				break;
			case 0x5:
				this.name = "8XY5";
				this.index = 15;
				break;
			case 0x6:
				this.name = "8XY6";
				this.index = 16;
				break;
			case 0x7:
				this.name = "8XY7";
				this.index = 17;
				break;
			case 0xE:
				this.name = "8XYE";
				this.index = 18;
				break;
			}
			break;
		case 0x9:
			if (n1 == 0x0){
			this.name = "9XY0";
			this.index = 19;
			}
			break;
		case 0xA:
			this.name = "ANNN";
			this.index = 20;
			break;
		case 0xB:
			this.name = "BNNN";
			this.index = 21;
			break;
		case 0xC:
			this.name = "CXNN";
			this.index = 22;
			break;
		case 0xD:
			this.name = "DXYN";
			this.index = 23;
			break;
		case 0xE:

			switch (getNN()) {
			case 0x9E:
				this.name = "EX9E";
				this.index = 24;
				break;

			case 0xA1:
				this.name = "EXA1";
				this.index = 25;
				break;
			}
			break;
		case 0xF:
			switch (getNN()) {
			case 0x07:
				this.name = "FX07";
				this.index = 26;
				break;

			case 0x0A:
				this.name = "FX0A";
				this.index = 27;
				break;
			case 0x15:
				this.name = "FX15";
				this.index = 28;
				break;

			case 0x18:
				this.name = "FX18";
				this.index = 29;
				break;
			case 0x1E:
				this.name = "FX1E";
				this.index = 30;
				break;
			case 0x29:
				this.name = "FX29";
				this.index = 31;
				break;
			case 0x33:
				this.name = "FX33";
				this.index = 32;
				break;
			case 0x55:
				this.name = "FX55";
				this.index = 33;
				break;
			case 0x65:
				this.name = "FX65";
				this.index = 34;
				break;
			}
			break;
		}
	}

	public short getNNN() {
		return (short) (value & 0x0FFF);
	}

	public short getNN() {
		return (short) (value & 0x00FF);
	}

	public short getVX() {
		return (short) ((value & 0x0F00) >> 8);
	}

	public short getVY() {
		return (short) ((value & 0x00F0) >> 4);
	}

	public short getByte() {
		return (short) (value & 0x000F);
	}

	@Override
	public String toString() {
		return " Value: \t" + Memory.toHex(value) +"\t"+ "index: \t" + getIndex();
	}
}
