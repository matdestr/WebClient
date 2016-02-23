import {Component} from "angular2/core";
import {NgIf, NgFor, NgSwitch, NgSwitchWhen} from "angular2/common";
import {RouteParams} from "angular2/router";
import {Response} from "angular2/http";
import {User} from "../../entities/user";
import {Organization} from "../../entities/organization";
import {UserService} from "../../services/user.service";
import {OrganizationService} from "../../services/organization.service";
import {ToolbarComponent} from "../widget/toolbar.component";
import {Router} from "angular2/router";

@Component({
    selector: 'profile',
    templateUrl: 'html/userprofile-edit.html',
    directives: [ToolbarComponent]
})

export class UserProfileEditComponent {
    public organizations:Organization[] = [];
    public user:User = User.createEmptyUser();

    public constructor(
        private _router:Router,
        private _routeArgs:RouteParams,
        private _userService:UserService,
        private _organizationService:OrganizationService){

        var username:string = _routeArgs.get("username");

        _userService.getUser(username).subscribe((user:User) => {
            this.user = this.user.deserialize(user);

            this._organizationService.getOrganizationsByUser(this.user.username).subscribe(
                data => {
                    this.organizations = data.json();
                },
                error => {console.log(error); this.organizations = []});
        });

    }

    public saveChanges():void {
        this._userService.saveUser(this.user).subscribe((res:Response) =>
            (data) => {
                console.log("User updated.");
            this._router.navigate(["/Profile", { username: this.user.username }])
            },
            (error) => {
                console.log(error);
                //todo show error dialog?
            });
    }
}