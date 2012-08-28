/**
 *  Starschema Big Query JDBC Driver
 *  Copyright (C) 2012, Starschema Ltd.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * This Junit test runs queries throught the jdbc driver and checks their results
 * @author Horv�th Attila 
 */
package BQJDBC.QueryResultTest;

import static org.junit.Assert.fail;

import java.net.URLEncoder;
import java.sql.DriverManager;
import java.sql.SQLException;

import junit.framework.Assert;
import net.starschema.clouddb.jdbc.BQConnection;
import net.starschema.clouddb.jdbc.BQJDBCAPI;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

public class Timeouttest {
	
	private static java.sql.Connection con=null;
	Logger logger = Logger.getLogger(Timeouttest.class);
	/**
	 * Makes a new Bigquery Connection to Hardcoded URL and gives back the Connection to static con member.
	 */
	@Before
	public void NewConnection(){
		
	try {
		if(con==null || !con.isValid(0))
		{
			BasicConfigurator.configure();
			 	logger.info("Testing the JDBC driver");
				try {
					
					Class.forName("net.starschema.clouddb.jdbc.BQDriver");
					con = DriverManager.getConnection("jdbc:BQDriver:"+
						URLEncoder.encode("serviceacc230262422504-8hee1pe5017iq1i48jehgnj3vj517694@developer.gserviceaccount.com","UTF-8")+":"+
						URLEncoder.encode("C:\\key.p12","UTF-8")+":"+
						URLEncoder.encode("starschema.net:clouddb","UTF-8"));
				} 
				catch (Exception e){
					logger.fatal("Error in connection" + e.toString());
					fail("General Exception:" + e.toString());
				}
				logger.info(((BQConnection) con).getURLPART());
			}
	} catch (SQLException e1) {	
		e1.printStackTrace();	}
	try {
		logger.info("thread will sleep for 1 minute");
		Thread.sleep(1000*1); //1000milisec = 1 sec * 60 = 1 minute
								  
	} catch (InterruptedException e) {	e.printStackTrace();} 
	
	}
	@Test
	public void isvalidtest(){
		try {
			Assert.assertTrue(con.isValid(0));
		} catch (SQLException e) {
			
		}
	}
	
	@Test
	public void QueryResultTest01() {
		final String sql = "SELECT TOP(word, 10), COUNT(*) FROM publicdata:samples.shakespeare";
		final String description =  "The top 10 word from shakespeare #TOP #COUNT";
		String[][] expectation = new String[][]{
		{"you","yet","would","world","without","with","will","why","whose","whom"},
		{"42","42","42","42","42","42","42","42","42","42"}};
		
		logger.info("Test number: 01");
		logger.info("Running query:" + sql);
		
		java.sql.ResultSet Result = null;
		try {
			Result = con.createStatement().executeQuery(sql);
		} 
		catch (SQLException e) {
			logger.fatal("SQLexception" + e.toString());
			fail("SQLException" + e.toString());
		}
		Assert.assertNotNull(Result);

		logger.debug(description);
		if(logger.getLevel() == Level.DEBUG)
		printer(expectation);
		try {
			Assert.assertTrue("Comparing failed in the String[][] array", 
					comparer(expectation, BQJDBCAPI.GetQueryResult(Result)));
		} 
		catch (SQLException e) {
			logger.fatal("SQLexception" + e.toString());
			fail(e.toString());
		}		
	}
	
	@Test
	public void QueryResultTest02() {
		final String sql = "SELECT corpus FROM publicdata:samples.shakespeare GROUP BY corpus ORDER BY corpus LIMIT 5";
		final String description =  "The book names of shakespeare #GROUP_BY #ORDER_BY";
		String[][] expectation = new String[][]	
		{
		{"1kinghenryiv","1kinghenryvi","2kinghenryiv","2kinghenryvi","3kinghenryvi"}
		};
		logger.info("Test number: 02");
		logger.info("Running query:" + sql);
		
		java.sql.ResultSet Result = null;
		try {
			Result = con.createStatement().executeQuery(sql);
		} 
		catch (SQLException e) {
			logger.fatal("SQLexception" + e.toString());
			fail("SQLException" + e.toString());
		}
		Assert.assertNotNull(Result);

		logger.debug(description);
		if(logger.getLevel() == Level.DEBUG)
		printer(expectation);
		try {
			Assert.assertTrue("Comparing failed in the String[][] array", 
					comparer(expectation, BQJDBCAPI.GetQueryResult(Result)));
		} 
		catch (SQLException e) {
			logger.fatal("SQLexception" + e.toString());
			fail(e.toString());
		}		
	}
	
