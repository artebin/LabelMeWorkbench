package net.trevize.labelme;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * 
 * 
 * @author Nicolas James <nicolas.james@gmail.com> [[http://njames.trevize.net]]
 * LabelMeWorkbenchProperties.java - Feb 22, 2010
 */

public class LabelMeWorkbenchProperties {

	private static final String config_properties_filename = "./config/LabelMeWorkbench.properties";

	private static Properties prop;

	private static String LabelMeImagesPath;
	private static String key_LabelMeImagesPath = "LabelMeImagesPath";

	private static String LabelMeAnnotationsPath;
	private static String key_LabelMeAnnotationsPath = "LabelMeAnnotationsPath";

	private static String DbxmlInstallLibPath;
	private static String key_DbxmlInstallLibPath = "DbxmlInstallLibPath";

	private static String LabelMeLuceneIndex_KeywordAnalyzerPath;
	private static String key_LabelMeLuceneIndex_KeywordAnalyzerPath = "LabelMeLuceneIndex_KeywordAnalyzerPath";

	private static String LabelMeLuceneIndex_WhitespaceAnalyzerPath;
	private static String key_LabelMeLuceneIndex_WhitespaceAnalyzerPath = "LabelMeLuceneIndex_WhitespaceAnalyzerPath";

	private static String LabelMeDbxmlIndexPath;
	private static String key_LabelMeDbxmlIndexPath = "LabelMeDbxmlIndexPath";

	private static int NumOfResultsPerPage;
	private static String key_NumOfResultsPerPage = "NumOfResultsPerPage";

	private static String Lucene_log_directory;
	private static String key_Lucene_log_directory = "Lucene_log_directory";

	private static String DBXML_log_directory;
	private static String key_DBXML_log_directory = "DBXML_log_directory";

	private static boolean Write_logs;
	private static String key_Write_logs = "Write_logs";

	private static void init() {
		prop = new Properties();
		try {
			FileInputStream fis = new FileInputStream(
					config_properties_filename);
			prop.load(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		LabelMeImagesPath = prop.getProperty(key_LabelMeImagesPath);

		LabelMeAnnotationsPath = prop.getProperty(key_LabelMeAnnotationsPath);

		DbxmlInstallLibPath = prop.getProperty(key_DbxmlInstallLibPath);

		LabelMeLuceneIndex_KeywordAnalyzerPath = prop
				.getProperty(key_LabelMeLuceneIndex_KeywordAnalyzerPath);

		LabelMeLuceneIndex_WhitespaceAnalyzerPath = prop
				.getProperty(key_LabelMeLuceneIndex_WhitespaceAnalyzerPath);

		LabelMeDbxmlIndexPath = prop.getProperty(key_LabelMeDbxmlIndexPath);

		NumOfResultsPerPage = Integer.parseInt(prop
				.getProperty(key_NumOfResultsPerPage));

		Lucene_log_directory = prop.getProperty(key_Lucene_log_directory);

		DBXML_log_directory = prop.getProperty(key_DBXML_log_directory);

		Write_logs = Boolean.parseBoolean(prop.getProperty(key_Write_logs));
	}

	public static String getLabelMeImagesPath() {
		if (prop == null) {
			init();
		}
		return LabelMeImagesPath;
	}

	public static String getLabelMeAnnotationsPath() {
		if (prop == null) {
			init();
		}
		return LabelMeAnnotationsPath;
	}

	public static String getDbxmlInstallLibPath() {
		if (prop == null) {
			init();
		}
		return DbxmlInstallLibPath;
	}

	public static String getLabelMeLuceneIndex_KeywordAnalyzerPath() {
		if (prop == null) {
			init();
		}
		return LabelMeLuceneIndex_KeywordAnalyzerPath;
	}

	public static String getLabelMeLuceneIndex_WhitespaceAnalyzerPath() {
		if (prop == null) {
			init();
		}
		return LabelMeLuceneIndex_WhitespaceAnalyzerPath;
	}

	public static String getLabelMeDbxmlIndexPath() {
		if (prop == null) {
			init();
		}
		return LabelMeDbxmlIndexPath;
	}

	public static int getNumOfResultsPerPage() {
		if (prop == null) {
			init();
		}
		return NumOfResultsPerPage;
	}

	public static String getLucene_log_directory() {
		if (prop == null) {
			init();
		}
		return Lucene_log_directory;
	}

	public static String getDBXML_log_directory() {
		if (prop == null) {
			init();
		}
		return DBXML_log_directory;
	}

	public static boolean isWrite_logs() {
		if (prop == null) {
			init();
		}
		return Write_logs;
	}

}
