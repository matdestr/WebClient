import {Component, Inject, OnInit} from "angular2/core";
import {Router, RouteParams} from "angular2/router";

import {Session} from "../../entities/session/session";
import {SessionStatus} from "../../entities/session/session-status";

import {SessionService} from "../../services/session.service";

import {ToolbarComponent} from "../widget/toolbar.component";
import {SessionInviteComponent} from "./session-invite.component";
import {SessionJoinComponent} from "./session-join.component";
import {SessionAddCardsComponent} from "./session-add-cards.component";
import {SessionReviewCardsComponent} from "./review-cards.component";
import {SessionChooseCardsComponent} from "./session-choose-cards.component";
import {SessionReadyToStartComponent} from "./session-ready-to-start.component";
import {SessionInProgressComponent} from "./session-in-progress.component";
import {SessionFinishedComponent} from "./session-finished-component";

/**
 * This component serves as a container for more specific components for a session.
 * This component handles the general functionality for a session, but loads other components
 * depending on the current state of the session.
 * 
 * General functionality includes redirecting to the dashboard when the user is not a participant of the session
 * and initializing web socket connections.
 * */
@Component({
    selector: 'session',
    templateUrl: 'html/session/session.html',
    directives: [
        ToolbarComponent, SessionInviteComponent, SessionJoinComponent, 
        SessionAddCardsComponent, SessionReviewCardsComponent, SessionChooseCardsComponent,
        SessionReadyToStartComponent, SessionInProgressComponent, SessionFinishedComponent
    ]
})
export class SessionComponent implements OnInit{
    private tokenName : string;
    
    private sessionId : number;
    private session : Session;
    
    private skipSessionRefreshFinishedComponent : boolean = false;

    constructor(private _router : Router,
                private _routeParams : RouteParams,
                private _sessionService : SessionService,
                @Inject('App.TokenName') tokenName : string) {
        this.tokenName = tokenName;
    }

    ngOnInit() : any {
        this.sessionId = parseInt(this._routeParams.get('sessionId'));

        if (isNaN(this.sessionId)) {
            this._router.navigate(['/Dashboard']);
        }

        this._sessionService.getSession(this.sessionId)
            .subscribe(data => {
                this.session = Session.createEmptySession().deserialize(data.json());
                
                if (this.session.sessionStatus == SessionStatus.FINISHED)
                    this.skipSessionRefreshFinishedComponent = true;
            }, error => {
                console.error(error);
                this._router.navigate(['/Dashboard']);
            });
        
        this.initWebSockets();
    }
    
    private initWebSockets() : void {
        let self : any = this;
        let socket = new SockJS('/kandoe/ws/sockjs?token=' + localStorage.getItem(this.tokenName));
        let stompClient = Stomp.over(socket);
        
        stompClient.connect({}, function() {
            stompClient.subscribe('/topic/sessions/' + self.session.sessionId + '/status', function(data) {
                let newSessionStatus : string = data.body;
                
                // Double quotes are included in the received string, so they must be filtered out
                // eg: "USERS_JOINING"  --> USERS_JOINING
                newSessionStatus = newSessionStatus.replace(/^"(.*)"$/, '$1');
                
                self.session.sessionStatus = newSessionStatus;
            });
        });
    }
}
