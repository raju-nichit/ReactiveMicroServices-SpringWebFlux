import oracle.soda.OracleException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SodaService {
    private final SodaRepository sodaRepository;

    public SodaService(SodaRepository sodaRepository) {
        this.sodaRepository = sodaRepository;
    }

    public String createDocument(String jsonData) throws OracleException {
        return sodaRepository.createDocument(jsonData);
    }

    public String getDocument(String key) throws OracleException {
        return sodaRepository.getDocument(key);
    }

    public List<String> getAllDocuments() throws OracleException {
        return sodaRepository.getAllDocuments();
    }

    public boolean updateDocument(String key, String newJsonData) throws OracleException {
        return sodaRepository.updateDocument(key, newJsonData);
    }

    public boolean deleteDocument(String key) throws OracleException {
        return sodaRepository.deleteDocument(key);
    }
}
