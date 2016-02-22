import {Component, OnInit} from 'angular2/core';
import {ROUTER_PROVIDERS, ROUTER_DIRECTIVES} from 'angular2/router';
import {SignOutComponent} from "../authentication/sign-out.component";
import {UserService} from "../../services/user.service";
import {getUsername} from "../../libraries/angular2-jwt";
import {User} from "../../entities/user";

import {Router} from "angular2/router";

@Component({
    selector: 'toolbar',
    templateUrl: 'html/toolbar.html',
    directives:[SignOutComponent, ROUTER_DIRECTIVES]
})
export class ToolbarComponent implements OnInit{

    private user: User = User.createEmptyUser();

    constructor(private _userService: UserService, private _router:Router){
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
        });

        return null;
    }

    public toProfile():void {
        console.log("Routing to profile");
        this._router.navigate(["/Profile", { username: this.user.username }]);
    }
}