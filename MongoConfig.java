import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.Base64;
import java.util.List;

public class ElasticsearchConfig {

    private static final Logger log = LoggerFactory.getLogger(ElasticsearchConfig.class);

    private String es_username = "your_username"; // Inject or load from config
    private String es_password = "your_base64_password"; // Inject or load from config
    private int es_port = 9921;

    public RestClientBuilder getRestClientBuilder(List<String> hosts) {

        if (hosts == null || hosts.isEmpty()) {
            throw new IllegalArgumentException("Elasticsearch hosts list cannot be null or empty");
        }

        log.info("es_host(s) in getRestClientBuilder: {}", hosts);

        String decodedPwd = new String(Base64.getDecoder().decode(es_password));
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
                AuthScope.ANY,
                new UsernamePasswordCredentials(es_username, decodedPwd)
        );

        HttpHost[] httpHosts = hosts.stream()
                .filter(StringUtils::hasText)
                .map(host -> {
                    log.info("Adding ES host: {}", host);
                    return new HttpHost(host, es_port);
                })
                .toArray(HttpHost[]::new);

        if (httpHosts.length == 0) {
            throw new IllegalArgumentException("No valid Elasticsearch hosts provided");
        }

        return RestClient.builder(httpHosts)
                .setHttpClientConfigCallback(httpClientBuilder -> {
                    httpClientBuilder.disableAuthCaching();
                    return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                });
    }
}
