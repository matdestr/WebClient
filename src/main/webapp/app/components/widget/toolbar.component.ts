import {Component, OnInit} from 'angular2/core';
import {ROUTER_PROVIDERS, ROUTER_DIRECTIVES} from 'angular2/router';
import {SignOutComponent} from "../authentication/sign-out.component";
import {UserService} from "../../services/user.service";
import {getUsername} from "../../libraries/angular2-jwt";
import {User} from "../../entities/user/user";
import {Organization} from "../../entities/organization";
import {OrganizationService} from "../../services/organization.service";

import {Router} from "angular2/router";

@Component({
    selector: 'toolbar',
    templateUrl: 'html/toolbar.html',
    directives:[SignOutComponent, ROUTER_DIRECTIVES]
})
export class ToolbarComponent implements OnInit{

    private organizations : Organization[] = [];
    public user: User = User.createEmptyUser();

    constructor(private _userService: UserService, private _router:Router, private _organizationService : OrganizationService){
        var token = localStorage.getItem('token');

        if (token == null)
            return; // TODO : Show error page
        
        this._userService.getUser(getUsername(token)).subscribe((user:User) => {
            this.user = this.user.deserialize(user);
        });
    }

    ngOnInit():any {
        var token = localStorage.getItem('token');

        this._userService.getUser(getUsername(token)).subscribe((user:User) => {
            this.user = this.user.deserialize(user);
            this.getOrganizations();
        });

        return null;
    }

    public getOrganizations(){
        this._organizationService.getOrganizationsByOwner(this.user.username).subscribe(
            data => {
                this.organizations = data.json();
            }, error => {console.log(error); this.organizations = []});
    }

    public toProfile():void {
        console.log("Routing to profile");
        this._router.navigate(["/Profile", { username: this.user.username }]);
    }
}
