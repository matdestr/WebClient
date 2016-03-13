import {Component} from "angular2/core";
import {Router, RouteParams} from "angular2/router";
import {CardDetails} from "../../entities/category/card-details";
import {CardDetailsService} from "../../services/card-details.service";
import {ToolbarComponent} from "../widget/toolbar.component";
import {CardDetailComponent} from "../cards/card-detail.component";
import {TopicService} from "../../services/topic.service";
import {SessionService} from "../../services/session.service";
import {Session} from "../../entities/session/session";

@Component({
    selector: 'session-add-cards',
    templateUrl: 'html/session-add-cards.html',
    directives: [ToolbarComponent, CardDetailComponent]
})
export class SessionAddCardsComponent{
    private session: Session = Session.createEmptySession();
    private cards:CardDetails[] = [];
    private currentCard:CardDetails;
    private sessionCards:CardDetails[] = [];
    private sessionCardsToAdd:CardDetails[] = [];

    constructor(private _router:Router,
                private _routeArgs:RouteParams,
                private _sessionService: SessionService) {

    }

    ngOnInit():any {
        var sessionId:number = +this._routeArgs.params["sessionId"];

        this._sessionService.getCardDetailsOfSession(sessionId).subscribe(
            data => {
                for (let card of data.json())
                    this.cards.push(CardDetails.createEmptyCard().deserialize(card));
            }
        )
    }

    public onSessionCardClick(card:CardDetails):void {
        this.currentCard = card;
    }

}