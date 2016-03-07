import {Component, OnInit, Input} from "angular2/core";
import {Router} from "angular2/router";
import {RouteParams} from "angular2/router";
import {ToolbarComponent} from "../widget/toolbar.component";
import {OrganizationService} from "../../services/organization.service";
import {User} from "../../entities/user/user";
import {UserService} from "../../services/user.service";
import {getUsername} from "../../libraries/angular2-jwt";
import {Organization} from "../../entities/organization/organization";

@Component({
    selector: 'dashboard',
    templateUrl: 'html/dashboard.html',
    directives: [ToolbarComponent]
})
export class DashboardComponent {
    public user: User = User.createEmptyUser();
    private organizations: Organization[]=[];

    constructor(private _router:Router,
                private _routeArgs:RouteParams,
                private _organizationService: OrganizationService,
                private _userService: UserService) {
        var token = localStorage.getItem('token');

        this._userService.getUser(getUsername(token)).subscribe((user:User) => {
            this.user = this.user.deserialize(user);
            this.getOrganizations();
        });
    }

    ngOnInit():any {
        var token = localStorage.getItem('token');
    }

    public getOrganizations(){
        this._organizationService.getOrganizationsByUser(this.user.username).subscribe(
            data => {
                this.organizations = data.json();
            } , error => {console.log(error); this.organizations = []});
    }

    public toOrganization(organizationId: number){
        this._router.navigate(["/OrganizationDetail", { organizationId : organizationId }])
    }

}