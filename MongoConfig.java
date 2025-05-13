package com.example.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import lombok.extern.slf4j.Slf4j;
import nl.altindag.ssl.SSLFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class MongoConfig {

    @Value("${application.dbpath}")
    private String dbpath;

    @Value("${application.dbusername}")
    private String dbusername;

    @Value("${application.dbpassword}")
    private String dbpassword;

    @Value("${application.dbname}")
    private String dbname;

    @Bean
    public MongoClientSettingsBuilderCustomizer mongoClientSettingsBuilderCustomizer() {
        return clientSettingsBuilder -> {
            try {
                // ⚠️ Build SSLFactory with UNSAFE trust (for local/test only)
                SSLFactory sslFactory = SSLFactory.builder()
                        .withUnsafeTrustMaterial() // Accepts all certs
                        .build();

                // ⚠️ URI-encode username/password if needed
                String encodedUser = java.net.URLEncoder.encode(dbusername, java.nio.charset.StandardCharsets.UTF_8);
                String encodedPass = java.net.URLEncoder.encode(dbpassword, java.nio.charset.StandardCharsets.UTF_8);

                String uri = String.format("mongodb://%s:%s@%s/%s?ssl=true", encodedUser, encodedPass, dbpath, dbname);

                log.info("Connecting to MongoDB at {}", dbpath);

                clientSettingsBuilder
                        .applyConnectionString(new ConnectionString(uri))
                        .applyToSslSettings(ssl -> {
                            ssl.enabled(true);
                            ssl.context(sslFactory.getSslContext());
                        });

            } catch (Exception e) {
                log.error("MongoDB SSL setup failed", e);
                throw new IllegalStateException("Unable to initialize MongoClientSettings with SSL", e);
            }
        };
    }
}
