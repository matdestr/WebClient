package be.kdg.kandoe.frontend.config.orika.converters;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class DateLocalDateTimeConverter extends BidirectionalConverter<Date, LocalDateTime> {
    @Override
    public LocalDateTime convertTo(Date source, Type<LocalDateTime> destinationType) {
        return source.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    @Override
    public Date convertFrom(LocalDateTime source, Type<Date> destinationType) {
        return Date.from(source.atZone(ZoneId.systemDefault()).toInstant());
    }
}
