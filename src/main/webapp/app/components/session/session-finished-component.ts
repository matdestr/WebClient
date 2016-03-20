import {Component, Input, OnInit} from "angular2/core";

import {SessionService} from "../../services/session.service";
import {SessionGameService} from "../../services/session-game.service";

import {Session} from "../../entities/session/session";
import {CardDetails} from "../../entities/category/card-details";
import {CardDetailComponent} from "../cards/card-detail.component";

/**
 * This component is responsible for displaying the winning cards after a session has ended.
 * */
@Component({
    selector: 'session-finished',
    templateUrl: 'html/session/session-finished.html',
    directives: [CardDetailComponent]
})
export class SessionFinishedComponent implements OnInit {
    @Input()
    private session : Session;
    
    @Input()
    private skipSessionRefresh : boolean;
    
    private winningCards : CardDetails[] = [];
    
    constructor(private _sessionService : SessionService, private _sessionGameService : SessionGameService) { }

    ngOnInit() : any {
        /*if (!this.skipSessionRefresh) {
            console.log('[SessionFinishedComponent] Refreshing session information');
            
            this._sessionService.getSession(this.session.sessionId)
                .subscribe(data => {
                    this.session = Session.createEmptySession().deserialize(data.json());
                }, error => {
                    console.error('Could not retrieve new session information');
                    console.error(error);
                });
        } else {
            console.log('[SessionFinishedComponent] Skipped refreshing session information');
        }*/
        
        this._sessionGameService.getWinningCards(this.session.sessionId)
            .subscribe(
                data => {
                    for (let c of data.json()) {
                        this.winningCards.push(CardDetails.createEmptyCard().deserialize(c));
                    }
                },
                error => {
                    console.error('Could not retrieve winning cards');
                    console.error(error);
                }
            );
    }
}
