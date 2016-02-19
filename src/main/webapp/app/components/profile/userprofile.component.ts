import {Component} from "angular2/core";
import {RouteParams} from "angular2/router";
import {User} from "../../entities/user";
import {UserService} from "../../services/user.service";

@Component({
    selector: 'profile',
    templateUrl: 'html/userprofile.html'
})

export class UserProfileComponent {
    public user:User = User.createEmptyUser();

    public constructor(
        private _routeArgs:RouteParams,
        private _userService:UserService){

        var username:string = _routeArgs.get("username");

        _userService.getUser(username).subscribe((user:User) => {
            this.user = this.user.deserialize(user);
            console.log("did it fam");
        });
    }
}