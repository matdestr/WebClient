
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
    private counterBegin:number=0;
    private counterEnd:number=4;
    private counterActBegin:number=0;
    private counterActEnd:number=4;
    private counterFutBegin:number=0;
    private counterFutEnd:number=4;
    private counterPrevBegin:number=0;
    private counterPrevEnd:number=4;
    private myLeftDisplay:string="block";
    private myRightDisplay:string="block";
    private myLeftActDisplay: string="block";
    private myRightActDisplay:string="block";
    private myLeftFutDisplay: string="block";
    private myRightFutDisplay:string="block";
    private myLeftPrevDisplay: string="block";
    private myRightPrevDisplay:string="block";
    private sessions:SessionListItem[]=[];
    private activeSessions: SessionListItem[]=[];
    private activeSessionsSubset: SessionListItem[]=[];
    private futureSessions: SessionListItem[]=[];
    private futureSessionsSubset: SessionListItem[]=[];
    private previousSessions: SessionListItem[]=[];
    private previousSessionsSubset: SessionListItem[]=[];

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
    }

    public updateDisplays(){


        if(this.activeSessions.length<=4) {
            this.myLeftActDisplay = "none";
            this.myRightActDisplay = "none";
        }else {
            this.myLeftActDisplay = "none";
            this.myRightActDisplay = "block";
        }

        if(this.futureSessions.length<=4) {
            this.myLeftFutDisplay = "none";
            this.myRightFutDisplay = "none";
        }else {
            this.myLeftFutDisplay = "none";
            this.myRightFutDisplay = "block";
        }

        if(this.previousSessions.length<=4) {
            this.myLeftPrevDisplay = "none";
            this.myRightPrevDisplay = "none";
        }else {
            this.myLeftPrevDisplay = "none";
            this.myRightPrevDisplay = "block";
        }
    }

    public getOrganizations(){
        this._organizationService.getOrganizationsByUser(this.user.username).subscribe(
            data => {
                this.organizations = data.json();
                this.organizationsSubSet = this.organizations.slice(0,4);
                if(this.organizations.length<=4) {
                    this.myLeftDisplay = "none";
                    this.myRightDisplay = "none";
                }else {
                    this.myLeftDisplay = "none";
                    this.myRightDisplay = "block";
                }
            } , error => {console.log(error); this.organizations = []});
    }

    public getSessions(){
        this._sessionService.getSessions().subscribe(
            data => {
                for(let sessionObject of data.json()) {
                    let session:SessionListItem = SessionListItem.createEmptySessionListItem().deserialize(sessionObject);
                    this.sessions.push(session);
                    switch (session.sessionStatus){
                        case SessionStatus.FINISHED:
                            this.previousSessions.push(session);break;
                        case SessionStatus.IN_PROGRESS:
                            this.activeSessions.push(session);break;
                        default: this.futureSessions.push(session); break;
                    }
                }
                this.updateSubSet();
                this.updateDisplays();
            } , error => {console.log(error)}
        ) ;
    }

    public updateSubSet(){
        this.activeSessionsSubset = this.activeSessions.slice(0,4);
        this.futureSessionsSubset = this.futureSessions.slice(0,4);
        this.previousSessionsSubset = this.previousSessions.slice(0,4);
    }

    public toOrganization(organizationId: number){
        this._router.navigate(["/OrganizationDetail", { organizationId : organizationId }])
    }

    public toSession(sessionId: number){
        this._router.navigate(["/ActiveSession"],{sessionId:sessionId})
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
        this.myLeftActDisplay = "block";
        if(this.counterActEnd >= this.activeSessions.length-1){
            this.myRightActDisplay="none";
        }
        if(this.counterActEnd >= this.activeSessions.length){
            return;
        }
        else{
            this.counterActBegin++;
            this.counterActEnd++;
            this.activeSessionsSubset = this.activeSessions.slice(this.counterActBegin,this.counterActEnd);
        }
    }

    public previousActiveSesPage(){
        this.myRightActDisplay = "block";
        if(this.counterActBegin <= 1){
            this.myLeftActDisplay="none";
        }
        if(this.counterActBegin <= 0){
            return;
        }  else {
            this.counterActBegin--;
            this.counterActEnd--;
            this.activeSessionsSubset = this.activeSessions.slice(this.counterActBegin, this.counterActEnd);
        }
    }

    public nextFutSesPage(){
        this.myLeftFutDisplay = "block";
        if(this.counterFutEnd >= this.futureSessions.length-1){
            this.myRightFutDisplay="none";
        }
        if(this.counterFutEnd >= this.futureSessions.length){
            return;
        }
        else{
            this.counterFutBegin++;
            this.counterFutEnd++;
            this.futureSessionsSubset = this.futureSessions.slice(this.counterFutBegin,this.counterFutEnd);
        }
    }

    public previousFutSesPage(){
        this.myRightFutDisplay = "block";
        if(this.counterFutBegin <= 1){
            this.myLeftFutDisplay="none";
        }
        if(this.counterFutBegin <= 0){
            return;
        }  else {
            this.counterFutBegin--;
            this.counterFutEnd--;
            this.futureSessionsSubset = this.futureSessions.slice(this.counterFutBegin, this.counterFutEnd);
        }
    }

    public nextPrevSesPage(){
        this.myLeftPrevDisplay = "block";
        if(this.counterPrevEnd >= this.previousSessions.length-1){
            this.myRightPrevDisplay="none";
        }
        if(this.counterPrevEnd >= this.previousSessions.length){
            return;
        }
        else{
            this.counterPrevBegin++;
            this.counterPrevEnd++;
            this.futureSessionsSubset = this.futureSessions.slice(this.counterPrevBegin,this.counterPrevEnd);
        }
    }

    public previousPrevSesPage(){
        this.myRightPrevDisplay = "block";
        if(this.counterPrevBegin <= 1){
            this.myLeftPrevDisplay="none";
        }
        if(this.counterPrevBegin <= 0){
            return;
        }  else {
            this.counterPrevBegin--;
            this.counterPrevEnd--;
            this.previousSessionsSubset = this.previousSessions.slice(this.counterPrevBegin, this.counterPrevEnd);
        }
    }

}