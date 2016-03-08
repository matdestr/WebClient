import {Component, Input, Output} from "angular2/core";
import {Invitation} from "../../entities/invitations/invitation";
import {InvitationService} from "../../services/invitation.service";
import {Organization} from "../../entities/organization/organization";
import {Router} from "angular2/router";
import {getUsername} from "../../libraries/angular2-jwt";
import {EventEmitter} from "angular2/core";

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
    private username:string = null;
    @Input() public invitations:Invitation[];
    @Output() public onaccept:EventEmitter<number> = new EventEmitter<number>();

    public constructor(private _invitationService:InvitationService,
                       private _router:Router){
        var token:string = localStorage.getItem("token");
        this.username = getUsername(token);
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
                    self.onaccept._next(index);
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
                    self.invitations.splice(index, 1)
                }
            )
    }
}