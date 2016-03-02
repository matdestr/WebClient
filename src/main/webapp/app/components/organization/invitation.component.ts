import {Component} from "angular2/core";
import {ToolbarComponent} from "../widget/toolbar.component";
import {Router} from "angular2/router";
import {RouteParams} from "angular2/router";
import {InvitationService} from "../../services/invitation.service";
import {ROUTER_DIRECTIVES} from "angular2/router";

@Component({
    selector: 'invitation',
    template : `<toolbar></toolbar>
                <button (click)="acceptInvitation()">Accept invitation</button>`,
    directives: [ToolbarComponent, ROUTER_DIRECTIVES]
})

export class InvitationComponent {
    private acceptCode:string = null;

    public constructor(private _router:Router,
                       private _routeParams:RouteParams,
                       private _invitationService:InvitationService){
        this.acceptCode = _routeParams.get("acceptId");

        if (this.acceptCode == null){
            //TODO navigate to not found page.
            this._router.navigate(["/Dashboard"]);
        }
    }

    public acceptInvitation(){
        this._invitationService.acceptInvitation(this.acceptCode).subscribe(
            (data) => { console.log(data); },
            (error) => { console.log(error); },
            () => {
                console.log("Accepted.");
            }
        );
    }
}
