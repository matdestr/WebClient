import {Component, OnInit} from "angular2/core";
import {RouteConfig, ROUTER_DIRECTIVES} from "angular2/router";
import {WelcomeComponent} from "./authentication/welcome.component";
import {DashboardComponent} from "./dashboard/dashboard.component";
import {CreateOrganizationComponent} from "./organization/create-organization.component";
import {UserProfileComponent} from "./profile/userprofile.component";
import {UserProfileEditComponent} from "./profile/userprofile-edit.component";
import {OrganizationDetailComponent} from "./organization/organization-detail.component";
import {CreateCategoryComponent} from "./categories/create-category.component";

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
    {path: '/new-organization', name: 'NewOrganization', component: CreateOrganizationComponent},
    {path: '/organization-detail', name:'OrganizationDetail', component: OrganizationDetailComponent},
    {path: '/create-category',  name: 'CreateCategory',     component: CreateCategoryComponent},
    //{path: '/my-organizations', name: 'MyOrganizations'} // TODO : Component
])
export class AppComponent implements OnInit{

    ngOnInit():any {
        return null;
    }
}
