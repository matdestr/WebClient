import {Component} from "angular2/core";
import {NgIf, NgFor, NgSwitch, NgSwitchWhen} from "angular2/common";
import {RouteParams} from "angular2/router";
import {Response} from "angular2/http";
import {getUsername} from "../../libraries/angular2-jwt";
import {User} from "../../entities/user/user";
import {Organization} from "../../entities/organization/organization";
import {UserService} from "../../services/user.service";
import {OrganizationService} from "../../services/organization.service";
import {ToolbarComponent} from "../widget/toolbar.component";
import {Router} from "angular2/router";
import {InvitationService} from "../../services/invitation.service";
import {Invitation} from "../../entities/invitations/invitation";
import {ErrorDialogComponent} from "../widget/error-dialog.component";
import {InvitationComponent} from "../invitations/invitation.component";
import {tokenNotExpired} from "../../libraries/angular2-jwt";

@Component({
    selector: 'profile',
    templateUrl: 'html/userprofile.html',
    directives: [ToolbarComponent, ErrorDialogComponent, InvitationComponent]
})

export class UserProfileComponent {
    private canEdit:boolean = false;
    private errorMessages:string[] = [];
    private invitations:Invitation[] = [];
    private organizations:Organization[] = [];
    private user:User = User.createEmptyUser();

    public constructor(
        private _router:Router,
        private _routeArgs:RouteParams,
        private _userService:UserService,
        private _organizationService:OrganizationService,
        private _invitationService:InvitationService){

        var username:string = _routeArgs.get("username");

        if (username == null)
            this._router.navigate(["/Dashboard"]);

        var token:string = localStorage.getItem("token");
        if (getUsername(token) === this.user.username)
            this.canEdit = true;

        _userService.getUser(username).subscribe((user:User) => {
            var self:any = this;
            this.user = this.user.deserialize(user);

            this._organizationService.getOrganizationsByUser(this.user.username).subscribe(
                data => {
                    this.organizations = data.json();
                },
                error => {
                    self.onError(error);
                    this.organizations = []
                });

            this._invitationService.getInvitationsForUser(this.user.userId).subscribe(
                data => { this.invitations = data.json(); },
                error => { self.onError(error); },
                () => {}
            );
        });

    }

    public editProfile():void {
        this._router.navigate(["/EditProfile", { username: this.user.username }]);
    }

    private onError(message) : void {
        if (message) {
            var obj = JSON.parse(message);

            if (obj)
                this.errorMessages.push(obj.message);
            else
                this.errorMessages = [];
        }
    }
}