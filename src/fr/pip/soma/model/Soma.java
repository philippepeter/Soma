/**
 * Copyright �2009 Philippe PETER.
 * Les sources qui constituent ce projet Soma de m�me que la documentation associ�e 
 * sont la propri�t� de leur auteur.
 * Je donne mon accord au site developpez.com pour l'utilisation de tout ou partie 
 * des sources et de la documentation de ce projet dans les pages developpez.com
 */
package fr.pip.soma.model;

import java.util.List;
import java.util.Vector;

/**
 * Puzzle � r�soudre, il est d�fini par une liste de points.
 * 
 * @author Philippe PETER.
 */
public class Soma {

	/** Les points du puzzle **/
	private Vector<Point3D> points = new Vector<Point3D>();

	public Soma(List<Point3D> pointsToAdd) {
		this.points.addAll(pointsToAdd);
	}

	public List<Point3D> getPoints() {
		return points;
	}

	/**
	 * Test si la figure est entierement contenue dans le puzzle.
	 * 
	 * @param shape
	 * @return
	 */
	public boolean shapeIsInSoma(Shape shape) {
		// Pour chaque point de la figure
		for (Point3D point : shape.getPoints()) {
			boolean isOutSide = true;
			// Pour chaque point du puzzle
			for (Point3D somaPoint : points) {
				// Si un des points du puzzle est egal au point test�, ce point
				// est bien dans le puzzle.
				if (somaPoint.equals(point)) {
					isOutSide = false;
				}
			}
			// Si un des point a �t� trouv� en dehors on renvoie false.
			if (isOutSide) {
				return false;
			}
		}
		// Si tous les points ont �t� trouv� dans le puzzle.
		return true;
	}

}
