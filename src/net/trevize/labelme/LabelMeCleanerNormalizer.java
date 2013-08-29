package net.trevize.labelme;

import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import net.trevize.labelme.jaxb.Annotation;
import net.trevize.tinker.ImageUtils;

/**
 * The goal of this class is to make some cleaning and normalization on the 
 * LabelMe annotations and images dataset.
 * 
 * In a first time, the cleaning only consists in applying a trimpp() 
 * on the free-text annotation (an improved version of String.trim()).
 * Indeed, some fields are cleaned.
 * 
 * In a second time, a normalization is done on the images, in resizing it if needed.
 * This step is a little tricky, because objects polygon have to be updated.
 * 
 * There is also a filename normalization, (for the %2520, which could be
 *  tricky), but it only concern the annotation XML filename, the image filename
 * referenced in the XML annotation file doesn't change.
 * 
 * IMPORTANT: When an free-text annotation is empty (i.e. contains only
 * space characters, or \n or \r or \t for example) the Object node is deleted.
 * 
 * This class should be applied on the annotation dataset before the indexing
 * in XML database like MonetDB or Berkeley DB XML. 
 * (in order to make the querying faster).
 * 
 * @author Nicolas James <nicolas.james@gmail.com> [[http://njames.trevize.net]]
 * LabelMeCleanerNormalizer.java - Jan 16, 2009
 */

public class LabelMeCleanerNormalizer {

	private String labelme_annotation_path;
	private String labelme_image_path;

	private Dimension image_max_size;

	private JAXBContext jc;
	private Unmarshaller u;
	private Marshaller m;
	private XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();

	//for verifying the filename (MonetDB for example doesn't like URL-encoding).
	private String filename_filter = "^.*(%2520).*$";
	private Pattern filename_pattern = Pattern.compile(filename_filter);

	public LabelMeCleanerNormalizer() {
	}

