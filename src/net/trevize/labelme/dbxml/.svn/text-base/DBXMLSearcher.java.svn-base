package net.trevize.labelme.dbxml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilderFactory;

import net.trevize.labelme.LabelMeResults;
import net.trevize.labelme.LabelMeSearcher;
import net.trevize.labelme.LabelMeWorkbenchProperties;

import org.apache.commons.lang.mutable.MutableInt;
import org.w3c.dom.DOMException;

import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.Environment;
import com.sleepycat.db.EnvironmentConfig;
import com.sleepycat.dbxml.XmlContainer;
import com.sleepycat.dbxml.XmlException;
import com.sleepycat.dbxml.XmlManager;
import com.sleepycat.dbxml.XmlManagerConfig;
import com.sleepycat.dbxml.XmlQueryContext;
import com.sleepycat.dbxml.XmlQueryExpression;
import com.sleepycat.dbxml.XmlResults;
import com.sleepycat.dbxml.XmlValue;

/**
 * 
 * 
 * @author Nicolas James <nicolas.james@gmail.com> [[http://njames.trevize.net]]
 * DBXMLSearcher.java - Nov 9, 2009
 */

public class DBXMLSearcher extends LabelMeSearcher {

	public static String xqueryQueryImagesByObjectName = "for $i in collection('LabelMe.dbxml') where $i/annotation/object[name=$annot and deleted='0'] "
			+ "return concat($i/annotation/folder/text(),'/',$i/annotation/filename/text(),',',"
			+ "string-join(for $j in $i/annotation/object/name/text() return $j, \",\")"
			+ ")";

	//dbxml environnement.
	private Environment myEnv;

	//environnement file for the dbxml database.
	private File envHome;

	private String example_query0 = "distinct-values(collection('LabelMe.dbxml')/annotation/object/name/text())";

	private String example_query1 = "for $i in collection('LabelMe.dbxml') where $i/annotation/object[name='car'] return concat($i/annotation/folder/text(), concat('/',$i/annotation/filename/text()))";

	private XmlManager xmlManager = null;

	private XmlContainer xmlContainer = null;

	private DocumentBuilderFactory dbfactory = DocumentBuilderFactory
			.newInstance();

	private XmlResults xmlResults;

	public DBXMLSearcher(String pathEnv, String pathToContainer) {
		Environment myEnv = null;
		File envHome = new File(pathEnv);

		try {
			EnvironmentConfig envConf = new EnvironmentConfig();

			/*
			 * If the environment does not exits, create it.
			 */
			envConf.setAllowCreate(true);

			/*
			 * Turn on the shared memory region.
			 */
			envConf.setInitializeCache(true);

			// Turn on the locking subsystem.
			envConf.setInitializeLocking(true);
			// Turn on the logging subsystem.
			envConf.setInitializeLogging(true);
			// Turn on the transactional subsystem.
			envConf.setTransactional(true);

			//the value by default for mutexes seems to low... i'm get some errors about that.
			envConf.setMaxMutexes(4096);
			envConf.setMutexIncrement(1);

			myEnv = new Environment(envHome, envConf);

			XmlManagerConfig managerConfig = new XmlManagerConfig();
			managerConfig.setAdoptEnvironment(true);
			xmlManager = new XmlManager(myEnv, managerConfig);
		} catch (DatabaseException de) {
			// Exception handling goes here
		} catch (FileNotFoundException fnfe) {
			// Exception handling goes here
		}

		openContainer(pathToContainer);
	}

	private void openContainer(String pathToContainer) {
		/*
		 * I don't know why but indicating the current directory ./ make the
		 * openContainer() method fails.
		 */
		try {
			xmlContainer = xmlManager.openContainer("LabelMe.dbxml");
		} catch (XmlException e) {
			System.out.println("XmlException" + e.getMessage());
		}
	}

	public void searchByObjectsName(String annotText) {
		System.out.println("\tDBXMLSearcher.searchByObjectsName begin.");

		//		String xqueryQuery = "for $i in collection('LabelMe.dbxml') where $i/annotation/object[name=$annot]"
		//				+ "return concat('<labelMeDocument><folder>',$i/annotation/folder/text(),'</folder><filename>',$i/annotation/filename/text(),'</filename></labelMeDocument>')";

		String xqueryQuery = "for $i in collection('LabelMe.dbxml') where $i/annotation/object[name=$annot and deleted='0'] "
				+ "return concat($i/annotation/folder/text(),'/',$i/annotation/filename/text(),',',"
				+ "string-join(for $j in $i/annotation/object/name/text() return $j, \",\")"
				+ ")";

		try {
			XmlQueryContext context = xmlManager.createQueryContext();

			context.setVariableValue("annot", new XmlValue(annotText));
		} catch (XmlException e) {
			e.printStackTrace();
		}

		System.out.println("\tDBXMLSearcher.searchByObjectsName end.");
	}

