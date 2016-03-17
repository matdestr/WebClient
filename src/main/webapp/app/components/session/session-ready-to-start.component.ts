import {Component, Input, OnInit, Inject} from "angular2/core";
import {getUsername} from "../../libraries/angular2-jwt";

import {Session} from "../../entities/session/session";
import {SessionGameService} from "../../services/session-game.service";
import {SessionService} from "../../services/session.service";

@Component({
    selector: 'session-ready',
    templateUrl: 'html/session/session-ready-to-start.html'
})
export class SessionReadyToStartComponent implements OnInit {
    @Input()
    private session : Session;
    
    private isOrganizer : boolean;
    private participantUsernames : string[];

    constructor(private _sessionService : SessionService,
                private _sessionGameService : SessionGameService,
                @Inject('App.TokenName') private tokenName : string) {}
    
    ngOnInit() : any {
        this.isOrganizer = false;
        this.participantUsernames = [];
        
        let username = getUsername(localStorage.getItem(this.tokenName));
        
        if (username == this.session.organizer.username)
            this.isOrganizer = true;

        this._sessionService.getSession(this.session.sessionId)
            .subscribe(data => {
                this.session = Session.createEmptySession().deserialize(data.json());
                
                for (let p of this.session.participantInfo) {
                    this.participantUsernames.push(p.participant.name);
                }
            }, error => {
                console.error('Could not retrieve new session information');
                console.error(error);
            });
    }
    
    private startGame() : void {
        this._sessionGameService.startSession(this.session.sessionId)
            .subscribe(
                data => {
                    console.log('Successfully started session');
                },
                error => {
                    console.log('Could not start session');
                    console.error(error);
                }
            );
    }
}
