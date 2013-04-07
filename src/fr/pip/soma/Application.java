/**
 * Copyright �2009 Philippe PETER.
 * Les sources qui constituent ce projet Soma de même que la documentation associée 
 * sont la propriété de leur auteur.
 * Je donne mon accord au site developpez.com pour l'utilisation de tout ou partie 
 * des sources et de la documentation de ce projet dans les pages developpez.com
 */
package fr.pip.soma;

import java.util.List;

import fr.pip.soma.algo.AlgorithmManager;
import fr.pip.soma.gui.ApplicationFrame;
import fr.pip.soma.model.Shape;
import fr.pip.soma.model.Soma;
import fr.pip.soma.parser.Parser;

/**
 * Point d'entr�e de l'application, construit l'IHM et d�marre les threads pour
 * le calcul. L'application est PropertyChangeListener pour les actions des
 * menus et BacktrackerListener pour l'état de l'algorithme en cours.
 * 
 * @author Philippe PETER
 * 
 */
public class Application {

	/** Point d'entrée de l'application **/
	public static void main(String[] args) {
		new Application();
	}

	public Application() {
		// Charge le puzzle par defaut.
		Soma soma = Parser.getSoma();
		// Les figures du puzzle.
		List<Shape> shapes = Parser.getShapes();
		// Frame principage de l'application.
		ApplicationFrame frame = new ApplicationFrame(soma, shapes);
		// Listener de l'algorithme et des actions du menu.
		AlgorithmManager listener = new AlgorithmManager(shapes, frame);
		// On ajoute l'AlgorithmeManager en �coute du menu de la frame.
		frame.addPropertyChangeListenerToMenus(listener);
	}
}
