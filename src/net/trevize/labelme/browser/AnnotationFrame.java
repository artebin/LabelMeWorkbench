package net.trevize.labelme.browser;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.tree.TreePath;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import net.trevize.jmagine.svgsymbol.SVGPolygon;
import net.trevize.jmagine.svgsymbol.SVGSymbol;
import net.trevize.labelme.LabelMeLogger;
import net.trevize.labelme.browser.namedobjecttreetable.NORenderData;
import net.trevize.labelme.browser.namedobjecttreetable.NORowModel;
import net.trevize.labelme.browser.namedobjecttreetable.NOTreeModel;
import net.trevize.labelme.browser.namedobjecttreetable.NOTreeNode;
import net.trevize.labelme.jaxb.Annotation;
import net.trevize.life.visualvoc.sift.RobHess;
import net.trevize.life.visualvoc.sift.SIFTSet;
import net.trevize.life.visualvoc.sift.SIFTSetFilter;
import net.trevize.life.visualvoc.sift.gui.SIFTSVGArrow;
import net.trevize.tinker.ImageUtils;

import org.netbeans.swing.outline.DefaultOutlineModel;
import org.netbeans.swing.outline.Outline;
import org.netbeans.swing.outline.OutlineModel;

/**
 * 
 * 
 * @author Nicolas James <nicolas.james@gmail.com> [[http://njames.trevize.net]]
 * AnnotationFrame.java - Apr 13, 2010
 */