	public void doXQueryQuery(String xqueryQuery, XmlQueryContext context) {
		System.out.println("\tDBXMLSearcher.doXQueryQuery begin.");

		System.out.println("XQuery = " + xqueryQuery);

		Vector<String> listfiles = new Vector<String>();

		try {
			if (context == null) {
				context = xmlManager.createQueryContext();
			}

			XmlQueryExpression qe = xmlManager.prepare(xqueryQuery, context);

			xmlResults = qe.execute(context);
			super.setNbOfResults(xmlResults.size());
		} catch (XmlException e) {
			System.out.println("XmlException" + e.getMessage());
		}

		//display number of results.
		try {
			System.out.println(xmlResults.size() + " results.");
		} catch (XmlException e) {
			e.printStackTrace();
		}

		//writing query log.
		if (LabelMeWorkbenchProperties.isWrite_logs()) {
			writeQueryLog(xqueryQuery, xmlResults);
		}

		System.out.println("\tDBXMLSearcher.doXQueryQuery end.");
	}

	private void writeQueryLog(String query, XmlResults xmlResults) {
		System.out.println("\tDBXMLSearcher.writeQueryLog begin.");

		String logFilename = LabelMeWorkbenchProperties
				.getDBXML_log_directory()
				+ File.separator
				+ "query-"
				+ Calendar.getInstance().getTimeInMillis() + ".log";

		new File(logFilename).getParentFile().mkdirs();

		System.out.println("writing file " + logFilename);

		FileWriter fw = null;
		BufferedWriter bw = null;

		//write the query result in a file.
		try {
			fw = new FileWriter(logFilename);
			bw = new BufferedWriter(fw);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			try {
				bw.write("####\n" + query + "\n####\n");
			} catch (IOException e) {
				e.printStackTrace();
			}

			while (xmlResults.hasNext()) {
				XmlValue xmlValue = xmlResults.next();
				try {
					bw.write(xmlValue.asString() + "\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (XmlException e1) {
			e1.printStackTrace();
		}

		try {
			bw.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("\tDBXMLSearcher.writeQueryLog end.");
	}

	/***************************************************************************
	 * implementation of LabelMeSearcher.
	 **************************************************************************/

	@Override
	public void doQuery(String query) {
		System.out.println("\tDBXMLSearcher.doQuery begin.");
		super.setQuery(query);
		//searchByObjectsName(query);
		doXQueryQuery(query, null);
		System.out.println("\tDBXMLSearcher.doQuery end.");
	}

	@Override
	public LabelMeResults getResults(int idxFrom, int n) {

		System.out.println("\tDBXMLSearcher.getResults begin.");
		LabelMeResults results = new LabelMeResults();

		try {
			if (xmlResults.size() > 0 && idxFrom < xmlResults.size()) {

				xmlResults.reset();

				//go to the idxFrom-ith results.
				for (int j = 0; j < idxFrom; xmlResults.next(), ++j)
					;

				for (int i = idxFrom; i < xmlResults.size()
						&& (i - idxFrom) < n; i++) {

					XmlValue xmlValue = xmlResults.next();

					/*
					 * if the result of the XQuery is an XML document.
					 */
					/*
					Document d = dbfactory.newDocumentBuilder().parse(
							new InputSource(new StringReader(xmlValue
									.asString())));
					String folder = trimpp(d.getElementsByTagName("folder")
							.item(0).getTextContent());
					String filename = trimpp(d.getElementsByTagName("filename")
							.item(0).getTextContent());
					*/

					//getting filepath.
					String[] values = xmlValue.asString().split(",");
					results.getListfiles().add(values[0]);

					//getting image metadata.
					//getting the image filename.
					String image_filename = values[0].split("/")[1];

					//getting the annotation filename.
					String annotation_filename = image_filename.substring(0,
							image_filename.length() - ".jpg".length())
							+ ".xml";

					//getting all object names and their respective occurrences.
					HashMap<String, MutableInt> collapsedKeywords = new HashMap<String, MutableInt>();

					for (int j = 1; j < values.length; ++j) {
						if (!collapsedKeywords.keySet().contains(values[j])) {
							collapsedKeywords.put(values[j], new MutableInt(1));
						} else {
							collapsedKeywords.get(values[j]).increment();
						}
					}

					Vector<Object> data = new Vector<Object>();
					results.getData().add(data);

					/*
					 * the first element of a result data is the textual description of this result
					 */
					StringBuffer sb = new StringBuffer();
					sb.append(image_filename + "\n");
					if (collapsedKeywords != null) {
						for (String key : collapsedKeywords.keySet()) {
							sb.append(key + " (" + collapsedKeywords.get(key)
									+ ")\n");
						}
					}
					data.add(sb.toString());

					/*
					 * the second element of a result data is the image filename.
					 */
					data.add(image_filename);

					/*
					 * the third element of a result data is the annotation filename.
					 */
					data.add(annotation_filename);

					/*
					 * the fourth element of a result data is a Vector<String> that corresponds to polygon names.
					 */
					if (collapsedKeywords != null) {
						Vector<String> polygon_names = new Vector<String>();
						for (String key : collapsedKeywords.keySet()) {
							polygon_names.add(key);
						}
						data.add(polygon_names);
					}
				}
			}
		} catch (XmlException e) {
			e.printStackTrace();
		} catch (DOMException e) {
			e.printStackTrace();
		}

		System.out.println("\tDBXMLSearcher.getResults end.");

		return results;
	}

}
