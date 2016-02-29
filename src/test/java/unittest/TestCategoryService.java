package unittest;

import be.kdg.kandoe.backend.config.BackendContextConfig;
import be.kdg.kandoe.backend.model.organizations.Category;
import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.service.api.CategoryService;
import be.kdg.kandoe.backend.service.api.OrganizationService;
import be.kdg.kandoe.backend.service.api.UserService;
import be.kdg.kandoe.backend.service.exceptions.CategoryServiceException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { BackendContextConfig.class })
@Transactional // Automatically rollbacks after each test
public class TestCategoryService {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private UserService userService;

    private Organization organization1, organization2;

    private Category testCategory;

    @Before
    public void setUp(){
        User user = new User("test-user", "test-password");
        userService.addUser(user);

        organization1 = new Organization("test-organization-1", userService.getUserByUsername(user.getUsername()));
        organization2 = new Organization("test-organization-2", userService.getUserByUsername(user.getUsername()));

        organization1 = organizationService.addOrganization(organization1);
        organization2 = organizationService.addOrganization(organization2);

        testCategory = new Category();
        testCategory.setName("test-category");
        testCategory.setDescription("This is a test category for test purposes only.");
        testCategory.setOrganization(organizationService.getOrganizationByName(organization1.getName()));
    }

    @Test
    public void testAddNewCategory() throws CategoryServiceException {
        categoryService.addCategory(testCategory);

        Category fetchedCategory = categoryService.getCategoryByName(testCategory.getName(), testCategory.getOrganization());

        assertEquals(testCategory.getCategoryId(), fetchedCategory.getCategoryId());
        assertEquals(testCategory.getOrganization().getOrganizationId(), fetchedCategory.getOrganization().getOrganizationId());
    }


    @Test(expected = CategoryServiceException.class)
    public void testAddExistingCategoryInSameOrganization() throws CategoryServiceException {
        categoryService.addCategory(testCategory);
        Category fetchedCategory = categoryService.getCategoryByName(testCategory.getName(), testCategory.getOrganization());
        assertEquals(testCategory, fetchedCategory);

        Category newTestCategory = new Category();
        newTestCategory.setName(fetchedCategory.getName());
        newTestCategory.setOrganization(fetchedCategory.getOrganization());

        categoryService.addCategory(newTestCategory);
    }


    @Test
    public void testAddExistingCategoryInDifferentOrganization() throws CategoryServiceException {
        categoryService.addCategory(testCategory);
        Category fetchedCategory = categoryService.getCategoryByName(testCategory.getName(), testCategory.getOrganization());
        assertEquals(testCategory, fetchedCategory);

        Category newTestCategory = new Category();
        newTestCategory.setName(fetchedCategory.getName());
        newTestCategory.setDescription(fetchedCategory.getDescription());
        newTestCategory.setOrganization(organizationService.getOrganizationByName(organization2.getName()));
        categoryService.addCategory(newTestCategory);

        Category fetchedNewCategory = categoryService.getCategoryByName(newTestCategory.getName(), newTestCategory.getOrganization());
        assertEquals(newTestCategory, fetchedNewCategory);
    }

}
