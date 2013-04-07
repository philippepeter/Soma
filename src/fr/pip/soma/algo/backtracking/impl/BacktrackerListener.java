/**
 * Copyright �2009 Philippe PETER.
 * Les sources qui constituent ce projet Soma de m�me que la documentation associ�e 
 * sont la propri�t� de leur auteur.
 * Je donne mon accord au site developpez.com pour l'utilisation de tout ou partie 
 * des sources et de la documentation de ce projet dans les pages developpez.com
 */
package fr.pip.soma.algo.backtracking.impl;

import java.util.List;

import fr.pip.soma.model.Shape;

/**
 * Interface d'�coute de l'�tat de l'algorithme de retour sur traces.
 * @author Philippe PETER.
 *
 */
public interface BacktrackerListener {

	/**
	 * Une solution est trouv�e.
	 * @param solutions liste des figures composant la solution.
	 * @param solution l'index de la solution.
	 */
	public void done(List<Shape> solutions, int solution);

	/**
	 * Une solution est test�e
	 * @param solutions liste des figures de la solution test�e.
	 */
	public void newTestedPossibilities(List<Shape> solutions);

}
