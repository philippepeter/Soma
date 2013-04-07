/**
 * Copyright �2009 Philippe PETER.
 * Les sources qui constituent ce projet Soma de même que la documentation associée 
 * sont la propriété de leur auteur.
 * Je donne mon accord au site developpez.com pour l'utilisation de tout ou partie 
 * des sources et de la documentation de ce projet dans les pages developpez.com
 */
package fr.pip.soma.gui;

import java.awt.event.ActionEvent;
import java.io.File;
import java.text.DecimalFormat;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import fr.pip.soma.model.Soma;
import fr.pip.soma.parser.Parser;

/**
 * JMenuBar contenant les menus de l'application.
 * 
 * @author Philippe PETER.
 */
@SuppressWarnings("serial")
public class SomaMenu extends JMenuBar {

	/** String pour le propertyChangeEvent de recherche d'une solution **/
	public final static String RESOLVE1 = "RESOLVE1";
	/** String pour le propertyChangeEvent de recherche de toutes les solutions **/
	public final static String RESOLVE_ALL = "RESOLVE_ALL";
	/** String pour le propertyChangeEvent d'ouverture de fichier **/
	public final static String OPEN = "OPEN";
	/** String pour le propertyChangeEvent d'arret d'un calcul de recherche **/
	public final static String STOP = "STOP";
	/** JFileChooser pour choisir le puzzle **/
	private JFileChooser fileChooser = new JFileChooser(new File("data"));
	/** La JFrame principae de l'application **/
	private JFrame frame;
	/** Le panneau d'affichage 3D du puzzle **/
	private SomaDisplayer somaDisplayer;
	private JMenuItem stopItem;

	public SomaMenu(SomaDisplayer somaDisplayer, JFrame frame) {
		this.frame = frame;
		this.somaDisplayer = somaDisplayer;
		JMenu menu = new JMenu("Fichier");
		// Ajout des fichiers internes.
		menu.add(getFileOpenAllMenu());
		// Ajout du JFileChooser.
		menu.setMnemonic('F');
		menu.add(getFileOpenMenu());

		// Menu résoudre.
		JMenuItem resolveItem = new JMenuItem(new AbstractAction(
				"Rechercher une solution") {

			public void actionPerformed(ActionEvent e) {
				SomaMenu.this.firePropertyChange(RESOLVE1, null, null);
				stopItem.setEnabled(true);
			}
		});
		resolveItem.setMnemonic('R');
		menu.add(resolveItem);

		// Menu Rechercher toutes les solutions.
		JMenuItem resolveAllItem = new JMenuItem(new AbstractAction(
				"Rechercher toutes les solutions") {

			public void actionPerformed(ActionEvent e) {
				SomaMenu.this.firePropertyChange(RESOLVE_ALL, null, null);
				stopItem.setEnabled(true);
			}
		});
		resolveAllItem.setMnemonic('T');
		menu.add(resolveAllItem);

		// Menu arreter
		stopItem = new JMenuItem(new AbstractAction("Arreter") {

			public void actionPerformed(ActionEvent e) {
				SomaMenu.this.firePropertyChange(STOP, null, null);
			}
		});
		stopItem.setMnemonic('A');
		stopItem.setEnabled(false);
		menu.add(stopItem);

		// Menu Quitter.
		JMenuItem quitMenu = new JMenuItem();
		quitMenu.setAction(new AbstractAction("Quitter") {
			public void actionPerformed(ActionEvent e) {
				System.exit(-1);
			}
		});
		quitMenu.setMnemonic('Q');
		menu.add(quitMenu);

		this.add(menu);

		// Menu aide.
		JMenu menuHelp = new JMenu("Aide");
		menuHelp.setMnemonic('A');
		menuHelp.add(getMenuHelpItem());
		this.add(menuHelp);

	}

	/**
	 * Menu d'aide.
	 * @return
	 */
	private JMenuItem getMenuHelpItem() {
		JMenuItem item = new JMenuItem();
		item.setAction(new AbstractAction("A propos de Soma...") {

			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(frame, getHelpText(),
						"A propos de Soma...", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		item.setMnemonic('A');
		return item;
	}

	/**
	 * Label pour le menu a propos de.
	 * @return
	 */
	protected Object getHelpText() {
		JLabel label = new JLabel(
				"Cube de SOMA, concours Developpez.com 2009, par Philippe PETER.\n");
		label.setEnabled(true);

		return label;
	}

	/**
	 * JMenu qui a pour action d'ouvrir un JFileChooser.
	 */
	private JMenuItem getFileOpenMenu() {
		fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
		AbstractAction action = new AbstractAction("Ouvrir depuis un fichier") {

			public void actionPerformed(ActionEvent e) {
				int returnVal = fileChooser.showOpenDialog(frame);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					SomaMenu.this.setSoma(file.getName(), Parser.getSoma(file));
				}
			}
		};
		JMenuItem item = new JMenuItem(action);
		item.setMnemonic('d');
		return item;
	}

	/**
	 * Création d'un menu contenant tous les puzzle préchargés dans le package
	 * fr.pip.soma.parser.
	 * 
	 * @return
	 */
	private JMenu getFileOpenAllMenu() {
		JMenu menuOpen = new JMenu("Ouvrir");
		menuOpen.setMnemonic('O');
		final DecimalFormat formatter = new DecimalFormat("000");
		// On crée un menu par fichier.
		for (int i = 0; i < 21; i++) {
			final int index = i;
			final String indexString = formatter.format(index);
			JMenuItem item = new JMenuItem(new AbstractAction(indexString) {

				public void actionPerformed(ActionEvent e) {
					SomaMenu.this.setSoma(indexString, Parser
							.getSoma(indexString + ".txt"));
				}
			});
			menuOpen.add(item);
		}
		return menuOpen;
	}

	protected void setSoma(String name, Soma soma) {
		if (soma != null) {
			somaDisplayer.setSoma(soma);
			firePropertyChange(OPEN, name, soma);
		} else {
			somaDisplayer.setSoma(Parser.getSoma());
			JOptionPane.showMessageDialog(frame, "Ce puzzle est invalide",
					"Puzzle invalide", JOptionPane.ERROR_MESSAGE);
		}

	}

	public void algoFinished() {
		stopItem.setEnabled(false);
	}

}
