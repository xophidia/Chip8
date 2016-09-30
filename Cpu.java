package chip8;

import java.util.Random;
import java.util.Stack;

/**
 * Fondation - Emulateur CHIP-8
 *
 * @author xophidia@gmail.com & Kryll@free.fr
 */
public class Cpu {

	private chip8.Memory memory;
	private short pointeurCounter;
	private Stack<Short> stack; // pile taille 16 v0-v15
	private Opcode opcode;
	private short[] registre;
	private short pointerI; // pointeur pour les A
	private boolean[][] pixels;
	private short delayTimer;
	private short soundTimer;
	private boolean[] keys; // Gestion clavier
	private static Cpu chip8 = null; // pour le singleton
	private short romStartAdresse = 0x200;

	// mise en place du Singleton
	public static Cpu getInstance() {
		if (chip8 == null)
			chip8 = new Cpu();
		System.out.println(chip8);
		return chip8;

	}

	private static final int[] chip8Font = {
		    0xF0, 0x90, 0x90, 0x90, 0xF0, // 0
			0x20, 0x60, 0x20, 0x20, 0x70, // 1
			0xF0, 0x10, 0xF0, 0x80, 0xF0, // 2
			0xF0, 0x10, 0xF0, 0x10, 0xF0, // 3
			0x90, 0x90, 0xF0, 0x10, 0x10, // 4
			0xF0, 0x80, 0xF0, 0x10, 0xF0, // 5
			0xF0, 0x80, 0xF0, 0x90, 0xF0, // 6
			0xF0, 0x10, 0x20, 0x40, 0x40, // 7
			0xF0, 0x90, 0xF0, 0x90, 0xF0, // 8
			0xF0, 0x90, 0xF0, 0x10, 0xF0, // 9
			0xF0, 0x90, 0xF0, 0x90, 0x90, // A
			0xE0, 0x90, 0xE0, 0x90, 0xE0, // B
			0xF0, 0x80, 0x80, 0x80, 0xF0, // C
			0xE0, 0x90, 0x90, 0x90, 0xE0, // D
			0xF0, 0x80, 0xF0, 0x80, 0xF0, // E
			0xF0, 0x80, 0xF0, 0x80, 0x80 // F
	};

	private Cpu() {
		reset();
		//chip8.loadROM("roms/TEST.ch8");
		 memory.loadFile(memory.getRomStart(), "roms/Missile.ch8"); // chargement
		// de la ROM
	}

	public void reset() {
		this.memory = new Memory(4096);
		memory.setRomStart((short) 0x200);
		this.pointeurCounter = memory.getRomStart();
		registre = new short[16];
		registre[0xF] = 0x0;
		this.stack = new Stack<Short>();
		this.stack.setSize(16);
		pixels = new boolean[64][32];
		keys = new boolean[16];
		this.delayTimer = 0x0;
		this.soundTimer = 0x0;
		memory.loadAt(0x0, chip8Font);
	}

	private void clearScreen() {
		for (int y = 0; y < 32; y++) {
			for (int x = 0; x < 64; x++) {
				pixels[x][y] = false;
			}
		}
	}

	public void updateTimers() {
		if (delayTimer > 0) {
			delayTimer--;
		}
		if (soundTimer > 0) {
			soundTimer--;
		}
	}

	private void draw(short vx, short vy, short k) {
		int x;
		int y;
		registre[0xF] = 0x0;
		for (int i = 0; i < k; i++) {
			byte ligne = memory.getByte(pointerI + i); //
			y = Math.abs((registre[vy] + i) % 32);
			for (int n = 0; n < 8; n++) {
				x = Math.abs((registre[vx] + n) % 64);
				if (((ligne) & (0x1 << (7 - n))) != 0) {
					if (pixels[x][y]) {
						registre[0xF] = 0x1;
						pixels[x][y] = false;
					} else
						pixels[x][y] = true;
				}
			}
		}
	}

	public void loadROM(String filename) {
		memory.loadFile(romStartAdresse, filename);
	}

