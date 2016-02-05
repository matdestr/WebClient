package be.kdg.kandoe.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;

import javax.sql.DataSource;

/**
 * Created by Vincent on 5-2-2016.
 */
@Configuration
public class DataSourceConfig {
    @Bean(name = "datasource")
    public DriverManagerDataSource datasource() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");

        // datasource in-memory
        dataSource.setUrl("jdbc:h2:mem:memorydb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");

        // datasource file
        //dataSource.setUrl("jdbc:h2:file:./filename;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");

        // datasource remote tcp server
        // in this case:also start H2 server from commandline with: java -cp h2*.jar org.h2.tools.Server
        // dataSource.setUrl("jdbc:h2:tcp://localhost/~DbName;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");

        return dataSource;
    }

    @Bean
    public DataSourceInitializer dataSourceInitializer(final DataSource dataSource) {
        //final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();

        final DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        //initializer.setDatabasePopulator(populator);
        return initializer;
    }

}
