import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AccountRequestTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testAccountRequestSerialization() throws JsonProcessingException {
        AccountRequest request = new AccountRequest(
                "high",
                "123456789",
                "ADMIN",
                "John Doe",
                "GFC123",
                "CID123",
                "USD",
                "otp123",
                "NYC001",
                "GFC456",
                "US"
        );

        String jsonString = objectMapper.writeValueAsString(request);

        String expectedJson = """
                {
                    "uaaSignatureStrength":"high",
                    "accountNumber":"123456789",
                    "accessLevel":"ADMIN",
                    "ccClientName":"John Doe",
                    "accountGFCId":"GFC123",
                    "coClientId":"CID123",
                    "accountCoy":"USD",
                    "uaaSignatureOtp":"otp123",
                    "accountBranch":"NYC001",
                    "branchCode":"GFC456",
                    "accountCtry":"US"
                }
                """;

        assertEquals(objectMapper.readTree(expectedJson), objectMapper.readTree(jsonString));
    }

    @Test
    public void testAccountRequestDeserialization() throws JsonProcessingException {
        String jsonString = """
                {
                    "uaaSignatureStrength":"high",
                    "accountNumber":"123456789",
                    "accessLevel":"ADMIN",
                    "ccClientName":"John Doe",
                    "accountGFCId":"GFC123",
                    "coClientId":"CID123",
                    "accountCoy":"USD",
                    "uaaSignatureOtp":"otp123",
                    "accountBranch":"NYC001",
                    "branchCode":"GFC456",
                    "accountCtry":"US"
                }
                """;

        AccountRequest request = objectMapper.readValue(jsonString, AccountRequest.class);

        assertEquals("high", request.signatureStrength());
        assertEquals("123456789", request.accountNumber());
        assertEquals("ADMIN", request.accessLevel());
        assertEquals("John Doe", request.clientName());
        assertEquals("GFC123", request.accountGFCId());
        assertEquals("CID123", request.clientId());
        assertEquals("USD", request.currency());
        assertEquals("otp123", request.signatureOtp());
        assertEquals("NYC001", request.accountBranch());
        assertEquals("GFC456", request.cdbeGFCId());
        assertEquals("US", request.accountCtry());
    }

    @Test
    public void testAccountRequestInvalidJson() {
        String invalidJsonString = """
                {
                    "uaaSignatureStrength":"high",
                    "accountNumber":"123456789"
                    // missing other fields and incorrect format
                }
                """;

        assertThrows(JsonProcessingException.class, () -> {
            objectMapper.readValue(invalidJsonString, AccountRequest.class);
        });
    }
}
