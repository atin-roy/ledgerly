package dev.atinroy.ledgerly.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class RailwayConfig {

    @Bean
    @Profile("prod") // Only active when 'prod' profile is set (Railway default)
    public DataSource dataSource() throws URISyntaxException {
        String dbUrl = System.getenv("DATABASE_URL");
        if (dbUrl == null || dbUrl.isEmpty()) {
            throw new RuntimeException("DATABASE_URL environment variable is missing!");
        }

        // Parse the postgres://user:pass@host:port/db URL
        URI uri = new URI(dbUrl);
        String username = uri.getUserInfo().split(":")[0];
        String password = uri.getUserInfo().split(":")[1];
        int port = uri.getPort();
        String host = uri.getHost();
        String dbName = uri.getPath().substring(1); // Remove leading '/'

        // Construct JDBC URL
        String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s?sslmode=require", host, port, dbName);

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName("org.postgresql.Driver");

        return new HikariDataSource(config);
    }
}