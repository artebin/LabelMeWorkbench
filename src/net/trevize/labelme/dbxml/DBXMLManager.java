package net.trevize.labelme.dbxml;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.comparator.NameFileComparator;

import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.Environment;
import com.sleepycat.db.EnvironmentConfig;
import com.sleepycat.dbxml.XmlContainer;
import com.sleepycat.dbxml.XmlException;
import com.sleepycat.dbxml.XmlManager;
import com.sleepycat.dbxml.XmlManagerConfig;
import com.sleepycat.dbxml.XmlResults;
import com.sleepycat.dbxml.XmlUpdateContext;
import com.sleepycat.dbxml.XmlValue;

/**
 * This class is a database manager for Berkeley DB XML for mining the LabelMe 
 * dataset.
 * See [[http://njames.trevize.net/wiki/doku.php/berkeley_db_xml]].
 * 
 * Do not forget to add the path to ~dbxml/lib in LD_LIBRARY_PATH.
 * 
 * Do not forget to clean and normalize the dataset before !
 * 
 * @author Nicolas James <nicolas.james@gmail.com> [[http://njames.trevize.net]]
 * BDBXMLManager.java - Jan 9, 2009
 */

public class DBXMLManager {

	//dbxml environnement.
	private Environment myEnv;

	//environnement file for the dbxml database.
	private File envHome;
	private XmlManager xmlManager = null;
	private XmlContainer xmlContainer = null;

	private String example_query0 = "distinct-values(collection('LabelMe.dbxml')/annotation/object/name/text())";
	private String example_query1 = "for $i in collection('LabelMe.dbxml') where $i/annotation/object[name='car'] return concat($i/annotation/folder/text(), concat('/',$i/annotation/filename/text()))";

	//the path to index when indexing.
	private String dir_dataset_path;

	//used during the indexing.
	private int file_id;

	private DocumentBuilderFactory dbfactory = DocumentBuilderFactory
			.newInstance();

