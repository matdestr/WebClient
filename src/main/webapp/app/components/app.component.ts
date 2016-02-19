import {Component, OnInit} from "angular2/core";
import {RouteConfig, ROUTER_DIRECTIVES} from "angular2/router";
import {WelcomeComponent} from "./authentication/welcome.component";
import {DashboardComponent} from "./dashboard/dashboard.component";

@Component({
    selector: 'my-app',
    template: `
        <router-outlet></router-outlet>
    `,
    directives: [ROUTER_DIRECTIVES]
})
@RouteConfig([
    {path: "/",             name: "Authentication",     component: WelcomeComponent},
    {path: "/dashboard",    name: "Dashboard",          component: DashboardComponent}
])
export class AppComponent implements OnInit{

    ngOnInit():any {
        return null;
    }
}
