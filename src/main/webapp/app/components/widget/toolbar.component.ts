import {Component, OnInit} from 'angular2/core';
import {SignOutComponent} from "../authentication/sign-out.component";
import {UserService} from "../../services/user.service";
import {getUsername} from "../../libraries/angular2-jwt";
import {User} from "../../entities/user";

@Component({
    selector: 'toolbar',
    templateUrl: 'html/toolbar.html',
    directives:[SignOutComponent]
})
export class ToolbarComponent implements OnInit{

    private user: User = User.createEmptyUser();

    constructor(private _userService: UserService){
        var token = localStorage.getItem('token');

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
}