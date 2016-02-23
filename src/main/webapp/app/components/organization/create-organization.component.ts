import {Component, OnInit} from 'angular2/core';
import {FORM_DIRECTIVES, NgFor, NgModel, NgForm} from "angular2/common";
import {Router, CanActivate} from "angular2/router";

import {User} from "../../entities/user";
import {OrganizationService} from "../../services/organization.service";
import {Organization} from "../../entities/organization";
import {tokenNotExpired} from "../../libraries/angular2-jwt";
import {ToolbarComponent} from "../widget/toolbar.component";
import {ErrorDialogComponent} from "../widget/error-dialog.component";

@Component({
    selector: 'create-organization',
    templateUrl: 'html/create-organization.html',
    directives: [ErrorDialogComponent, NgFor, NgForm, FORM_DIRECTIVES, ToolbarComponent]
})
//@CanActivate(() => tokenNotExpired())
export class CreateOrganizationComponent implements OnInit {
    private organization : Organization;
    private usersToInvite : User[];
    
    private organizationCreated : boolean;
    private showErrorOrganizationName : boolean;
    private isError : boolean;
    
    constructor (private _organizationService : OrganizationService, private _router : Router) { }
    
    ngOnInit() : any {
        this.organization = new Organization();
        this.usersToInvite = [];

        for (let i = 0; i < 3; i++) {
            this.usersToInvite.push(User.createEmptyUser());
        }

        this.organizationCreated = false;
        this.showErrorOrganizationName = false;
        this.isError = false;
    }

    private onSubmit(form) {
        if (this.organization.name) {
            this._organizationService.saveOrganization(this.organization)
                .subscribe(null,
                    error => {
                        this.isError = true;
                        console.log(error);
                    },
                    () => {
                        this.organizationCreated = true;
                        this.isError = false;
                        this.inviteUsers();
                    });

            this.inviteUsers();
            this._router.navigate(['/Dashboard']);
        }
    }
    
    private setShowErrorOrganizationName(show : boolean) {
        this.showErrorOrganizationName = show;
    }
    
    private addUserEntry() {
        this.usersToInvite.push(User.createEmptyUser());
    }
    
    private inviteUsers() {
        var nonEmptyUsers : User[] = this.usersToInvite.filter(u => {return u.email && u.email.length > 0});
        console.log(nonEmptyUsers);
    }
    
    private removeUserFromUsersToInvite(index : number) {
        this.usersToInvite.splice(index, 1);
    }
}
