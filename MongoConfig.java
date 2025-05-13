import org.apache.http.HttpHost;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;
import org.apache.commons.lang3.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Configuration
@Slf4j
public class ApplicationConfig {

    // Elasticsearch Hosts and Credentials (replace these with your values)
    private List<String> esHosts = List.of("your-elasticsearch-hosts");
    private String es_username = "your-username";
    private String es_password = "your-password";

    // RestTemplate Bean for general HTTP requests
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = null;
        try {
            // Optional: Set system properties like proxy, timeouts, etc.
            setSystemProperty();

            CloseableHttpClient client = HttpClients.createSystem();
            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(client);

            restTemplate = new RestTemplate(requestFactory);
            // Set UTF-8 encoding for string responses
            restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

        } catch (Exception e) {
            log.error("Error initializing RestTemplate bean", e);
        }

        return restTemplate;
    }

    // Method to set system properties like proxy (optional)
    private void setSystemProperty() {
        // Example: System.setProperty("http.proxyHost", "proxy.example.com");
    }

    // HttpHeaders Bean for request headers
    @Bean
    public HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/json");
        headers.add("Content-Type", "application/json");
        return headers;
    }

    // Elasticsearch RestHighLevelClient Bean
    @Bean(name = "restHighLevelClient", destroyMethod = "close")
    public RestHighLevelClient restHighLevelClient() {
        RestHighLevelClient restHighLevelClient = null;
        RestClientBuilder builder = getRestClientBuilder(esHosts);

        if (builder != null) {
            restHighLevelClient = new RestHighLevelClient(builder);
        }

        return restHighLevelClient;
    }

    // Method to get RestClientBuilder for Elasticsearch client
    private RestClientBuilder getRestClientBuilder(List<String> hosts) {
        RestClientBuilder builder = null;

        // Use Apache HttpClient 4.x classes for Elasticsearch
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        log.info("es host 0 getRestClientBuilder: " + esHosts);

        // Decode password from Base64
        String decodedPwd = new String(Base64.getDecoder().decode(es_password));

        credentialsProvider.setCredentials(
            AuthScope.ANY,
            new UsernamePasswordCredentials(es_username, decodedPwd)
        );

        HttpHost[] httpHosts = new HttpHost[hosts.size()];
        boolean isHostExist = false;

        // Setup HttpHosts based on your list of hosts
        for (int i = 0; i < hosts.size(); i++) {
            if (!StringUtils.isEmpty(hosts.get(i))) {
                isHostExist = true;
                httpHosts[i] = new HttpHost(hosts.get(i), 9921);
                log.info("es_host_0 getHostName: " + httpHosts[i].getHostName());
            }
        }

        if (isHostExist) {
            builder = RestClient.builder(httpHosts).setHttpClientConfigCallback(httpClientBuilder -> {
                httpClientBuilder.disableAuthCaching();
                return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
            });
        }

        return builder;
    }
}
