package net.trevize.labelme.browser;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import net.trevize.jmagine.Jmagine;

/**
 * 
 * 
 * @author Nicolas James <nicolas.james@gmail.com> [[http://njames.trevize.net]]
 * ImageFrame.java - Apr 13, 2010
 */

public class ImageFrame extends JFrame {

	private LabelMeDocumentViewer docviewer;
	private String image_path;
	private String annotation_path;

	//classical swing components.
	private JPanel p0;
	private Jmagine jmagine;

	public ImageFrame(LabelMeDocumentViewer docviewer) {
		this.docviewer = docviewer;
		this.image_path = docviewer.getImage_path();
		this.annotation_path = docviewer.getAnnotation_path();

		setTitle("View image: " + image_path);

		init();
	}

	private void init() {
		p0 = new JPanel();
		p0.setLayout(new BorderLayout());
		getContentPane().add(p0);

		jmagine = new Jmagine(image_path);
		p0.add(jmagine, BorderLayout.CENTER);
	}

	/***************************************************************************
	 * getters and setters.
	 **************************************************************************/

	public LabelMeDocumentViewer getDocviewer() {
		return docviewer;
	}

	public void setDocviewer(LabelMeDocumentViewer docviewer) {
		this.docviewer = docviewer;
	}

	public String getImage_path() {
		return image_path;
	}

	public void setImage_path(String imagePath) {
		image_path = imagePath;
	}

	public String getAnnotation_path() {
		return annotation_path;
	}

	public void setAnnotation_path(String annotationPath) {
		annotation_path = annotationPath;
	}

	public Jmagine getJmagine() {
		return jmagine;
	}

	public void setJmagine(Jmagine jmagine) {
		this.jmagine = jmagine;
	}

}
