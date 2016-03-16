import {Component, Input} from "angular2/core";

import {Session} from "../../entities/session/session";

import {SessionGameService} from "../../services/session-game.service";

@Component({
    selector: 'session-joining',
    templateUrl: 'html/session/session-join.html'
})
export class SessionJoinComponent {
    @Input()
    public session : Session;
    
    constructor(private _sessionGameService : SessionGameService) { }
    
    private join() : void {
        this._sessionGameService.joinSession(this.session.sessionId)
            .subscribe(
                data => {
                    console.log('Joined session');
                },
                error => {
                    console.log('Failed to join session');
                }
            );
    }
}
