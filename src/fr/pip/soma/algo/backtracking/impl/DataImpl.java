/**
 * Copyright �2009 Philippe PETER.
 * Les sources qui constituent ce projet Soma de m�me que la documentation associ�e 
 * sont la propri�t� de leur auteur.
 * Je donne mon accord au site developpez.com pour l'utilisation de tout ou partie 
 * des sources et de la documentation de ce projet dans les pages developpez.com
 */
package fr.pip.soma.algo.backtracking.impl;

import java.util.ArrayList;
import java.util.List;

import fr.pip.soma.algo.backtracking.Candidate;
import fr.pip.soma.algo.backtracking.Data;
import fr.pip.soma.model.Point3D;
import fr.pip.soma.model.Shape;
import fr.pip.soma.model.ShapeComputer;
import fr.pip.soma.model.Soma;

/**
 * Implementation de l'interface Data. Cette classe stocke les figures test�es
 * et permet de savoir si le puzzle est termin�.
 * 
 * @author Philippe PETER.
 * 
 */
public class DataImpl implements Data {

	/** Liste des figures du puzzle **/
	private List<Shape> shapes;
	/** Liste des solutions en cours de test **/
	private ArrayList<Shape> solutions = new ArrayList<Shape>();
	/**
	 * Le puzzle en cours est t il valide, par defaut on met isValid a true et a
	 * chaque ajout de piece on le recalcule.
	 **/
	private boolean isValid = true;
	/** Le candidat parent pour d�marrer la recherche de solution **/
	private Candidate root;
	/** Compteur de solutions trouv�es **/
	private int solutionsCount = 0;
	/** Listener pour �couter l'�tat de l'algorithme **/
	private BacktrackerListener listener;
	/** Compteur pour connaitre le nombres de figures qui ne logent pas dans le puzzle **/
	private int impossibleShapesCount = 0;

	public DataImpl(Soma soma, List<Shape> shapes, BacktrackerListener listener) {
		this.shapes = shapes;
		this.listener = listener;
		// Le candidat root est le noeud de d�part de l'arbre.
		root = new CandidateImpl(new ArrayList<Shape>(), -1);
		Candidate father = root;
		// On parcourt les pieces du puzzle et pour chacune on cr�e un Candidate
		for (int i = 0; i < shapes.size(); i++) {
			// On calcule toutes les rotations possibles de la piece du puzzle
			List<Shape> rotatedshapes = ShapeComputer
					.getAllPossibleRotatedShapes(shapes.get(i));
			// On supprime les pieces identiques
			ShapeComputer.removeSameShapesAccordingToTranslation(rotatedshapes);
			// On calcule ensuite parmis toutes les solutions precedement
			// calcul�es toutes les positions possibles dans le puzzle (celle ou
			// la piece ne sort pas du puzzle)
			List<Shape> allPossibilities = ShapeComputer
					.getAllTranslatedPossibilitiesInSoma(soma, rotatedshapes);
			// On cr�e le candidat avec toutes ses possibilit�es.
			Candidate candidate = new CandidateImpl(allPossibilities, i);
			if(allPossibilities.size() == 0) {
				impossibleShapesCount++;
			}
			// On le lie � son p�re pour former un arbre.
			father.setChild(candidate);
			father = candidate;
		}
	}
	
	public int getImpossibleShapesCount() {
		return impossibleShapesCount;
	}

	public Candidate getRoot() {
		return root;
	}

	public boolean isDone(int index) {
		// Le puzzle est termin� si on travaille sur la derniere piece et que
		// cet assemblage est valide.
		return (index == shapes.size() - 1) && isValid();
	}

	public boolean isValid() {
		return isValid;
	}

	public void addTestedSolution(Candidate candidate) {
		// On r�cupere la piece test�e
		Shape shape = ((CandidateImpl) candidate).getTestedShape();
		// On teste si elle n'empiete pas sur une autre et on met a jour isValid en fonction.
		testAddOf(shape);
		// On l'ajoute au tableau des solutions test�es.
		solutions.add(shape);
		// On informe le listener qu'une possibilit� est test�e.
		listener.newTestedPossibilities(solutions);
	}

	public void removeTestedSolution(Candidate candidate) {
		// On enleve une figure test�e.
		solutions.remove(solutions.size() - 1);
		isValid = true;
	}

	public void updateTestedSolution(Candidate candidate) {
		// On change la figure (Shape) courante.
		solutions.remove(solutions.size() - 1);
		Shape shape = ((CandidateImpl) candidate).getTestedShape();
		// On teste si elle n'empiete pas sur une autre et on met a jour isValid en fonction.
		testAddOf(shape);
		// On l'ajoute au tableau des solutions test�es.
		solutions.add(shape);
		// On informe le listener qu'une possibilit� est test�e.
		listener.newTestedPossibilities(solutions);
	}

	/**
	 * On test si la figure (Shape) n'empiete pas sur une figure deja presente dans le puzzle.
	 * @param shape la figure � tester.
	 */
	private void testAddOf(Shape shape) {
		isValid = true;
		// On parcourt les points de la figure
		for (Point3D point : shape.getPoints()) {
			// Pour chaque point de la figure on parcourt les figures deja pr�sentes dans le puzzle
			for (Shape testedShape : solutions) {
				// Si la figure deja presente dans le puzzle contient le point test� on arrete et on met isValid � false.
				if (testedShape.getPoints().contains(point)) {
					isValid = false;
					return;
				}
			}
		}
	}

	public List<Shape> getShapes() {
		return solutions;
	}

	public void solutionFound() {
		// On incr�mente le compteur de solutions.
		solutionsCount++;
		// On informe l'�couteur.
		listener.done(solutions, solutionsCount);
	}

}
