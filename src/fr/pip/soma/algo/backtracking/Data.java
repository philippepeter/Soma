/**
 * Copyright �2009 Philippe PETER.
 * Les sources qui constituent ce projet Soma de même que la documentation associée 
 * sont la propriété de leur auteur.
 * Je donne mon accord au site developpez.com pour l'utilisation de tout ou partie 
 * des sources et de la documentation de ce projet dans les pages developpez.com
 */
package fr.pip.soma.algo.backtracking;

import java.util.List;

import fr.pip.soma.model.Shape;

/**
 * Interface des donn�es d'un puzzle � resoudre avec l'algorithme de retour sur trace.
 * Cf. http://en.wikipedia.org/wiki/Backtracking
 * @author Philippe PETER.
 *
 */

public interface Data {

	public Candidate getRoot();

	public void addTestedSolution(Candidate candidate);

	public void removeTestedSolution(Candidate candidate);

	public boolean isDone(int index);
	
	public boolean isValid();

	public void updateTestedSolution(Candidate candidate);

	public List<Shape> getShapes();

	public void solutionFound();

}
