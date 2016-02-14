package be.kdg.kandoe.frontend.config.orika.converters;


import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class LocalDateTimeConvertor extends BidirectionalConverter<LocalDateTime, LocalDateTime>
{
    @Override
    public LocalDateTime convertTo(LocalDateTime source, Type<LocalDateTime> destinationType)
    {
        return (LocalDateTime.from(source));
    }

    @Override
    public LocalDateTime convertFrom(LocalDateTime source, Type<LocalDateTime> destinationType)
    {
        return (LocalDateTime.from(source));
    }
}
