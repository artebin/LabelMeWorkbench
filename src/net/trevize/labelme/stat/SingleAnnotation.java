package net.trevize.labelme.stat;

import java.io.Serializable;
import java.util.HashMap;

import org.apache.commons.lang.mutable.MutableInt;

/**
 * This class represents a SingleAnnotation, i.e. this class encapsulates a String
 * which is an annotation (clearly containing a sense unit), and 
 * the usage of this annotation in the LabelMe dataset (i.e. the multiplicity,
 * i.e. the number of instances of the annotation in the dataset).
 * The cooccurences field is used to extract the annotations which is used cooccurently
 * with an annotation. 
 * 
 * @author Nicolas James <nicolas.james@gmail.com> [[http://njames.trevize.net]]
 */

public class SingleAnnotation implements Serializable {
	/**
	 * the path to the file in the dataset, i.e. in ~LabelMe/database/annotation/
	 */
	public String filepath = null;

	/**
	 * the annotation content.
	 */
	public String annotation = null;

	/**
	 * if this class is used like for class for an annotation (and not an 
	 * instance), multiplicity is the usage of this annotation in the dataset.
	 * 
	 * nbrDocument is the number of document in which this annotation class
	 * appear.
	 */
	public MutableInt multiplicity = null;

	public MutableInt nbrDocument = null;

	/**
	 * considering the current annotation, this variable brought which annotations
	 * cooccurred with, path to these annotations and the multiplicity of
	 * the cooccurrence.
	 */
	public HashMap<String, MutableInt> cooccurrences = null;

	public SingleAnnotation(String filepath, String annotation,
			MutableInt multiplicity) {
		this.filepath = filepath;
		this.annotation = annotation;
		this.multiplicity = multiplicity;
		this.nbrDocument = new MutableInt(0);
		cooccurrences = new HashMap<String, MutableInt>();
	}
	
}