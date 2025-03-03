import oracle.soda.OracleCollection;
import oracle.soda.OracleDatabase;
import oracle.soda.OracleDocument;
import oracle.soda.OracleException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class SodaRepository {

    private final OracleDatabase oracleDatabase;
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON Converter

    public SodaRepository(OracleDatabase oracleDatabase) {
        this.oracleDatabase = oracleDatabase;
    }

    /**
     * Generic search method supporting AND and OR conditions separately.
     *
     * @param andConditions Map of field names and their values that must all match (AND logic).
     * @param orConditions  Map of field names and values where at least one must match (OR logic).
     * @return List of matching documents.
     */
    public List<String> searchDocuments(Map<String, Object> andConditions, Map<String, Object> orConditions) throws OracleException {
        OracleCollection collection = oracleDatabase.openCollection("my_collection");

        try {
            // Construct the AND & OR JSON query
            String filterJson = buildFilterJson(andConditions, orConditions);
            OracleDocument filterDoc = oracleDatabase.createDocumentFromString(filterJson);

            // Execute query and collect results
            List<String> results = new ArrayList<>();
            for (OracleDocument doc : collection.find().filter(filterDoc).getIterator()) {
                results.add(doc.getContentAsString());
            }
            return results;
        } catch (Exception e) {
            throw new OracleException("Error constructing query filter: " + e.getMessage());
        }
    }

    /**
     * Builds a dynamic JSON filter combining AND & OR conditions.
     */
    private String buildFilterJson(Map<String, Object> andConditions, Map<String, Object> orConditions) throws Exception {
        Map<String, Object> query = new java.util.HashMap<>();

        // Add AND conditions if present
        if (andConditions != null && !andConditions.isEmpty()) {
            query.put("$and", List.of(andConditions)); // AND requires a list of conditions
        }

        // Add OR conditions if present
        if (orConditions != null && !orConditions.isEmpty()) {
            query.put("$or", List.of(orConditions)); // OR requires a list of conditions
        }

        return objectMapper.writeValueAsString(query);
    }
}
