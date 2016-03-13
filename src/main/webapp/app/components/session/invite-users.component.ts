import {Component, OnInit, Input} from "angular2/core";
import {ToolbarComponent} from "../widget/toolbar.component";
import {Router} from "angular2/router";
import {RouteParams} from "angular2/router";
import {User} from "../../entities/user/user";
import {Email} from "../../entities/user/email";
import {SessionService} from "../../services/session.service";
import {InvitationService} from "../../services/invitation.service";

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
                private _invitationService:InvitationService) {
        this.sessionid= +_routeArgs.params["sessionId"];
    }

    ngOnInit() : any {
        this.usersToInvite = [];
        this.usersToInvite.push(new Email());
    }

    private onSubmit(form) {
        for(let user of this.usersToInvite){
            let userEmail:string = user.email;
            this._invitationService.getInvitationsForUser(userEmail);
        }

        this._router.navigate(["/SessionAddCards",{sessionId:this.sessionid}])
    }


    private addUserEntry() {
        this.usersToInvite.push(new Email());
    }

    private filterEmails() {
        return this.usersToInvite.filter(u => {return u && u.email.length > 0});
    }

    private removeUserFromUsersToInvite(index : number) {
        this.usersToInvite.splice(index, 1);
    }
}