	@Test
	public void QueryResultTest03() {
		final String sql = "SELECT COUNT(DISTINCT web100_log_entry.connection_spec.remote_ip) AS num_clients FROM [guid754187384106:m_lab.2010_01] " +
				"WHERE IS_EXPLICITLY_DEFINED(web100_log_entry.connection_spec.remote_ip) AND IS_EXPLICITLY_DEFINED(web100_log_entry.log_time) " +
				"AND web100_log_entry.log_time > 1262304000 AND web100_log_entry.log_time < 1262476800";
		final String description =  "A sample query from google, but we don't have Access for the query table #ERROR #accessDenied #403";
		
		logger.info("Test number: 03");
		logger.info("Running query:" + sql);
		logger.debug(description);
		try {
			con.createStatement().executeQuery(sql);
		} 
		catch (SQLException e) {
			logger.debug("SQLexception" + e.toString());
			//fail("SQLException" + e.toString());
			Assert.assertTrue(e.getCause().toString().contains("Access Denied: Table measurement-lab:m_lab.2010_01: QUERY_TABLE"));
		}
	}

	@Test
	public void QueryResultTest04() {
		final String sql = "SELECT corpus FROM publicdata:samples.shakespeare WHERE LOWER(word)=\"lord\" GROUP BY corpus ORDER BY corpus DESC LIMIT 5;";
		final String description =  "A query which gets 5 of Shakespeare were the word lord is present";
		String[][] expectation = new String[][]
		{
		{"winterstale","various","twogentlemenofverona","twelfthnight","troilusandcressida"}
		};
		
		logger.info("Test number: 04");
		logger.info("Running query:" + sql);
		
		java.sql.ResultSet Result = null;
		try {
			Result = con.createStatement().executeQuery(sql);
		} 
		catch (SQLException e) {
			logger.fatal("SQLexception" + e.toString());
			fail("SQLException" + e.toString());
		}
		Assert.assertNotNull(Result);

		logger.debug(description);
		if(logger.getLevel() == Level.DEBUG)
		printer(expectation);
		try {
			Assert.assertTrue("Comparing failed in the String[][] array", 
					comparer(expectation, BQJDBCAPI.GetQueryResult(Result)));
		} 
		catch (SQLException e) {
			logger.fatal("SQLexception" + e.toString());
			fail(e.toString());
		}		
	}
	
	@Test
	public void QueryResultTest05() {
		final String sql = "SELECT word FROM publicdata:samples.shakespeare WHERE word=\"huzzah\"";
		final String description =  "The word \"huzzah\" NOTE: It doesn't appear in any any book, so it returns with a null #WHERE";
		
		logger.info("Test number: 05");
		logger.info("Running query:" + sql);
		
		java.sql.ResultSet Result = null;
		try {
			Result = con.createStatement().executeQuery(sql);
			logger.debug(Result.getMetaData().getColumnCount());
		} 
		catch (SQLException e) {
			logger.fatal("SQLexception" + e.toString());
			fail("SQLException" + e.toString());
		}
		Assert.assertNotNull(Result);

		logger.debug(description);
		try {
			Assert.assertFalse(Result.first());
		} 
		catch (SQLException e) {
			logger.fatal("SQLexception" + e.toString());
			fail(e.toString());
		}		
	}
	
	@Test
	public void QueryResultTest06() {
		final String sql = "SELECT corpus_date,SUM(word_count) FROM publicdata:samples.shakespeare GROUP BY corpus_date ORDER BY corpus_date DESC LIMIT 5;";
		final String description =  "A query which gets how many words Shapespeare wrote in a year (5 years displayed descending)";
		String[][] expectation = new String[][]
		{
		{"1612","1611","1610","1609","1608"},
		{"26265","17593","26181","57073","19846"}
		};
		
		logger.info("Test number: 06");
		logger.info("Running query:" + sql);
		
		java.sql.ResultSet Result = null;
		try {
			Result = con.createStatement().executeQuery(sql);
		} 
		catch (SQLException e) {
			logger.fatal("SQLexception" + e.toString());
			fail("SQLException" + e.toString());
		}
		Assert.assertNotNull(Result);

		logger.debug(description);
		if(logger.getLevel() == Level.DEBUG)
		printer(expectation);
		try {
			Assert.assertTrue("Comparing failed in the String[][] array", 
					comparer(expectation, BQJDBCAPI.GetQueryResult(Result)));
		} 
		catch (SQLException e) {
			logger.fatal("SQLexception" + e.toString());
			fail(e.toString());
		}		
	}
	
