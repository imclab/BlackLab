/*******************************************************************************
 * Copyright (c) 2010, 2012 Institute for Dutch Lexicology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package nl.inl.blacklab.indexers.alto;

import java.io.File;
import java.util.Properties;

import nl.inl.blacklab.index.Indexer;
import nl.inl.util.LogUtil;
import nl.inl.util.PropertiesUtil;

/**
 * The indexer class and main program for the ANW corpus.
 */
public class IndexAlto {
	/**
	 * If true, always wipes existing index. If false, appends to existing index.
	 */
	final static boolean CREATE_INDEX_FROM_SCRATCH = false;

	public static void main(String[] args) throws Exception {
		System.out.println("IndexAlto\n");
		if (args.length < 1 || args.length > 2) {
			System.out
					.println("Usage:\n"
							+ "  java nl.inl.blacklab.indexers.alto.IndexAlto <propfile> [<single_input_file>]\n"
							+ "(see docs for more information)");
			return;
		}
		File propFile = new File(args[0]);
		File baseDir = propFile.getParentFile();
		//String dataSetName = args[0];

		LogUtil.initLog4jBasic();

		// Do we wish to index a single input file?
		String whichFile = null;
		if (args.length == 2)
			whichFile = args[1]; // yes

		// Read property file
		System.out.println("Read property file\n");
		Properties properties = PropertiesUtil.readFromFile(propFile);

		// Metadata is in separate file for EDBO set
		// TODO: generalize this so we don't need this special case anymore
		AltoUtils.setMetadataFile(PropertiesUtil.getFileProp(properties, "metadataFile", null));

		// Where to create the index and UTF-16 content
		File indexDir = new File(baseDir, properties.getProperty("indexDir", "index"));
		if (!indexDir.isDirectory())
			indexDir.mkdir();

		boolean createLuceneIndex = CREATE_INDEX_FROM_SCRATCH;
		if (!createLuceneIndex && !isLuceneIndex(indexDir))
			createLuceneIndex = true;

		// The indexer tool
		System.out.println("Create Indexer\n");
		Indexer indexer = new Indexer(indexDir, createLuceneIndex, DocIndexerAlto.class);
		try {
			// How many documents to process (0 = all of them)
			int maxDocs = PropertiesUtil.getIntProp(properties, "maxDocs", 0);
			if (maxDocs > 0)
				indexer.setMaxDocs(maxDocs);

			// Where the source files are
			File inputDir = new File(baseDir, properties.getProperty("inputDir", "input"));

			// Index a directory
			File fileToIndex = inputDir;
			if (whichFile != null)
				fileToIndex = new File(inputDir, whichFile);
			System.out.println("Start indexing\n");
			indexer.index(fileToIndex);
		} catch (Exception e) {
			System.err.println("An error occurred, aborting indexing. Error details follow.");
			e.printStackTrace();
		} finally {
			// Finalize and close the index.
			indexer.close();
		}
	}

	private static boolean isLuceneIndex(File indexDir) {
		boolean found = false;
		for (File f : indexDir.listFiles()) {
			if (f.getName().startsWith("segments")) {
				found = true;
				break;
			}
		}
		return found;
	}
}