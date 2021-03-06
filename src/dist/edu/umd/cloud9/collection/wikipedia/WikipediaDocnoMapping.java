/*
 * Cloud9: A MapReduce Library for Hadoop
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0 
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package edu.umd.cloud9.collection.wikipedia;

import java.io.IOException;
import java.util.Arrays;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.log4j.Logger;

import edu.umd.cloud9.collection.DocnoMapping;
import edu.umd.cloud9.util.FSLineReader;

/**
 * <p>
 * Provides a mapping between Wikipedia internal ids (docids) and
 * sequentially-numbered ints (docnos).
 * </p>
 * 
 * <p>
 * The <code>main</code> of this class provides a simple program for accessing
 * docno mappings. Command-line arguments are as follows:
 * </p>
 * 
 * <ul>
 * <li>getDocno, getDocid: get docno from docid or, get docid from docno</li>
 * <li>[mappings-file]: the mappings file</li>
 * <li>[docid/docno]: the docid or docno (optional)</li>
 * </ul>
 * 
 * @author Jimmy Lin
 */
public class WikipediaDocnoMapping implements DocnoMapping {

	private static final Logger sLogger = Logger.getLogger(WikipediaDocnoMapping.class);

	private int[] mDocids;

	/**
	 * Creates a <code>WikipediaDocnoMapping</code> object
	 */
	public WikipediaDocnoMapping() {
	}

	public int getDocno(String docid) {
		return Arrays.binarySearch(mDocids, Integer.parseInt(docid));
	}

	public String getDocid(int docno) {
		return String.valueOf(mDocids[docno]);
	}

	public void loadMapping(Path p, FileSystem fs) throws IOException {
		mDocids = WikipediaDocnoMapping.readDocnoMappingData(p, fs);
	}

	/**
	 * Creates a mappings file from the contents of a flat text file containing
	 * docid to docno mappings. This method is used by
	 * {@link BuildWikipediaDocnoMapping} internally.
	 * 
	 * @param inputFile
	 *            flat text file containing docid to docno mappings
	 * @param outputFile
	 *            output mappings file
	 * @throws IOException
	 */
	static public void writeDocnoMappingData(String inputFile, int n, String outputFile)
			throws IOException {
		sLogger.info("Writing " + n + " docids to " + outputFile);
		FSLineReader reader = new FSLineReader(inputFile);

		int cnt = 0;
		Text line = new Text();

		FSDataOutputStream out = FileSystem.get(new Configuration()).create(new Path(outputFile),
				true);
		out.writeInt(n);
		for (int i = 0; i < n; i++) {
			reader.readLine(line);
			String[] arr = line.toString().split("\\t");
			out.writeInt(Integer.parseInt(arr[0]));
			cnt++;
			if (cnt % 100000 == 0) {
				sLogger.info(cnt + " articles");
			}
		}
		out.close();
		sLogger.info("Done!\n");
	}

	/**
	 * Reads a mappings file into memory.
	 * 
	 * @param p
	 *            path to the mappings file
	 * @param fs
	 *            appropriate FileSystem
	 * @return an array of docids; the index position of each docid is its docno
	 * @throws IOException
	 */
	static public int[] readDocnoMappingData(Path p, FileSystem fs) throws IOException {
		FSDataInputStream in = fs.open(p);

		// docnos start at one, so we need an array that's one larger than
		// number of docs
		int sz = in.readInt() + 1;
		int[] arr = new int[sz];

		for (int i = 1; i < sz; i++) {
			arr[i] = in.readInt();
		}
		in.close();

		arr[0] = 0;

		return arr;
	}

	/**
	 * Simple program the provides access to the docno/docid mappings.
	 * 
	 * @param args
	 *            command-line arguments
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		if (args.length < 2) {
			System.out.println("usage: (getDocno|getDocid) [mapping-file] [docid/docno]");
			System.exit(-1);
		}

		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);

		System.out.println("loading mapping file " + args[1]);
		WikipediaDocnoMapping mapping = new WikipediaDocnoMapping();
		mapping.loadMapping(new Path(args[1]), fs);

		if (args[0].equals("getDocno")) {
			System.out.println("looking up docno for \"" + args[2] + "\"");
			int idx = mapping.getDocno(args[2]);
			if (idx > 0) {
				System.out.println(mapping.getDocno(args[2]));
			} else {
				System.err.print("Invalid docid!");
			}
		} else if (args[0].equals("getDocid")) {
			try {
				System.out.println("looking up docid for " + args[2]);
				System.out.println(mapping.getDocid(Integer.parseInt(args[2])));
			} catch (Exception e) {
				System.err.print("Invalid docno!");
			}
		} else {
			System.out.println("Invalid command!");
			System.out.println("usage: (getDocno|getDocid) [mapping-file] [docid/docno]");
		}
	}

}