	public DBXMLManager(String pathEnv) {
		File pathEnv_file = new File(pathEnv);
		if (!pathEnv_file.exists()) {
			pathEnv_file.mkdirs();
		}

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

			myEnv = new Environment(envHome, envConf);

			XmlManagerConfig managerConfig = new XmlManagerConfig();
			managerConfig.setAdoptEnvironment(true);
			xmlManager = new XmlManager(myEnv, managerConfig);
		} catch (DatabaseException de) {
			// Exception handling goes here
		} catch (FileNotFoundException fnfe) {
			// Exception handling goes here
		}
	}

	public void openContainer(String pathToContainer) {
		System.out.println("\tDBXMLManager.openContainer begin.");

		/*
		 * I don't know why but indicating the current directory ./ make the
		 * openContainer() method fails.
		 */
		try {
			xmlContainer = xmlManager.openContainer("LabelMe.dbxml");
		} catch (XmlException e) {
			System.out.println("XmlException" + e.getMessage());
		}

		System.out.println("\tDBXMLManager.openContainer end.");
	}

	public void createContainer(String pathToContainer) {
		System.out.println("\tDBXMLManager.createContainer begin.");

		try {
			xmlManager = new XmlManager();
			xmlManager.setDefaultContainerType(XmlContainer.NodeContainer);
			xmlContainer = xmlManager.createContainer(pathToContainer);
		} catch (XmlException e) {
			System.out.println("XmlException" + e.getMessage());
		} catch (java.io.FileNotFoundException e) {
			System.out.println("FileNotFoundException" + e.getMessage());
		}

		System.out.println("\tDBXMLManager.createContainer end.");
	}

	public void indexDataset(String dir_dataset_path, String pathToContainer) {
		System.out.println("\tDBXMLManager.indexDataset begin.");

		createContainer(pathToContainer);
		this.dir_dataset_path = dir_dataset_path;
		file_id = 0;
		indexDir("");

		System.out.println("\tDBXMLManager.indexDataset end.");
	}

	private void indexDir(String dir_path) {
		System.out.println("\tDBXMLManager.indexDir begin.");

		System.out.println("\tfor directory: " + dir_dataset_path
				+ File.separator + dir_path);

		File[] lf = new File(dir_dataset_path + File.separator + dir_path)
				.listFiles();

		//inserting using a lexicographic order on directory names. 
		Arrays.sort(lf, NameFileComparator.NAME_COMPARATOR);

		for (File f : lf) {
			if (f.isDirectory()) {
				indexDir(dir_path + File.separator + f.getName());
			} else {
				if (f.getName().endsWith(".xml")) {
					indexFile(dir_path, f.getName());
				}
			}
		}

		System.out.println("\tDBXMLManager.indexDir end.");
	}

	private void indexFile(String dir_path, String file_name) {
		String file_path = dir_dataset_path + File.separator + dir_path
				+ File.separator + file_name;

		//creating the XmlUpdateContext.
		XmlUpdateContext updateContext = null;
		try {
			updateContext = xmlManager.createUpdateContext();
		} catch (XmlException e) {
			e.printStackTrace();
		}

		//read the file and extract it in a String object.
		StringBuffer sb = null;
		try {
			File f = new File(file_path);
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);
			sb = new StringBuffer();
			char[] cbuf = new char[256];
			int len = 0;
			while ((len = br.read(cbuf, 0, 256)) > 0) {
				sb.append(cbuf, 0, len);
			}
			//closing files.
			br.close();
			fr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//put the document in the database.
		try {
			xmlContainer.putDocument(file_id + "_" + file_name, sb.toString(),
					updateContext, null);
		} catch (XmlException e) {
			e.printStackTrace();
		}

		//synchronize the container.
		//this operation is a very time consuming operation...
		//		try {
		//			xmlContainer.sync();
		//		} catch (XmlException e) {
		//			e.printStackTrace();
		//		}

		//increment the file ID.
		file_id++;
	}

	//listing all documents.
	public void listDocuments() {
		System.out.println("\tDBXMLManager.listDocuments begin.");

		try {
			XmlResults results = xmlContainer.getAllDocuments(null);
			while (results.hasNext()) {
				XmlValue xmlValue = results.next();
				System.out.println(xmlValue.asString());
			}
		} catch (XmlException e) {
			e.printStackTrace();
		}

		System.out.println("\tDBXMLManager.listDocuments end.");
	}

	public static void main(String[] args) {
		//FOR TESTING PURPOSE
		//do not forget to add the path to ~dbxml/lib in LD_LIBRARY_PATH.

		String dir_dataset_path = "/home/nicolas/datasets/LabelMe-jul2009/database/annotations";
		String dbxml_container_path = "/home/nicolas/datasets/LabelMe-jul2009/LabelMeDBXMLIndex";
		
		DBXMLManager dbxml_manager = new DBXMLManager("./");
		dbxml_manager.createContainer(dbxml_container_path);
		dbxml_manager.indexDataset(dir_dataset_path, dbxml_container_path);

		dbxml_manager.openContainer("LabelMe.dbxml");
		dbxml_manager.listDocuments();

		//		String query0 = "distinct-values(collection('LabelMe.dbxml')/annotation/object/name/text())";
		//
		//		String query1 = "for $i in collection('LabelMe.dbxml') where $i/annotation/object[name='car'] return concat($i/annotation/folder/text(), concat('/',$i/annotation/filename/text()))";
		//
		//		dbxml_manager.doQuery(query1);
		//		dbxml_manager.searchByAnnotation("car");
		//
		//		dbxml_manager.modifyDocument();
		//		dbxml_manager.updateDocument();
		//
		//		String query0 = "for $i in collection('LabelMe.dbxml') where $i/annotation/folder[name='car'] and $i/annotation/filename[name=''] return $i/text()";
	}

}
