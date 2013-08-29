package net.trevize.labelme.stat;

import java.awt.Point;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import net.trevize.labelme.jaxb.Annotation;
import net.trevize.labelme.jaxb.Pt;

import org.apache.commons.lang.mutable.MutableInt;

import com.seisw.util.geom.Poly;
import com.seisw.util.geom.PolyDefault;

/**
 * This class extracts from the LabelMe dataset the visual collocation
 * of annotated objects.
 * 
 * This class produce 2 files:
 * 		# labelme_visualObjectCollocation.txt: contains all collocations.
 * 		# labelme_visualObjectCollocation_list.txt: contains only the list
 * of classes implied in collocation and the number of collocation with another
 * annotation classe.
 * 
 * Use JAXB for mining the annotation dataset.
 * 
 * The collocation (polygons intersection) is computed with GPCJ, a Java version of the 
 * General Poly Clipper algorithm developed by Alan Murta.
 * See [[http://www.seisw.com/GPCJ/GPCJ.html]]
 * 
 * @author Nicolas James <nicolas.james@gmail.com> [[http://njames.trevize.net]]
 */

public class VisualObjectCollocationExtractor {
	//the resulted file.
	private FileWriter fw0;
	private BufferedWriter bw0;

	//for reading the dataset.
	private JAXBContext jc;
	private Unmarshaller u;
	private XMLInputFactory inputFactory;

	//the result of the search in the dataset.
	private HashMap<String, SingleAnnotation> annotations;

	private int instancesFoundTotal; //number of instances in the dataset.
	private int annotFile; //number of annotation files in the dataset (could be 46302 from my dataset...).
	private int instancesDeleted; //number of deleted instances in the dataset.
	private int visualCollocation; //number of visual collocation found in the dataset.

	private String dir_dataset_path;

