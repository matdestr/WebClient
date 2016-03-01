import {Component, OnInit} from "angular2/core";
import {RouteConfig, ROUTER_DIRECTIVES} from "angular2/router";
import {WelcomeComponent} from "./authentication/welcome.component";
import {DashboardComponent} from "./dashboard/dashboard.component";
import {CreateOrganizationComponent} from "./organization/create-organization.component";
import {UserProfileComponent} from "./profile/userprofile.component";
import {UserProfileEditComponent} from "./profile/userprofile-edit.component";
import {OrganizationDetailComponent} from "./organization/organization-detail.component";
import {CreateCategoryComponent} from "./categories/create-category.component";
import {TokenService} from "../services/token.service";
import {Router} from "angular2/router";
import {CategoryDetailComponent} from "./categories/category-detail.component";
import {CreateTopicComponent} from "./topic/create-topic.component";

@Component({
    selector: 'my-app',
    template: `
        <router-outlet></router-outlet>
    `,
    directives: [ROUTER_DIRECTIVES]
})
@RouteConfig([
    {path: "/",                 name: "Authentication",      component: WelcomeComponent},
    {path: "/dashboard",        name: "Dashboard",           component: DashboardComponent},
    {path: "/profile",          name: "Profile",             component: UserProfileComponent},
    {path: "/profile/edit",     name: "EditProfile",    component: UserProfileEditComponent},
    {path: '/organization/create', name: 'NewOrganization', component: CreateOrganizationComponent},
    {path: '/organization/:organizationId/detail', name:'OrganizationDetail', component: OrganizationDetailComponent},
    {path: '/organization/:organizationId/category/create',  name: 'CreateCategory',     component: CreateCategoryComponent},
    {path: '/categories/:categoryId/detail', name:'CategoryDetail',component:CategoryDetailComponent},
    {path: '/topic/create', name:'CreateTopic', component:CreateTopicComponent},
])
export class AppComponent {

}
