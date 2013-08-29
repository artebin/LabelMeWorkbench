package net.trevize.labelme;

import java.io.File;

import net.trevize.labelme.dbxml.DBXMLManager;
import net.trevize.labelme.lucene.LuceneLabelMeIndexer;

/**
 * 
 * 
 * @author Nicolas James <nicolas.james@gmail.com> [[http://njames.trevize.net]]
 * LabelMeIndexer.java - Jun 2, 2009
 */

public class LabelMeIndexer {

	public static void main(String args[]) {

		/*
		 * Create Lucene index.
		 */

		/*
		 * create an index with a KeywordAnalyzer, taking the freetext annotation like one keyword.
		 */

//		String indexPath = LabelMeWorkbenchProperties
//				.getLabelMeLuceneIndex_KeywordAnalyzerPath();
//		LuceneLabelMeIndexer llmi = new LuceneLabelMeIndexer(
//				LuceneLabelMeIndexer.KEYWORD_ANALYZER);
//		llmi.indexLabelMeDataset(LabelMeWorkbenchProperties
//				.getLabelMeAnnotationsPath(), indexPath);

		/*
		 * create an index with a WhitespaceAnalyzer, so the freetext annotation is cut in token considering spaces.
		 */

//		indexPath = LabelMeWorkbenchProperties
//				.getLabelMeLuceneIndex_WhitespaceAnalyzerPath();
//		llmi = new LuceneLabelMeIndexer(
//				LuceneLabelMeIndexer.WHITESPACE_ANALYZER);
//		llmi.indexLabelMeDataset(LabelMeWorkbenchProperties
//				.getLabelMeAnnotationsPath(), indexPath);

		/*
		 * Create DBXML database.
		 */

		//do not forget to add the path to ~dbxml/lib in LD_LIBRARY_PATH.
		DBXMLManager bdbXML = new DBXMLManager(LabelMeWorkbenchProperties
				.getLabelMeDbxmlIndexPath());
		bdbXML.indexDataset(LabelMeWorkbenchProperties
				.getLabelMeAnnotationsPath(), LabelMeWorkbenchProperties
				.getLabelMeDbxmlIndexPath()
				+ File.separator + "LabelMe.dbxml");
	}

}
