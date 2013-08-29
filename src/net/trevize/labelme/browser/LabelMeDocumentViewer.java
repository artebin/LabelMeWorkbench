package net.trevize.labelme.browser;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * 
 * 
 * @author Nicolas James <nicolas.james@gmail.com> [[http://njames.trevize.net]]
 * LabelMeDocumentViewer.java - Apr 13, 2010
 */

public class LabelMeDocumentViewer implements WindowListener {

	private String annotation_path;
	private String image_path;

	private AnnotationFrame f_annotation;
	private ImageFrame f_image;

	private String sift_path;
	private String sifted_image_path;

	public LabelMeDocumentViewer(String image_path, String annotation_path) {
		this.image_path = image_path;
		this.annotation_path = annotation_path;

		init();
		f_annotation.setVisible(true);
		f_image.setVisible(true);
		f_image.getJmagine().updateScaleToComponentSizeIfLarger();
	}

	private void init() {
		initAnnotationFrame();
		initImageFrame();
		f_annotation.setF_image(f_image);
		f_annotation.addAllObjectsToJmagine();

		//setting the annotation frame.
		f_annotation.setSize(256, 512);
		f_annotation.setLocation(256, 256);
		f_annotation.addWindowListener(this);

		//setting the image frame.
		f_image.setSize(768, 768);
		f_image.setLocation(768, 256);
		f_image.addWindowListener(this);
	}

	private void initAnnotationFrame() {
		f_annotation = new AnnotationFrame(this);
	}

	private void initImageFrame() {
		f_image = new ImageFrame(this);
	}

	/***************************************************************************
	 * implementation of WindowListener.
	 **************************************************************************/

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		if (e.getSource() == f_annotation || e.getSource() == f_image) {
			f_annotation.dispose();
			f_image.dispose();
		}
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	/***************************************************************************
	 * a main method for testing purpose only.
	 **************************************************************************/

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

		LabelMeDocumentViewer docviewer = new LabelMeDocumentViewer(
				"/home/nicolas/MOntoMatch/MOntoMatch_experiment_6/all/05june05_static_street_boston/p1010738.jpg",
				"/home/nicolas/MOntoMatch/MOntoMatch_experiment_6/all/05june05_static_street_boston/p1010738.xml");

		String sift_path = "/home/nicolas/MOntoMatch/MOntoMatch_experiment_6/all/05june05_static_street_boston/p1010738.sift";
		String sifted_image_path = "/home/nicolas/MOntoMatch/MOntoMatch_experiment_6/all/05june05_static_street_boston/p1010738.pgm";

		docviewer.setSift_path(sift_path);
		docviewer.setSifted_image_path(sifted_image_path);
		docviewer.getF_annotation().addAllSIFTVectors();
	}

	/***************************************************************************
	 * getters and setters.
	 **************************************************************************/

	public String getAnnotation_path() {
		return annotation_path;
	}

	public void setAnnotation_path(String annotationPath) {
		annotation_path = annotationPath;
	}

	public String getImage_path() {
		return image_path;
	}

	public void setImage_path(String imagePath) {
		image_path = imagePath;
	}

	public AnnotationFrame getF_annotation() {
		return f_annotation;
	}

	public void setF_annotation(AnnotationFrame fAnnotation) {
		f_annotation = fAnnotation;
	}

	public ImageFrame getF_image() {
		return f_image;
	}

	public void setF_image(ImageFrame fImage) {
		f_image = fImage;
	}

	public String getSift_path() {
		return sift_path;
	}

	public void setSift_path(String siftPath) {
		sift_path = siftPath;
	}

	public String getSifted_image_path() {
		return sifted_image_path;
	}

	public void setSifted_image_path(String siftedImagePath) {
		sifted_image_path = siftedImagePath;
	}

}