	public void cleanLabelMeDataset(String labelme_annotation_path,
			String labelme_image_path, Dimension image_max_size) {
		LabelMeLogger.getBrowserLogger().log(Level.INFO,
				"cleanLabelMeDataset begin.");

		this.labelme_annotation_path = labelme_annotation_path;
		this.labelme_image_path = labelme_image_path;
		this.image_max_size = image_max_size;

		//create the JAXBContext.
		try {
			jc = JAXBContext.newInstance("net.trevize.labelme.jaxb");
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		//instantiate an Unmarshaller.
		try {
			u = jc.createUnmarshaller();
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		//instantiate a Marshaller.
		try {
			m = jc.createMarshaller();
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		_processDataset();

		LabelMeLogger.getBrowserLogger().log(Level.INFO,
				"cleanLabelMeDataset end.");
	}

	private void _processDataset() {
		LabelMeLogger.getBrowserLogger().log(Level.INFO,
				"_processDataset begin.");

		File[] dl = new File(labelme_annotation_path).listFiles();

		for (File d : dl) {
			_processDir(d.getName());
		}

		LabelMeLogger.getBrowserLogger()
				.log(Level.INFO, "_processDataset end.");
	}

	private void _processDir(String dir_path) {
		LabelMeLogger.getBrowserLogger().log(
				Level.INFO,
				"\tentering " + labelme_annotation_path + File.separator
						+ dir_path);

		File dir = new File(labelme_annotation_path + File.separator + dir_path);

		/*
		 * we have some .txt files in the dataset for storing statistics, 
		 * we skip them.
		 */
		if (!dir.isDirectory()) {
			return;
		}

		//iterate on all files on the current directory.
		File[] dir_children = dir.listFiles();

		//iterate on all XML annotation files in the current directory.
		for (File f : dir_children) {
			//if the file is not an annotation file then go to the next iteration.
			if (!f.getName().endsWith(".xml")) {
				continue;
			}

			//create an XMLStreamReader on the annotation file.
			XMLStreamReader xmlsr = null;
			try {
				xmlsr = xmlInputFactory
						.createXMLStreamReader(new FileInputStream(f));
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (XMLStreamException e1) {
				e1.printStackTrace();
			}

			//load the annotation.
			Annotation a = null;
			try {
				a = (Annotation) u.unmarshal(xmlsr);
			} catch (JAXBException e) {
				e.printStackTrace();
			}

			//clean content.
			cleanContent(a);

			//Normalize the XML element filename.
			//The effective renaming of the file is done later, because we have
			//loaded the XML annotation file, we have to finish updating before
			//renaming the file.
			if (filename_pattern.matcher(a.getFilename().getContent()).find()) {
				a.getFilename().setContent(
						a.getFilename().getContent().replace("%2520", "_"));
			}

			String image_path = labelme_image_path + File.separator
					+ a.getFolder().getContent() + File.separator
					+ a.getFilename().getContent();

			/*
			 * Resizing the image size if needed.
			 * The resizing is performed only if the image size is larger
			 * than the variable max_image_size.
			 * 
			 * Note than when reducing the image size, we have to update the polygon
			 * points. 
			 */
			if (image_max_size == null) {
				//ImageUtils.ImageMagick_convertImage(image_path, image_path);
			} else {
				Dimension image_size = ImageUtils.getImageSize(image_path);

				if (image_size.width > image_max_size.width
						|| image_size.height > image_max_size.height) {
					//resizing the image.
					ImageUtils.JAI_loadAndResizeImage(image_max_size.width,
							image_max_size.height, image_path, true);

					//updating the annotation file with the new size.
					Dimension image_new_size = ImageUtils
							.getImageSize(image_path);

					float scale_x = (float) image_new_size.width
							/ (float) image_size.width;
					float scale_y = (float) image_new_size.height
							/ (float) image_size.height;

					for (net.trevize.labelme.jaxb.Object o : a.getObject()) {
						for (net.trevize.labelme.jaxb.Pt pt : o.getPolygon()
								.getPt()) {
							pt.getX().setContent(
									""
											+ (int) (Integer.parseInt(pt.getX()
													.getContent()) * scale_x));

							pt.getY().setContent(
									""
											+ (int) (Integer.parseInt(pt.getY()
													.getContent()) * scale_y));
						}
					}
				}

			}

			//re-write the XML annotation file.
			try {
				m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
				FileOutputStream fos = new FileOutputStream(f.getAbsoluteFile());
				m.marshal(new JAXBElement<Annotation>(new QName("annotation"),
						Annotation.class, a), fos);
				fos.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (JAXBException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			//the XML annotation file is rewritten and close, now we can
			//rename the file if it is needed.

			//normalize the image filename.
			normalizeFileName(new File(image_path));

			//normalize the annotation filename.
			normalizeFileName(f);

		}//end for all file in the current directory.
	}

	private void cleanContent(Annotation a) {
		//cleaning content.
		if (a.getFilename() != null) {
			a.getFilename().setContent(trimpp(a.getFilename().getContent()));
		}

		if (a.getFolder() != null) {
			a.getFolder().setContent(trimpp(a.getFolder().getContent()));
		}

		if (a.getSource() != null & a.getSource().getSourceAnnotation() != null) {
			a.getSource()
					.getSourceAnnotation()
					.setContent(
							trimpp(a.getSource().getSourceAnnotation()
									.getContent()));
		}

		if (a.getSource() != null & a.getSource().getSourceImage() != null) {
			a.getSource()
					.getSourceImage()
					.setContent(
							trimpp(a.getSource().getSourceImage().getContent()));
		}

		List<net.trevize.labelme.jaxb.Object> a_objects = a.getObject();
		for (int i = 0; i < a_objects.size(); ++i) {
			net.trevize.labelme.jaxb.Object a_object = a.getObject().get(i);

			if (a_object.getDate() != null) {
				a_object.getDate().setContent(
						trimpp(a_object.getDate().getContent()));
			}

			if (a_object.getDeleted() != null) {
				a_object.getDeleted().setContent(
						trimpp(a_object.getDeleted().getContent()));
			}

			if (a_object.getId() != null) {
				a_object.getId().setContent(
						trimpp(a_object.getId().getContent()));
			}

			if (a_object.getName() != null) {
				a_object.getName().setContent(
						trimpp(a_object.getName().getContent()));
			}

			if (a_object.getVerified() != null) {
				a_object.getVerified().setContent(
						trimpp(a_object.getVerified().getContent()));
			}

			if (a_object.getViewpoint() != null
					&& a_object.getViewpoint().getAzimuth() != null) {
				a_object.getViewpoint()
						.getAzimuth()
						.setContent(
								trimpp(a_object.getViewpoint().getAzimuth()
										.getContent()));
			}

			if (a_object.getPolygon() != null
					&& a_object.getPolygon().getUsername() != null) {
				a_object.getPolygon()
						.getUsername()
						.setContent(
								trimpp(a_object.getPolygon().getUsername()
										.getContent()));
			}

			if (a_object.getPolygon() != null
					&& a_object.getPolygon().getPt() != null) {
				List<net.trevize.labelme.jaxb.Pt> a_pts = a_object.getPolygon()
						.getPt();
				for (int j = 0; j < a_pts.size(); ++j) {
					net.trevize.labelme.jaxb.Pt a_pt = a_pts.get(j);

					if (a_pt.getX() != null) {
						a_pt.getX()
								.setContent(trimpp(a_pt.getX().getContent()));
					}

					if (a_pt.getY() != null) {
						a_pt.getY()
								.setContent(trimpp(a_pt.getY().getContent()));
					}
				}
			}
		}
	}

	/**
	 * Some files in the dataset could have name using URL-encoding, this
	 * method renames these files.
	 * For instance, replace %2520 by '_'.
	 * (the double URL-encoding is tricky for some XML database).
	 * 
	 * @param f the file to rename
	 * @return a File object for the renamed file
	 */
	private File normalizeFileName(File f) {
		String name = f.getName();
		if (filename_pattern.matcher(name).find()) {
			File res = new File(f.getParent() + "/"
					+ name.replace("%2520", "_"));
			f.renameTo(res);
			return res;
		} else {
			return f;
		}
	}

	/**
	 * An augmented version of the String.trim() method which remove '\n', '\r'
	 * and '\t'.
	 * @param s
	 * @return a corrected String based on the String given in parameter.
	 */
	private String trimpp(String s) {
		/*
		 * analyzing between the content of the string.
		 */
		s = s.replaceAll("\r", " ");
		s = s.replaceAll("\n", " ");
		s = s.replaceAll("\t", " ");

		/*
		 * String.trim() removes \n, \r and \t from the head and the queue
		 * of a string.
		 */
		s = s.trim();

		/*
		 * It could be a problem to transform '\t' by '', it could be more
		 * interesting to tranform '\t' by ' ' and to transform '\s+' by 
		 * the String ' '. 
		 */
		s = s.replaceAll("\\s+", " ");

		return s;
	}

	public static void main(String args[]) {
		String labelme_annotation_path = LabelMeWorkbenchProperties
				.getLabelMeAnnotationsPath();
		String labelme_image_path = LabelMeWorkbenchProperties
				.getLabelMeImagesPath();
		Dimension image_max_size = null;

		LabelMeCleanerNormalizer dc = new LabelMeCleanerNormalizer();
		dc.cleanLabelMeDataset(labelme_annotation_path, labelme_image_path,
				image_max_size);
	}

}
