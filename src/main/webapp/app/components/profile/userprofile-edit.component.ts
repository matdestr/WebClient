import {Component, OnInit} from "angular2/core";
import {NgIf, NgFor, NgSwitch, NgSwitchWhen, NgClass} from "angular2/common";
import {RouteParams} from "angular2/router";
import {Response} from "angular2/http";
import {getUsername} from "../../libraries/angular2-jwt";
import {User} from "../../entities/user/user";
import {Organization} from "../../entities/organization/organization";
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
    directives: [ToolbarComponent, ErrorDialogComponent, NgClass]
})

export class UserProfileEditComponent implements OnInit{
    private errorMessages:string[] = [];
    private fileChanged:boolean = false;
    private file:File = null;
    private updatingClass:string = "hide";
    public user:User = User.createEmptyUser();
    public imageUrl:string = "";
    public updateModel:UpdateUserModel = new UpdateUserModel();

    public constructor(
                       private _router:Router,
                       private _routeArgs:RouteParams,
                       private _userService:UserService,
                       private _tokenService:TokenService
    ) {}

    ngOnInit():any {
        var username:string = this._routeArgs.get("username");

        if (username == null)
            this._router.navigate(["/Dashboard"]);

        var token:string = localStorage.getItem("token");
        if (getUsername(token) !== username)
            this._router.navigate(["/Dashboard"]);

        this._userService.getUser(username).subscribe((user:User) => {
            this.user = this.user.deserialize(user);

            this.imageUrl = user.profilePictureUrl;

            this.updateModel.name = this.user.name;
            this.updateModel.surname = this.user.surname;
            this.updateModel.email = this.user.email;
            this.updateModel.username = this.user.username;
        });
    }

    public saveChanges():void {
        this.onError(null); //Reset errors

        if (this.updateModel.verifyPassword != "") {
            this.updatingClass = "show";
            var fileUploadFailed = false;
            if (this.fileChanged) {
                var request = this._userService.uploadPhoto(this.user.userId, this.file);
                request.onreadystatechange = (event) => {
                    var target = event.target || event.srcElement;

                    //Ignore the errors on status and readyState, it just works.
                    if (target.status == 200) {
                        if (target.readyState == 4)
                            this.updateUser();
                    } else {
                        if (!fileUploadFailed) {
                            this.updatingClass = "hide";
                            fileUploadFailed = true;
                            //target.response is empty?
                            var message = this.messageToJson("Image is too large (max. 500kb).");
                            this.onError(message);
                        }
                    }
                };
            } else
                this.updateUser();
        } else {
            this.updatingClass = "hide";
            var message = this.messageToJson("Fill in your password.");
            this.onError(message);
        }
    }

    public updateUser() : void {
        var self = this;
        this._userService.updateUser(this.user.userId, this.updateModel).subscribe((res:Response) =>
                (data) => {
                    console.log(data);
                },
            (error) => {
                this.updatingClass = "hide";
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
        var self:any = this;

        this.file = event.srcElement.files[0];
        var reader:FileReader = new FileReader();
        reader.readAsDataURL(this.file);
        reader.onload = function() {
            self.imageUrl = reader.result;
        };
        this.fileChanged = true;
    }

    private onError(message) : void {
        if (message) {
            var obj = JSON.parse(message);
            if (obj)
                this.errorMessages.push(obj.message);
        } else
            this.errorMessages = [];
    }

    private messageToJson(message) : string {
        if (typeof(message) !== "string")
            return null;

        return "{\"message\":\"" + message +"\"}";
    }
}