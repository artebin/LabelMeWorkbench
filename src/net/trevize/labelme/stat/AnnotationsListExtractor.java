package net.trevize.labelme.stat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import net.trevize.labelme.jaxb.Annotation;

import org.apache.commons.lang.mutable.MutableInt;

/**
 * This class extract from the LabelMe annotations dataset the list of all
 * annotations classes with their instances numbers.
 * 
 * Instances that are marked deleted are not taken in consideration.
 * 
 * The processDataset(String) method produces 3 files in ~LabelMe/database/annotations:
 * 		# labelme_annotations_list.ser: a serialization of an object 
 * HashMap<String, SingleAnnotation>, where the keys are annotations.
 * 		# labelme_annotations_list.txt: a text-flavor serialization of the
 * previous object, contains only annotations classes listed by instances numbers in
 * LabelMe.
 * 		# labelme_annotations_cooccurrences.txt: a text-file which contains all annotations
 * class, each of them is associated to a set of annotations classes, it's the 
 * co-occurrence information. 
 * 
 * @author Nicolas James <nicolas.james@gmail.com> [[http://njames.trevize.net]]
 * CreateDatasetAnnotationsList.java - Jan 21, 2009
 */

public class AnnotationsListExtractor {

	private String pathToAnnotations = "/home/nicolas/public_html/LabelMe/database/annotations";

	/**
	 * The annotations object.
	 * keys are free-text annotations, values are multiplicity i.e. the usage
	 * of the annotation in the dataset.
	 * We'll sort annotations by multiplicity in the resulting text file.
	 */
	private HashMap<String, SingleAnnotation> annotations = new HashMap<String, SingleAnnotation>();

	private SingleAnnotation[] sortedByMultiplicityAnnotations;
	
	private SingleAnnotation[] sortedByNbrDocumentAnnotations;

	private JAXBContext jc;

	private Unmarshaller u;

	private XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();

	private int instancesFoundTotal;

	private int instancesDeleted;

	private int annotFile;

