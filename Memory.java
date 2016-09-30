package chip8;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Fondation - Emulateur CHIP-8
 *
 * @author xophidia@gmail.com & Kryll@free.fr
 */

public class Memory {

	private int size;
	private byte[] memory;
	private short RomStart;

	/*
	 * @Constructeur memory
	 * 
	 * @Methodes getSize - taille de la memoire getMemory = recupère toute la
	 * memoire getByte - recupere un byte dans la memoire getOpCode - recupère
	 * un opcode sur 16bits setByte - test pour debugger la lecture ecriture en
	 * memoire reset - vide la memoire LoadAt - charge un tab de byte à
	 * l'endroit voulu
	 * 
	 * @Tools : toHex : pour affichage et debuggage de la memoire
	 */
	public Memory(int size) {
		this.size = size;
		memory = new byte[size];

	}

	public int getSize() {
		// return memory[size];
		return this.size;

	}

	public byte[] getMemory() {
		return memory;

	}

	public byte getByte(int position) {
		return memory[position];
	}

	public void setByte(int position, byte value) {
		memory[position] = value;
	}

	/**
	 * Permet de r�cuperer un short contenat l'opcode
	 * 
	 * @param position
	 * @return
	 */
	public short getWord(int position) {
		return (short) (((memory[position] << 8) & 0xFF00) + (memory[position + 1] & 0x00FF));
	}

	public Opcode getOpcode(int position) {
		return new Opcode(getWord(position));
	}

	public void reset() {

		for (int i = 0; i < size; i++) {
			memory[i] = 0;
		}
	}

	public void loadAt(int position, byte[] tab) {
		for (int i = 0; i < tab.length; i++) {
			memory[position + i] = tab[i];

		}
	}

	public void loadAt(int position, int[] binary) {
		for (int i = 0; i < binary.length; i++) {
			memory[position + i] = (byte) (binary[i] & 0xFF);
		}
	}

	/*
	 * Chargement de la ROM
	 */
	public void loadFile(int position, String filename) {
		try {
			File file = new File(filename);
			byte[] result = new byte[(int) file.length()];
			InputStream input = null;
			int totalBytesRead = 0;
			input = new BufferedInputStream(new FileInputStream(file));
			while (totalBytesRead < result.length) {
				int bytesRemaining = result.length - totalBytesRead;
				int bytesRead = input.read(result, totalBytesRead,
						bytesRemaining);
				if (bytesRead > 0) {
					totalBytesRead = totalBytesRead + bytesRead;
				}
			}
			input.close();
			loadAt(position, result);
		} catch (IOException ex) {
			Logger.getLogger(Memory.class.getName())
					.log(Level.SEVERE, null, ex);
		}
	}

	/*
	 * @Tools
	 */
	public static String toHex(byte value) {
		String s = String.format("%x", value);
		while (s.length() < 2) {
			s = "0" + s;
		}

		return s;
	}

	public static String toHex(short value) {
		String s = String.format("%x", value);
		while (s.length() < 4) {
			s = "0" + s;
		}

		return s;
	}

	public static String toHex(byte[] memory, int position, int taille) {
		String s = "";
		for (int i = 0; i < taille; i++) {
			s += toHex(memory[position + i]);
			if ((i + 1) % 16 == 0) {
				s += "\n";
			} else {
				s += " ";
			}
		}

		return s;
	}

	
	public short getRomStart() {
		return RomStart;
	}

	public void setRomStart(short RomStart) {
		this.RomStart = RomStart;
	}
}
