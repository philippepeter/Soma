/**
 * Copyright �2009 Philippe PETER.
 * Les sources qui constituent ce projet Soma de même que la documentation associée 
 * sont la propriété de leur auteur.
 * Je donne mon accord au site developpez.com pour l'utilisation de tout ou partie 
 * des sources et de la documentation de ce projet dans les pages developpez.com
 */
package fr.pip.soma.algo.backtracking;

/**
 * Classe générique permettant d'executer un algorithme de retour sur trace (backtracking)
 * L'algorithme suivant est inspir� de sa d�finition sur wikipedia:
 * http://en.wikipedia.org/wiki/Backtracking
 * 
 * @author Philippe PETER
 * 
 */
public class BacktrackerResolver {

	/** Une solution est trouvée **/
	private boolean isDone;
	/** L'algorithme doit s'arreter si une solution est trouvée **/
	private boolean stopOnSolutionFound;
	/** boolean pour provoquer l'arret du calcul **/
	private boolean mustStop;

	public BacktrackerResolver(boolean stopOnSolutionFound) {
		this.stopOnSolutionFound = stopOnSolutionFound;
	}

	/**
	 * D�marre l'algorithme.
	 * @param data Les données d'entrée.
	 * @param root Le candidat parent.
	 * @return L'algorithme s'est terminé et a trouvé au moins une solution.
	 */
	public boolean resolve(Data data, Candidate root) {
		mustStop = false;
		backTrack(data, root);
		return isDone;
	}

	/**
	 * Méthode appelée recursivement pour appeler toutes les solutions d'un arbre de Candidate.
	 */
	private void backTrack(Data data, Candidate candidate) {
		// On arrete l'algorithme si une solution a été trouvée ou si l'arret est provoqué.
		if ((isDone && stopOnSolutionFound) || mustStop) {
				return;
		}
		// Si le puzzle n'est pas valide on remonte dans l'arbre.
		if (!data.isValid()) {
			return;
		}
		// Si cette feuille de l'arbre des solutions est valide le puzzle a une solution.
		if (candidate.isDone(data)) {
			isDone = true;
			data.solutionFound();
		}
		// On appelle la premiere extension du candidat courant.
		Candidate newCandidate = candidate.getFirstExtension(data, candidate);
		// On parcourt toutes les possibilités de cette extension
		while (newCandidate != null) {
			// Appel recursif
			backTrack(data, newCandidate);
			// On passe au candidat suivant.
			newCandidate = newCandidate.getNextCandidateOf(data, newCandidate);
		}
	}

	public void stop() {
		mustStop = true;
	}

	public boolean getStopped() {
		return mustStop;
	}

}
