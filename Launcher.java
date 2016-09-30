package chip8;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 * Fondation - Emulateur CHIP-8
 *
 * @author xophidia@gmail.com & Kryll@free.fr
 */

public class Launcher {

	public static void main(String[] args) {
		
		SwingUtilities.invokeLater((new Runnable() {

			public void run() {

				Screen fenetre;
				try {
					fenetre = new Screen();
					fenetre.setVisible(true);
				} catch (IOException ex) {
					Logger.getLogger(Launcher.class.getName()).log(
							Level.SEVERE, null, ex);
				}

			}

		}));
	}
}
