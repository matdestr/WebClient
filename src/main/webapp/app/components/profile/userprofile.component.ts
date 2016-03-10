import {Component, OnInit} from "angular2/core";
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

export class UserProfileComponent implements OnInit {
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
    }

    ngOnInit():any {
        var username:string = this._routeArgs.get("username");

        if (username == null || username.length == 0)
            this._router.navigate(["/Dashboard"]);

        var token:string = localStorage.getItem("token");

        if (getUsername(token) === username)
            this.canEdit = true;

        this._userService.getUser(username).subscribe(
            (data) => {
                var self:any = this;
                this.user = this.user.deserialize(data);

                this.retrieveOrganizations();

                this._invitationService.getInvitationsForUser(this.user.email).subscribe(
                    data => { this.invitations = data.json(); },
                    error => { self.onError(error); },
                    () => {}
                );
            },
            (error) => { this._router.navigate(["/Dashboard"]) },
            () => { });

        return null;
    }


    private retrieveOrganizations() : void {
        var self:any = this;
        this._organizationService.getOrganizationsByUser(this.user.username).subscribe(
            data => {
                this.organizations = data.json();
            },
            error => {
                self.onError(error);
                this.organizations = []
            });
    }

    public onInviteAccepted(e) : void {
        this.invitations.splice(e, 1)
        this.retrieveOrganizations();
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