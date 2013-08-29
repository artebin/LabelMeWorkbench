package net.trevize.labelme.stat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import net.trevize.labelme.jaxb.Annotation;

/**
 * This class creates a text file in each directory of the LabelMe dataset.
 * The file is a list of all object's names of all annotations files in each directory.
 * 
 * The file is formated like this:
 * path to the annotation file
 * annotation_{0}, annotation_{1}, ..., annotation_{n-1}
 * 
 * @author Nicolas James <nicolas.james@gmail.com> [[http://njames.trevize.net]]
 * CreateAnnotationsLists.java - Jan 21, 2009
 */

public class AnnotationsListsPerDirectoryExtractor {
	private final String FILE_NAME_ANNOTATIONS_LISTS = "annotations_list.txt";

	private String dataset_path;

	private JAXBContext jc;

	private Unmarshaller u;

	private XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();

	public AnnotationsListsPerDirectoryExtractor() {
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
	}

	public void processDataset() {
		System.out.println("CreateAnnotationsListsPerDirectory.processDataset begin.");

		File[] dl = new File(dataset_path).listFiles();

		for (File d : dl) {
			processDir(d.getName());
		}

		System.out.println("CreateAnnotationsListsPerDirectory.processDataset end.");
	}

	public void processDir(String dir_path) {
		System.out.println("\tentering " + dataset_path + "/" + dir_path);

		//creating the "annotations list" file.
		File fal = new File(dataset_path + "/" + dir_path + "/"
				+ FILE_NAME_ANNOTATIONS_LISTS);

		//if it ever exists, delete it.
		if (fal.exists()) {
			fal.delete();
		}

		File tmp = new File(dataset_path + "/" + dir_path + "/"
				+ "FTAnnotation.txt");
		if (tmp.exists()) {
			tmp.delete();
		}

		FileWriter fw = null;
		try {
			fw = new FileWriter(fal);
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		BufferedWriter bw = new BufferedWriter(fw);

		//for storing annotations of one annotation file.
		//we'll make one writing in the "annotations list" file per annotation file.
		StringBuffer sb = new StringBuffer();

		//iterate on all file on the current directory.
		File[] lf = new File(dataset_path + "/" + dir_path).listFiles();
		for (File f : lf) {

			//if the file is not an annotation file then go to the next iteration.
			if (!f.getName().endsWith(".xml")) {
				continue;
			}

			//add the filepath of the annotation file to the "annotations list" file.
			try {
				bw.append(f.getAbsolutePath() + "\n");
			} catch (IOException e2) {
				e2.printStackTrace();
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

			//iterate on all net.trevize.labelme.Object and get the Name.
			List<net.trevize.labelme.jaxb.Object> ol = a.getObject();
			for (net.trevize.labelme.jaxb.Object o : ol) {
				sb.append(o.getName().getContent() + ", ");
			}

			//write the buffer in the "annotations list" file.
			try {
				bw.write(sb.toString() + "\n\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		//close the "annotations list" file for the current directory.
		try {
			bw.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * the path to ~LabelMe/database/annotations
	 */
	public void setDatasetPath(String dataset_path) {
		this.dataset_path = dataset_path;
	}

	public static void main(String[] args) {
		AnnotationsListsPerDirectoryExtractor o = new AnnotationsListsPerDirectoryExtractor();
		o
				.setDatasetPath("/home/nicolas/public_html/LabelMe/database/annotations");
		o.processDataset();
	}
}
