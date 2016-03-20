import {Component} from "angular2/core";
import {NgIf} from "angular2/common";
import {ToolbarComponent} from "../widget/toolbar.component";
import {Router} from "angular2/router";
import {RouteParams} from "angular2/router";
import {InvitationService} from "../../services/invitation.service";
import {ROUTER_DIRECTIVES} from "angular2/router";
import {Organization} from "../../entities/organization/organization";
import {OrganizationService} from "../../services/organization.service";
import {routerCanDeactivate} from "angular2/src/router/lifecycle_annotations_impl";
import {ErrorDialogComponent} from "../widget/error-dialog.component";

/**
 * This component is responsible for all the functionality of the invitation page.
 */
@Component({
    selector: 'invitation',
    templateUrl : 'html/invitation.html',
    directives: [ToolbarComponent, ErrorDialogComponent, ROUTER_DIRECTIVES]
})

export class InvitationAcceptComponent {
    private errorMessages:string[] = [];
    private organization:Organization = Organization.createEmptyOrganization();
    private acceptCode:string = null;

    public constructor(private _router:Router,
                       private _routeParams:RouteParams,
                       private _invitationService:InvitationService,
                       private _organizationService:OrganizationService){

        this.acceptCode = _routeParams.get("acceptId");
        var organizationId:number = Number.parseInt(_routeParams.get("organizationId"));

        if (this.acceptCode == null || organizationId == null){
            //TODO navigate to not found page.
            this._router.navigate(["/Dashboard"]);
        }

        var self:any = this;

        this._organizationService.getOrganization(organizationId).subscribe(
            (data) => { this.organization = data.json();
                        console.log(this.organization)
            },
            (error) => { self.onError(error); },
            () => { console.log("Request organization complete.") }
        )
    }

    public acceptInvitation() : void {
        var self:any = this;

        this._invitationService.acceptInvitation(this.acceptCode, this.organization.organizationId).subscribe(
            (data) => { console.log(data); },
            (error) => { self.onError(error); },
            () => {
                this._router.navigate(["/OrganizationDetail", { organizationId: this.organization.organizationId }])
            }
        );
    }

    public declineInvitation() : void {
        var self:any = this;

        this._invitationService.declineInvitation(this.acceptCode, this.organization.organizationId).subscribe(
            (data) => { console.log(data); },
            (error) => { self.onError(error); },
            () => {
                this._router.navigate(["/Dashboard"])
            }
        )
    }

    private onError(message) : void {
        if (message) {
            var obj = JSON.parse(message);

            if (obj)
                this.errorMessages.push(obj.message);
            else
                this.errorMessages = [];
        }
    }
}
