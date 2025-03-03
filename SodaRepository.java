import oracle.soda.OracleDatabase;
import oracle.soda.OracleException;
import oracle.soda.OracleCollection;
import oracle.soda.OracleDocument;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class SodaRepository {

    private final OracleDatabase oracleDatabase;

    public SodaRepository(OracleDatabase oracleDatabase) {
        this.oracleDatabase = oracleDatabase;
    }

    public String createDocument(String jsonData) throws OracleException {
        OracleCollection collection = oracleDatabase.admin().createCollection("my_collection");
        OracleDocument doc = oracleDatabase.createDocumentFromString(jsonData);
        OracleDocument result = collection.insertAndGet(doc);
        return result.getKey();
    }

    public String getDocument(String key) throws OracleException {
        OracleCollection collection = oracleDatabase.openCollection("my_collection");
        OracleDocument doc = collection.find().key(key).getOne();
        return doc != null ? doc.getContentAsString() : null;
    }

    public List<String> getAllDocuments() throws OracleException {
        OracleCollection collection = oracleDatabase.openCollection("my_collection");
        List<String> results = new ArrayList<>();
        for (OracleDocument doc : collection.find().getIterator()) {
            results.add(doc.getContentAsString());
        }
        return results;
    }

    public boolean updateDocument(String key, String newJsonData) throws OracleException {
        OracleCollection collection = oracleDatabase.openCollection("my_collection");
        OracleDocument newDoc = oracleDatabase.createDocumentFromString(newJsonData);
        return collection.find().key(key).replaceOne(newDoc);
    }

    public boolean deleteDocument(String key) throws OracleException {
        OracleCollection collection = oracleDatabase.openCollection("my_collection");
        return collection.find().key(key).remove() > 0;
    }
}


