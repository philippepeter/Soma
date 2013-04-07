/**
 * Copyright �2009 Philippe PETER.
 * Les sources qui constituent ce projet Soma de même que la documentation associée 
 * sont la propriété de leur auteur.
 * Je donne mon accord au site developpez.com pour l'utilisation de tout ou partie 
 * des sources et de la documentation de ce projet dans les pages developpez.com
 */
package fr.pip.soma.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GraphicsConfiguration;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Material;
import javax.media.j3d.Node;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.universe.SimpleUniverse;

import fr.pip.soma.model.Point3D;
import fr.pip.soma.model.Shape;

/**
 * Panneau d'affichage 3D via Java3D d'une figure (Shape). Il est possible de
 * faire tourner la figure avec la souris.
 * 
 * @author Philippe PETER
 */
@SuppressWarnings("serial")
public class ShapePanel extends JPanel {

	/** Dimension du panneau **/
	private Dimension size;

	public ShapePanel(Shape shape, Color color, Color backgroundColor,
			String name, Dimension size) {
		this.size = size;
		this.setLayout(new BorderLayout());
		this.setBackground(backgroundColor);
		// Création de l'univers JAVA3D
		GraphicsConfiguration config = SimpleUniverse
				.getPreferredConfiguration();
		// Création du Canvas3D
		Canvas3D canvas3D = new Canvas3D(config);
		this.add(canvas3D, BorderLayout.CENTER);
		SimpleUniverse simpleU = new SimpleUniverse(canvas3D);
		// Création de la scène 3D.
		BranchGroup scene = createSceneGraph(shape, color, simpleU);
		scene.compile();
		simpleU.getViewingPlatform().setNominalViewingTransform();

		// On applique la couleur de fond.
		Background bg = new Background();
		bg.setColor(new Color3f(backgroundColor));
		bg.setApplicationBounds(new BoundingSphere());
		BranchGroup sceneBackground = new BranchGroup();
		sceneBackground.addChild(bg);
		sceneBackground.addChild(scene);

		simpleU.addBranchGraph(sceneBackground);

		// Ajout du nom de la figure.
		JPanel panelLabel = new JPanel(new FlowLayout());
		panelLabel.setBackground(backgroundColor);
		JLabel label = new JLabel(name);
		label.setBackground(backgroundColor);
		label.setForeground(Color.WHITE);
		panelLabel.add(label);
		this.add(panelLabel, BorderLayout.SOUTH);

		// On déplace la caméra pour mieux centrer la figure.
		TransformGroup cameraTransform = simpleU.getViewingPlatform()
				.getMultiTransformGroup().getTransformGroup(0);
		Transform3D translate = new Transform3D();
		translate.setTranslation(new Vector3d(0, 0, 12));
		cameraTransform.setTransform(translate);
	}

	/**
	 * Création de la scène.
	 * 
	 * @param shape
	 *            La figure à afficher.
	 * @param color
	 *            La couleur à utiliser pour la figure.
	 * @param simpleU
	 *            L'univers JAVA3D
	 * @return Un Branchgroup JAVA3D contenant la figure.
	 */
	private BranchGroup createSceneGraph(Shape shape, Color color,
			SimpleUniverse simpleU) {
		BranchGroup objRoot = new BranchGroup();
		Transform3D rotate = new Transform3D();
		Transform3D tempRotate = new Transform3D();

		// Rotation utilisée par la souris
		rotate.rotX(Math.PI / 4.0d);
		tempRotate.rotY(Math.PI / 5.0d);
		rotate.mul(tempRotate);
		TransformGroup objRotate = new TransformGroup(rotate);
		objRotate.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		objRotate.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		MouseRotate myMouseRotate = new MouseRotate();
		myMouseRotate.setTransformGroup(objRotate);
		myMouseRotate.setSchedulingBounds(new BoundingSphere());

		// On crée la figure et on l'ajoute.
		objRotate.addChild(getShapeNode(shape, color));
		objRoot.addChild(objRotate);
		objRoot.addChild(myMouseRotate);

		// On applique une couleur et une lumiere au cube.
		BoundingSphere bounds2 = new BoundingSphere(new Point3d(0, 0.0, 50),
				100.0);
		Vector3f light1Direction = new Vector3f(0.0f, 0.0f, -1f);
		Color3f lightColor = new Color3f(0.6f, 0.6f, 0.6f);
		DirectionalLight light1 = new DirectionalLight(lightColor,
				light1Direction);
		light1.setInfluencingBounds(bounds2);
		objRoot.addChild(light1);
		AmbientLight ambientLightNode = new AmbientLight(lightColor);
		ambientLightNode.setInfluencingBounds(bounds2);
		objRoot.addChild(ambientLightNode);
		objRoot.compile();

		return objRoot;
	}

