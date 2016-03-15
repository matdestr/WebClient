import {Component, OnInit, Input} from "angular2/core";
import {ToolbarComponent} from "../widget/toolbar.component";
import {Router} from "angular2/router";
import {RouteParams} from "angular2/router";
import {User} from "../../entities/user/user";
import {Email} from "../../entities/user/email";
import {SessionService} from "../../services/session.service";
import {InvitationService} from "../../services/invitation.service";
import {SessionInvitationService} from "../../services/session-invitation.service";

@Component({
    selector: 'invite-users',
    templateUrl: 'html/invite-users.html',
    directives: [ToolbarComponent]
})
export class InviteUsersComponent{
    private usersToInvite : Email[];
    private sessionid: number;

    constructor(private _router:Router,
                private _routeArgs:RouteParams,
                private _sessionInvitationService: SessionInvitationService) {
        this.sessionid= +_routeArgs.params["sessionId"];
    }

    ngOnInit() : any {
        this.usersToInvite = [];
        this.usersToInvite.push(new Email());
    }

    public onSubmit(form) {
        for(let email of this.usersToInvite){
            let userEmail:string = email.email;
            this._sessionInvitationService.inviteUsersToSession(this.sessionid,userEmail);
        }
        this._router.navigate(["/SessionAddCards",{sessionId:this.sessionid}])
    }


    public addUserEntry() {
        this.usersToInvite.push(new Email());
    }

    public filterEmails() {
        return this.usersToInvite.filter(u => {return u && u.email.length > 0});
    }

    public removeUserFromUsersToInvite(index : number) {
        this.usersToInvite.splice(index, 1);
    }
}