	/**
	 * This method is the main method of the class.
	 * Entry method for extracting objects visual collocation from
	 * the LabelMe dataset. 
	 * @param dirpath the path to ~LabelMe/database/annotations
	 */
	public void processDataset(String dir_dataset_path) {
		System.out
				.println("ExtractVisualObjectCollocation.processDataset begin.");

		//creating file descriptors for the results.
		fw0 = null;
		try {
			fw0 = new FileWriter(dir_dataset_path + "/"
					+ "labelme_visualObjectCollocation.txt");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		bw0 = new BufferedWriter(fw0);

		//creating the JAXB context.
		jc = null;
		try {
			jc = JAXBContext.newInstance("net.trevize.labelme.jaxb");
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		//instantiate an unmarshaller.
		u = null;
		try {
			u = jc.createUnmarshaller();
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		//creating an XMLStreamReader factory.
		inputFactory = XMLInputFactory.newInstance();

		//instantiate the vocabulary HashMap.
		annotations = new HashMap<String, SingleAnnotation>();

		//initializing counters.
		instancesFoundTotal = 0;
		annotFile = 0;
		instancesDeleted = 0;
		visualCollocation = 0;

		//extracting information from the dataset.
		processDir(dir_dataset_path);

		//closing files.
		try {
			bw0.close();
			fw0.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//now, we write the resulted file.

		//sort annotations by instance multiplicity (decrease order).
		SingleAnnotation[] sortedByMultiplicityVocabulary = new SingleAnnotation[annotations
				.size()];
		annotations.values().toArray(sortedByMultiplicityVocabulary);
		Arrays.sort(sortedByMultiplicityVocabulary,
				new SingleAnnotationComparatorByMultiplicity());

		//creating file descriptors.
		fw0 = null;
		try {
			fw0 = new FileWriter(dir_dataset_path + "/"
					+ "labelme_visualObjectCollocation_list.txt");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		bw0 = new BufferedWriter(fw0);

		//writing the date.
		try {
			fw0.write("### " + GregorianCalendar.getInstance().getTime()
					+ ".\n\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fw0.write("### " + visualCollocation
					+ " visual collocation encountered.\n");
			fw0.write("### " + sortedByMultiplicityVocabulary.length
					+ " annotations classes used for these "
					+ visualCollocation + " visual collocations.\n\n");
		} catch (IOException e) {
			e.printStackTrace();
		}

		//writing the vocabulary.
		try {
			fw0.write("### vocabulary\n");

			//write the vocabulary to the file.
			for (int i = sortedByMultiplicityVocabulary.length - 1; i >= 0; --i) {
				fw0
						.write(sortedByMultiplicityVocabulary[i].annotation
								+ ","
								+ (Integer) sortedByMultiplicityVocabulary[i].multiplicity
										.getValue() + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		//closing files.
		try {
			bw0.close();
			fw0.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//notify the end of the extractVisuallyCollocatedAnnotations process.
		System.out
				.println("ExtractVisualObjectCollocation.processDataset end.");
	}

	/**
	 * Recursive directory listing function for processing each annotation
	 * files. 
	 * @param dirpath a directory in ~/LabelMe/database/annotations
	 */
	private void processDir(String dirpath) {
		System.out.println("\tprocessing " + dirpath);

		//listing the directory.
		File[] lf = new File(dirpath).listFiles();

		for (File f : lf) {
			if (f.isDirectory()) { //recursive call.
				try {
					processDir(f.getCanonicalPath());
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else { //process the annotation.
				if (f.getName().endsWith(".xml")) {
					try {
						processAnnotation(f.getCanonicalPath());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * Extracting information from a LabelMe annotation file. 
	 * @param path the path to the annotation.
	 */
	private void processAnnotation(String path) {
		//update the annotation file counter.
		annotFile++;

		//creating an XMLStreamReader for reading the annotation file. 
		XMLStreamReader xmlsr = null;
		try {
			xmlsr = inputFactory
					.createXMLStreamReader(new FileInputStream(path));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (XMLStreamException e1) {
			e1.printStackTrace();
		}

		//unmarshall the annotation.
		Annotation a = null;
		try {
			a = (Annotation) u.unmarshal(xmlsr);
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		//the list of polygons of the current annotation.
		ArrayList<Poly> list_poly = new ArrayList<Poly>();

		//the list of polygons' annotations of the current image annotation. 
		ArrayList<String> list_annot = new ArrayList<String>();

		int polyNum = 0; //the polygon id in the annotation file.

		//for each annotated object in the annotation file.
		for (net.trevize.labelme.jaxb.Object o : a.getObject()) {
			instancesFoundTotal++;

			//we only considers not deleted annotation.
			if (o.getDeleted().getContent().equals("1")) {
				instancesDeleted++;
				polyNum++; //we switch to the next polygon in the annotation file.
				continue;
			}

			//retrieving the annotation.
			String name = o.getName().getContent();

			//cleaning the free text annotation.
			name = trimpp(name);

			//retrieve a com.seisw.util.geom.Poly objeckthet from the points.
			List<Pt> points = o.getPolygon().getPt();
			Poly p = new PolyDefault();
			for (Pt pt : points) {
				p.add(new Point((int) Float.parseFloat(trimpp(pt.getX()
						.getContent())), (int) Float.parseFloat(trimpp(pt
						.getY().getContent()))));
			}

			/*
			 * compute intersections of this Poly with others Poly in the 
			 * annotation file.
			 */
			for (int i = 0; i < list_poly.size(); ++i) {
				//retrieve the i-th poly of the list.
				Poly pi = list_poly.get(i);

				//retrieve the intersection between pi and the current poly.
				Poly intersect = p.intersection(pi);

				//test polygons intersection.
				boolean polyintersect = (intersect != null && !intersect
						.isEmpty());
				if (!polyintersect) {
					continue;
				}

				//considering the constraint, what we do?
				if (polyintersect) {
					visualCollocation++;

					/*
					 * adding the polygons annotations to the list of used
					 * annotations in the corpus "annotations from visual
					 * objects that intersect".
					 */
					if (!annotations.keySet().contains(name)) {
						annotations.put(name, new SingleAnnotation(path, name,
								new MutableInt(1)));
					} else {
						annotations.get(name).multiplicity.increment();
					}

					if (!annotations.keySet().contains(list_annot.get(i))) {
						annotations.put(list_annot.get(i),
								new SingleAnnotation(path, list_annot.get(i),
										new MutableInt(1)));
					} else {
						annotations.get(list_annot.get(i)).multiplicity
								.increment();
					}

					String folder = trimpp(a.getFolder().getContent());

					//add data to the file.
					try {
						bw0.write("### " + folder + ","
								+ new File(path).getName() + "\n");
						bw0.write("poly-" + i + "-" + list_annot.get(i) + "\n");
						bw0.write("poly-" + polyNum + "-" + name + "\n");
						bw0.write("\n");
						bw0.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}//end for list_poly.

			list_annot.add(name);
			list_poly.add(p);

			polyNum++; //we'll go the next polygon in the annotation file.

		}//end for all object in the annotation file.

	}

	/**
	 * An augmented version of the String.trim() method which remove '\n', '\r'
	 * and '\t'.
	 * @param s
	 * @return
	 */
	private String trimpp(String s) {
		s = s.replaceAll("\r", " ");
		s = s.replaceAll("\n", " ");
		s = s.replaceAll("\t", " ");
		s = s.trim();

		/*
		 * It could be a problem to transform '\t' by '', it could be more
		 * interesting to tranform '\t' by ' ' and to transform '\s+' by 
		 * the String ' '. 
		 */
		s = s.replaceAll("\\s+", " ");

		return s;
	}

	public static void main(String[] args) {
		String dirpath = "/home/nicolas/public_html/LabelMe/database/annotations";

		VisualObjectCollocationExtractor o = new VisualObjectCollocationExtractor();

		o.processDataset(dirpath);
	}
}
