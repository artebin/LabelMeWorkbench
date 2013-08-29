package net.trevize.labelme.browser.namedobjecttreetable;

import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

import net.trevize.labelme.browser.AnnotationFrame;

/**
 * 
 * 
 * @author Nicolas James <nicolas.james@gmail.com> [[http://njames.trevize.net]]
 * NOTreeNode.java - Apr 13, 2010
 */

public class NOTreeNode extends DefaultMutableTreeNode {

	public static final String NODE_TYPE_ROOT = "NODE_TYPE_ROOT";
	public static final String NODE_TYPE_OBJECT_CATEGORY = "NODE_TYPE_OBJECT_CATEGORY";
	public static final String NODE_TYPE_OBJECT = "NODE_TYPE_OBJECT";

	private AnnotationFrame f_annotation;
	private String node_type;
	private Vector<Object> tableRowValues;
	private boolean checked;

	private String id;
	private ArrayList<Point> polygon_points;

	public NOTreeNode(AnnotationFrame f_annotation, String node_type) {
		super();
		this.f_annotation = f_annotation;
		this.node_type = node_type;
		tableRowValues = new Vector<Object>();
		checked = true;
	}

	public ArrayList<Point> getBoundingRectanglePoints() {
		ArrayList<Point> points = new ArrayList<Point>();

		Polygon poly = new Polygon();
		for (Point p : polygon_points) {
			poly.addPoint(p.x, p.y);
		}

		/*
		 * we want to display a rectangle
		 * 
		 * A-------D
		 * |       |
		 * B-------C
		 * 
		 */

		//point A.
		points.add(new Point((int) poly.getBounds().getX(), (int) poly
				.getBounds().getY()));

		//point B.
		points.add(new Point((int) poly.getBounds().getX(), (int) poly
				.getBounds().getY()
				+ (int) poly.getBounds().getHeight() - 1));

		//point C.
		points.add(new Point((int) poly.getBounds().getX()
				+ (int) poly.getBounds().getWidth() - 1, (int) poly.getBounds()
				.getY()
				+ (int) poly.getBounds().getHeight() - 1));

		//point D.
		points.add(new Point((int) poly.getBounds().getX()
				+ (int) poly.getBounds().getWidth() - 1, (int) poly.getBounds()
				.getY()));

		return points;
	}

	/***************************************************************************
	 * getters and setters.
	 **************************************************************************/

	public void setTableRowValues(Vector<Object> values) {
		tableRowValues = values;
	}

	public Vector<Object> getTableRowValues() {
		return tableRowValues;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
		if (node_type.equals(NODE_TYPE_OBJECT)) {
			f_annotation.getF_image().getJmagine().setVisibilityForSymbol(id,
					checked);
		}

		else

		if (node_type.equals(NODE_TYPE_OBJECT_CATEGORY)) {
			f_annotation.getF_image().getJmagine().setVisibilityForGroup(id,
					checked);
		}
	}

	public ArrayList<Point> getPolygon_points() {
		return polygon_points;
	}

	public void setPolygon_points(ArrayList<Point> polygonPoints) {
		polygon_points = polygonPoints;
	}

	public String getNode_type() {
		return node_type;
	}

	public void setNode_type(String nodeType) {
		node_type = nodeType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
