import {Component, OnInit, Input} from "angular2/core";
import {Router, RouteParams} from "angular2/router";
import {User} from "../../entities/user/user";
import {Email} from "../../entities/user/email";
import {Session} from "../../entities/session/session";

import {SessionService} from "../../services/session.service";
import {InvitationService} from "../../services/invitation.service";
import {SessionInvitationService} from "../../services/session-invitation.service";

@Component({
    selector: 'session-invite',
    templateUrl: 'html/session/session-invite.html'
})
export class SessionInviteComponent {
    @Input()
    public session : Session;
    
    private usersToInvite : Email[];
    private statusMessage : string;
    private errorMessage : string;
    private submitDisabled : boolean = false;

    constructor(private _sessionInvitationService: SessionInvitationService) { }

    ngOnInit() : any {
        this.usersToInvite = [];
        this.usersToInvite.push(new Email());
    }

    public onSubmit(form) : void {
        this.errorMessage = '';
        
        let filteredEmails : Email[] = this.filterEmails();
        
        let invitedUsers : number = 0;
        let invitedUsersFailed : number = 0;

        if (filteredEmails.length == 0) {
            this.errorMessage = 'Please fill in one or more valid email addresses';
            return;
        }
        
        this.submitDisabled = true;
        this.statusMessage = 'Sending invites ...';
        
        for (let email of filteredEmails) {
            var userEmail : string = email.email;
            
            this._sessionInvitationService
                .inviteUsersToSession(this.session.sessionId, userEmail)
                .subscribe(
                    data => {
                        console.log('Sent invitation for session to ' + userEmail);
                        invitedUsers += 1;
                        this.confirmUserInvites(filteredEmails.length, invitedUsers, invitedUsersFailed);
                    },
                    error => {
                        console.log('Could not send invitation to ' + userEmail);
                        invitedUsersFailed += 1;
                        this.confirmUserInvites(filteredEmails.length, invitedUsers, invitedUsersFailed);
                    }
                );
        }
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
    
    private confirmUserInvites(totalAmountOfInvites : number, amountOfInvitesSucceeded : number, amountOfInvitesFailed : number) : void {
        if (totalAmountOfInvites > amountOfInvitesSucceeded + amountOfInvitesFailed)
            return;
        
        this.statusMessage = '';
        this.errorMessage = '';
        
        if (amountOfInvitesSucceeded < 1 || amountOfInvitesFailed >= amountOfInvitesSucceeded + amountOfInvitesFailed) {
            this.errorMessage = 'Failed to invite user(s). Please make sure the email address is correct and try again.';
            this.submitDisabled = false;
            return;
        }
        
        this.statusMessage = 'Confirming invites ...';
        
        this._sessionInvitationService
            .confirmInvitedUsers(this.session.sessionId)
            .subscribe(
                data => {
                    console.log('Confirmed user invites');
                },
                error => {
                    console.log('Failed to confirm user invites');
                    this.submitDisabled = false;
                }
            );
    }
}