	public void gestionOpcode() {

		opcode = memory.getOpcode(pointeurCounter);
		System.out.println(opcode);
		short VX = opcode.getVX();
		short VY = opcode.getVY();
		short NIBBLE = opcode.getByte();
		short NNN = opcode.getNNN();
		short NN = opcode.getNN();

		switch (opcode.getIndex()) {
		case 0: // effacer l'ecran
			clearScreen();
			pointeurCounter += 2;
			break;
		case 1: // retourn d'une sous routine
			if (stack.size() > 0) {
				pointeurCounter = stack.pop();
			} else {
				System.out.println("<DEBUG>Stack empty");
			}
			break;
		case 3: // 1NNN : Effectue un saut � l'adresse NNN
			pointeurCounter = NNN;

			break;
		case 4: // 2NNN : Execute un sous programme � l'adresse NNN
			stack.push(pointeurCounter);
			pointeurCounter = NNN;
			break;
		case 5: // 3XNN : Saute l'instruction suivante si VX == NN
			if (registre[VX] == NN) {
				pointeurCounter += 4; // skip
			} else {
				pointeurCounter += 2;
			}
			break;
		case 6: // 4XNN : Saute l'instruction suivante si VX != NN
			if (registre[VX] != NN) {
				pointeurCounter += 4;
			} else {
				pointeurCounter += 2;
			}
			break;
		case 7: // 5XY0 : Saute l'instruction suivante si VX == VY
			if (registre[VX] == VY) {
				pointeurCounter += 4;
			} else {
				pointeurCounter += 2;
			}
			break;
		case 8: // 6XNN : D�fini VX � NN
			//System.out.println(Memory.toHex(VX) + "-" + Memory.toHex(NN));
			registre[VX] = NN;
			pointeurCounter += 2;
			break;
		case 9: // 7XNN : Ajoute NN � VX
			registre[VX] += NN;
			pointeurCounter += 2;
			break;
		case 10: // 8XY0 : D�fini VX � la valeur VY
			registre[VX] = registre[VY];
			pointeurCounter += 2;
			break;
		case 11: // 8XY1 :D�fini VX � VX OR VY
			registre[VX] = (short) (registre[VX] | registre[VY]);
			pointeurCounter += 2;
			break;
		case 12: // 8XY2 : D�fini VX � VX AND VY
			registre[VX] = (short) (registre[VX] & registre[VY]);
			pointeurCounter += 2;
			break;
		case 13: // 8XY3 : D�fini VX � VX XOR VY
			registre[VX] = (short) (registre[VX] ^ registre[VY]);
			pointeurCounter += 2;
			break;
		case 14: // 8XY4 : VY += VX
		     // si le r�sultat est > 0xff VF = 0x1 sinon VF = 0x0
			if ((registre[VX] + registre[VY]) > 0xFF) //
			{
				registre[0xF] = 0x1;

			} else {
				registre[0xF] = 0x0;
			}
			registre[VX] += registre[VY];
			pointeurCounter += 2;
			break;
		case 15: // 8XY5 : VY -= VX
		     // si le r�sultat est n�gatif VF = 0x1 sinon VF = 0x0
			int subVY = (registre[VX] - registre[VY]);
			
			if (subVY < 0) {
				registre[0xF] = 0x1;
				subVY *= -1;
			} else {
				registre[0xF] = 0x0;
			}

			registre[VX] = (short) subVY;
			pointeurCounter += 2;
			break;
		case 16:// 8XY6 : D�cale VX � droite de 1 bit
		     // VF = valeur du bit de poids faible de VX avant d�calage
			registre[0xF] = (short) (registre[VX] & 0x01);
			registre[VX] = (short) (registre[VX] >> 1);
			pointeurCounter += 2;
			break;
		case 17:// 8XY7 : VX = VY - VX
		     // si le r�sultat est < 0 VF = 0x1 sinon VF = 0x0
			int subVX = (registre[VY] - registre[VX]); 
			if  (subVX < 0 ){
				registre[0xF] = 0x1;
			} else {
				registre[0xF] = 0x0;
			}
			registre[VX] = (short) (subVX);
			pointeurCounter += 2;
			break;
		case 18:// 8XYE : D�cale VX � gauche de 1 bit
		     // VF = valeur du bit de poids fort de VX avant d�calage
			registre[0xF] = (short) (registre[VX] >> 7); 
			registre[VX] = (short) (registre[VX] << 1);
			pointeurCounter += 2;
			break;
		case 19:// 9XY0 : Saute l'instruction suivante si VX et VY ne sont pas �gaux
			if (registre[VX] != registre[VY]) {
				pointeurCounter += 4;
			} else {pointeurCounter += 2;
			}
			break;
		case 20:// ANNN : Affecte NNN � I
			pointerI = NNN;
			pointeurCounter += 2;
			break;
		case 21:// BNNN : Passe � l'adresse NNN + V0
			pointeurCounter = (short) (NNN + registre[0x0]);
			break;
		case 22:// CXNN : D�finit VX � un nombre al�atoire < NN
			Random random = new Random();
			registre[VX] = (short) (random.nextInt(255) % (NN + 1)); 
			pointeurCounter += 2;
			break;
		case 23:// DXYN : Dessine un sprite � l'�cran
			draw(VX, VY, NIBBLE);
			pointeurCounter += 2;
			break;
		case 24:// EX9E : Saute l'instruction suivante si la touche repr�sent�e par VX == 0x1
			// System.out.println(registre[VX]);
			if (getKeys()[registre[VX]] == true) {
				pointeurCounter += 4;
			} else {
				pointeurCounter += 2;
			}
			break;
		case 25:// EXA1 : Saute l'instruction suivante si la touche repr�sent�e par VX == 0x0
			// System.out.println(registre[VX]);
			if (getKeys()[registre[VX]] == false) {
				pointeurCounter += 4;
			} else {
				pointeurCounter += 2;
			}
			break;

		case 26:// FX07 : D�fini VX comme minuterie
			registre[VX] = delayTimer;
			pointeurCounter += 2;
			break;
		case 27:// FX0A : Attend l'appuie sur une touche et le retour est stock� dans VX
			System.out.println("DEBUG -----------------------> FX0A");

			for (byte i = 0; i < 16; i++) {
				if (chip8.getKeys()[i] == true) {
					registre[VX] = i;
				}
			}

			pointeurCounter += 2;
			break;
		case 28:// FX15 : D�fini la tempo du jeu � VX 
			delayTimer = registre[VX];
			pointeurCounter += 2;
			break;
		case 29:// FX18 : D�finie la tempo du son � VX
			soundTimer = registre[VX];
			pointeurCounter += 2;
			break;
		case 30: // FX1E : I = VX + I, VF = 1 si il y a un depassement de m�moire sinon VF = 0
			if (pointerI > 0xFFF)
				registre[0xF] = 0x1;
			else
				registre[0xF] = 0x0;
			pointerI = (short) (registre[VX] + pointerI);
			pointeurCounter += 2;
			break;
		case 31:// FX29 : D�finit I � l'emplacement du caract�re sotck� dans VX
			pointerI = (short) (5 * VX);
			pointeurCounter += 2;
			break;
		case 32: // FX33 : Stocke dans la m�moire le code d�cimal repr�sentant VX
			memory.setByte(pointerI, (byte) (registre[VX] / 100));
			memory.setByte(pointerI + 1, (byte) ((registre[VX] % 100) / 10));
			memory.setByte(pointerI + 2, (byte) ((registre[VX] % 100) % 10));
			pointeurCounter = 2;
			break;
		case 33: // FX55 : Stocke le contenu des registres V0 � VX en m�moire � partir de l'adresse I
			for (byte c = 0; c <= VX; c++)
				memory.setByte(pointerI + c, (byte) registre[c]);
			pointeurCounter += 2;
			break;
		case 34: // FX65 : Rempli les registres V0 � VX avec le contenu de la m�moire � partir de I
			for (byte c = 0; c <= VX; c++)
				registre[c] = memory.getByte(pointerI + c);
			pointeurCounter = 2;
			break;

		default:
			break;
		}

	}

	public String showScreen() {
		String s = "";
		for (int y = 0; y < 32; y++) {
			for (int x = 0; x < 64; x++) {
				if (pixels[x][y])
					s += "*";
				else
					s += " ";
			}
			s = s + "\n";
		}
		return s;
	}

	public String debug() {
		String s = "";
		String tmp = "";
		s = "Stack:" + stack + ", ";
		s += "pc:" + Memory.toHex(pointeurCounter) + ", ";
		s += "i:" + Memory.toHex(pointerI) + ", ";
		for (int i = 0; i < 16; i++)
			tmp += Memory.toHex(registre[i]) + ",";
		s += "v:" + tmp;
		return s;
	}

	/**
	 * Accesseurs et Mutateurs
	 * 
	 */

	public String getPointeur() {
		return Memory.toHex(pointeurCounter);
	}

	public Memory getMemory() {
		return memory;
	}

	public boolean[][] getPixel() {
		return pixels;
	}

	public boolean[] getKeys() {
		return keys;
	}

	public short getSoundTimer() {
		return soundTimer;
	}

	public void setSoundTimer(short soundTimer) {
		this.soundTimer = soundTimer;
	}

	public short getDelayTimer() {
		return delayTimer;
	}

}
