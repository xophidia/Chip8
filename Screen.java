package chip8;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Fondation - Emulateur CHIP-8
 *
 * @author xophidia@gmail.com & Kryll@free.fr
 */

public class Screen extends JFrame {

	private static final long serialVersionUID = -8567536407321988164L;
	private JPanel panel;
	private Cpu chip8;
	private int m_interval = 2;
	private Timer m_timer;
	private int counter = 0;

	Screen() throws IOException {
		build(); // conception graphique
		init(); // initialisation de la fenêtre
		m_timer.start(); // run
	}

	public void build() {

		setTitle("Fondation - CHIP 8 By Ophidia @ Kryll");
		setSize(8 * 64 + 2 * 3 + 1, 8 * 32 + 25 + 3 + 1);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false); // permet un affichage complet de la page
		panel = new JPanel();
		panel.setBackground(Color.black);

		panel.setFocusable(true); // nécessaire pour l'utilisation du clavier

		/**
		 * Gestion du clavier en fonction des keycode, met � true ou false les
		 * keys dans le CPU. La gestion se fera par la suite avec les opcodes
		 * qui doivent �tre d�clar�s
		 */
		panel.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				int keyCode = e.getKeyCode();

				if (keyCode == 96)
					chip8.getKeys()[0] = false; // 0 : 96
				if (keyCode == 97)
					chip8.getKeys()[7] = false; // 1 : 97
				if (keyCode == 98)
					chip8.getKeys()[8] = false; // 2 : 98
				if (keyCode == 99)
					chip8.getKeys()[9] = false; // 3 : 99
				if (keyCode == 100)
					chip8.getKeys()[4] = false; // 4 : 100
				if (keyCode == 101)
					chip8.getKeys()[5] = false; // 5 : 101
				if (keyCode == 102)
					chip8.getKeys()[6] = false; // 6 : 102
				if (keyCode == 103)
					chip8.getKeys()[1] = false; // 7 : 103
				if (keyCode == 104)
					chip8.getKeys()[2] = false; // 8 : 104
				if (keyCode == 105)
					chip8.getKeys()[3] = false; // 9 : 105
				if (keyCode == 70)
					chip8.getKeys()[15] = false; // F : 70
				if (keyCode == 65)
					chip8.getKeys()[10] = false; // A
				if (keyCode == 66)
					chip8.getKeys()[11] = false; // B
				if (keyCode == 67)
					chip8.getKeys()[12] = false; // C
				if (keyCode == 68)
					chip8.getKeys()[13] = false; // D
				if (keyCode == 69)
					chip8.getKeys()[14] = false; // E

			}

			@Override
			public void keyPressed(KeyEvent e) {
				int keyCode = e.getKeyCode();
				if (keyCode == 96)
					chip8.getKeys()[0] = true; // 0 : 96
				if (keyCode == 97)
					chip8.getKeys()[7] = true; // 1 : 97
				if (keyCode == 98)
					chip8.getKeys()[8] = true; // 2 : 98
				if (keyCode == 99)
					chip8.getKeys()[9] = true; // 3 : 99
				if (keyCode == 100)
					chip8.getKeys()[4] = true; // 4 : 100
				if (keyCode == 101)
					chip8.getKeys()[5] = true; // 5 : 101
				if (keyCode == 102)
					chip8.getKeys()[6] = true; // 6 : 102
				if (keyCode == 103)
					chip8.getKeys()[1] = true; // 7 : 103
				if (keyCode == 104)
					chip8.getKeys()[2] = true; // 8 : 104
				if (keyCode == 105)
					chip8.getKeys()[3] = true; // 9 : 105
				if (keyCode == 70)
					chip8.getKeys()[15] = true; // F : 70
				if (keyCode == 65)
					chip8.getKeys()[10] = true; // A
				if (keyCode == 66)
					chip8.getKeys()[11] = true; // B
				if (keyCode == 67)
					chip8.getKeys()[12] = true; // C
				if (keyCode == 68)
					chip8.getKeys()[13] = true; // D
				if (keyCode == 69)
					chip8.getKeys()[14] = true; // E
			}

		});

		add(panel);

	}

	public void init() {

		chip8 = Cpu.getInstance();

		m_timer = new Timer(m_interval, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				// 1 affichage tous les 4 opcodes

				counter = (counter + 1) % 4;
				chip8.gestionOpcode();
				if (counter == 0) {
					repaint();
					chip8.updateTimers();
				}
				
			}

		});

	}

	public void paint(Graphics g) {
		g = panel.getGraphics();

		for (int y = 0; y < 32; y++) {
			for (int x = 0; x < 64; x++) {
				if (chip8.getPixel()[x][y]) {
					g.setColor(Color.white);
				} else {
					g.setColor(Color.black);
				}
				g.fillRect(x * 8, y * 8, 8, 8);
			}
		}

	}

}
