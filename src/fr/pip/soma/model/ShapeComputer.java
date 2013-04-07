/**
 * Copyright ©2009 Philippe PETER.
 * Les sources qui constituent ce projet Soma de même que la documentation associée 
 * sont la propriété de leur auteur.
 * Je donne mon accord au site developpez.com pour l'utilisation de tout ou partie 
 * des sources et de la documentation de ce projet dans les pages developpez.com
 */
package fr.pip.soma.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe utilitaire pour le calcul de figures avec rotations et translations.
 * 
 * @author Philippe PETER
 */
public final class ShapeComputer {

	/**
	 * Cette méthode calcule toutes les rotations possibles à partir d'une
	 * figure donnée.
	 * 
	 * @param originalShape
	 *            La figure d'origine.
	 * @return Une liste de figures contenant toutes les rotations possibles.
	 */
	public final static List<Shape> getAllPossibleRotatedShapes(
			Shape originalShape) {
		ArrayList<Shape> shapes = new ArrayList<Shape>();
		Shape xShape = originalShape;
		Shape yShape = null;
		Shape zShape = null;
		xShape = originalShape;
		// Sur chaque axe 4 rotations sont possibles: 0, Pi/2, Pi, 3Pi/2
		for (int i = 0; i < 4; i++) {
			xShape = createXRotatedShape(xShape);
			// Test si la figure existe deja.
			boolean alreadyContained = alreadyContained(shapes, xShape);
			if (!alreadyContained) {
				shapes.add(xShape);
				yShape = xShape;
			}
			if (yShape != null) {
				for (int j = 0; j < 4; j++) {
					yShape = createYRotatedShape(yShape);
					// Test si la figure existe deja.
					alreadyContained = alreadyContained(shapes, yShape);
					if (!alreadyContained) {
						shapes.add(yShape);
						zShape = yShape;
					}
					if (zShape != null) {
						for (int z = 0; z < 4; z++) {
							zShape = createZRotatedShape(zShape);
							// Test si la figure existe deja.
							alreadyContained = alreadyContained(shapes, zShape);
							if (!alreadyContained) {
								shapes.add(zShape);
							}
						}
					}
					zShape = null;
				}
			}
			yShape = null;
		}
		return shapes;

	}

	/**
	 * Calcul toutes les translations valides (sans points sortant du puzzle)
	 * d'une figure dans le puzzle.
	 * 
	 * @param soma
	 *            Le puzzle
	 * @param shapes
	 *            La liste des figures à translater.
	 * @return Une liste contenant toutes les translations de toutes les figures
	 *         passées en paramètres.
	 */
	public static List<Shape> getAllTranslatedPossibilitiesInSoma(Soma soma,
			List<Shape> shapes) {
		ArrayList<Shape> toReturn = new ArrayList<Shape>();

		// Pour chaque figure on calcule les translations en chaque point du puzzle.
		for (Shape shape : shapes) {
			for (Point3D point : soma.getPoints()) {
				Shape translatedShape = shape.getTranslatedShape(point);
				if (soma.shapeIsInSoma(translatedShape)) {
					toReturn.add(translatedShape);
				}
			}
		}

		return toReturn;
	}

