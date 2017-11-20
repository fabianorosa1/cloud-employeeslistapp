package  com.sap.employeeslist;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
/**
 * Handles the singleton {@link EntityManagerFactory} instance.
 * <p>
 * This class is responsible for fetching the details of the {@link DataSource}
 * that has the connection related details (driver,url,username, password) on how to
 * connect to the data source.
 * <p>
 * <b>Note</b> - This class fetches the {@link DataSource} details via JNDI, so
 * ensure that you have the DataSource configured as a JNDI lookup variable in
 * the respective ServletContainer where the application is deployed.
 */
public class JpaEntityManagerFactory {
	private static final Logger logger = LoggerFactory.getLogger(JpaEntityManagerFactory.class);
	
	
	/**
	 * The package name which contains all the model classes.
	 */
	 private static JsonNode readCredentialsFromEnvironment() throws IOException {
			ObjectMapper mapper = new ObjectMapper();
			logger.info("VCAP_SERVICES: " + System.getenv("VCAP_SERVICES"));
			
			JsonNode actualObj = mapper.readTree(System.getenv("VCAP_SERVICES"));
			return actualObj.get("hanatrial").get(0).get("credentials");
	 }
	public static final String PERSISTENCE_UNIT_NAME = "employeeslist";

	/**
	 * The static {@link EntityManagerFactory}
	 */
	private static EntityManagerFactory entityManagerFactory = null;

	/**
	 * Returns the singleton EntityManagerFactory instance for accessing the
	 * default database.
	 * 
	 * @return the singleton EntityManagerFactory instance
	 * @throws NamingException
	 *             if a naming exception occurs during initialization
	 * @throws SQLException
	 *             if a database occurs during initialization
	 * @throws IOException 
	 */
	public static synchronized EntityManagerFactory getEntityManagerFactory()
			throws NamingException, SQLException, IOException {
		if (entityManagerFactory == null) {
			InitialContext ctx = new InitialContext();
		    BasicDataSource ds = new BasicDataSource();
		    JsonNode credentials = readCredentialsFromEnvironment();
			ds.setDriverClassName(credentials.get("driver").asText());
		    ds.setUrl(credentials.get("url").asText());
		    ds.setUsername(credentials.get("user").asText());
		    ds.setPassword(credentials.get("password").asText());
			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, ds);
			
			
			properties.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.DROP_AND_CREATE);
			properties.put(PersistenceUnitProperties.DDL_GENERATION_MODE, PersistenceUnitProperties.DDL_BOTH_GENERATION);
			
			entityManagerFactory = Persistence.createEntityManagerFactory(
					PERSISTENCE_UNIT_NAME, properties);
		}
		return entityManagerFactory;
	}
   

   
	
  


}
