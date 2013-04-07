/**
 * Copyright ©2009 Philippe PETER.
 * Les sources qui constituent ce projet Soma de même que la documentation associée 
 * sont la propriété de leur auteur.
 * Je donne mon accord au site developpez.com pour l'utilisation de tout ou partie 
 * des sources et de la documentation de ce projet dans les pages developpez.com
 */
package fr.pip.soma.algo.backtracking;

/**
 * Interface qui définit un candidat à l'algorithme de retour sur trace.
 * Un candidat peut etre vu comme un noeud dans un arbre des solutions.
 * Cf. http://en.wikipedia.org/wiki/Backtracking
 * 
 * @author Philippe PETER.
 */
public interface Candidate {

	public boolean isDone(Data data);

	public Candidate getFirstExtension(Data data, Candidate candidate);

	public Candidate getNextCandidateOf(Data data, Candidate candidate);
	
	public void setChild(Candidate candidate);
	
	public int getIndex();

}
