package be.kdg.kandoe.backend.service.impl;

import be.kdg.kandoe.backend.model.organizations.Category;
import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.persistence.api.CategoryRepository;
import be.kdg.kandoe.backend.service.api.CategoryService;
import be.kdg.kandoe.backend.service.exceptions.CategoryServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository repository;
    private final Logger logger;

    @Autowired
    public CategoryServiceImpl(CategoryRepository repository) {
        this.repository = repository;
        this.logger = LogManager.getLogger(this.getClass());
    }

    @Override
    public Category addCategory(Category category) throws CategoryServiceException {
        if (category == null)
            throw new CategoryServiceException("Category cannot be null");

        if (category.getOrganization() == null)
            throw new CategoryServiceException("Category must be linked to an organization");
        
        if (category.getName() == null || category.getName().isEmpty())
            throw new CategoryServiceException("Category name must not be null");
        
        Category fetchedCategory = getCategoryByName(category.getName(), category.getOrganization());
        
        if (fetchedCategory != null) {
            if (fetchedCategory.getOrganization().equals(category.getOrganization())) {
                logger.warn(String.format("Tried to add category with name '%s' to organization '%s', but a category with that name already exists", 
                        category.getName(), category.getOrganization().getName()));
                
                throw new CategoryServiceException(
                        String.format("A category with the name '%s' already exists within organization '%s'",
                                fetchedCategory.getName(), fetchedCategory.getOrganization().getName())
                );
            }
        }

        try {
            category = repository.save(category);
        } catch (Exception e) {
            throw new CategoryServiceException("Could not save category");
        }

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
