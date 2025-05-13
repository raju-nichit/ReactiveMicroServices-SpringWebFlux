import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import nl.altindag.ssl.SSLFactory;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.net.ssl.SSLContext;

@Configuration
public class MongoConfig {

    @Bean
    public MongoClientSettings mongoClientSettingsDev(MongoProperties properties, Environment environment) {
        // Build SSL context using sslcontext-kickstart
        SSLFactory sslFactory = SSLFactory.builder()
                .withUnsafeTrustMaterial() // or load from keystore/truststore
                .build();

        MongoClientSettings.Builder builder = MongoClientSettings.builder();

        // Apply connection string from properties
        String uri = properties.getUri(); // usually from application.yml
        builder.applyConnectionString(new ConnectionString(uri));

        // Apply SSL context
        builder.applyToSslSettings(b -> {
            b.enabled(true);
            b.context(sslFactory.getSslContext());
        });

        // Optional: socket, pool, credentials etc.
        builder.applyToSocketSettings(socket -> socket.connectTimeout(10000, java.util.concurrent.TimeUnit.MILLISECONDS));

        return builder.build();
    }
}
