System.register(["angular2/core", "angular2/router", "./authentication/welcome.component", "./dashboard/dashboard.component", "./organization/create-organization.component", "./profile/userprofile.component", "./profile/userprofile-edit.component", "./organization/organization-detail.component", "./categories/create-category.component"], function(exports_1) {
    var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
        var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
        if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
        else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
        return c > 3 && r && Object.defineProperty(target, key, r), r;
    };
    var __metadata = (this && this.__metadata) || function (k, v) {
        if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
    };
    var core_1, router_1, welcome_component_1, dashboard_component_1, create_organization_component_1, userprofile_component_1, userprofile_edit_component_1, organization_detail_component_1, create_category_component_1;
    var AppComponent;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (router_1_1) {
                router_1 = router_1_1;
            },
            function (welcome_component_1_1) {
                welcome_component_1 = welcome_component_1_1;
            },
            function (dashboard_component_1_1) {
                dashboard_component_1 = dashboard_component_1_1;
            },
            function (create_organization_component_1_1) {
                create_organization_component_1 = create_organization_component_1_1;
            },
            function (userprofile_component_1_1) {
                userprofile_component_1 = userprofile_component_1_1;
            },
            function (userprofile_edit_component_1_1) {
                userprofile_edit_component_1 = userprofile_edit_component_1_1;
            },
            function (organization_detail_component_1_1) {
                organization_detail_component_1 = organization_detail_component_1_1;
            },
            function (create_category_component_1_1) {
                create_category_component_1 = create_category_component_1_1;
            }],
        execute: function() {
            AppComponent = (function () {
                function AppComponent() {
                }
                AppComponent = __decorate([
                    core_1.Component({
                        selector: 'my-app',
                        template: "\n        <router-outlet></router-outlet>\n    ",
                        directives: [router_1.ROUTER_DIRECTIVES]
                    }),
                    router_1.RouteConfig([
                        { path: "/", name: "Authentication", component: welcome_component_1.WelcomeComponent },
                        { path: "/dashboard", name: "Dashboard", component: dashboard_component_1.DashboardComponent },
                        { path: "/profile", name: "Profile", component: userprofile_component_1.UserProfileComponent },
                        { path: "/profile/edit", name: "EditProfile", component: userprofile_edit_component_1.UserProfileEditComponent },
                        { path: '/organization/create', name: 'NewOrganization', component: create_organization_component_1.CreateOrganizationComponent },
                        { path: '/organization/:organizationId/detail', name: 'OrganizationDetail', component: organization_detail_component_1.OrganizationDetailComponent },
                        { path: '/organization/:organizationId/category/create', name: 'CreateCategory', component: create_category_component_1.CreateCategoryComponent },
                    ]), 
                    __metadata('design:paramtypes', [])
                ], AppComponent);
                return AppComponent;
            })();
            exports_1("AppComponent", AppComponent);
        }
    }
});
//# sourceMappingURL=app.component.js.map