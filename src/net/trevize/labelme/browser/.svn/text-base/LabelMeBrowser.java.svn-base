package net.trevize.labelme.browser;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

/**
 * 
 * 
 * @author Nicolas James <nicolas.james@gmail.com> [[http://njames.trevize.net]]
 * LabelMeBrowser.java - Apr 8, 2010
 */

public class LabelMeBrowser extends JPanel {

	private JTabbedPane jtp0;
	private KeywordAnalyzerIndexSearchPanel searchp0;
	private WhitespaceAnalyzerIndexSearchPanel searchp1;
	private DBXMLSearchPanel searchp2;

	public LabelMeBrowser() {
		init();
	}

	private void init() {
		setBorder(new EmptyBorder(3, 3, 3, 3));
		setLayout(new BorderLayout());

		jtp0 = new JTabbedPane();
		add(jtp0, BorderLayout.CENTER);

		searchp0 = new KeywordAnalyzerIndexSearchPanel();
		jtp0.add("Index0 (KeywordAnalyzer)", searchp0);

		searchp1 = new WhitespaceAnalyzerIndexSearchPanel();
		jtp0.add("Index1 (WhitespaceAnalyzer)", searchp1);

		searchp2 = new DBXMLSearchPanel();
		jtp0.add("DBXML index", searchp2);
	}

	public static void main(String[] args) {
		// setting the system look and feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		JFrame f = new JFrame("LabelMeBrowser");

		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		p.add(new LabelMeBrowser(), BorderLayout.CENTER);
		f.getContentPane().add(p);

		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(1024, 768);
		f.setLocationRelativeTo(null);
		f.setVisible(true);
	}

}
