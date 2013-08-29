package net.trevize.labelme.lucene;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Vector;

import net.trevize.labelme.LabelMeResults;
import net.trevize.labelme.LabelMeSearcher;
import net.trevize.labelme.LabelMeWorkbenchProperties;

import org.apache.commons.lang.mutable.MutableInt;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;

/**
 * 
 * 
 * @author Nicolas James <nicolas.james@gmail.com> [[http://njames.trevize.net]]
 * LuceneLabelMeSearcher.java - May 28, 2009
 */

public class LuceneLabelMeSearcher extends LabelMeSearcher {

	private String index_path;
	private Analyzer analyzer;
	private IndexSearcher index_searcher;
	private TopDocs topdocs;

	private String field;
	private String query_string;

	public LuceneLabelMeSearcher(String indexPath, Analyzer analyzer) {
		this.index_path = indexPath;
		this.analyzer = analyzer;

		//to prevent org.apache.lucene.search.BooleanQuery$TooManyClauses: maxClauseCount is set to 1024
		BooleanQuery.setMaxClauseCount(Integer.MAX_VALUE);

		try {
			index_searcher = new IndexSearcher(new SimpleFSDirectory(new File(
					indexPath)));
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private TopDocs search(String field, String queryString) {
		this.field = field;
		this.query_string = queryString;

		System.out.println("\tLuceneLabelMeSearcher.search begin.");
		topdocs = null;

		QueryParser qparser = new QueryParser(Version.LUCENE_30, field,
				analyzer);
		qparser.setAllowLeadingWildcard(true);
		Query query = null;
		try {
			query = qparser.parse(queryString);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		try {
			topdocs = index_searcher.search(query, index_searcher
					.getIndexReader().numDocs());
		} catch (IOException e) {
			e.printStackTrace();
		}

		//display number of topdocs.
		System.out.println(topdocs.totalHits + " topdocs.");

		//writing query log.
		if (LabelMeWorkbenchProperties.isWrite_logs()) {
			writeQueryLog();
		}

		System.out.println("\tLuceneLabelMeSearcher.search end.");

		return topdocs;
	}

	private void writeQueryLog() {
		System.out.println("\tLuceneLabelMeSearcher.writeQueryLog begin.");

		String logFilename = LabelMeWorkbenchProperties
				.getLucene_log_directory()
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
			bw.write("####\n" + query_string + "\n####\n");
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (topdocs.totalHits > 0) {
			for (int i = 0; i < topdocs.totalHits; i++) {
				try {
					Document d = index_searcher.doc(topdocs.scoreDocs[i].doc);
					bw.write(d.get(LuceneLabelMeDocument.FIELD_IMAGE_FOLDER
							.toString())
							+ File.separator
							+ d.get(LuceneLabelMeDocument.FIELD_IMAGE_FILENAME
									.toString()) + "\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		try {
			bw.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("\tLuceneLabelMeSearcher.writeQueryLog end.");
	}

	/***************************************************************************
	 * implementation of LabelMeSearcher.
	 **************************************************************************/

	@Override
	public void doQuery(String query) {
		super.setQuery(query);

		topdocs = search(LuceneLabelMeDocument.FIELD_OBJECT_NAME, query);

		super.setNbOfResults(topdocs.totalHits);
	}

	@Override
	public LabelMeResults getResults(int idxFrom, int n) {
		System.out.println("\tLuceneLabelMeSearcher.getResults begin.");
		LabelMeResults results = new LabelMeResults();

		if (topdocs.totalHits > 0 && idxFrom < topdocs.totalHits) {
			for (int i = idxFrom; i < topdocs.totalHits && (i - idxFrom) < n; i++) {

				Document d = null;
				try {
					d = index_searcher.doc(topdocs.scoreDocs[i].doc);
				} catch (CorruptIndexException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				//getting filepath.
				String filepath = d
						.get(LuceneLabelMeDocument.FIELD_IMAGE_FOLDER)
						+ "/"
						+ d.get(LuceneLabelMeDocument.FIELD_IMAGE_FILENAME)
								.toString();
				results.getListfiles().add(filepath);

				//getting image metadata.
				//getting the image filename.
				String image_filename = d.get(
						LuceneLabelMeDocument.FIELD_IMAGE_FILENAME).toString();

				//getting the annotation filename.
				String annotation_filename = d.get(
						LuceneLabelMeDocument.FIELD_ANNOTATION_FILENAME)
						.toString();

				//getting all object names and their respective occurrences.
				String[] objectnames = d
						.getValues(LuceneLabelMeDocument.FIELD_OBJECT_NAME);

				//if there is one or several named object in the current document.
				HashMap<String, MutableInt> collapsedKeywords = null;
				if (objectnames != null && objectnames.length != 0) {
					collapsedKeywords = new HashMap<String, MutableInt>();

					//computing the occurrences.
					for (int j = 0; j < objectnames.length; ++j) {
						if (!collapsedKeywords.keySet()
								.contains(objectnames[j])) {
							collapsedKeywords.put(objectnames[j],
									new MutableInt(1));
						} else {
							collapsedKeywords.get(objectnames[j]).increment();
						}
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

		System.out.println("\tLuceneLabelMeSearcher.getResults end.");

		return results;
	}

}
