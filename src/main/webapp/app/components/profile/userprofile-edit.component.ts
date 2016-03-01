import {Component} from "angular2/core";
import {NgIf, NgFor, NgSwitch, NgSwitchWhen} from "angular2/common";
import {RouteParams} from "angular2/router";
import {Response} from "angular2/http";
import {User} from "../../entities/user/user";
import {Organization} from "../../entities/organization";
import {UserService} from "../../services/user.service";
import {OrganizationService} from "../../services/organization.service";
import {ToolbarComponent} from "../widget/toolbar.component";
import {Router} from "angular2/router";
import {UpdateUserModel} from "../../entities/user/edit";

@Component({
    selector: 'profile',
    templateUrl: 'html/userprofile-edit.html',
    directives: [ToolbarComponent]
})

export class UserProfileEditComponent {
    private fileChanged:boolean = false;
    private file:File = null;
    public organizations:Organization[] = [];
    public user:User = User.createEmptyUser();
    public updateModel:UpdateUserModel = new UpdateUserModel();

    public constructor(
                       private _router:Router,
                       private _routeArgs:RouteParams,
                       private _userService:UserService,
                       private _organizationService:OrganizationService) {

        var username:string = _routeArgs.get("username");

        _userService.getUser(username).subscribe((user:User) => {
            this.user = this.user.deserialize(user);

            this.updateModel.name = this.user.name;
            this.updateModel.surname = this.user.surname;
            this.updateModel.email = this.user.email;
            this.updateModel.username = this.user.username;

            this._organizationService.getOrganizationsByOwner(this.user.username).subscribe(
                data => {
                    this.organizations = data.json();
                },
                error => {
                    console.log(error);
                    this.organizations = []
                });
        });

    }

    public saveChanges():void {
        if (this.updateModel.verifyPassword != "")
            if (this.fileChanged){
                console.log("Image changed, uploading image...");
                var request = this._userService.uploadPhoto(this.user.userId, this.file);

                request.onreadystatechange = (e) => {
                    if (e.srcElement.status == 200){
                        if (e.srcElement.readyState == 4){
                            this.updateUser();
                        }
                    }
                }
            } else {
                console.log("Image didn't change");
                this.updateUser();
            }
    }

    public updateUser() {
        this._userService.updateUser(this.user.userId, this.updateModel).subscribe((res:Response) =>
                (data) => {
                    console.log(data);
                },
            (error) => {
                console.log(error);
                //todo show error dialog?
            },
            () => {
                this.returnToProfile();
            });
    }

    public returnToProfile():void {
        this._router.navigate(["/Profile", {username: this.user.username}]);
    }

    public onFileChanged(event):void {
        this.file = event.srcElement.files[0];
        this.fileChanged = true;

    }

}