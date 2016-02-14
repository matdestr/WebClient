package be.kdg.kandoe.frontend.config.orika;

import java.util.Map;

import ma.glasnost.orika.Converter;
import ma.glasnost.orika.Mapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;

import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * This class registers all converters and mappers with Orika's MapperFactory
 * during application startup.
 * 
 * GitHub link: https://github.com/dlizarra/orika-spring-integration/blob/master/src/main/java/com/dlizarra/orika/mapper/OrikaBeanMapper.java
 * */
public class OrikaBeanMapper extends ConfigurableMapper implements ApplicationContextAware {
    private MapperFactory mapperFactory;
    private ApplicationContext applicationContext;

    public OrikaBeanMapper() {
        super(false);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        this.init();
    }
    
    private void addAllSpringBeans() {
        Map<String, Mapper> mappers = this.applicationContext.getBeansOfType(Mapper.class);
        Map<String, Converter> converters = applicationContext.getBeansOfType(Converter.class);
        
        mappers.values().forEach(this::addMapper);
        converters.values().forEach(this::addConverter);
    }

    @Override
    protected void configure(MapperFactory factory) {
        this.mapperFactory = factory;
        addAllSpringBeans();
    }

    @Override
    protected void configureFactoryBuilder(DefaultMapperFactory.Builder factoryBuilder) {
        factoryBuilder
                .mapNulls(false)
                .build();
    }

    //@SuppressWarnings("unchecked")
    private void addMapper(Mapper<?, ?> mapper) {
        this.mapperFactory
                .classMap(mapper.getAType(), mapper.getBType())
                .byDefault()
                .mapNulls(false)
                .mapNullsInReverse(false)
                .customize((Mapper) mapper)
                .register();
    }
    
    private void addConverter(Converter<?, ?> converter) {
        this.mapperFactory.getConverterFactory().registerConverter(converter);
    }
}
