package edu.gatech.chai.gtfhir2.config;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

//import edu.gatech.chai.omopv5.jpa.service.CareSiteService;
//import edu.gatech.chai.omopv5.jpa.service.CareSiteServiceImp;

@Configuration
@EnableTransactionManagement
@ComponentScans(value = { @ComponentScan("edu.gatech.chai.omopv5.jpa.dao"),
		@ComponentScan("edu.gatech.chai.omopv5.jpa.service"),
		@ComponentScan("edu.gatech.chai.fhir.jpa.service") })
@ImportResource({
    "classpath:database-config.xml"
})
public class FhirServerConfig {
	@Autowired
	DataSource dataSource;
//	@Bean(destroyMethod = "close")
//	public DataSource dataSource() {
//		BasicDataSource retVal = new BasicDataSource();
//		retVal.setDriver(new org.postgresql.Driver());
//		retVal.setUrl("jdbc:postgresql://localhost:5432/postgres?currentSchema=omop_v5");
//		retVal.setUsername("omop_v5");
//		retVal.setPassword("i3lworks");
//		return retVal;
//	}

	@Bean()
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean retVal = new LocalContainerEntityManagerFactoryBean();
		retVal.setPersistenceUnitName("GT-FHIR2");
//		retVal.setDataSource(dataSource());
		retVal.setDataSource(dataSource);
		retVal.setPackagesToScan("edu.gatech.chai.omopv5.jpa.entity", "edu.gatech.chai.fhir.jpa.entity");
		retVal.setPersistenceProvider(new HibernatePersistenceProvider());
		retVal.setJpaProperties(jpaProperties());
		return retVal;
	}

	private Properties jpaProperties() {
		Properties extraProperties = new Properties();
		extraProperties.put("hibernate.dialect", org.hibernate.dialect.PostgreSQL94Dialect.class.getName());
		extraProperties.put("hibernate.format_sql", "true");
		extraProperties.put("hibernate.show_sql", "false");
		extraProperties.put("hibernate.hbm2ddl.auto", "update");
//		extraProperties.put("hibernate.hbm2ddl.auto", "none");
//		extraProperties.put("hibernate.enable_lazy_load_no_trans", "true");
		extraProperties.put("hibernate.jdbc.batch_size", "20");
		extraProperties.put("hibernate.cache.use_query_cache", "false");
		extraProperties.put("hibernate.cache.use_second_level_cache", "false");
		extraProperties.put("hibernate.cache.use_structured_entries", "false");
		extraProperties.put("hibernate.cache.use_minimal_puts", "false");
		// extraProperties.put("hibernate.search.model_mapping",
		// SearchMappingFactory.class.getName());
		extraProperties.put("hibernate.search.default.directory_provider", "filesystem");
		extraProperties.put("hibernate.search.default.indexBase", "target/lucenefiles");
		extraProperties.put("hibernate.search.lucene_version", "LUCENE_CURRENT");
		// extraProperties.put("hibernate.search.default.worker.execution",
		// "async");
		return extraProperties;
	}

	@Bean()
	public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
		JpaTransactionManager retVal = new JpaTransactionManager();
		retVal.setEntityManagerFactory(entityManagerFactory);
		return retVal;
	}
//	
//	@Bean()
//	public CareSiteService careSiteService() {
//		return new CareSiteServiceImp();
//	}
}
