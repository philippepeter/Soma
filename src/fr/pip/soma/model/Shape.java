/**
 * Copyright �2009 Philippe PETER.
 * Les sources qui constituent ce projet Soma de m�me que la documentation associ�e 
 * sont la propri�t� de leur auteur.
 * Je donne mon accord au site developpez.com pour l'utilisation de tout ou partie 
 * des sources et de la documentation de ce projet dans les pages developpez.com
 */
package fr.pip.soma.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe repr�sentant une figure compos�e d'une liste de points (Point3D).
 * La m�thode hashcode et equals sont redefinies
 * @author Philippe PETER.
 */
public class Shape {

	/** Les points de la figure. **/
	private ArrayList<Point3D> points = new ArrayList<Point3D>();

	/**
	 * Cr�� une nouvelle figure avec la liste des Point3D pass�e en param�tres.
	 */
	public Shape(List<Point3D> pointsToAdd) {
		points.addAll(pointsToAdd);
	}

	/**
	 * Cr�� une nouvelle figure avec le tableau de Point3D pass� en param�tres.
	 */
	public Shape(Point3D... newPoints) {
		for(Point3D point : newPoints) {
			points.add(point);
		}
	}
	
	/**
	 * Ajoute un Point3D � la liste.
	 * @param pointToAdd
	 */
	public void addPoint(Point3D pointToAdd) {
		points.add(pointToAdd);
	}
	/**
	 * Cr�e la m�me figure mais avec une translation vers le point pass� en param�tre.
	 * @param point Le point ou translater la figure.
	 * @return Une nouvelle figure translat�e.
	 */
	public Shape getTranslatedShape(Point3D point) {
		ArrayList<Point3D> newPoints = new ArrayList<Point3D>();
		for (Point3D pointToRead : points) {
			newPoints.add(new Point3D(pointToRead.getX() + point.getX(),
					pointToRead.getY() + point.getY(), pointToRead.getZ()
							+ point.getZ()));
		}
		return new Shape(newPoints);
	}

	/**
	 * @return the points
	 */
	public List<Point3D> getPoints() {
		return points;
	}

	@Override
	public String toString() {
		String toReturn = "";
		for (Point3D p : points) {
			toReturn += p + " ";
		}
		return toReturn;

	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((points == null) ? 0 : points.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Shape other = (Shape) obj;
		if (points == null) {
			if (other.points != null)
				return false;
		} else {
			boolean eq = true;
			for(Point3D p : points) {
				if(!other.points.contains(p)) {
					return false;
				}
			}
			return eq;
		}
		return true;
	}
	
}
