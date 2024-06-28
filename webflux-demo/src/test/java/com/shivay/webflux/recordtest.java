
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = GraphQLController.class)
@Import(GraphQLController.class)
public class GraphQLControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CitiDirectGraphQLService clientDefinitionService;

    @BeforeEach
    public void setup() {
        // Setup any necessary mocks
        when(clientDefinitionService.findClientDefinitionDetails(any(String.class), any(String.class)))
                .thenReturn(Flux.just(new CUBEModel()));
        when(clientDefinitionService.getAccountOrClientDetails(any(String.class)))
                .thenReturn(Flux.just(new AccountOrClientDetailResponse()));
        when(clientDefinitionService.getAccountEntitlementQuery(any(String.class)))
                .thenReturn(Flux.just(new AccountEntitlementModel()));
        when(clientDefinitionService.getAccountEntitlementXmta(any(String.class)))
                .thenReturn(Flux.just(new ServiceClassDetails()));
        when(clientDefinitionService.accountEntitlementUaaSearch(any(String.class)))
                .thenReturn(Flux.just(new UaaSearchResponse()));
        when(clientDefinitionService.retrieveAllAccountEntitlementData(any(String.class)))
                .thenReturn(Flux.just(new AccountEntitlementModel()));
    }

    @Test
    public void testFindClientDefinitionByReferenceIdAndType() {
        webTestClient.post()
                .uri("/graphql")
                .bodyValue("{\"query\":\"{ findClientDefinitionByReferenceIdAndType(referenceId: \\\"ref1\\\", type: \\\"type1\\\") { } }\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.findClientDefinitionByReferenceIdAndType").exists();
    }

    @Test
    public void testGetCDBEDetailsByDealId() {
        webTestClient.post()
                .uri("/graphql")
                .bodyValue("{\"query\":\"{ getCDBEDetailsByDealId(dealId: \\\"deal1\\\", type: \\\"type1\\\") { } }\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.getCDBEDetailsByDealId").exists();
    }

    @Test
    public void testAccountEntitlement() {
        webTestClient.post()
                .uri("/graphql")
                .bodyValue("{\"query\":\"{ accountEntitlement(referenceId: \\\"ref1\\\") { } }\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.accountEntitlement").exists();
    }

    @Test
    public void testRetrieveUaaSearch() {
        webTestClient.post()
                .uri("/graphql")
                .bodyValue("{\"query\":\"{ retrieveUaaSearch(referenceId: \\\"ref1\\\", type: \\\"type1\\\") { } }\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.retrieveUaaSearch").exists();
    }

    @Test
    public void testRetrieveAccountEntitlementCIE() {
        webTestClient.post()
                .uri("/graphql")
                .bodyValue("{\"query\":\"{ retrieveAccountEntitlementCIE(referenceId: \\\"ref1\\\") { } }\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.retrieveAccountEntitlementCIE").exists();
    }
}
