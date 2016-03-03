import {Component, Input} from "angular2/core";
import {Invitation} from "../../entities/invitations/invitation";
import {InvitationService} from "../../services/invitation.service";
import {Organization} from "../../entities/organization";
import {Router} from "angular2/router";

@Component({
    selector: 'invitations',
    template:
        `
        <div class="container">
            <div class="row">
                <div *ngFor="#invitation of invitations; #i = index">
                   <h4 class="invitation-organization-title">{{ invitation.organization.name }}</h4>

                    <form class="invitation-accept-form">
                        <input (click)="accept(i)" class="invitation-button-accept" type="submit" value="Accept"/>
                        <input (click)="decline(i)" class="invitation-button-decline" type="submit" value="Decline"/>
                    </form>
                </div>
            </div>
        </div>
        `
})

export class InvitationComponent {
    @Input() public invitations:Invitation[];

    public constructor(private _invitationService:InvitationService){
    }

    public accept(index:number) : void {
        var self:any = this;
        this._invitationService.acceptInvitation(
            this.invitations[index].acceptId,
            this.invitations[index].organization.organizationId)
            .subscribe(
                (data) => { console.log(data) },
                (error) => { console.log(error) },
                () => {
                    //console.log("Invitation accepted. " + this.invitations[index].acceptId + " is now invalid.");
                    self.invitations.splice(index, 1)
                }
            )
    }

    public decline(index:number) : void {
        var self:any = this;
        this._invitationService.declineInvitation(
            this.invitations[index].acceptId,
            this.invitations[index].organization.organizationId)
            .subscribe(
                (data) => { console.log(data) },
                (error) => { console.log(error) },
                () => {
                    //console.log("Invitation declined. " + this.invitations[index].acceptId + " is now invalid.");
                    self.invitations.splice(index, 1)
                }
            )
    }
}