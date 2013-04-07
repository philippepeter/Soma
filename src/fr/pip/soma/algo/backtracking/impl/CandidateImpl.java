/**
 * Copyright �2009 Philippe PETER.
 * Les sources qui constituent ce projet Soma de même que la documentation associée 
 * sont la propriété de leur auteur.
 * Je donne mon accord au site developpez.com pour l'utilisation de tout ou partie 
 * des sources et de la documentation de ce projet dans les pages developpez.com
 */
package fr.pip.soma.algo.backtracking.impl;

import java.util.List;

import fr.pip.soma.algo.backtracking.Candidate;
import fr.pip.soma.algo.backtracking.Data;
import fr.pip.soma.model.Shape;

/**
 * Implementation de l'interface Candidate.
 * Piece du puzzle contenant toutes ses possibilitées (Shape) dans le puzzle (rotations, translations).
 * 
 * @author Philippe PETER.
 * 
 */
public class CandidateImpl implements Candidate {

	/**
	 * Liste de toutes les possibilitées pour une figures donnée dans le puzzle
	 * (rotations et translations).
	 **/
	private List<Shape> allPossibilities;
	/** Index de la possibilité testée **/
	private int testedIndex = 0;
	/** Piece du puzzle suivante **/
	private CandidateImpl child;
	/** Index de la piece **/
	private int index;

	public CandidateImpl(List<Shape> allPossibilities, int index) {
		this.allPossibilities = allPossibilities;
		this.index = index;
	}

	public Candidate getFirstExtension(Data data, Candidate candidate) {
		if (child == null) {
			return null;
		} else {
			Candidate toReturn = child.getFirstSolution();
			// C'est data qui contient l'ensemble des Shape testées
			data.addTestedSolution(toReturn);
			return toReturn;
		}
	}

	/** Incrementer les solutions revient à incrementer l'index qui pointe sur allPossibilities **/
	protected Candidate getFirstSolution() {
		testedIndex = 0;
		return this;
	}

	public Candidate getNextCandidateOf(Data data, Candidate candidate) {
		// On incremente l'index
		testedIndex++;
		// Si on a tout parcouru
		if (testedIndex >= allPossibilities.size()) {
			data.removeTestedSolution(this);
			return null;
			//Sinon on dit a data de se mettre à jour avec la nouvelle solution.
		} else {
			data.updateTestedSolution(this);
			return this;
		}
	}

	public boolean isDone(Data data) {
		// On demande a data si le puzzle est terminé.
		return data.isDone(this.index);
	}

	/**
	 * Retourne la figure (Shape) actuelement testée
	 */
	public Shape getTestedShape() {
		if (testedIndex >= allPossibilities.size()) {
			return null;
		} else {
			return allPossibilities.get(testedIndex);
		}
	}

	/**
	 * @return the child
	 */
	public Candidate getChild() {
		return child;
	}

	/**
	 * @param child
	 *            the child to set
	 */
	public void setChild(Candidate child) {
		this.child = (CandidateImpl) child;
	}

	/**
	 * @return the allPossibilities
	 */
	public List<Shape> getAllPossibilities() {
		return allPossibilities;
	}

	/**
	 * @param allPossibilities
	 *            the allPossibilities to set
	 */
	public void setAllPossibilities(List<Shape> allPossibilities) {
		this.allPossibilities = allPossibilities;
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index
	 *            the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}

}