	/**
	 * Test si une figure est deja contenue dans une liste de figures. Cette
	 * méthode marche aussi si les points ne sont pas dans le même ordre, d'ou
	 * la non utilisation de Shape.equals().
	 */
	private static boolean alreadyContained(List<Shape> shapes, Shape shape) {
		for (Shape shapeToTest : shapes) {
			if (pointsEquals(shapeToTest, shape)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Test si deux figures contiennent les meme points meme dans un ordre
	 * différent.
	 */
	private static boolean pointsEquals(Shape shape1, Shape shape2) {
		int size = shape1.getPoints().size();
		List<Point3D> s1Points = shape1.getPoints();
		List<Point3D> s2Points = shape2.getPoints();

		for (int i = 0; i < size; i++) {
			boolean contained = false;
			for (int j = 0; j < size; j++) {
				// Pour tout point de shape1, shape2 contient elle ce point
				if (s1Points.get(i).equals(s2Points.get(j))) {
					contained = true;
				}
			}
			// Si un des points de shape1 n'est pas dans shape2 on retourne
			// false.
			if (!contained) {
				return false;
			}
		}
		// Chaque point de shape1 est contenu dans shape2
		return true;
	}

	/**
	 * Calcul une rotation de figure sur l'axe des X : x->x y->-z z->y
	 * 
	 * @param originalShape
	 * @return
	 */
	public static Shape createXRotatedShape(Shape originalShape) {

		List<Point3D> points = originalShape.getPoints();
		List<Point3D> newPoints = new ArrayList<Point3D>();
		for (Point3D point : points) {
			int x = point.getX();
			int y = -point.getZ();
			int z = point.getY();
			newPoints.add(new Point3D(x, y, z));
		}
		Shape rotatedShape = new Shape(newPoints);
		return rotatedShape;
	}

	/**
	 * Calcul une rotation de figure sur l'axe des Y : x->-z y->y z->x
	 * 
	 * @param originalShape
	 * @return
	 */
	public static Shape createYRotatedShape(Shape originalShape) {

		List<Point3D> points = originalShape.getPoints();
		List<Point3D> newPoints = new ArrayList<Point3D>();
		for (Point3D point : points) {
			int x = -point.getZ();
			int y = point.getY();
			int z = point.getX();
			newPoints.add(new Point3D(x, y, z));
		}
		Shape rotatedShape = new Shape(newPoints);
		return rotatedShape;
	}

	/**
	 * Calcul une rotation de figure sur l'axe des Z : x->-y y->x z->z
	 * 
	 * @param originalShape
	 * @return
	 */
	public static Shape createZRotatedShape(Shape originalShape) {

		List<Point3D> points = originalShape.getPoints();
		List<Point3D> newPoints = new ArrayList<Point3D>();
		for (Point3D point : points) {
			int x = -point.getY();
			int y = point.getX();
			int z = point.getZ();
			newPoints.add(new Point3D(x, y, z));
		}
		Shape rotatedShape = new Shape(newPoints);
		return rotatedShape;
	}

	/**
	 * Supprime d'une liste les figures qui sont identiques en translation.
	 * 
	 * @param shapes
	 */
	public static void removeSameShapesAccordingToTranslation(List<Shape> shapes) {
		// pour chaque piece, on translate la piece sur chacun de ses points et
		// on vérifie qu'elle n'est pas egale a une autre piece.
		boolean doublonFound = true;
		// Tant qu'on a trouvé un double on le supprime et on recommence.
		while (doublonFound) {
			doublonFound = false;
			// Pour chaque figure
			for (int i = 0; i < shapes.size(); i++) {
				Shape shape = shapes.get(i);
				// Pour chaque point de la figure
				for (int j = 0; j < shape.getPoints().size(); j++) {
					// Création d'un figure en translation sur le point.
					Shape translatedShape = shape
							.getTranslatedShape(invertCoords(shape.getPoints()
									.get(j)));
					// Cette nouvelle figure est elle dans la liste?
					if (alreadyContained(shapes, translatedShape, i)) {
						doublonFound = true;
					}
				}
				// Si un doublon est trouvé on le supprime et on arrete la
				// boucle car la liste a changé.
				if (doublonFound) {
					shapes.remove(i);
					break;
				}
			}
		}

	}

	/**
	 * Inversion des coordonnées d'un point, utilisé pour la translation.
	 * 
	 * @param point3d
	 * @return
	 */
	private static Point3D invertCoords(Point3D point3d) {
		return new Point3D(-point3d.getX(), -point3d.getY(), -point3d.getZ());
	}

	/**
	 * Test si une figure est en double dans une liste.
	 * 
	 * @param shapes
	 * @param translatedShape
	 * @param indexNotToTest
	 * @return
	 */
	private static boolean alreadyContained(List<Shape> shapes,
			Shape translatedShape, int indexNotToTest) {
		for (int i = 0; i < shapes.size(); i++) {
			if (i != indexNotToTest
					&& pointsEquals(translatedShape, shapes.get(i))) {
				return true;
			}
		}
		return false;
	}

}
