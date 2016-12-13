package cn.edu.pku.EOSCN.test;

public class ASD {
	  public void search(String[] args) { 
		    // Nothing given? Search for "Web". 
		    String querystr = args.length > 0 ? args[0] : "Web"; 
		 
		    try { 
		      // Instantiate a query parser 
		      QueryParser parser = new QueryParser(Version.LUCENE_30, "content", 
		          analyzer); 
		      // Parse 
		      Query q = parser.parse(querystr); 
		      // We look for top-10 results 
		      int hitsPerPage = 10; 
		      // Instantiate a searcher 
		      IndexSearcher searcher = new IndexSearcher(index, true); 
		      // Ranker 
		      TopScoreDocCollector collector = TopScoreDocCollector.create( 
		          hitsPerPage, true); 
		      // Search! 
		      searcher.search(q, collector); 
		      // Retrieve the top-10 documents 
		      ScoreDoc[] hits = collector.topDocs().scoreDocs; 
		 
		      // Display results 
		      System.out.println("Found " + hits.length + " hits."); 
		      for (int i = 0; i < hits.length; ++i) { 
		        int docId = hits[i].doc; 
		        Document d = searcher.doc(docId); 
		        System.out.println((i + 1) + ". " + d.get("content")); 
		      } 
		 
		      // Close the searcher 
		      searcher.close(); 
		    } catch (Exception e) { 
		      System.out.println("Got an Exception: " + e.getMessage()); 
		    } 
		  }

}
class QueryParser{}