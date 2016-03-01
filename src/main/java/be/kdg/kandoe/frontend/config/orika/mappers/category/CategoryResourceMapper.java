package be.kdg.kandoe.frontend.config.orika.mappers.category;


import be.kdg.kandoe.backend.model.organizations.Category;
import be.kdg.kandoe.frontend.controller.resources.organizations.categories.CategoryResource;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import org.springframework.stereotype.Component;

@Component
public class CategoryResourceMapper extends CustomMapper<Category, CategoryResource> {
    @Override
    public void mapAtoB(Category category, CategoryResource categoryResource, MappingContext context) {
        categoryResource.setOrganizationId(category.getOrganization().getOrganizationId());
    }

    @Override
    public void mapBtoA(CategoryResource categoryResource, Category category, MappingContext context) {
        super.mapBtoA(categoryResource, category, context);
    }
}
