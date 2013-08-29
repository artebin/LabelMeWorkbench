package net.trevize.labelme.lucene;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import net.trevize.labelme.LabelMeResults;
import net.trevize.labelme.LabelMeSearcher;
import net.trevize.labelme.LabelMeWorkbenchProperties;

import org.apache.lucene.analysis.KeywordAnalyzer;

/**
 * These classes have been written primarily for the MOntoMatch code.
 * 
 * @author Nicolas James <nicolas.james@gmail.com> [[http://njames.trevize.net]]
 * InstanceSearcher.java - May 31, 2010
 */

public class InstanceSearcher {

	public static void getInstancesForQueriesListFile(String queries_filepath,
			int nbr_of_instances, String results_prefix_filepath) {
		LabelMeSearcher searcher = new LuceneLabelMeSearcher(
				LabelMeWorkbenchProperties
						.getLabelMeLuceneIndex_KeywordAnalyzerPath(),
				new KeywordAnalyzer());

		ArrayList<String> queries = new ArrayList<String>();
		try {
			FileReader fr = new FileReader(queries_filepath);
			BufferedReader br = new BufferedReader(fr);
			String line;
			while ((line = br.readLine()) != null) {
				queries.add(line);
			}
			br.close();
			fr.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		for (String query : queries) {
			searcher.doQuery("\"" + query + "\"");
			LabelMeResults results = searcher.getResults(0, nbr_of_instances);

			try {
				FileWriter fw = new FileWriter(results_prefix_filepath
						+ File.separator + "LabelMe_" + query.replace(' ', '_')
						+ ".txt");
				BufferedWriter bw = new BufferedWriter(fw);
				for (int i = 0; i < results.getListfiles().size(); ++i) {

					//build the line: image_path,polygon_name*
					StringBuffer sb = new StringBuffer();
					sb.append(results.getListfiles().get(i));
					Vector<String> polygon_names_list = (Vector<String>) results
							.getData().get(i)
							.get(LabelMeResults.DATA_INDEX_POLYGON_NAMES_LIST);
					for (String name : polygon_names_list) {
						sb.append("," + name);
					}
					sb.append("\n");

					bw.write(sb.toString());
				}
				bw.close();
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void getInstancesForQuery(String query, int nbr_of_instances,
			String results_filepath) {
		LabelMeSearcher searcher = new LuceneLabelMeSearcher(
				LabelMeWorkbenchProperties
						.getLabelMeLuceneIndex_KeywordAnalyzerPath(),
				new KeywordAnalyzer());

		searcher.doQuery("\"" + query + "\"");
		LabelMeResults results = searcher.getResults(0, nbr_of_instances);

		try {
			FileWriter fw = new FileWriter(results_filepath);
			BufferedWriter bw = new BufferedWriter(fw);
			for (int i = 0; i < results.getListfiles().size(); ++i) {

				//build the line: image_path,polygon_name*
				StringBuffer sb = new StringBuffer();
				sb.append(results.getListfiles().get(i));
				Vector<String> polygon_names_list = (Vector<String>) results
						.getData().get(i).get(
								LabelMeResults.DATA_INDEX_POLYGON_NAMES_LIST);
				for (String name : polygon_names_list) {
					sb.append("," + name);
				}
				sb.append("\n");

				bw.write(sb.toString());
			}
			bw.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
