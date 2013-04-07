/**
 * Copyright ©2009 Philippe PETER.
 * Les sources qui constituent ce projet Soma de même que la documentation associée 
 * sont la propriété de leur auteur.
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
 * Implementation de l'interface Data. Cette classe stocke les figures testées
 * et permet de savoir si le puzzle est terminé.
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
	/** Le candidat parent pour démarrer la recherche de solution **/
	private Candidate root;
	/** Compteur de solutions trouvées **/
	private int solutionsCount = 0;
	/** Listener pour écouter l'état de l'algorithme **/
	private BacktrackerListener listener;
	/** Compteur pour connaitre le nombres de figures qui ne logent pas dans le puzzle **/
	private int impossibleShapesCount = 0;

	public DataImpl(Soma soma, List<Shape> shapes, BacktrackerListener listener) {
		this.shapes = shapes;
		this.listener = listener;
		// Le candidat root est le noeud de départ de l'arbre.
		root = new CandidateImpl(new ArrayList<Shape>(), -1);
		Candidate father = root;
		// On parcourt les pieces du puzzle et pour chacune on crée un Candidate
		for (int i = 0; i < shapes.size(); i++) {
			// On calcule toutes les rotations possibles de la piece du puzzle
			List<Shape> rotatedshapes = ShapeComputer
					.getAllPossibleRotatedShapes(shapes.get(i));
			// On supprime les pieces identiques
			ShapeComputer.removeSameShapesAccordingToTranslation(rotatedshapes);
			// On calcule ensuite parmis toutes les solutions precedement
			// calculées toutes les positions possibles dans le puzzle (celle ou
			// la piece ne sort pas du puzzle)
			List<Shape> allPossibilities = ShapeComputer
					.getAllTranslatedPossibilitiesInSoma(soma, rotatedshapes);
			// On crée le candidat avec toutes ses possibilitées.
			Candidate candidate = new CandidateImpl(allPossibilities, i);
			if(allPossibilities.size() == 0) {
				impossibleShapesCount++;
			}
			// On le lie à son père pour former un arbre.
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
		// Le puzzle est terminé si on travaille sur la derniere piece et que
		// cet assemblage est valide.
		return (index == shapes.size() - 1) && isValid();
	}

	public boolean isValid() {
		return isValid;
	}

	public void addTestedSolution(Candidate candidate) {
		// On récupere la piece testée
		Shape shape = ((CandidateImpl) candidate).getTestedShape();
		// On teste si elle n'empiete pas sur une autre et on met a jour isValid en fonction.
		testAddOf(shape);
		// On l'ajoute au tableau des solutions testées.
		solutions.add(shape);
		// On informe le listener qu'une possibilité est testée.
		listener.newTestedPossibilities(solutions);
	}

	public void removeTestedSolution(Candidate candidate) {
		// On enleve une figure testée.
		solutions.remove(solutions.size() - 1);
		isValid = true;
	}

	public void updateTestedSolution(Candidate candidate) {
		// On change la figure (Shape) courante.
		solutions.remove(solutions.size() - 1);
		Shape shape = ((CandidateImpl) candidate).getTestedShape();
		// On teste si elle n'empiete pas sur une autre et on met a jour isValid en fonction.
		testAddOf(shape);
		// On l'ajoute au tableau des solutions testées.
		solutions.add(shape);
		// On informe le listener qu'une possibilité est testée.
		listener.newTestedPossibilities(solutions);
	}

	/**
	 * On test si la figure (Shape) n'empiete pas sur une figure deja presente dans le puzzle.
	 * @param shape la figure à tester.
	 */
	private void testAddOf(Shape shape) {
		isValid = true;
		// On parcourt les points de la figure
		for (Point3D point : shape.getPoints()) {
			// Pour chaque point de la figure on parcourt les figures deja présentes dans le puzzle
			for (Shape testedShape : solutions) {
				// Si la figure deja presente dans le puzzle contient le point testé on arrete et on met isValid à false.
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
		// On incrémente le compteur de solutions.
		solutionsCount++;
		// On informe l'écouteur.
		listener.done(solutions, solutionsCount);
	}

}
