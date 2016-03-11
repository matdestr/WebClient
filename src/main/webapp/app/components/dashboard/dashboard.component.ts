
import {Component, OnInit, Input} from "angular2/core";
import {Router} from "angular2/router";
import {RouteParams} from "angular2/router";
import {ToolbarComponent} from "../widget/toolbar.component";
import {OrganizationService} from "../../services/organization.service";
import {User} from "../../entities/user/user";
import {UserService} from "../../services/user.service";
import {getUsername} from "../../libraries/angular2-jwt";
import {Organization} from "../../entities/organization/organization";
import {CanActivate} from "angular2/router";
import {tokenNotExpired} from "../../libraries/angular2-jwt";
import {SessionListItem} from "../../entities/session/session-list-item";
import {SessionService} from "../../services/session.service";
import {SessionStatus} from "../../entities/session/session-status";

@Component({
    selector: 'dashboard',
    templateUrl: 'html/dashboard.html',
    directives: [ToolbarComponent]
})

export class DashboardComponent {
    public user: User = User.createEmptyUser();
    private organizations: Organization[]=[];
    private organizationsSubSet:Organization[]=[];
    public counterBegin:number=0;
    public counterEnd:number=4;
    private myLeftDisplay:string="block";
    private myRightDisplay:string="block";
    private sessions:SessionListItem[]=[];
    private activeSessions: SessionListItem[]=[];
    private futureSessions: SessionListItem[]=[];
    private previousSessions: SessionListItem[]=[];

    constructor(private _router:Router,
                private _organizationService: OrganizationService,
                private _userService: UserService,
                private _sessionService: SessionService) {}

    ngOnInit():any {
        var token = localStorage.getItem('token');

        this._userService.getUser(getUsername(token)).subscribe((user:User) => {
            this.user = this.user.deserialize(user);
            this.getOrganizations();
            this.getSessions();
        });

        if(this.organizations.length<=4) {
            this.myLeftDisplay = "none";
            this.myRightDisplay = "none";
        }else {
            this.myLeftDisplay = "block";
            this.myRightDisplay = "block";
        }
    }

    public getOrganizations(){
        this._organizationService.getOrganizationsByUser(this.user.username).subscribe(
            data => {
                this.organizations = data.json();
                this.updateSubSet();
            } , error => {console.log(error); this.organizations = []});
    }

    public getSessions(){
        this._sessionService.getSessions().subscribe(
            data => {
                for(let sessionObject of data.json()) {
                    let session:SessionListItem = SessionListItem.createEmptySessionListItem().deserialize(sessionObject);
                    this.sessions.push(session);
                    switch (session.sessionStatus){
                        case SessionStatus.CREATED:
                            this.futureSessions.push(session); break;
                        case SessionStatus.FINISHED:
                            this.previousSessions.push(session);break;
                        case SessionStatus.IN_PROGRESS:
                            this.activeSessions.push(session);break;
                        default: this.futureSessions.push(session); break;
                    }
                }
            } , error => {console.log(error)}
        )
    }

    public updateSubSet(){
        this.organizationsSubSet = this.organizations.slice(0,4);
        if(this.organizations.length>4){
            this.myRightDisplay = "block";
        }
    }

    public toOrganization(organizationId: number){
        this._router.navigate(["/OrganizationDetail", { organizationId : organizationId }])
    }

    public nextOrgPage(){
        this.myLeftDisplay = "block";
        if(this.counterEnd >= this.organizations.length-1){
            this.myRightDisplay="none";
        }
        if(this.counterEnd >= this.organizations.length){
            return;
        }
        else{
        this.counterBegin++;
        this.counterEnd++;
        this.organizationsSubSet = this.organizations.slice(this.counterBegin,this.counterEnd);
        }
    }

    public previousOrgPage(){
        this.myRightDisplay = "block";
        if(this.counterBegin <= 1){
            this.myLeftDisplay="none";
        }
        if(this.counterBegin <= 0){
            return;
        }  else {
            this.counterBegin--;
            this.counterEnd--;
            this.organizationsSubSet = this.organizations.slice(this.counterBegin, this.counterEnd);
        }
    }

    public nextActiveSesPage(){

    }

    public previousActiveSesPage(){

    }

}