	/**
	 * Launch the annotations extraction.
	 * @param dirpath the path to ~LabelMe/database/annotations
	 */
	public void processDataset(String pathToAnnotations) {
		System.out.println("AnnotationsListExtractor.processDataset begin.");

		this.pathToAnnotations = pathToAnnotations;

		//create the JAXBContext.
		jc = null;
		try {
			jc = JAXBContext.newInstance("net.trevize.labelme.jaxb");
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		//instantiate an Unmarshaller.
		u = null;
		try {
			u = jc.createUnmarshaller();
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		//launch the processing.
		instancesFoundTotal = 0;
		instancesDeleted = 0;
		annotFile = 0;
		processDir(pathToAnnotations);

		//sort the vocabulary by instance multiplicity (decrease order).
		sortedByMultiplicityAnnotations = new SingleAnnotation[annotations
				.size()];
		annotations.values().toArray(sortedByMultiplicityAnnotations);
		Arrays.sort(sortedByMultiplicityAnnotations,
				new SingleAnnotationComparatorByMultiplicity());
		
		//sort the vocabulary by occurrence by document in the dataset (decrease order).
		sortedByNbrDocumentAnnotations = new SingleAnnotation[annotations
				.size()];
		annotations.values().toArray(sortedByNbrDocumentAnnotations);
		Arrays.sort(sortedByNbrDocumentAnnotations,
				new SingleAnnotationComparatorByNbrDocument());

		//write the vocabulary object on disk.
		serializeAnnotationsListObject();

		//write a vocabulary text file on disk.
		writeAnnotationsList();

		//notify the end of the extractRawVocabulary process.
		System.out.println("AnnotationsListExtractor.processDataset end.");
	}

	/**
	 * This method use a recursive descent to list all sub-directories. 
	 * @param dirpath the path to a directory containing LabelMe annotations 
	 * files.
	 */
	private void processDir(String dirpath) {
		System.out.println("\tprocessing directory " + dirpath);

		File[] lf = new File(dirpath).listFiles();
		for (File f : lf) {
			if (f.isDirectory()) {
				try {
					processDir(f.getCanonicalPath());
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
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
	 * This method retrieve all annotation (i.e. for each Object of the
	 * annotation file.  
	 * @param path the path to an annotation file.
	 */
	private void processAnnotation(String path) {
		//we encounter a new annotation file.
		annotFile++;

		//create an XMLStreamReader on the annotation file.
		XMLStreamReader xmlsr = null;
		try {
			xmlsr = xmlInputFactory.createXMLStreamReader(new FileInputStream(
					path));
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

		/*
		 * for computing the co-occurrence information, I've to get
		 * a list of all annotation in the annotation file that are not
		 * marked deleted.
		 */
		ArrayList<SingleAnnotation> list_sa_notMarkedDeleted = new ArrayList<SingleAnnotation>();

		//getting annotations.
		for (net.trevize.labelme.jaxb.Object o : a.getObject()) {
			//we encouter a new annotated object.
			instancesFoundTotal++;

			//we only considers not deleted annotation.
			if (o.getDeleted().getContent().equals("1")) {
				instancesDeleted++;
				continue;
			}

			//retrieving the annotation.
			String name = o.getName().getContent();

			//cleaning the free text annotation.
			name = trimpp(name);

			//push the annotation in the HashMap.
			//the sentence is preserved, i.e. no exploding.
			SingleAnnotation sa = null;
			if (annotations.keySet().contains(name)) {
				sa = annotations.get(name);
				sa.multiplicity.increment();
			} else {
				sa = new SingleAnnotation(path, name, new MutableInt(1));
				annotations.put(name, sa);
			}

			if (!list_sa_notMarkedDeleted.contains(sa)) {
				list_sa_notMarkedDeleted.add(sa);
			}
		}

		/*
		 * update annotation class occured in a document (nbrDocument in SingleAnnotation).
		 */
		for (SingleAnnotation sa : list_sa_notMarkedDeleted) {
			sa.nbrDocument.increment();
		}

		/*
		 * update annotation cooccurrences informations for all annotations
		 * in this annotation file.
		 */
		for (SingleAnnotation sa : list_sa_notMarkedDeleted) {
			for (SingleAnnotation iter_sa : list_sa_notMarkedDeleted) {
				if (iter_sa != sa) {
					if (sa.cooccurrences.get(iter_sa.annotation) != null) {
						sa.cooccurrences.get(iter_sa.annotation).increment();
					} else {
						sa.cooccurrences.put(iter_sa.annotation,
								new MutableInt(1));
					}
				}
			}
		}
	}

	/**
	 * Serialize the vocabulary object. 
	 */
	public void serializeAnnotationsListObject() {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(pathToAnnotations + "/"
					+ "labelme_annotations_list.ser");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(annotations);
			oos.flush();
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Unserialize the annotations list object.
	 */
	public void unserializeAnnotationsListObject() {
		try {
			FileInputStream fis = new FileInputStream(pathToAnnotations + "/"
					+ "labelme_annotations_list.ser");
			ObjectInputStream ois = new ObjectInputStream(fis);
			annotations = (HashMap<String, SingleAnnotation>) ois.readObject();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Write the annotations list in a text file flavor.
	 */
	private void writeAnnotationsList() {
		//create a FileWriter.
		FileWriter fw = null;
		try {
			fw = new FileWriter(pathToAnnotations + "/"
					+ "labelme_annotations_list.txt");
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		//create a BufferedReader.
		BufferedWriter bw = new BufferedWriter(fw);

		//add some informations.
		//writing the date.
		try {
			fw.write("### " + GregorianCalendar.getInstance().getTime()
					+ ".\n\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fw.write("### " + annotFile
					+ " annotation files found in the dataset.\n");
			fw.write("### i.e. " + annotFile + " annotated images.\n\n");

			fw.write("### " + (instancesFoundTotal - instancesDeleted)
					+ " instances (" + instancesFoundTotal
					+ " found in the dataset but " + instancesDeleted
					+ " marked deleted and not taken in consideration).\n");

			fw.write("### " + sortedByNbrDocumentAnnotations.length
					+ " annotations for "
					+ (instancesFoundTotal - instancesDeleted)
					+ " instances.\n\n");
		} catch (IOException e) {
			e.printStackTrace();
		}

		//write the annotations list.
		try {
			fw.write("### annotations\n");

			for (int i = sortedByNbrDocumentAnnotations.length - 1; i >= 0; --i) {
				fw
						.write(sortedByNbrDocumentAnnotations[i].annotation
								+ ","
								+ (Integer) sortedByNbrDocumentAnnotations[i].multiplicity
										.getValue()
								+ ","
								+ (Integer) sortedByNbrDocumentAnnotations[i].nbrDocument
										.getValue() + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		//close files.
		try {
			bw.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//now, writing the cooccurences informations.

		//create a FileWriter.
		fw = null;
		try {
			fw = new FileWriter(pathToAnnotations + "/"
					+ "labelme_annotations_cooccurrences.txt");
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		//create a BufferedReader.
		bw = new BufferedWriter(fw);

		//add some informations.
		//writing the date.
		try {
			fw.write("### " + GregorianCalendar.getInstance().getTime()
					+ ".\n\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fw.write("### " + annotFile
					+ " annotation files found in the dataset.\n");
			fw.write("### i.e. " + annotFile + " annotated images.\n\n");

			fw.write("### " + (instancesFoundTotal - instancesDeleted)
					+ " instances (" + instancesFoundTotal
					+ " found in the dataset but " + instancesDeleted
					+ " marked deleted and not taken in consideration).\n");

			fw.write("### " + sortedByMultiplicityAnnotations.length
					+ " annotations for "
					+ (instancesFoundTotal - instancesDeleted)
					+ " instances.\n\n");
		} catch (IOException e) {
			e.printStackTrace();
		}

		//write the annotations co-occurrences.
		try {
			fw.write("### cooccurrences\n");

			for (int i = sortedByMultiplicityAnnotations.length - 1; i >= 0; --i) {
				fw.write(sortedByMultiplicityAnnotations[i].annotation + "\n");

				Iterator<String> iter = sortedByMultiplicityAnnotations[i].cooccurrences
						.keySet().iterator();
				while (iter.hasNext()) {
					String key = iter.next();
					String line = key
							+ " ("
							+ sortedByMultiplicityAnnotations[i].cooccurrences
									.get(key).toInteger() + ")";
					if (iter.hasNext()) {
						line += ", ";
					}

					fw.write(line);
				}
				fw.write("\n\n");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		//close files.
		try {
			bw.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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

	public int getCooccurrence(String t1, String t2) {
		SingleAnnotation sa = annotations.get(t1);
		if (sa == null) {
			/*
			 * the term t1 is not contained in the annotation vocabulary.
			 */
			return 0;
		}

		MutableInt mi = sa.cooccurrences.get(t2);
		if (mi == null) {
			/*
			 * the term t2 is not contained in the annotation vocabulary.
			 */
			return 0;
		} else {
			return mi.intValue();
		}
	}

	/**
	 * write a CSV file containing co-occurrences between the words in the
	 * word list wl1 and the words in the word list wl2. 
	 * @param wl1
	 * @param wl2
	 */
	public void writeCooccurrenceTable(String csvFilename, String[] wl1,
			String[] wl2) {

		FileWriter fw = null;

		try {
			fw = new FileWriter(csvFilename);

			fw.write(",");
			for (int i = 0; i < wl2.length; ++i) {
				fw.write(wl2[i] + ((i == wl2.length - 1) ? "" : ","));
			}
			fw.write("\n");

			for (int i = 0; i < wl1.length; ++i) {

				fw.write(wl1[i] + ",");

				for (int j = 0; j < wl2.length; ++j) {

					int c = getCooccurrence(wl1[i], wl2[j]);
					fw.write("" + c + ((j == wl2.length - 1) ? "" : ","));

				}

				fw.write("\n");

			}

			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		AnnotationsListExtractor o = new AnnotationsListExtractor();
		o
				.processDataset("/media/nell/datasets/LabelMe-jul2009/database/annotations");

		/*
		o.unserializeAnnotationsListObject();

		String[] wl11 = new String[] { "rodent", "gnawer", "rat", "snout",
				"ears", "body", "slender", "tail" };

		String[] wl12 = new String[] { "device", "cursor", "computer",
				"screen", "pad", "ball" };

		String[] wl2 = new String[] { "light", "cpu", "keyword", "mouse",
				"mousepad", "mug", "bottle", "post-it" };

		o.writeCooccurrenceTable("./cooccurrenceTable1.cvs", wl2, wl11);

		o.writeCooccurrenceTable("./cooccurrenceTable2.cvs", wl2, wl12);
		*/

		//int c = o.getCooccurrence("mouse", "light");
		//System.out.println(c);

	}

}
