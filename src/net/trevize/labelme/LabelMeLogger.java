package net.trevize.labelme;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LabelMeLogger {

	public static final String file_text_cleaner_normalizer_filepath = "./logs/cleaner_normalizer-logs.txt";
	public static final String file_text_indexer_filepath = "./logs/indexer-logs.txt";
	public static final String file_text_browser_filepath = "./logs/browser-logs.txt";

	private static Logger logger_cleaner_normalizer;
	private static FileHandler file_txt_cleaner_normalizer;
	private static Logger logger_indexer;
	private static FileHandler file_txt_indexer;
	private static Logger logger_browser;
	private static FileHandler file_txt_browser;
	private static SimpleFormatter formatter_txt;

	public static void setup() {
		formatter_txt = new SimpleFormatter();

		// logger for the cleaner-normalizer
		logger_cleaner_normalizer = Logger
				.getLogger("net.trevize.labelme.LabelMeCleanerNormalizer");
		logger_cleaner_normalizer.setLevel(Level.INFO);
		try {
			file_txt_cleaner_normalizer = new FileHandler(
					file_text_cleaner_normalizer_filepath);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		file_txt_cleaner_normalizer.setFormatter(formatter_txt);
		logger_cleaner_normalizer.addHandler(file_txt_cleaner_normalizer);

		//logger for the indexer
		logger_indexer = Logger.getLogger("net.trevize.labelme.LabelMeIndexer");
		logger_indexer.setLevel(Level.INFO);
		try {
			file_txt_indexer = new FileHandler(file_text_indexer_filepath);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		file_txt_indexer.setFormatter(formatter_txt);
		logger_indexer.addHandler(file_txt_indexer);

		//logger for the browser
		logger_browser = Logger
				.getLogger("net.trevize.labelme.browser.LabelMeBrowser");
		logger_browser.setLevel(Level.INFO);
		try {
			file_txt_browser = new FileHandler(file_text_browser_filepath);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		file_txt_browser.setFormatter(formatter_txt);
		logger_browser.addHandler(file_txt_browser);
	}

	public static Logger getCleanerNormalizerLogger() {
		if (logger_cleaner_normalizer == null) {
			LabelMeLogger.setup();
		}
		return logger_cleaner_normalizer;
	}

	public static Logger getIndexerLogger() {
		if (logger_indexer == null) {
			LabelMeLogger.setup();
		}
		return logger_indexer;
	}

	public static Logger getBrowserLogger() {
		if (logger_browser == null) {
			LabelMeLogger.setup();
		}
		return logger_browser;
	}

}