public class AnnotationFrame extends JFrame implements MouseListener,
		ActionListener {

	public static final String ACTION_FULLY_EXPAND_ALL_CHILDREN = "ACTION_FULLY_EXPAND_ALL_CHILDREN";
	public static final String ACTION_CHECK_ALL_CHILDREN = "ACTION_CHECK_ALL_CHILDREN";
	public static final String ACTION_UNCHECK_ALL_CHILDREN = "ACTION_UNCHECK_ALL_CHILDREN";
	public static final String ACTION_ADD_SIFT_VECTORS_FOR_OBJECT = "ACTION_ADD_SIFT_VECTORS_FOR_OBJECT";

	private LabelMeDocumentViewer docviewer;
	private String image_path;
	private String annotation_path;
	private ImageFrame f_image;

	//for unmarshalling the annotation.
	private XMLInputFactory xmlInputFactory;
	private XMLStreamReader xmlsr;
	private JAXBContext jc;
	private Unmarshaller u;
	private Annotation annotation;

	//classical swing components.
	private JPanel p0;
	private JTabbedPane jtp0;
	private NOTreeModel tree_model;
	private OutlineModel outline_model;
	private Outline outline;
	private JTextArea jta0;
	private JPopupMenu popupmenu;

	//dealing with the tree.
	private TreePath popuped_treepath;

	public AnnotationFrame(LabelMeDocumentViewer docviewer) {
		this.docviewer = docviewer;
		this.image_path = docviewer.getImage_path();
		this.annotation_path = docviewer.getAnnotation_path();

		setTitle("View image: " + image_path);

		unmarshallAnnotation();
		init();
	}

	private void unmarshallAnnotation() {
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

		//create an XMLStreamReader on the annotation file.
		xmlInputFactory = XMLInputFactory.newInstance();
		xmlsr = null;
		try {
			xmlsr = xmlInputFactory.createXMLStreamReader(new FileInputStream(
					new File(annotation_path)));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (XMLStreamException e1) {
			e1.printStackTrace();
		}

		//load the annotation.
		annotation = null;
		try {
			annotation = (Annotation) u.unmarshal(xmlsr);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	private void init() {
		p0 = new JPanel();
		p0.setLayout(new BorderLayout());
		getContentPane().add(p0);

		jtp0 = new JTabbedPane();
		p0.add(jtp0, BorderLayout.CENTER);

		//setting the first tab.
		jtp0.add("Labelme objects", getNamedObjectsTreeTable());

		//setting the second tab.
		jtp0.add("XML", getXMLJTextArea());
	}

	private JScrollPane getNamedObjectsTreeTable() {
		tree_model = new NOTreeModel(this);
		outline_model = DefaultOutlineModel.createOutlineModel(tree_model,
				new NORowModel(), true);

		outline = new Outline();
		outline.setRootVisible(true);
		outline.setTableHeader(null);
		outline.setModel(outline_model);

		outline.setShowGrid(true);
		outline.setIntercellSpacing(new Dimension(0, 0));
		outline.setRowHeight(24);

		// setting the selection model and the selection mode
		outline.setSelectionModel(new DefaultListSelectionModel());
		outline.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		NORenderData renderData = new NORenderData(tree_model);
		outline.setRenderDataProvider(renderData);
		outline.setAutoscrolls(true);
		outline.addMouseListener(this);

		//init the popupmenu.
		popupmenu = new JPopupMenu();
		JMenuItem menuitem0 = new JMenuItem("expand all");
		menuitem0.setActionCommand(ACTION_FULLY_EXPAND_ALL_CHILDREN);
		menuitem0.addActionListener(this);
		popupmenu.add(menuitem0);

		JMenuItem menuitem1 = new JMenuItem("check all children");
		menuitem1.setActionCommand(ACTION_CHECK_ALL_CHILDREN);
		menuitem1.addActionListener(this);
		popupmenu.add(menuitem1);

		JMenuItem menuitem2 = new JMenuItem("uncheck all children");
		menuitem2.setActionCommand(ACTION_UNCHECK_ALL_CHILDREN);
		menuitem2.addActionListener(this);
		popupmenu.add(menuitem2);

		JMenuItem menuitem3 = new JMenuItem("add SIFT vectors for this object");
		menuitem3.setActionCommand(ACTION_ADD_SIFT_VECTORS_FOR_OBJECT);
		menuitem3.addActionListener(this);
		popupmenu.add(menuitem3);

		JScrollPane jsp0 = new JScrollPane();
		jsp0.setViewportView(outline);
		jsp0.setViewportBorder(null);

		return jsp0;
	}

	private JScrollPane getXMLJTextArea() {
		JScrollPane jsp0 = new JScrollPane();

		jta0 = new JTextArea();
		jta0.setEditable(false);
		jta0.setWrapStyleWord(false);
		jta0.setLineWrap(false);

		jsp0.setViewportView(jta0);

		StringBuffer sb = new StringBuffer();
		try {

			FileReader fr = new FileReader(annotation_path);
			BufferedReader br = new BufferedReader(fr);
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
			br.close();
			fr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		jta0.setText(sb.toString());

		return jsp0;
	}

	public void addAllObjectsToJmagine() {
		//for all object name (it's the key of the HashMap tree_model.getIndex_node_object() ).
		for (String key : tree_model.getIndex_node_object().keySet()) {
			//get the list of the node for this name.
			ArrayList<NOTreeNode> node_list = tree_model.getIndex_node_object()
					.get(key);

			//push (the jmagine is not yet displayed) a group for this key.
			f_image.getJmagine().pushSVGGroup(key);

			//for all nodes (nodes are named polygon).
			int i = 0;
			for (NOTreeNode node : node_list) {

				//instantiate a new SVGPolygon and push it to the jmagine object.
				SVGPolygon svg_polygon = new SVGPolygon(
						node.getPolygon_points());
				svg_polygon
						.setStyle("stroke:#fff000;stroke-width:2;fill:none;");
				svg_polygon.setGroupId(key + "" + i);
				f_image.getJmagine().pushSVGSymbol(svg_polygon, key);

				//instantiate a new SVGPolygon for the bounding rectangle of this polygon and push it to the jmagine object. 
				SVGPolygon svg_polygon_bounding_rectangle = new SVGPolygon(
						node.getBoundingRectanglePoints());
				svg_polygon_bounding_rectangle
						.setStyle("stroke:#0000ff;stroke-width:2;fill:none;");
				svg_polygon_bounding_rectangle.setGroupId(key + "" + i + "_br");
				f_image.getJmagine().pushSVGSymbol(
						svg_polygon_bounding_rectangle, key);

				++i;

			}
		}
	}

	public void addAllSIFTVectors() {
		//get the SIFTSet for the indicated sift file.
		SIFTSet siftset = new RobHess(new File(docviewer.getSift_path()));
		LabelMeLogger.getBrowserLogger().log(Level.INFO,
				"" + siftset.getNumberOfSIFT());

		//get the scale of the image for which the SIFT vectors have been computed.
		Dimension sifted_image_dim = ImageUtils.getImageSize(docviewer
				.getSifted_image_path());
		Dimension image_dim = ImageUtils.getImageSize(image_path);
		double scale_x = (double) image_dim.width
				/ (double) sifted_image_dim.width;
		double scale_y = (double) image_dim.height
				/ (double) sifted_image_dim.height;

		//add a group to jmagine for the sift arrows.
		f_image.getJmagine().addSVGGroup("sift_vectors");

		//get a list of SVGArrow for these sift vectors.
		ArrayList<SVGSymbol> sift_arrows = new ArrayList<SVGSymbol>();
		for (int i = 0; i < siftset.getNumberOfSIFT(); ++i) {
			sift_arrows.add(SIFTSVGArrow.createSIFTSVGArrow(siftset.getFrames()
					.get(i), siftset.getDescriptors().get(i), "sift_" + i,
					scale_x, scale_y));
		}

		//add the SVGArrow objects to the jmagine object.
		f_image.getJmagine().addSVGSymbol(sift_arrows, "sift_vectors");
	}

	public void addSIFTVectorsForPolygon(NOTreeNode node) {
		//instantiate a java.awt.polygon for filtering the SIFT vectors of this image.
		Polygon poly = new Polygon();
		for (Point p : node.getPolygon_points()) {
			poly.addPoint(p.x, p.y);
		}

		//get the SIFTSet for the indicated sift file.
		SIFTSet siftset = new RobHess(new File(docviewer.getSift_path()));

		//get the scale of the image for which the SIFT vectors have been computed.
		Dimension sifted_image_dim = ImageUtils.getImageSize(docviewer
				.getSifted_image_path());
		Dimension image_dim = ImageUtils.getImageSize(image_path);
		double scale_x = (double) image_dim.width
				/ (double) sifted_image_dim.width;
		double scale_y = (double) image_dim.height
				/ (double) sifted_image_dim.height;

		//filter the SIFTSet with the polygon.
		ArrayList<Polygon> polygons = new ArrayList<Polygon>();
		polygons.add(poly);
		SIFTSet filtered_siftset = SIFTSetFilter.polygonsFilter(siftset,
				scale_x, scale_y, polygons);

		//add a group to jmagine for the sift arrows.
		f_image.getJmagine().addSVGGroup(node.getId() + "_sift_vectors");
		ArrayList<SVGSymbol> sift_arrows = new ArrayList<SVGSymbol>();
		for (int i = 0; i < filtered_siftset.getNumberOfSIFT(); ++i) {
			sift_arrows.add(SIFTSVGArrow.createSIFTSVGArrow(filtered_siftset
					.getFrames().get(i),
					filtered_siftset.getDescriptors().get(i), node.getId()
							+ "sift_" + i, scale_x, scale_y));
		}

		//add the SVGArrow objects to the jmagine object.
		f_image.getJmagine().addSVGSymbol(sift_arrows,
				node.getId() + "_sift_vectors");
	}

	private void expandAllChildren(TreePath treepath) {
		outline_model.getTreePathSupport().expandPath(treepath);

		int num_of_children = tree_model.getChildCount(treepath
				.getLastPathComponent());

		for (int i = 0; i < num_of_children; ++i) {
			Object child = outline_model.getChild(
					treepath.getLastPathComponent(), i);
			TreePath child_treepath = treepath.pathByAddingChild(child);
			expandAllChildren(child_treepath);
		}
	}

	private void checkAllChildren(TreePath treepath) {
		((NOTreeNode) treepath.getLastPathComponent()).setChecked(true);

		int num_of_children = tree_model.getChildCount(treepath
				.getLastPathComponent());

		for (int i = 0; i < num_of_children; ++i) {
			Object child = outline_model.getChild(
					treepath.getLastPathComponent(), i);
			TreePath child_treepath = treepath.pathByAddingChild(child);
			checkAllChildren(child_treepath);
		}
	}

	private void uncheckAllChildren(TreePath treepath) {
		((NOTreeNode) treepath.getLastPathComponent()).setChecked(false);

		int num_of_children = tree_model.getChildCount(treepath
				.getLastPathComponent());

		for (int i = 0; i < num_of_children; ++i) {
			Object child = outline_model.getChild(
					treepath.getLastPathComponent(), i);
			TreePath child_treepath = treepath.pathByAddingChild(child);
			uncheckAllChildren(child_treepath);
		}
	}

	/***************************************************************************
	 * implementation of MouseListener.
	 **************************************************************************/
	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getSource() == outline) {
			if (e.getButton() == MouseEvent.BUTTON3) {
				popuped_treepath = outline.getClosestPathForLocation(e.getX(),
						e.getY());
				if (e.isPopupTrigger()) {
					popupmenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getSource() == outline) {
			if (e.getButton() == MouseEvent.BUTTON3) {
				popuped_treepath = outline.getClosestPathForLocation(e.getX(),
						e.getY());
				if (e.isPopupTrigger()) {
					popupmenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		}
	}

	/***************************************************************************
	 * implementation of ActionListener.
	 **************************************************************************/

	@Override
	public void actionPerformed(ActionEvent e) {
		String action_command = e.getActionCommand();

		if (action_command.equals(ACTION_FULLY_EXPAND_ALL_CHILDREN)) {
			expandAllChildren(popuped_treepath);
		}

		else

		if (action_command.equals(ACTION_CHECK_ALL_CHILDREN)) {
			checkAllChildren(popuped_treepath);
			outline.repaint();
		}

		else

		if (action_command.equals(ACTION_UNCHECK_ALL_CHILDREN)) {
			uncheckAllChildren(popuped_treepath);
			outline.repaint();
		}

		else

		if (action_command.equals(ACTION_ADD_SIFT_VECTORS_FOR_OBJECT)) {
			//retrieve the NOTreeNode.
			NOTreeNode node = (NOTreeNode) popuped_treepath
					.getLastPathComponent();

			if (node.getNode_type().equals(NOTreeNode.NODE_TYPE_OBJECT)) {
				addSIFTVectorsForPolygon(node);
			}
		}
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

	public ImageFrame getF_image() {
		return f_image;
	}

	public void setF_image(ImageFrame fImage) {
		f_image = fImage;
	}

}
