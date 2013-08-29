package net.trevize.labelme.lucene;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import net.trevize.labelthem.jaxb.Annotation;
import net.trevize.tinker.FileUtils;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

/**
 * This class provides a static method for creating Lucene Document for 
 * indexing. These documents are filled with some fields for an image 
 * depicted with the LabelThem.xsd XML Schema.
 * 
 * Using the fields:
 * 	1. filenames (image and annotation),
 * 	2. folder (image and annotation),
 * 	3. object name (multiple fields in each Lucene Document).
 * 
 * TODO:
 *   - adding more fields from the LabelMe dataset (polygon, etc.), it could
 *   be very useful for example for querying with a sketch for locating named
 *   objects in images.
 * 
 * @author Nicolas James <nicolas.james@gmail.com> [[http://njames.trevize.net]]
 * LabelMeDocument.java - May 20, 2009
 */

public class LuceneLabelMeDocument {

	public static final String FIELD_IMAGE_FILENAME = "FIELD_IMAGE_FILENAME";
	public static final String FIELD_IMAGE_FOLDER = "FIELD_IMAGE_FOLDER";
	public static final String FIELD_ANNOTATION_FILENAME = "FIELD_ANNOTATION_FILENAME";
	public static final String FIELD_ANNOTATION_FOLDER = "FIELD_ANNOTATION_FOLDER";
	public static final String FIELD_OBJECT_NAME = "FIELD_OBJECT_NAME";

	private static ArrayList<String> field_list;

	public static ArrayList<String> getFieldList() {
		if (field_list == null) {
			field_list = new ArrayList<String>();
			field_list.add(FIELD_IMAGE_FILENAME);
			field_list.add(FIELD_IMAGE_FOLDER);
			field_list.add(FIELD_ANNOTATION_FILENAME);
			field_list.add(FIELD_ANNOTATION_FOLDER);
			field_list.add(FIELD_OBJECT_NAME);
		}
		return field_list;
	}

	public static Document createLabelMeDocument(Annotation a)
			throws FileNotFoundException {

		//a Lucene document
		Document doc = new Document();

		//Field.Store.YES for set on the indexing
		//Field.Index.UN_TOKENIZED for not tokenizing the field

		//FIELD_IMAGE_FILENAME.
		String image_filename = trimpp(a.getFilename().getContent());
		doc.add(new Field(FIELD_IMAGE_FILENAME, image_filename,
				Field.Store.YES, Field.Index.NOT_ANALYZED));

		//FIELD_IMAGE_FOLDER.
		String image_folder = trimpp(a.getFolder().getContent());
		doc.add(new Field(FIELD_IMAGE_FOLDER, image_folder, Field.Store.YES,
				Field.Index.NOT_ANALYZED));

		//FIELD_ANNOTATION_FILENAME.
		String annotation_filename = trimpp(FileUtils.changeExtension(a
				.getFilename().getContent(), ".xml"));
		doc.add(new Field(FIELD_ANNOTATION_FILENAME, annotation_filename,
				Field.Store.YES, Field.Index.NOT_ANALYZED));

		//FIELD_ANNOTATION_FOLDER.
		String annotation_folder = trimpp(a.getFolder().getContent());
		doc.add(new Field(FIELD_ANNOTATION_FOLDER, annotation_folder,
				Field.Store.YES, Field.Index.NOT_ANALYZED));

		//FIELD_OBJECT_NAME.
		for (int i = 0; i < a.getObject().size(); ++i) {
			//skip deleted object.
			if (a.getObject().get(i).getDeleted().getContent().equals("1")) {
				continue;
			}

			//getting the object name.
			String name = trimpp(a.getObject().get(i).getName().getContent());

			//skip object with no name.
			if (name.equals("")) {
				continue;
			}

			doc.add(new Field(FIELD_OBJECT_NAME, name, Field.Store.YES,
					Field.Index.ANALYZED));
		}

		return doc;
	}

	public static String trimpp(String s) {
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

}
