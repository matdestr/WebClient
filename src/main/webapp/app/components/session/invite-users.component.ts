import {Component, OnInit, Input} from "angular2/core";
import {ToolbarComponent} from "../widget/toolbar.component";
import {Router} from "angular2/router";
import {RouteParams} from "angular2/router";
import {User} from "../../entities/user/user";
import {Email} from "../../entities/user/email";

@Component({
    selector: 'invite-users',
    templateUrl: 'html/invite-users.html',
    directives: [ToolbarComponent]
})
export class InviteUsersComponent{
    private usersToInvite : Email[];

    constructor(private _router:Router,
                private _routeArgs:RouteParams) {
    }

    ngOnInit() : any {
        this.usersToInvite = [];
        this.usersToInvite.push(new Email());
    }

    private onSubmit(form) {

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