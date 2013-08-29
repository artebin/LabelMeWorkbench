package net.trevize.labelme.browser.namedobjecttreetable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.swing.tree.TreeModel;

import net.trevize.labelme.LabelMeUtils;
import net.trevize.labelme.browser.AnnotationFrame;
import net.trevize.labelme.jaxb.Annotation;

/**
 * 
 * 
 * @author Nicolas James <nicolas.james@gmail.com> [[http://njames.trevize.net]]
 * NOTreeModel.java - Apr 13, 2010
 */

public class NOTreeModel implements TreeModel {

	private AnnotationFrame f_annotation;
	private Annotation annotation;
	private NOTreeNode node_root;

	//all the nodes for 'LabelMe objects' indexed by their names.
	private HashMap<String, ArrayList<NOTreeNode>> index_node_object;

	public NOTreeModel(AnnotationFrame f_annotation) {
		this.f_annotation = f_annotation;
		annotation = f_annotation.getAnnotation();

		index_node_object = new HashMap<String, ArrayList<NOTreeNode>>();

		loadModelFromAnnotation();
	}

	public void loadModelFromAnnotation() {

		//get the list of the net.trevize.labelme.jaxb.Object in the annotation.
		List<net.trevize.labelme.jaxb.Object> objects = annotation.getObject();

		//indexing the objects by their name (this index is just locally used for build the tree model).
		HashMap<String, ArrayList<net.trevize.labelme.jaxb.Object>> objects_index = new HashMap<String, ArrayList<net.trevize.labelme.jaxb.Object>>();
		for (net.trevize.labelme.jaxb.Object object : objects) {
			String object_name = object.getName().getContent().trim();
			ArrayList<net.trevize.labelme.jaxb.Object> objects_for_name = objects_index
					.get(object_name);
			if (objects_for_name == null) {
				objects_for_name = new ArrayList<net.trevize.labelme.jaxb.Object>();
				objects_index.put(object_name, objects_for_name);
			}
			objects_for_name.add(object);
		}

		//fill the model using a lexicographic order on the object names.
		String[] sorted_names = objects_index.keySet().toArray(new String[0]);
		Arrays.sort(sorted_names);

		//creating the root node.
		node_root = new NOTreeNode(f_annotation, NOTreeNode.NODE_TYPE_ROOT);
		node_root.setUserObject(annotation.getFilename().getContent());

		/*
		 * add the nodes node_name (i.e. the annotation category).
		 * for each node node_name we'll add its associated nodes node_object.
		 * 
		 * We have something like that:
		 * 
		 * car (all the polygons named by the label 'car', it's a node_name)
		 * |_ car (polygon n°0, it's a node_object)
		 *   |car (polygon n°1, it's a node_object)
		 */
		for (int i = 0; i < sorted_names.length; ++i) {
			String name = sorted_names[i];

			ArrayList<NOTreeNode> node_object_list = null; //the list of node for this name.
			NOTreeNode node_name = null; //the node for this name (it'a a category node).

			int id = 0; //this variable is used to build a String id for each node_object (we can have several 'car' in an image).
			for (net.trevize.labelme.jaxb.Object labelme_object : objects_index
					.get(name)) {

				//does not considerate deleted object in a first time.
				if (labelme_object.getDeleted().getContent().equals("1")) {
					continue;
				}

				//the first time we encounter a not deleted object, we create a new node_name (i.e. a category node).
				if (id == 0) {
					//create a new node list for this name and put it in the index_node_object.
					node_object_list = new ArrayList<NOTreeNode>();
					index_node_object.put(name, node_object_list);

					//create a new node_name.
					node_name = new NOTreeNode(f_annotation,
							NOTreeNode.NODE_TYPE_OBJECT_CATEGORY);
					//the id of a node_name is the name.
					node_name.setId(name);
					node_name.setUserObject(name);
					node_root.add(node_name);
				}

				//create a new NOTreeNode.NODE_TYPE_OBJECT node.
				NOTreeNode node_object = new NOTreeNode(f_annotation,
						NOTreeNode.NODE_TYPE_OBJECT);

				//set the id.
				node_object.setId(name + "" + id);
				++id;

				//add this new node to the list of the node objects for the current name.
				node_object_list.add(node_object);

				//create a Poly object and save it in the node.
				node_object.setPolygon_points(LabelMeUtils
						.getPointListFromLabelMeObject(labelme_object));

				//set the userObject tree node and add this node_object to the node_name (i.e. the annotation category node). 
				node_object.setUserObject(name);
				node_name.add(node_object);

			}//for all node_object.

		}//for all node_name.

	}

	/***************************************************************************
	 * implementation of TreeModel.
	 **************************************************************************/

	@Override
	public void addTreeModelListener(javax.swing.event.TreeModelListener l) {
		//do nothing
	}

	@Override
	public Object getChild(Object parent, int index) {
		return ((NOTreeNode) parent).getChildAt(index);
	}

	@Override
	public int getChildCount(Object parent) {
		return ((NOTreeNode) parent).getChildCount();
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		return ((NOTreeNode) parent).getIndex((NOTreeNode) child);
	}

	@Override
	public Object getRoot() {
		return node_root;
	}

	@Override
	public boolean isLeaf(Object node) {
		int child_count = getChildCount(node);
		if (child_count == 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void removeTreeModelListener(javax.swing.event.TreeModelListener l) {
		//do nothing
	}

	@Override
	public void valueForPathChanged(javax.swing.tree.TreePath path,
			Object newValue) {
		//do nothing
	}

	/***************************************************************************
	 * getters and setters.
	 **************************************************************************/

	public Annotation getAnnotation() {
		return annotation;
	}

	public void setAnnotation(Annotation annotation) {
		this.annotation = annotation;
	}

	public NOTreeNode getNode_root() {
		return node_root;
	}

	public void setNode_root(NOTreeNode nodeRoot) {
		node_root = nodeRoot;
	}

	public HashMap<String, ArrayList<NOTreeNode>> getIndex_node_object() {
		return index_node_object;
	}

	public void setIndex_node_object(
			HashMap<String, ArrayList<NOTreeNode>> indexNodeObject) {
		index_node_object = indexNodeObject;
	}

}