	/**
	 * Création du Node JAVA3D définissant la figure en 3D
	 * 
	 * @param shape
	 *            La figure à utiliser.
	 * @param color
	 *            La couleur à utiliser.
	 * @return Le Node JAVA3D contenant la figure.
	 */
	private Node getShapeNode(Shape shape, Color color) {
		BranchGroup shapeBranchGroup = new BranchGroup();
		// Pour chaque point de la figure on crée un cube.
		for (Point3D point : shape.getPoints()) {
			// Position du cube avec facteur 0.2 pour etre adapté au zoom.
			Transform3D translate = new Transform3D();
			// Le cube ayant une taille de 1 il faut translater les points d'un
			// facteur 2 auquel on ajoute 0.1 pour faire un leger espace
			// esthetique.
			Vector3d vector = new Vector3d(2.1 * point.getX(), 2.1 * point
					.getY(), -2.1 * point.getZ());
			translate.setTranslation(vector);

			// Application de la couleur.
			Appearance appearance = new Appearance();
			Material mat = new Material();
			mat.setAmbientColor(new Color3f(color));
			mat.setDiffuseColor(new Color3f(0.7f, 0.7f, 0.7f));
			mat.setSpecularColor(new Color3f(0.7f, 0.7f, 0.7f));
			appearance.setMaterial(mat);
			appearance.setColoringAttributes(new ColoringAttributes(
					new Color3f(color), ColoringAttributes.SHADE_GOURAUD));
			// Création du cube.
			Box box = new Box(1f, 1f, 1f, appearance);

			TransformGroup tg = new TransformGroup(translate);
			tg.addChild(box);
			shapeBranchGroup.addChild(tg);
		}

		// On translate la figure à son barycentre pour la rotation à la souris.
		Transform3D translateToBarycenter = new Transform3D();
		translateToBarycenter.setTranslation(getBarycenter(shape));
		TransformGroup tgToBarycenter = new TransformGroup(
				translateToBarycenter);
		tgToBarycenter.addChild(shapeBranchGroup);

		return tgToBarycenter;
	}

	/**
	 * Calcul du barycentre d'une figure.
	 */
	private Vector3f getBarycenter(Shape shape) {
		int x = 0;
		int y = 0;
		int z = 0;
		int size = shape.getPoints().size();
		for (int i = 0; i < size; i++) {
			Point3D point = shape.getPoints().get(i);
			x += point.getX();
			y += point.getY();
			z += point.getZ();
		}
		float valuex = (float) x / size;
		float valuey = (float) y / size;
		float valuez = (float) z / size;
		// Le cube ayant une taille de 1 il faut translater les points d'un
		// facteur 2 auquel on ajoute 0.1 pour faire un leger espace
		// esthetique.
		return new Vector3f(-2.1f * valuex, -2.1f * valuey, +2.1f * valuez);
	}

	@Override
	public Dimension getSize() {
		return size;
	}

	@Override
	public Dimension getPreferredSize() {
		return size;
	}

	@Override
	public Dimension getMinimumSize() {
		return size;
	}

	@Override
	public Dimension getMaximumSize() {
		return size;
	}

}
