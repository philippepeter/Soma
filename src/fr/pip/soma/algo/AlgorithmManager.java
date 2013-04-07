/**
 * Copyright �2009 Philippe PETER.
 * Les sources qui constituent ce projet Soma de m�me que la documentation associ�e 
 * sont la propri�t� de leur auteur.
 * Je donne mon accord au site developpez.com pour l'utilisation de tout ou partie 
 * des sources et de la documentation de ce projet dans les pages developpez.com
 */
package fr.pip.soma.algo;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.JOptionPane;

import fr.pip.soma.algo.backtracking.BacktrackerResolver;
import fr.pip.soma.algo.backtracking.impl.BacktrackerListener;
import fr.pip.soma.algo.backtracking.impl.DataImpl;
import fr.pip.soma.gui.ApplicationFrame;
import fr.pip.soma.gui.SomaMenu;
import fr.pip.soma.model.Shape;
import fr.pip.soma.model.Soma;

/**
 * Manager d'algorithme, d�marre les algorithmes de resolution sur demande
 * (PropertyChangeListener) et notifie les changements d'�tat de l'algorithme �
 * l'ApplicationFrame.
 * 
 * @author Pip
 * 
 */
public class AlgorithmManager implements BacktrackerListener,
		PropertyChangeListener {
	private final static String SOLUTIONSFOUND = "Solutions trouv�es : ";
	private final static String ITERATION = " It�ration : ";
	private final static String DEFAULT_TEXT = "Choisissez un puzzle et lancez la r�solution via le menu, vous pouvez faire tourner les pi�ces et le puzzle avec la souris.";

	/** Pour gagner en performances on echantillone les iterations **/
	private int samplingRate = 100;
	/** Compteur d'iterations de l'algorithme **/
	private int iteration;
	/** Compteur de solutions **/
	private int solutionsIndex;
	/** Les pieces du puzzle **/
	private List<Shape> shapes;
	/** JFrame contenant tous les composants Swing de l'application **/
	private ApplicationFrame frame;
	/** Algorithme de calcul **/
	private BacktrackerResolver backTrackResolver;
	/** Compteur pour le calcul du temps **/
	private long beginTime = 0;
	/**
	 * Compteur du nombre de points dans les figures pour savoir si un puzzle
	 * est valide ou non
	 **/
	int shapesPointsCount = 0;

	public AlgorithmManager(List<Shape> shapes, ApplicationFrame frame) {
		this.shapes = shapes;
		this.frame = frame;
		this.frame.setLogLabelText(DEFAULT_TEXT);
		// On compte le nombre de cube dans toutes les figures.
		for (Shape shape : shapes) {
			shapesPointsCount += shape.getPoints().size();
		}
	}

	/**
	 * Cette m�thode est appel�e par l'algorithme de retour sur trace lorsqu'une
	 * solution est trouv�e.
	 */
	public void done(List<Shape> solutions, int solution) {
		// Une solution est trouv�e
		solutionsIndex++;
		// On affiche la solution
		frame.displayListOfShapes(solutions);
		// On met a jour le label.
		frame.setLogLabelText(SOLUTIONSFOUND + solutionsIndex + ITERATION
				+ iteration);
	}

	/**
	 * Cette m�thode est appel�e par l'algorithme de retour sur trace lorsqu'une
	 * solution est test�e.
	 */
	public void newTestedPossibilities(List<Shape> solutions) {
		// Une possibilit�e a �t� test�e
		iteration++;
		// On echantillonne le setText qui ralentit l'algorithme.
		if (iteration % samplingRate == 0) {
			frame.setLogLabelText(SOLUTIONSFOUND + solutionsIndex + ITERATION
					+ iteration);
		}

	}

	/**
	 * M�thode appel�e par les menus.
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		// Demande de trouver une solution. On d�marre un Thread.
		if (evt.getPropertyName().equals(SomaMenu.RESOLVE1)) {
			new Thread() {
				public void run() {
					resolveOne();
				};
			}.start();
			// Demande de trouver toutes les solutions. On d�marre un Thread.
		} else if (evt.getPropertyName().equals(SomaMenu.RESOLVE_ALL)) {
			new Thread() {
				public void run() {
					resolveAll();
				};
			}.start();
		} else if (evt.getPropertyName().equals(SomaMenu.STOP)) {
			backTrackResolver.stop();
		} else if (evt.getPropertyName().equals(SomaMenu.OPEN)) {
			// Si le nombre de cubes dans le puzzle est diff�rent du nombre de
			// cubes disponible le puzzle n'est pas valide.
			int somaPoints = ((Soma) evt.getNewValue()).getPoints().size();
			if (somaPoints != shapesPointsCount) {
				JOptionPane.showMessageDialog(frame, "Il y a " + somaPoints
						+ " points dans ce puzzle pour " + shapesPointsCount
						+ " points dans les figures.", "Puzzle invalide",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * Recherche de toutes les solutions d'un puzzle. On avertit l'utilisateur
	 * que le programme ne supprime pas les solutions sym�triques et que par
	 * consequent cette m�thode peut etre longue.
	 */
	protected void resolveAll() {
		if (frame.getCurrentSoma() != null) {
			JOptionPane
					.showMessageDialog(
							frame,
							"Les sym�tries ne sont pas g�r�es, la recherche de toutes les solutions peut etre long",
							"Attention", JOptionPane.WARNING_MESSAGE);

			// Le nombre de solutions est tr�s grand on echantillonne tous les
			// 100000.
			samplingRate = 100000;

			startAlgo(false);
		}

	}

	/**
	 * Recherche de la premiere solution du puzzle
	 */
	protected void resolveOne() {
		if (frame.getCurrentSoma() != null) {
			// On echantillonne les iterations tous les 100.
			samplingRate = 100;
			startAlgo(true);
		}

	}

	/**
	 * D�marrage de l'algorithme, si aucune solution n'est trouv�e on affiche un
	 * dialogue d'erreur.
	 * 
	 * @param stopOnSolutionFound
	 *            arrete l'algorithme en cas de solution trouv�e.
	 */
	private void startAlgo(boolean stopOnSolutionFound) {
		beginTime = System.currentTimeMillis();;
		solutionsIndex = 0;
		iteration = 0;
		frame.setLogLabelText("Initialisation de l'algorithme...");
		// Creation de l'algorithme.
		backTrackResolver = new BacktrackerResolver(stopOnSolutionFound);
		// Creation des donn�es.
		DataImpl data = new DataImpl(frame.getCurrentSoma(), shapes,
				AlgorithmManager.this);
		// Si des figures ne sont pas placable dans le puzzle on ne lance pas le
		// calcul.
		if (data.getImpossibleShapesCount() != 0) {
			JOptionPane.showMessageDialog(frame, data
					.getImpossibleShapesCount()
					+ " pi�ces ne rentrent pas dans le puzzle",
					"Aucune solution", JOptionPane.ERROR_MESSAGE);
			frame.setLogLabelText(DEFAULT_TEXT);
		} else {
			// D�marrage
			boolean hasSolution = backTrackResolver.resolve(data, data
					.getRoot());
			// Si il n'y a pas de solution et qu'on a pas manuellement stopp�
			// l'algorithme.
			if (!hasSolution && !backTrackResolver.getStopped()) {
				JOptionPane.showMessageDialog(frame,
						"Ce puzzle n'a pas de solution", "Aucune solution",
						JOptionPane.ERROR_MESSAGE);
				frame.setLogLabelText(DEFAULT_TEXT);
			}
		}
		long endTime = System.currentTimeMillis();
		frame.algoFinished(endTime - beginTime);

	}

}
