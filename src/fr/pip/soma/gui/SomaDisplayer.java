/**
 * Copyright ©2009 Philippe PETER.
 * Les sources qui constituent ce projet Soma de même que la documentation associée 
 * sont la propriété de leur auteur.
 * Je donne mon accord au site developpez.com pour l'utilisation de tout ou partie 
 * des sources et de la documentation de ce projet dans les pages developpez.com
 */
package fr.pip.soma.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.util.HashMap;
import java.util.List;

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
import javax.media.j3d.TransparencyAttributes;
import javax.swing.JFrame;
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
import fr.pip.soma.model.Soma;
import fr.pip.soma.parser.Parser;

/**
 * Panneau dynamique d'affichage du puzzle et de ses éventuelles solutions. Le
 * puzzle est affiché en transparence et ses solutions sont des pieces opaques
 * de couleur. Le tout peut etre tourné à la souris.
 * 
 * @author Philippe PETER.
 */
@SuppressWarnings("serial")
public class SomaDisplayer extends JPanel {

	/** Variable contenant les figures afin d'appeler add et/ou remove **/
	private TransformGroup shapeGroup;
	/** Listes des couleurs pour les figures **/
	private Color[] colors;
	/** Dimensions du panneau **/
	private Dimension size;
	/**
	 * HashMap contenant les figures et leur noeud Java3D correspondant,
	 * utilise pour faire des ajout/suppression de figures
	 **/
	private HashMap<Shape, Node> nodesMap = new HashMap<Shape, Node>();
	/** Puzzle courant **/
	private Soma soma;
	/** Branchgroup contenant le noeud Java3D du puzzle **/
	private BranchGroup somaBranchGroup;
	/** Groupe principal contenant le puzzle et les pieces **/
	private TransformGroup objRotate;

	public SomaDisplayer(Soma soma, Color[] colors, Color backgroundColor,
			Dimension size) {
		this.size = size;
		this.soma = soma;
		this.colors = colors;
		this.setLayout(new BorderLayout());
		// Création de l'univers JAVA3D
		GraphicsConfiguration config = SimpleUniverse
				.getPreferredConfiguration();
		// Création du Canvas3D
		Canvas3D canvas3D = new Canvas3D(config);
		this.add(canvas3D, BorderLayout.CENTER);
		// Création de la scène 3D.
		SimpleUniverse simpleU = new SimpleUniverse(canvas3D);
		BranchGroup scene = createSceneGraph(simpleU);
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

		// On déplace la caméra pour mieux centrer la figure.
		TransformGroup cameraTransform = simpleU.getViewingPlatform()
				.getMultiTransformGroup().getTransformGroup(0);
		Transform3D translate = new Transform3D();
		translate.setTranslation(new Vector3d(0, 0, 25));
		cameraTransform.setTransform(translate);
	}

