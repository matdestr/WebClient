import {Component} from "angular2/core";
import {NgIf, NgFor, NgSwitch, NgSwitchWhen} from "angular2/common";
import {RouteParams} from "angular2/router";
import {Response} from "angular2/http";
import {getUsername} from "../../libraries/angular2-jwt";
import {User} from "../../entities/user/user";
import {Organization} from "../../entities/organization";
import {UserService} from "../../services/user.service";
import {OrganizationService} from "../../services/organization.service";
import {ToolbarComponent} from "../widget/toolbar.component";
import {Router} from "angular2/router";
import {UpdateUserModel} from "../../entities/user/edit";

import {CredentialsModel} from "../../entities/authenticatie/credentials"
import {TokenService} from "../../services/token.service";
import {Token} from "../../entities/authenticatie/token";
import {ErrorDialogComponent} from "../widget/error-dialog.component";

@Component({
    selector: 'profile',
    templateUrl: 'html/userprofile-edit.html',
    directives: [ToolbarComponent, ErrorDialogComponent]
})

export class UserProfileEditComponent {
    private errorMessages:string[] = new Array();
    private fileChanged:boolean = false;
    private file:File = null;
    public user:User = User.createEmptyUser();
    public updateModel:UpdateUserModel = new UpdateUserModel();

    public constructor(
                       private _router:Router,
                       private _routeArgs:RouteParams,
                       private _userService:UserService,
                       private _tokenService:TokenService
    ) {

        var username:string = _routeArgs.get("username");

        if (username == null)
            this._router.navigate(["/Dashboard"]);

        var token:string = localStorage.getItem("token");
        if (getUsername(token) !== this.user.username)
            this._router.navigate(["/Dashboard"]);

        _userService.getUser(username).subscribe((user:User) => {
            this.user = this.user.deserialize(user);

            this.updateModel.name = this.user.name;
            this.updateModel.surname = this.user.surname;
            this.updateModel.email = this.user.email;
            this.updateModel.username = this.user.username;
        });

    }

    public saveChanges():void {
        this.onError(null); //Reset errors

        if (this.updateModel.verifyPassword != "")
            if (this.fileChanged){
                console.log("Image changed, uploading image...");
                var request = this._userService.uploadPhoto(this.user.userId, this.file);
                request.onreadystatechange = (event) => {
                    var target = event.target || event.srcElement;

                    //Ignore the errors on status and readyState, it just works.
                    if (target.status == 200){
                        if (target.readyState == 4)
                            this.updateUser();
                    } else {
                        var obj = JSON.parse(target.responseText);
                        this.onError(obj.message);
                    }
                }
            } else {
                console.log("Image didn't change");
                this.updateUser();
            }
    }

    public updateUser() : void {
        var self = this;
        this._userService.updateUser(this.user.userId, this.updateModel).subscribe((res:Response) =>
                (data) => {
                    console.log(data);
                },
            (error) => {
                var obj = JSON.parse(error.text());

                if (obj.fieldErrors){
                    obj.fieldErrors.forEach(function (entry) {
                        self.onError(entry.message);
                    });
                } else {
                    if (obj.message.indexOf("Username") > -1)
                        self.onError("Password is incorrect");
                    else
                        self.onError(obj.message);
                }
            },
            () => {
                this.requestNewTokenAndGoBackToProfile();
            });
    }

    public requestNewTokenAndGoBackToProfile() : void {
        var credentials = new CredentialsModel();
        credentials.username = this.updateModel.username;
        credentials.password = this.updateModel.verifyPassword;

        this._tokenService.authenticate(credentials).subscribe(
            (token:Token) => {
                this._tokenService.saveToken(token);
                this.returnToProfile();
            },
            (error) => {
                console.log(error);
            }),
            () => {
                return true;
            };
    }

    public returnToProfile():void {
        this._router.navigate(["/Profile", {username: this.updateModel.username}]);
    }

    public onFileChanged(event):void {
        this.file = event.srcElement.files[0];
        this.fileChanged = true;
    }

    private onError(message:string){
        if (message) {
            this.errorMessages.push(message);
        } else {
            this.errorMessages = new Array();
        }
    }
}