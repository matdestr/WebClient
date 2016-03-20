import {Component, OnInit} from 'angular2/core';
import {FORM_DIRECTIVES, NgFor, NgModel, NgForm} from "angular2/common";
import {Router, CanActivate} from "angular2/router";

import {User} from "../../entities/user/user";
import {Email} from "../../entities/user/email";
import {OrganizationService} from "../../services/organization.service";
import {UserService} from "../../services/user.service";
import {CreateOrganization} from "../../entities/organization/organization";
import {tokenNotExpired} from "../../libraries/angular2-jwt";
import {ToolbarComponent} from "../widget/toolbar.component";
import {ErrorDialogComponent} from "../widget/error-dialog.component";
import {Response} from "angular2/http";

/**
 * This component is responsible for all the functionality of the create organization page.
 */
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
    private errorMessages:string[] = new Array();

    constructor (private _organizationService : OrganizationService, private _router : Router) {

    }
    
    ngOnInit() : any {
        this.organization = new CreateOrganization();
        this.usersToInvite = [];
        this.usersToInvite.push(new Email());
        this.organizationCreated = false;
        this.showErrorOrganizationName = false;
    }

    private onSubmit(form) {
        var self = this;
        if (this.organization.name) {
            this.organization.emails = this.filterEmails();

            this._organizationService.createOrganization(this.organization)
                .subscribe((data) => { console.log(data); },
                    (error) => {
                        self.handleError(error);
                    }, () => {
                        this.organizationCreated = true;
                        this._router.navigate(['/Dashboard']);
                    });

        }
    }

    private handleError(error:Response){
        var obj = JSON.parse(error.text());
        console.log(obj);
        if (obj.fieldErrors){
            obj.fieldErrors.forEach(e => this.onError(e.message));
        } else {
            this.onError(obj.message);
        }
    }

    private onError(message:string){
        if (message) {
            this.errorMessages.push(message);
        } else {
            this.errorMessages = new Array();
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
