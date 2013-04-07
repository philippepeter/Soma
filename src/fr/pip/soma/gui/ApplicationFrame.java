/**
 * Copyright �2009 Philippe PETER.
 * Les sources qui constituent ce projet Soma de même que la documentation associée 
 * sont la propriété de leur auteur.
 * Je donne mon accord au site developpez.com pour l'utilisation de tout ou partie 
 * des sources et de la documentation de ce projet dans les pages developpez.com
 */
package fr.pip.soma.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import fr.pip.soma.model.Shape;
import fr.pip.soma.model.Soma;

/**
 * Frame principale de l'application. Contient les menus, la liste des pieces du
 * puzzle en 3D, le puzzle et un label pour afficher l'état de l'algorithme.
 * 
 * @author Philippe PETER.
 */
@SuppressWarnings("serial")
public class ApplicationFrame extends JFrame implements PropertyChangeListener{

	/** Les couleurs pour chaque pieces du puzzle **/
	private final static Color[] COLORS = { Color.RED, Color.GREEN, Color.BLUE,
			Color.YELLOW, Color.ORANGE, Color.CYAN, Color.MAGENTA };
	/** Les noms pour chaque pieces du puzzle **/
	private final static String[] NAMES = { "tricube � V �", "t�tracube � L �",
			"t�tracube � T �", "t�tracube � Z �", "t�tracube � A �",
			"t�tracube � B �", "t�tracube � P �" };

	/** Panneau d'affichage 3D du puzzle vide et des solutions testées **/
	private SomaDisplayer somaDisplayer;
	/** Label dynamique pour afficher l'état de l'algorithme en cours **/
	private JLabel logLabel = new JLabel();
	/** Les menus **/
	private SomaMenu menus;
	/** label du nom du puzzle **/
	private JLabel somaNameLabel;

	/**
	 * Construction de l'IHM.
	 * 
	 * @param initSoma
	 *            soma a charger à l'initialisation.
	 * @param shapes
	 *            pieces du puzzle.
	 * @param listener
	 *            listener écoutant les actions sur les menus.
	 */
	public ApplicationFrame(Soma initSoma, List<Shape> shapes) {
		// Fond noir pour les panneaux 3D et swing.
		Color backgroundColor = Color.BLACK;
		// Panneau d'affichage du puzzle et des solutions testées//
		somaDisplayer = new SomaDisplayer(initSoma, COLORS, backgroundColor,
				new Dimension(500, 500));

		// Panneau central contenant en haut (NORTH les pieces du puzzle et au
		// centre le puzzle //
		JPanel centralPanel = new JPanel(new BorderLayout());
		JPanel somaNamePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		somaNameLabel = new JLabel("000");
		somaNameLabel.setBackground(Color.BLACK);
		somaNamePanel.setBackground(Color.BLACK);
		somaNameLabel.setForeground(Color.WHITE);
		somaNamePanel.add(somaNameLabel);
		centralPanel.add(getShapesPanel(shapes, backgroundColor),
				BorderLayout.NORTH);
		centralPanel.add(somaDisplayer, BorderLayout.CENTER);
		centralPanel.add(somaNamePanel, BorderLayout.SOUTH);
		// Panneau contenant le label pour afficher l'état de l'algorithme.
		JPanel logPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		logPanel.add(logLabel);
		// Création des menus, delegué dans une classe pour plus de clareté.
		menus = new SomaMenu(somaDisplayer, this);
		menus.addPropertyChangeListener(this);
		// Mise en place de la frame.
		this.setTitle("Le cube de SOMA - Concours Developpez.com - par Philippe PETER");
		ImageIcon icon = new ImageIcon(ApplicationFrame.class
				.getResource("Soma.png"));
		this.setIconImage(icon.getImage());
		this.getContentPane().add(menus, BorderLayout.NORTH);
		this.getContentPane().add(centralPanel, BorderLayout.CENTER);
		this.getContentPane().add(logPanel, BorderLayout.SOUTH);
		this.pack();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// On centre la fenetre
		int width = (int) Toolkit.getDefaultToolkit().getScreenSize()
				.getWidth();
		int height = (int) Toolkit.getDefaultToolkit().getScreenSize()
				.getHeight();
		this.setLocation(width / 2 - this.getWidth() / 2, height / 2
				- this.getHeight() / 2);
		this.setVisible(true);
	}

	/**
	 * Création des panneau 3D pour chaque piece du puzzle.
	 * 
	 * @param shapes
	 *            Liste des pieces du puzzle.
	 * @param backgroundColor
	 *            Couleur de fond.
	 * @return Un JPanel contenant les panneaux 3D de chaque piece.
	 */
	private Component getShapesPanel(List<Shape> shapes, Color backgroundColor) {
		// Creation du panneau principal
		JPanel panel = new JPanel(new GridLayout(1, shapes.size(), 2, 2));
		panel.setBackground(backgroundColor);
		// Dimension pour chaque panneau
		Dimension dimension = new Dimension(100, 100);
		// Pour chaque piece on créé un ShapePanel
		for (int i = 0; i < shapes.size(); i++) {
			panel.add(new ShapePanel(shapes.get(i), COLORS[i], backgroundColor,
					NAMES[i], dimension));
		}
		return panel;
	}

	/**
	 * Affiches les figures passées en paramètre dans le puzzle.
	 * 
	 * @param solutions
	 */
	public void displayListOfShapes(List<Shape> solutions) {
		somaDisplayer.newTestedPossibility(solutions);
	}

	/**
	 * Mise à jour du label.
	 * 
	 * @param string
	 */
	public void setLogLabelText(String string) {
		logLabel.setText(string);
	}
	
	public void setSomaName(String name) {
		this.somaNameLabel.setText(name);
	}

	/**
	 * @return Le Puzzle en cours.
	 */
	public Soma getCurrentSoma() {
		return somaDisplayer.getSoma();
	}
	
	public void addPropertyChangeListenerToMenus(PropertyChangeListener listener) {
		// L'applicationFrame écoute les actions sur les menus.
		menus.addPropertyChangeListener(listener);
	}

	public void algoFinished(long result) {
		menus.algoFinished();
		// On ajoute le temps.
		String newText = logLabel.getText() + " terminé en " + result + " ms";
		logLabel.setText(newText);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		// On affiche le nom de la figure chargée.
		if(evt.getPropertyName().equals(SomaMenu.OPEN)) {
			somaNameLabel.setText((String) evt.getOldValue());
		}
	}

}