	@Test
	public void QueryResultTest07() {
		final String sql = "SELECT corpus, SUM(word_count) as w_c FROM publicdata:samples.shakespeare GROUP BY corpus HAVING w_c > 20000 ORDER BY w_c ASC LIMIT 5;";
		final String description =  "A query which gets Shakespeare were there are more words then 20000 (only 5 is displayed ascending)";
		String[][] expectation = new String[][]
		{
		{"juliuscaesar","twelfthnight","titusandronicus","kingjohn","tamingoftheshrew"},
		{"21052","21633","21911","21983","22358"}
		};
		
		logger.info("Test number: 07");
		logger.info("Running query:" + sql);
		
		java.sql.ResultSet Result = null;
		try {
			Result = con.createStatement().executeQuery(sql);
		} 
		catch (SQLException e) {
			logger.fatal("SQLexception" + e.toString());
			fail("SQLException" + e.toString());
		}
		Assert.assertNotNull(Result);

		logger.debug(description);
		if(logger.getLevel() == Level.DEBUG)
		printer(expectation);
		try {
			Assert.assertTrue("Comparing failed in the String[][] array", 
					comparer(expectation, BQJDBCAPI.GetQueryResult(Result)));
		} 
		catch (SQLException e) {
			logger.fatal("SQLexception" + e.toString());
			fail(e.toString());
		}		
	}
	
	@Test
	public void QueryResultTest08() {
		final String sql = "SELECT corpus, MAX(word_count) as m, word FROM publicdata:samples.shakespeare GROUP BY corpus,word ORDER BY m DESC LIMIT 5;";
		final String description =  "A query which gets those Shakespeare with the most common word ordered by count descending (only 5 is displayed)";
		String[][] expectation = new String[][]
		{
		{"hamlet","coriolanus","kinghenryv","2kinghenryiv","kingrichardiii"},
		{"995","942","937","894","848"},
		{"the","the","the","the","the"}
		};
		
		logger.info("Test number: 08");
		logger.info("Running query:" + sql);
		
		java.sql.ResultSet Result = null;
		try {
			Result = con.createStatement().executeQuery(sql);
		} 
		catch (SQLException e) {
			logger.fatal("SQLexception" + e.toString());
			fail("SQLException" + e.toString());
		}
		Assert.assertNotNull(Result);

		logger.debug(description);
		if(logger.getLevel() == Level.DEBUG)
		printer(expectation);
		try {
			Assert.assertTrue("Comparing failed in the String[][] array", 
					comparer(expectation, BQJDBCAPI.GetQueryResult(Result)));
		} 
		catch (SQLException e) {
			logger.fatal("SQLexception" + e.toString());
			fail(e.toString());
		}		
	}
	
	@Test
	public void QueryResultTest09() {
		final String sql = "SELECT corpus, corpus_date FROM publicdata:samples.shakespeare GROUP BY corpus, corpus_date ORDER BY corpus_date DESC LIMIT 3;";
		final String description =  "Shakespeare's 3 latest";
		String[][] expectation = new String[][]
		{
		{"kinghenryviii","tempest","winterstale"},
		{"1612","1611","1610"}
		};
		
		logger.info("Test number: 09");
		logger.info("Running query:" + sql);
		
		java.sql.ResultSet Result = null;
		try {
			Result = con.createStatement().executeQuery(sql);
		} 
		catch (SQLException e) {
			logger.fatal("SQLexception" + e.toString());
			fail("SQLException" + e.toString());
		}
		Assert.assertNotNull(Result);

		logger.debug(description);
		if(logger.getLevel() == Level.DEBUG)
		printer(expectation);
		try {
			Assert.assertTrue("Comparing failed in the String[][] array", 
					comparer(expectation, BQJDBCAPI.GetQueryResult(Result)));
		} 
		catch (SQLException e) {
			logger.fatal("SQLexception" + e.toString());
			fail(e.toString());
		}		
	}

	/**
	 * Prints a String[][] QueryResult to Log
	 * @param input
	 */
	private void printer(String[][] input){
		for(int s=0;s<input[0].length;s++)
		{
			String Output = "";
			for(int i=0;i<input.length;i++)
			{
				if(i==input.length-1)Output += input[i][s];
				else
				Output += input[i][s]+"\t";
			}
			logger.debug(Output);
		}
	}
	
	
	/**
	 * Compares two String[][]
	 * @param expected
	 * @param reality
	 * @return true if they are equal false if not
	 */
	private boolean comparer(String[][] expected, String[][] reality){
		for (int i = 0; i < expected.length; i++) {
			for (int j = 0; j < expected[i].length; j++) {
				if(expected[i][j].toString().equals(reality[i][j])== false) return false;	
			}
		}
		
		return true;
	}
}