/**
 * Copyright �2009 Philippe PETER.
 * Les sources qui constituent ce projet Soma de même que la documentation associée 
 * sont la propriété de leur auteur.
 * Je donne mon accord au site developpez.com pour l'utilisation de tout ou partie 
 * des sources et de la documentation de ce projet dans les pages developpez.com
 */
package fr.pip.soma.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import fr.pip.soma.model.Point3D;
import fr.pip.soma.model.Shape;
import fr.pip.soma.model.Soma;

/**
 * Classe utilitaire pour charger un puzzle ou une liste de figures dans des
 * fichiers au format CSV.
 * 
 * @author Philippe PETER.
 * 
 */
public final class Parser {

	private final static String SEPARATOR = ",";
	private final static String EMPTY_STRING = "";
	private final static String WHITE = " ";

	/**
	 * Lit le fichier de puzzle par defaut.
	 * 
	 * @return
	 */
	public static final Soma getSoma() {
		// On recupere le fichier par defaut qui est dans le jar.
		return getSoma("000.txt");
	}
	
	/**
	 * Lit dans le jar un fichier dont le nom est fileName.
	 * @param fileName
	 * @return
	 */
	public static final Soma getSoma(String fileName) {
		// On recupere le fichier par defaut qui est dans le jar.
		InputStream inputStream = Parser.class.getResourceAsStream(fileName);
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));
		return getSomaFromBufferedReader(bufferedReader);
	}

	/**
	 * Lit le fichier passé en paramètre.
	 * 
	 * @param file
	 * @return
	 */
	public static Soma getSoma(File file) {
		FileReader fileInputStream = null;
		// Ouverture d'un flux de lecture sur le fichier.
		try {
			fileInputStream = new FileReader(file);
			return getSomaFromBufferedReader(new BufferedReader(fileInputStream));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				// En cas d'erreur on ferme le flux.
				fileInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * Utilise un BufferedReader pour lire un puzzle.
	 * @param bufferedReader
	 * @return
	 */
	private static Soma getSomaFromBufferedReader(BufferedReader bufferedReader) {
		try {
			ArrayList<Point3D> points = new ArrayList<Point3D>();
			String line = null;
			// Tant qu'il y a des lignes dans le fichier
			while ((line = bufferedReader.readLine()) != null) {
				String[] values = line.split(SEPARATOR);
				if (line.replaceAll(WHITE, EMPTY_STRING).equals(EMPTY_STRING)) {
					// On ne leve pas d'erreur pour les lignes vides.
				} else if (values.length != 3) {
					// Fichier invalide
					return null;
				} else {
					// On autorise les blancs.
					int x = Integer.parseInt(values[0].replaceAll(WHITE,
							EMPTY_STRING));
					int y = Integer.parseInt(values[1].replaceAll(WHITE,
							EMPTY_STRING));
					int z = Integer.parseInt(values[2].replaceAll(WHITE,
							EMPTY_STRING));
					Point3D point = new Point3D(x, y, z);
					points.add(point);
				}
			}
			Soma soma = new Soma(points);
			return soma;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * Lecture de la liste des figures.
	 * 
	 * @return
	 */
	public static final List<Shape> getShapes() {
		// Ouverture du fichier.
		InputStream inputStream = Parser.class.getResourceAsStream("shapes.txt");
		try {
			// Liste des points
			ArrayList<Point3D> points = new ArrayList<Point3D>();
			// Liste des figures
			ArrayList<Shape> shapes = new ArrayList<Shape>();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String line = null;
			// Tant qu'il y a une ligne dans le fichier.
			while ((line = reader.readLine()) != null) {
				// On decoupe la ligne lue
				String[] values = line.split(SEPARATOR);
				if (values.length != 3 && !values[0].equals(EMPTY_STRING)) {
					// Le fichier n'est pas valide.
					return null;
				} else {
					// Une ligne vide indique qu'on crée une nouvelle figure
					// (Shape)
					if (values[0].equals(EMPTY_STRING)) {
						shapes.add(new Shape(points));
						points = new ArrayList<Point3D>();
					} else {
						// Lecture des coordonnées.
						int x = Integer.parseInt(values[0]);
						int y = Integer.parseInt(values[1]);
						int z = Integer.parseInt(values[2]);
						Point3D point = new Point3D(x, y, z);
						points.add(point);
					}

				}
			}
			shapes.add(new Shape(points));
			return shapes;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}


}
