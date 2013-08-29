package net.trevize.labelme.browser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URI;
import java.util.Vector;
import java.util.logging.Level;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

import net.trevize.galatee.GEvent;
import net.trevize.galatee.GItem;
import net.trevize.galatee.GListener;
import net.trevize.galatee.Galatee;
import net.trevize.galatee.GalateeProperties;
import net.trevize.gui.layout.CellStyle;
import net.trevize.gui.layout.XGridBag;
import net.trevize.labelme.LabelMeLogger;
import net.trevize.labelme.LabelMeResults;
import net.trevize.labelme.LabelMeSearcher;
import net.trevize.labelme.LabelMeWorkbenchProperties;
import net.trevize.labelme.lucene.LuceneLabelMeDocument;
import net.trevize.labelme.lucene.LuceneLabelMeSearcher;
import net.trevize.tinker.SystemCommandHandler;

import org.apache.lucene.analysis.WhitespaceAnalyzer;

/**
 * 
 * 
 * @author Nicolas James <nicolas.james@gmail.com> [[http://njames.trevize.net]]
 * WhitespaceAnalyzerIndexSearchPanel.java - Apr 13, 2010
 */

public class WhitespaceAnalyzerIndexSearchPanel extends JPanel implements
		ActionListener {

	public static final String ACTION_COMMAND_DO_QUERY = "ACTION_COMMAND_DO_QUERY";
	public static final String ACTION_COMMAND_PREVIOUS_RESULT_PAGE = "ACTION_COMMAND_PREVIOUS_RESULT_PAGE";
	public static final String ACTION_COMMAND_NEXT_RESULT_PAGE = "ACTION_COMMAND_NEXT_RESULT_PAGE";

	private String lucene_index_whitespace_analyzer_path = LabelMeWorkbenchProperties
			.getLabelMeLuceneIndex_WhitespaceAnalyzerPath();
	private String labelme_images_path = LabelMeWorkbenchProperties
			.getLabelMeImagesPath();
	private String labelme_annotations_path = LabelMeWorkbenchProperties
			.getLabelMeAnnotationsPath();

	//classical swing components.
	private XGridBag xgb;
	private JComboBox jcb0;
	private JTextField jtf0;
	private JButton b0, b1, b2;
	private JLabel results_status;
	private JPanel galatee_panel_container;
	private Galatee galatee;

	private CellStyle style_p0 = new CellStyle(1., 0.,
			GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL,
			new Insets(0, 0, 0, 0), 0, 0);
	private CellStyle style_p1 = new CellStyle(1., 1.,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,
					0, 0, 0), 0, 0);

	//for managing the search query and the results.
	private LabelMeSearcher searcher;
	private LabelMeResults results;
	private int nbOfResults;
	private int pageOfResultsViewed;
	private int numOfResultsPerPage = LabelMeWorkbenchProperties
			.getNumOfResultsPerPage();

	public WhitespaceAnalyzerIndexSearchPanel() {
		init();
	}

	private void init() {
		setBorder(new EmptyBorder(3, 3, 3, 3));
		setLayout(new GridBagLayout());
		xgb = new XGridBag(this);

		//setting the search query text field.
		JPanel p0 = new JPanel();
		p0.setLayout(new BoxLayout(p0, BoxLayout.X_AXIS));

		JLabel l0 = new JLabel("Query:");
		p0.add(l0, BorderLayout.WEST);

		/*
		 * adding a JComboBox for choosing the Lucene FIELD to use.
		 * the FIELD used by default is LuceneLabelMeDocument.FIELD_OBJECT_NAME.
		 */
		jcb0 = new JComboBox(LuceneLabelMeDocument.getFieldList().toArray());
		jcb0.setSelectedIndex(LuceneLabelMeDocument.getFieldList().indexOf(
				LuceneLabelMeDocument.FIELD_OBJECT_NAME));
		p0.add(jcb0);

		jtf0 = new JTextField();
		p0.add(jtf0, BorderLayout.CENTER);

		b0 = new JButton("search \u21B5");
		b0.addActionListener(this);
		b0.setActionCommand(ACTION_COMMAND_DO_QUERY);
		p0.add(b0, BorderLayout.EAST);
		xgb.add(p0, style_p0, 0, 0);

		//setting the toolbar.
		JPanel p1 = new JPanel();
		p1.setBorder(new EmptyBorder(3, 0, 3, 0));
		p1.setLayout(new BoxLayout(p1, BoxLayout.X_AXIS));

		b1 = new JButton(new ImageIcon(
				"./gfx/org/freedesktop/tango/go-previous.png"));
		b1.setActionCommand(ACTION_COMMAND_PREVIOUS_RESULT_PAGE);
		b1.addActionListener(this);
		p1.add(b1);

		b2 = new JButton(new ImageIcon(
				"./gfx/org/freedesktop/tango/go-next.png"));
		b2.setActionCommand(ACTION_COMMAND_NEXT_RESULT_PAGE);
		b2.addActionListener(this);

		p1.add(b2);
		p1.add(Box.createHorizontalGlue());
		results_status = new JLabel();
		p1.add(results_status);
		p1.add(Box.createHorizontalGlue());

		xgb.add(p1, style_p0, 1, 0);

		//setting the results panel.
		galatee_panel_container = new JPanel();
		galatee_panel_container.setLayout(new BorderLayout());
		galatee_panel_container.setBackground(Color.WHITE);

		xgb.add(galatee_panel_container, style_p1, 2, 0);
	}

	public void updateResultsStatus() {
		String results_status_text = "query: "
				+ jtf0.getText()
				+ ", "
				+ nbOfResults
				+ " results, page "
				+ (pageOfResultsViewed + 1)
				+ "/"
				+ ((nbOfResults % numOfResultsPerPage == 0) ? (nbOfResults / numOfResultsPerPage)
						: (nbOfResults / numOfResultsPerPage + 1));

		results_status.setText(results_status_text);
	}

	public void updateGalateePanel(Vector<String> listfiles,
			Vector<Vector<Object>> data) {

		Vector<URI> files;
		files = new Vector<URI>();

		for (int i = 0; i < listfiles.size(); ++i) {
			files.add(new File(labelme_images_path + File.separator
					+ listfiles.get(i)).toURI());
		}

		final Galatee g0 = new Galatee(files, data, new Dimension(
				GalateeProperties.getImage_width(),
				GalateeProperties.getImage_height()),
				GalateeProperties.getDescription_width(),
				GalateeProperties.getNumber_of_column());

		g0.enableSearchFunctionality();

		g0.addGalateeListener(new GListener() {
			class VisuThread extends Thread {
				private String command = "display ";
				private SystemCommandHandler sch = new SystemCommandHandler();
				private String path;

				public VisuThread(String path) {
					this.path = path;
				}

				public void run() {
					openLabelMeImageViewer();
					//openWithImageMagick();
				}

				private void openWithImageMagick() {
					sch.exec(command + path);
				}

				private void openLabelMeImageViewer() {
					/*
					 * Retrieving the image path.
					 * We could only retrieve the image path from the
					 * LabelMeImagePanel2, so we retrieve the image relative
					 * path in ~LabelMe/database/images, which is the same for
					 * the annotation path inside ~LabelMe/database/annotations
					 */
					String[] pathExplode = path.split(File.separator);
					String annotationRelativePath = pathExplode[pathExplode.length - 2];

					/*
					 * using the LabelMeCleanerNormalizer, the extension of
					 * the image filename is .jpg
					 */
					String image_filename = pathExplode[pathExplode.length - 1];
					String annotationFileName = image_filename.substring(0,
							image_filename.length() - 4) + ".xml";

					new LabelMeDocumentViewer(path, labelme_annotations_path
							+ File.separator + annotationRelativePath
							+ File.separator + annotationFileName);
				}
			}

			@Override
			public void itemDoubleClicked(GEvent arg0) {
				new VisuThread(arg0.getSelectedItem().getLocalFilepath())
						.start();
			}

			@Override
			public void selectionChanged(GEvent arg0) {
			}
		});

		/*
		 * add a menuitem in the popupmenu of galatee for displaying image info.
		 */
		JMenuItem menu_item = new JMenuItem("Image info");
		g0.getPopup_menu().add(menu_item);
		menu_item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final GItem gitem = (GItem) g0.getTable().getValueAt(
						g0.getTable().getSelectedRow(),
						g0.getTable().getSelectedColumn());
				new Thread() {
					@Override
					public void run() {
						new ImageInfoFrame(gitem.getLocalFilepath());
					}
				}.start();
			}
		});

		if (galatee != null) {
			galatee_panel_container.removeAll();
		}

		galatee = g0;
		galatee_panel_container.add(galatee, BorderLayout.CENTER);

		System.gc();

	}

	/***********************************************************************
	 * implementation of ActionListener.
	 **********************************************************************/

	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();

		if (actionCommand.equals(ACTION_COMMAND_DO_QUERY)) {
			String query = jtf0.getText();
			if (query.equals("")) {
				return;
			}

			//querying with Lucene.
			LabelMeLogger.getBrowserLogger().log(Level.INFO,
					"querying with Lucene (WhitespaceAnalyzer).");

			searcher = new LuceneLabelMeSearcher(
					lucene_index_whitespace_analyzer_path,
					new WhitespaceAnalyzer());

			searcher.doQuery(jcb0.getSelectedItem() + ":" + query);
			nbOfResults = searcher.getNbOfResults();

			if (nbOfResults == 0) {
				results_status.setText("0 results");
				return;
			}

			pageOfResultsViewed = 0;
			results = searcher.getResults(pageOfResultsViewed
					* numOfResultsPerPage, numOfResultsPerPage);

			/*
			 * updating the results_status.
			 */
			updateResultsStatus();

			/*
			 * updating the Galatee panel.
			 */
			updateGalateePanel(results.getListfiles(), results.getData());
		}

		else

		if (actionCommand.equals(ACTION_COMMAND_PREVIOUS_RESULT_PAGE)) {
			if (pageOfResultsViewed != 0) {
				pageOfResultsViewed--;
				results = searcher.getResults(pageOfResultsViewed
						* numOfResultsPerPage, numOfResultsPerPage);

				/*
				 * updating the results_status.
				 */
				updateResultsStatus();

				/*
				 * updating the Galatee panel.
				 */
				updateGalateePanel(results.getListfiles(), results.getData());
			}
		}

		else

		if (actionCommand.equals(ACTION_COMMAND_NEXT_RESULT_PAGE)) {
			if ((pageOfResultsViewed + 1) * numOfResultsPerPage < nbOfResults) {
				pageOfResultsViewed++;
				results = searcher.getResults(pageOfResultsViewed
						* numOfResultsPerPage, numOfResultsPerPage);

				/*
				 * updating the results_status.
				 */
				updateResultsStatus();

				/*
				 * updating the Galatee panel.
				 */
				updateGalateePanel(results.getListfiles(), results.getData());
			}
		}
	}

	public static void main(String[] args) {
		// setting the system look and feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		JFrame f = new JFrame();

		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		p.add(new WhitespaceAnalyzerIndexSearchPanel(), BorderLayout.CENTER);
		f.getContentPane().add(p);

		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(1024, 768);
		f.setLocationRelativeTo(null);
		f.setVisible(true);
	}

}
