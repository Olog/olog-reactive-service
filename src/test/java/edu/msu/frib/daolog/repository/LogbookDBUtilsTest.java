/**
 * 
 */
package edu.msu.frib.daolog.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import edu.msu.frib.daolog.Application;
import edu.msu.frib.daolog.ApplicationConfig;
import edu.msu.frib.daolog.controller.LogbookController;
import edu.msu.frib.daolog.db.MongoDBDaologConfig;
import edu.msu.frib.daolog.log.Logbook;
import edu.msu.frib.daolog.log.State;

/**
 * @author vagrant
 * //PropertySource("classpath:db/logbooks.json")
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes= {Application.class}, webEnvironment=WebEnvironment.DEFINED_PORT)
@ContextConfiguration(classes={ApplicationConfig.class,MongoDBDaologConfig.class})
public class LogbookDBUtilsTest {

	private static Logger logger = LoggerFactory.getLogger(LogbookDBUtilsTest.class);
	
	@Autowired
	LogbookRepository logbookRepository;
    	
	public InputStream logbooksStream;
	public Set<Logbook> populatedLogbooks;
	
	public List<Logbook> createdLogbooks;
	
	public Integer logbookCount;
	
	public void init() {

		logbookRepository.deleteAll();
		createdLogbooks = new LinkedList<Logbook>();
		
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCreateLogbooks() throws JsonParseException, JsonMappingException, IOException {

		Resource logbookFile = new ClassPathResource("db/logbooks.json");
		try {
			logbooksStream = logbookFile.getInputStream(); // Resource method
						
			// could not get to function because repository was null
			this.populatedLogbooks = LogbookDBUtils.insertLogbooks(this.logbookRepository, logbooksStream);
			
		} catch (IOException e) {
			logger.error("failed to load data for logbooks!");
			e.printStackTrace();
		}
		
		assertTrue(populatedLogbooks != null && !populatedLogbooks.isEmpty());
		System.out.println("hey the size is: " + populatedLogbooks.size());
		logbookCount = populatedLogbooks.size();
	}

	@Test
	public void testRetrieveLogbooks() throws JsonParseException, JsonMappingException, IOException {
		List<String> logbooksIds = new LinkedList<String>();
		
		// create the entries
		testCreateLogbooks();
		System.out.println("after create logbooks called");
		populatedLogbooks.forEach(logbook -> logbooksIds.add(logbook.getId()));
		
		Set<Logbook> retrievedLogbooks = LogbookDBUtils.findLogbooks(this.logbookRepository, logbooksIds);
		
		for (Logbook logbook : retrievedLogbooks) {
			LogbookDBUtilsTest.validateLogbook(logbook);
		}
	}
	
	private static void validateLogbook(Logbook logbook) {

		assertNotNull(logbook);
		assertNotNull(logbook.getName());
		assertTrue(!logbook.getName().isEmpty());
		assertNotNull(logbook.getOwner());
		assertTrue(!logbook.getOwner().isEmpty());
		assertNotNull(logbook.getCreatedDate());
	}
}
