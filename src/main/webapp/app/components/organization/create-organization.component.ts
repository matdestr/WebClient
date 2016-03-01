import {Component, OnInit} from 'angular2/core';
import {FORM_DIRECTIVES, NgFor, NgModel, NgForm} from "angular2/common";
import {Router, CanActivate} from "angular2/router";

import {User} from "../../entities/user/user";
import {Email} from "../../entities/user/email";
import {OrganizationService} from "../../services/organization.service";
import {UserService} from "../../services/user.service";
import {CreateOrganization} from "../../entities/organization";
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
    private organization : CreateOrganization;
    private usersToInvite : Email[];
    
    private organizationCreated : boolean;
    private showErrorOrganizationName : boolean;
    private isError : boolean;
    
    constructor (private _organizationService : OrganizationService, private _router : Router) {

    }
    
    ngOnInit() : any {
        this.organization = new CreateOrganization();
        this.usersToInvite = [];
        this.usersToInvite.push(new Email());
        this.organizationCreated = false;
        this.showErrorOrganizationName = false;
        this.isError = false;
    }

    private onSubmit(form) {
        if (this.organization.name) {
            this.organization.emails = this.filterEmails();

            console.log("Creating organization");
            this._organizationService.createOrganization(this.organization)
                .subscribe(null,
                    error => {
                        this.isError = true;
                        console.log(error);
                    },
                    () => {
                        this.organizationCreated = true;
                        this.isError = false;
                        this._router.navigate(['/Dashboard']);
                    });

        }
    }
    
    private setShowErrorOrganizationName(show : boolean) {
        this.showErrorOrganizationName = show;
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
