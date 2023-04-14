package ch.uzh.ifi.hase.soprafs23.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.Properties;
@Configuration
public class DataSourceConfig {

    @Value("${SP_DS_UR:jdbc:h2:mem:testdb}")
    private String url;

    @Value("${SP_DS_UN:username}")
    private String username;

    @Value("${SP_DS_PW:password}")
    private String password;

    @Value("${SP_DS_DRIVER:org.h2.Driver}")
    private String driver;

    @Value("${SP_DS_PLATFORM:org.hibernate.dialect.H2Dialect}")
    private String platform;


    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(driver);
        return dataSource;
    }

    @Bean
    public String printEnvVariables() {
        System.out.println("SP_DS_UR: " + url);
        System.out.println("SP_DS_UN: " + username);
        System.out.println("SP_DS_PW: " + password);
        System.out.println("SP_DS_DRIVER: " + driver);
        System.out.println("SP_DS_PLATFORM: " + platform);
        return "Printed all env for debugging";
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan("ch/uzh/ifi/hase/soprafs23/entity");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        em.setJpaProperties(hibernateProperties());
        em.setJpaProperties(additionalProperties());
        return em;
    }


    @Bean
    public Properties additionalProperties() {
        Properties properties = new Properties();
        properties.setProperty("spring.jpa.hibernate.ddl-auto", "update");
        return properties;
    }

    private Properties hibernateProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", platform);
        return properties;
    }
}