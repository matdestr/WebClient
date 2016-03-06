package be.kdg.kandoe.backend.service.impl;

import be.kdg.kandoe.backend.model.organizations.Category;
import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.persistence.api.CategoryRepository;
import be.kdg.kandoe.backend.service.api.CategoryService;
import be.kdg.kandoe.backend.service.exceptions.CategoryServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository repository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public Category addCategory(Category category) throws CategoryServiceException {

        Category fetchedCategory = getCategoryByName(category.getName(), category.getOrganization());

        if (fetchedCategory != null)
            if (fetchedCategory.getOrganization().equals(category.getOrganization()))
                throw new CategoryServiceException(String.format(
                        "Category name '%s' already exists in organization '%s'.",
                        fetchedCategory.getName(), fetchedCategory.getOrganization().getName()));

        repository.save(category);

        return category;
    }

    @Override
    public List<Category> getCategoriesByOrganizationId(int organizationId) {
        return repository.findCategoriesByOrganizationOrganizationId(organizationId);
    }

    @Override
    public Category getCategoryById(int categoryId) {
        Category category = repository.findOne(categoryId);
        
        if (category == null)
            throw new CategoryServiceException("Category with ID " + categoryId + " does not exist");
        
        return category;
    }

    @Override
    public Category getCategoryByName(String categoryName, Organization organization) {
        return repository.findCategoryByNameAndOrganization(categoryName, organization);
    }
}