	/**
	 * Creation de la scene JAVA3D
	 */
	private BranchGroup createSceneGraph(SimpleUniverse simpleU) {
		BranchGroup objRoot = new BranchGroup();
		Transform3D rotate = new Transform3D();
		Transform3D tempRotate = new Transform3D();

		// Rotation utilisée par la souris
		rotate.rotX(Math.PI / 4.0d);
		tempRotate.rotY(Math.PI / 5.0d);
		rotate.mul(tempRotate);
		objRotate = new TransformGroup(rotate);
		objRotate.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		objRotate.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		objRotate.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
		objRotate.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
		MouseRotate myMouseRotate = new MouseRotate();
		myMouseRotate.setTransformGroup(objRotate);
		myMouseRotate.setSchedulingBounds(new BoundingSphere());

		// On crée le puzzle et on l'ajoute.
		somaBranchGroup = new BranchGroup();
		somaBranchGroup.setCapability(BranchGroup.ALLOW_DETACH);
		if(soma != null) {
			somaBranchGroup.addChild(getSomaNode());
		}
		objRotate.addChild(somaBranchGroup);
		shapeGroup = objRotate;
		objRoot.addChild(objRotate);
		objRoot.addChild(myMouseRotate);

		// On applique une couleur et une lumiere au puzzle.
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
	 * Création du Noeud Java3d du puzzle.
	 * 
	 * @return
	 */
	private Node getSomaNode() {
		BranchGroup shapeBranchGroup = new BranchGroup();
		// Pour chaque point du puzzle on crée un cube.
		for (Point3D point : soma.getPoints()) {
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
			mat.setAmbientColor(new Color3f(Color.BLUE));
			mat.setDiffuseColor(0.7f, 0.7f, 0.7f, 0.9f);
			mat.setSpecularColor(new Color3f(0.7f, 0.7f, 0.7f));
			appearance.setMaterial(mat);
			appearance.setColoringAttributes(new ColoringAttributes(
					new Color3f(Color.BLUE), ColoringAttributes.SHADE_GOURAUD));
			appearance.setTransparencyAttributes(new TransparencyAttributes(
					TransparencyAttributes.FASTEST, 0.8f));
			Box box = new Box(1f, 1f, 1f, appearance);
			TransformGroup tg = new TransformGroup(translate);
			tg.addChild(box);
			shapeBranchGroup.addChild(tg);
		}

		// On translate la figure à son barycentre pour la rotation à la souris.
		Transform3D translateToBarycenter = new Transform3D();
		translateToBarycenter.setTranslation(getBarycenter(soma.getPoints()));
		TransformGroup tgToBarycenter = new TransformGroup(
				translateToBarycenter);
		tgToBarycenter.addChild(shapeBranchGroup);
		return tgToBarycenter;
	}
	
	/**
	 * Calcul du barycentre du puzzle
	 */
	private Vector3f getBarycenter(List<Point3D> points) {
		int x = 0;
		int y = 0;
		int z = 0;
		int size = points.size();
		for (int i = 0; i < size; i++) {
			Point3D point = points.get(i);
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

	/**
	 * Créé un Noeud Java3D pour une figure donnée.
	 */
	private Node getShapeNode(Shape shape, Color color) {
		BranchGroup shapeBranchGroup = new BranchGroup();
		shapeBranchGroup.setCapability(BranchGroup.ALLOW_DETACH);
		// Pour chaque point de la figure on crée un cube.
		for (Point3D point : shape.getPoints()) {
			Transform3D translate = new Transform3D();
			// Le cube ayant une taille de 1 il faut translater les points d'un
			// facteur 2 auquel on ajoute 0.1 pour faire un leger espace
			// esthetique.
			Vector3d vector = new Vector3d(2.1 * point.getX(), 2.1 * point
					.getY(), -2.1 * point.getZ());
			translate.setTranslation(vector);

			// Creation de la couleur et de la texture.
			Appearance appearance = new Appearance();
			Material mat = new Material();
			mat.setAmbientColor(new Color3f(color));
			mat.setDiffuseColor(new Color3f(0.7f, 0.7f, 0.7f));
			mat.setSpecularColor(new Color3f(0.7f, 0.7f, 0.7f));
			appearance.setMaterial(mat);
			appearance.setColoringAttributes(new ColoringAttributes(
					new Color3f(color), ColoringAttributes.SHADE_GOURAUD));
			Box box = new Box(1f, 1f, 1f, appearance);

			TransformGroup tg = new TransformGroup(translate);
			tg.addChild(box);
			shapeBranchGroup.addChild(tg);
		}

		return shapeBranchGroup;
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

	/**
	 * Affichage d'un nouvel ensemble de figures (Shape)
	 * 
	 * @param shapes
	 */
	public void newTestedPossibility(List<Shape> shapes) {
		for (Node node : nodesMap.values()) {
			shapeGroup.removeChild(node);
		}
		nodesMap.clear();

		int i = 0;

		for (Shape shape : shapes) {
			Node node = getShapeNode(shape, colors[i]);
			Transform3D translateToBarycenter = new Transform3D();
			translateToBarycenter.setTranslation(getBarycenter(soma.getPoints()));
			TransformGroup tgToBarycenter = new TransformGroup(
					translateToBarycenter);
			tgToBarycenter.addChild(node);
			BranchGroup bg = new BranchGroup();
			bg.setCapability(BranchGroup.ALLOW_DETACH);
			bg.addChild(tgToBarycenter);
			nodesMap.put(shape, bg);
			shapeGroup.addChild(bg);
			i++;
		}
	}

	public void setSoma(Soma soma) {
		for (Node node : nodesMap.values()) {
			shapeGroup.removeChild(node);
		}
		nodesMap.clear();
		this.soma = soma;
		objRotate.removeChild(somaBranchGroup);
		if (soma != null) {
			somaBranchGroup = new BranchGroup();
			somaBranchGroup.setCapability(BranchGroup.ALLOW_DETACH);
			somaBranchGroup.addChild(getSomaNode());
			objRotate.addChild(somaBranchGroup);
		}

	}

	/**
	 * @return the soma
	 */
	public Soma getSoma() {
		return soma;
	}

	/** Test **/
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		Color[] colors = { Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW,
				Color.ORANGE, Color.CYAN, Color.MAGENTA };

		SomaDisplayer somaDisplayer = new SomaDisplayer(Parser.getSoma(),
				colors, Color.BLACK, new Dimension(500, 500));
		frame.getContentPane().add(somaDisplayer);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		somaDisplayer.shapeAdded(Parser.getShapes().get(0), 0);
	}

	/**
	 * Permet de retirer une figure (Shape), non utilisé
	 */
	public void shapeAdded(Shape shape, int index) {
		Node node = getShapeNode(shape, colors[index]);
		Transform3D translateToBarycenter = new Transform3D();
		translateToBarycenter.setTranslation(new Vector3d(-2.1, -2.1, 2.1));
		TransformGroup tgToBarycenter = new TransformGroup(
				translateToBarycenter);
		tgToBarycenter.addChild(node);
		BranchGroup bg = new BranchGroup();
		bg.setCapability(BranchGroup.ALLOW_DETACH);
		bg.addChild(tgToBarycenter);
		nodesMap.put(shape, bg);
		shapeGroup.addChild(bg);
	}

	/**
	 * Permet de retirer une figure (Shape), non utilisé 
	 */
	public void shapeRemoved(Shape shape) {
		Node toRemove = nodesMap.remove(shape);
		if (toRemove != null) {
			shapeGroup.removeChild(toRemove);
		}

	}

}
