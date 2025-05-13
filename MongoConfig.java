package com.example.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import java.security.NoSuchAlgorithmException;

@Configuration
@Slf4j
public class MongoConfig {

    @Value("${application.dbpath}")
    private String dbpath;

    @Value("${application.dbusername}")
    private String dbusername;

    @Value("${application.dbname}")
    private String dbname;

    @Value("${application.dbpassword}")
    private String dbpassword; // Assuming password is provided

    @Bean
    public MongoClientSettingsBuilderCustomizer mongoClientSettingsBuilderCustomizer() {
        return clientSettingsBuilder -> {
            try {
                // Build SSL context (you should use a real SSLFactory implementation)
                SSLContext sslContext = SSLContext.getDefault(); // Replace with actual SSLFactory if needed

                // Construct the MongoDB URI
                String uri = "mongodb://" + dbusername + ":" + dbpassword + "@" + dbpath + "/" + dbname + "?ssl=true";

                log.info("Connecting to MongoDB at {}", uri);

                clientSettingsBuilder
                    .applyConnectionString(new ConnectionString(uri))
                    .applyToSslSettings(ssl -> {
                        ssl.enabled(true);
                        ssl.context(sslContext);
                    });

            } catch (Exception e) {
                log.error("Error connecting to MongoDB. Please check connection details.", e);
                throw new RuntimeException(e);
            }
        };
    }
}
