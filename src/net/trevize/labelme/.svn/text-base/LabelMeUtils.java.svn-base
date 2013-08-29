package net.trevize.labelme;

import java.awt.Point;
import java.awt.Polygon;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import net.trevize.labelme.jaxb.Annotation;
import net.trevize.labelme.jaxb.Pt;
import net.trevize.tinker.FileUtils;

import com.seisw.util.geom.PolyDefault;

/**
 * 
 * 
 * @author Nicolas James <nicolas.james@gmail.com> [[http://njames.trevize.net]]
 * LabelMeUtils.java - Apr 19, 2010
 */

public class LabelMeUtils {

	public static Polygon getPolygonFromLabelMeObject(
			net.trevize.labelme.jaxb.Object labelme_object) {
		List<Pt> labelme_points_list = labelme_object.getPolygon().getPt();
		Polygon polygon = new Polygon();
		for (Pt pt : labelme_points_list) {
			polygon.addPoint((int) Float.parseFloat(FileUtils.trimpp(pt.getX()
					.getContent())), (int) Float.parseFloat(FileUtils.trimpp(pt
					.getY().getContent())));
		}

		return polygon;
	}

	public static ArrayList<Point> getPointListFromLabelMeObject(
			net.trevize.labelme.jaxb.Object labelme_object) {
		ArrayList<Point> points = new ArrayList<Point>();

		List<Pt> labelme_points_list = labelme_object.getPolygon().getPt();
		for (Pt pt : labelme_points_list) {
			points.add(new Point((int) Float.parseFloat(FileUtils.trimpp(pt
					.getX().getContent())), (int) Float.parseFloat(FileUtils
					.trimpp(pt.getY().getContent()))));
		}

		return points;
	}

	public static ArrayList<Polygon> loadPolygonsFromLabelMeAnnotation(
			File annotation_file) {

		//creating the JAXB context.
		JAXBContext jc = null;
		try {
			jc = JAXBContext.newInstance("net.trevize.labelme.jaxb");
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		//instantiate an unmarshaller.
		Unmarshaller u = null;
		try {
			u = jc.createUnmarshaller();
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		//creating an XMLStreamReader factory.
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();

		//creating an XMLStreamReader for reading the LabelMe annotation file. 
		XMLStreamReader xmlsr = null;
		try {
			xmlsr = inputFactory.createXMLStreamReader(new FileInputStream(
					annotation_file));
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
		ArrayList<Polygon> list_poly = new ArrayList<Polygon>();

		//for each annotated object in the annotation file.
		for (net.trevize.labelme.jaxb.Object labelme_object : a.getObject()) {

			//we only considers not deleted annotation.
			if (labelme_object.getDeleted().getContent().equals("1")) {
				continue;
			}

			//retrieving the annotation.
			Polygon p = getPolygonFromLabelMeObject(labelme_object);
			list_poly.add(p);
		}//end for all Object in the LabelMe annotation.

		return list_poly;
	}

	/**
	 * This method return a list of Polygon objects that corresponds to the
	 * polygons contained in the indicated annotation file, and for which
	 * the name is the one given in parameter. 
	 * 
	 * @param annotation_file a LabelMe annotation file.
	 * @param labelme_object_name the of an object in the image.
	 * @return
	 */
	public static ArrayList<Polygon> loadPolygonsForLabelMeObjectName(
			File annotation_file, String labelme_object_name) {

		//creating the JAXB context.
		JAXBContext jc = null;
		try {
			jc = JAXBContext.newInstance("net.trevize.labelme.jaxb");
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		//instantiate an unmarshaller.
		Unmarshaller u = null;
		try {
			u = jc.createUnmarshaller();
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		//creating an XMLStreamReader factory.
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();

		//creating an XMLStreamReader for reading the LabelMe annotation file. 
		XMLStreamReader xmlsr = null;
		try {
			xmlsr = inputFactory.createXMLStreamReader(new FileInputStream(
					annotation_file));
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
		ArrayList<Polygon> list_poly = new ArrayList<Polygon>();

		//for each annotated object in the annotation file.
		for (net.trevize.labelme.jaxb.Object labelme_object : a.getObject()) {

			//we only considers not deleted annotation.
			if (labelme_object.getDeleted().getContent().equals("1")) {
				continue;
			}

			//retrieving the annotation.
			String name = labelme_object.getName().getContent().trim();
			if (name.equals(labelme_object_name)) {
				Polygon p = getPolygonFromLabelMeObject(labelme_object);
				list_poly.add(p);
			}
		}//end for all Object in the LabelMe annotation.

		return list_poly;
	}

	public static ArrayList<Polygon> getNotIntersectingPolygons(
			ArrayList<Polygon> polygons) {
		ArrayList<Polygon> results = new ArrayList<Polygon>();

		/*
		 * converting java.awt.Polygon in com.seisw.util.geom.Poly, because there is no methods
		 * in the java API for computing the intersection of polygons.
		 */
		ArrayList<PolyDefault> seisw_polygons = new ArrayList<PolyDefault>();

		for (Polygon awt_p : polygons) {
			PolyDefault seisw_p = new PolyDefault();
			for (int i = 0; i < awt_p.npoints; ++i) {
				seisw_p.add(awt_p.xpoints[i], awt_p.ypoints[i]);
			}
			seisw_polygons.add(seisw_p);
		}

		for (int i = 0; i < seisw_polygons.size(); ++i) {
			PolyDefault seisw_pi = seisw_polygons.get(i);

			boolean empty_intersection = true;
			for (int j = 0; j < seisw_polygons.size(); ++j) {
				if (j == i) {
					continue;
				}

				PolyDefault seisw_pj = seisw_polygons.get(j);

				if (!seisw_pi.intersection(seisw_pj).isEmpty()) {
					empty_intersection = false;
					break;
				}
			}

			if (empty_intersection) {
				results.add(polygons.get(i));
			}
		}

		return results;
	}

}
