<dependency>
    <groupId>nl.altindag</groupId>
    <artifactId>sslcontext-kickstart</artifactId>
    <version>9.0.3</version> <!-- ✅ Compatible with Java 17+ and Spring Boot 3.x -->
</dependency>
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
                // ✅ Build SSLFactory with unsafe trust for dev/test
                SSLFactory sslFactory = SSLFactory.builder()
                        .withUnsafeTrustMaterial()
                        .build();

                String uri = "mongodb://" + dbusername + ":" + dbpassword + "@" + dbpath + "/" + dbname + "?ssl=true";

                log.info("Connecting to MongoDB with URI: {}", uri);

                clientSettingsBuilder
                    .applyConnectionString(new ConnectionString(uri))
                    .applyToSslSettings(ssl -> {
                        ssl.enabled(true);
                        ssl.context(sslFactory.getSslContext());
                    });

            } catch (Exception e) {
                log.error("MongoDB connection failed with SSL configuration", e);
                throw new RuntimeException(e);
            }
        };
    }
}
