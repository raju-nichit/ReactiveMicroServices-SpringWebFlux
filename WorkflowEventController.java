import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/workflow-events")
public class WorkflowEventController {

    private final WorkflowEventService service;

    @Autowired
    public WorkflowEventController(WorkflowEventService service) {
        this.service = service;
    }

    @PostMapping
    public Mono<ResponseEntity<ApiResponse<WorkflowEvent>>> insert(@RequestBody WorkflowEvent workflowEvent) {
        return service.insert(workflowEvent)
                .map(savedEvent -> ResponseEntity.ok(new ApiResponse<>("Event created successfully", savedEvent, true)))
                .onErrorResume(ex -> Mono.just(ResponseEntity.internalServerError().body(new ApiResponse<>(ex.getMessage(), null, false))));
    }

    @PutMapping("/{recordId}")
    public Mono<ResponseEntity<ApiResponse<WorkflowEvent>>> update(@PathVariable String recordId, @RequestBody WorkflowEvent workflowEvent) {
        return service.update(recordId, workflowEvent)
                .map(updatedEvent -> ResponseEntity.ok(new ApiResponse<>("Event updated successfully", updatedEvent, true)))
                .switchIfEmpty(Mono.just(ResponseEntity.status(404).body(new ApiResponse<>("Event not found", false))))
                .onErrorResume(ex -> Mono.just(ResponseEntity.internalServerError().body(new ApiResponse<>(ex.getMessage(), null, false))));
    }
}


public class ApiResponse<T> {
    private String message;
    private T data;
    private boolean success;

    public ApiResponse(String message, T data, boolean success) {
        this.message = message;
        this.data = data;
        this.success = success;
    }

    public ApiResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    // Getters and Setters
}


public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}





import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.time.ZoneOffset;

@Service
public class WorkflowEventService {

    private final WorkflowEventRepository repository;

    @Autowired
    public WorkflowEventService(WorkflowEventRepository repository) {
        this.repository = repository;
    }

    public Mono<WorkflowEvent> insert(WorkflowEvent workflowEvent) {
        convertToUTC(workflowEvent);
        return repository.save(workflowEvent);
    }

    public Mono<WorkflowEvent> update(String recordId, WorkflowEvent workflowEvent) {
        convertToUTC(workflowEvent);
        return repository.findByRecordId(recordId)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Event with record ID " + recordId + " not found")))
                .flatMap(existingEvent -> {
                    existingEvent.setModifiedBy(workflowEvent.getModifiedBy());
                    existingEvent.setModifiedDate(workflowEvent.getModifiedDate());
                    existingEvent.setCreatedBy(workflowEvent.getCreatedBy());
                    existingEvent.setCreatedDate(workflowEvent.getCreatedDate());
                    existingEvent.setType(workflowEvent.getType());
                    existingEvent.setClientId(workflowEvent.getClientId());
                    existingEvent.setGfcId(workflowEvent.getGfcId());
                    existingEvent.setEventName(workflowEvent.getEventName());
                    existingEvent.setStatus(workflowEvent.getStatus());
                    existingEvent.setOperationCd(workflowEvent.getOperationCd());
                    existingEvent.setOperationDt(workflowEvent.getOperationDt());
                    return repository.save(existingEvent);
                });
    }

    private void convertToUTC(WorkflowEvent workflowEvent) {
        if (workflowEvent.getModifiedDate() != null) {
            workflowEvent.setModifiedDate(workflowEvent.getModifiedDate().atZone(ZoneOffset.UTC).toLocalDateTime());
        }
        if (workflowEvent.getCreatedDate() != null) {
            workflowEvent.setCreatedDate(workflowEvent.getCreatedDate().atZone(ZoneOffset.UTC).toLocalDateTime());
        }
        if (workflowEvent.getOperationDt() != null) {
            workflowEvent.setOperationDt(workflowEvent.getOperationDt().atZone(ZoneOffset.UTC).toLocalDateTime());
        }
    }
}


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<ApiResponse<Object>> handleException(Exception ex) {
        return Mono.just(new ApiResponse<>(ex.getMessage(), null, false));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Mono<ApiResponse<Object>> handleEntityNotFoundException(EntityNotFoundException ex) {
        return Mono.just(new ApiResponse<>(ex.getMessage(), null, false));
    }
}
