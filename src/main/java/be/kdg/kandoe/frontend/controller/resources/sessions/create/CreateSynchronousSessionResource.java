package be.kdg.kandoe.frontend.controller.resources.sessions.create;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class CreateSynchronousSessionResource extends CreateSessionResource {
    public static final String TYPE = "sync";

    //@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME, pattern = "yyyy-MM-dd HH:mm")
    //@JsonDeserialize(using = LocalDateDeserializer.class)
    //@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDateTime;

    public CreateSynchronousSessionResource() {
        super(CreateSynchronousSessionResource.TYPE);
    }
}
