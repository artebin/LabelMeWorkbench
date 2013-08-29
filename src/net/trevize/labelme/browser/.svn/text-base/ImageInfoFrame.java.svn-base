package net.trevize.labelme.browser;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import net.trevize.tinker.SystemCommandHandler3;

/**
 * 
 * 
 * @author Nicolas James <nicolas.james@gmail.com> [[http://njames.trevize.net]]
 * ImageInfoFrame.java - Jul 9, 2010
 */

public class ImageInfoFrame extends JFrame {

	private JEditorPane editorp;

	public ImageInfoFrame(String image_path) {
		super();
		setTitle(image_path);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(768, 512);
		setLocationRelativeTo(null);

		editorp = new JEditorPane();
		editorp.setEditable(false);
		JScrollPane jsp = new JScrollPane();
		getContentPane().add(jsp);
		jsp.setViewportView(editorp);

		SystemCommandHandler3 sch3 = new SystemCommandHandler3();
		String command = "identify -verbose "
				+ SystemCommandHandler3.ESCAPED_DOUBLE_QUOTE_CHARACTER
				+ image_path
				+ SystemCommandHandler3.ESCAPED_DOUBLE_QUOTE_CHARACTER;
		sch3.exec(new String[] { command });

		editorp.setText(sch3.getStdOut().toString());
		setVisible(true);
	}

	public static void main(String[] args) {
		new ImageInfoFrame("/home/nicolas/datasets/4173.jpg");
	}

}
