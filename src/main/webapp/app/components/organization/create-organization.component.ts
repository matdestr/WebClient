import {Component} from 'angular2/core';
import {NgFor} from "angular2/common";
import {FORM_DIRECTIVES} from "angular2/common";
import {NgModel} from "angular2/common";
import {NgForm} from "angular2/common";
import {CanActivate} from "angular2/router";

import {User} from "../../entities/user";
import {OrganizationService} from "../../services/organization.service";
import {Organization} from "../../entities/organization";
import {tokenNotExpired} from "../../libraries/angular2-jwt";
//import {ErrorDialogComponent} from "../error-dialog.component";

@Component({
    selector: 'create-organization',
    templateUrl: 'html/create-organization.html',
    directives: [/*ErrorDialogComponent,*/ NgFor, NgForm, FORM_DIRECTIVES]
})
@CanActivate(() => tokenNotExpired())
export class CreateOrganizationComponent {
    private organizationService : OrganizationService;
    
    private organization : Organization;
    private usersToInvite : User[];
    
    private organizationCreated : boolean;
    private isError : boolean;
    
    constructor (organizationService : OrganizationService) {
        this.organizationService = organizationService;
        this.organization = new Organization();
        this.usersToInvite = [];

        this.usersToInvite.push(new User());
        
        this.organizationCreated = false;
        this.isError = false;
    }
    
    private onSubmit() {
        if (this.organization.name) {
            this.organizationService.saveOrganization(this.organization)
                .subscribe(null,
                    error => {
                        this.isError = true;
                    },
                    () => {
                        this.organizationCreated = true;
                        this.isError = false;
                        this.inviteUsers();
                    });

            this.inviteUsers();
            // TODO : Route to other component
        }
    }
    
    private addUserEntry() {
        this.usersToInvite.push(new User());
    }
    
    private inviteUsers() {
        var nonEmptyUsers : User[] = this.usersToInvite.filter(u => {return u.email && u.email.length > 0});
        console.log(nonEmptyUsers);
    }
    
    private removeUserFromUsersToInvite(index : number) {
        this.usersToInvite.splice(index, 1);
    }
}
