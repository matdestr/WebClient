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
    selector: 'session-choose-cards',
    templateUrl: 'html/session-choose-cards.html',
    directives: [ToolbarComponent, CardDetailComponent]
})
export class SessionChooseCardsComponent{
    private session: Session = Session.createEmptySession();
    private cards:CardDetails[] = [];
    private sessionCardsToAdd:CardDetails[] = [];
    private currentCard:CardDetails;

    constructor(private _router:Router,
                private _routeArgs:RouteParams,
                private _sessionService: SessionService,
                private _cardDetailService: CardDetailsService) {

    }

    ngOnInit():any {
        var sessionId:number = +this._routeArgs.params["sessionId"];

        this._sessionService.getCardDetailsOfSession(sessionId).subscribe(
            data => {
                for (let card of data.json()) {
                    this.cards.push(CardDetails.createEmptyCard().deserialize(card));
                }
            }
        )
    }

    public onSessionCardClick(card:CardDetails):void {
        var index = this.sessionCardsToAdd.indexOf(card);
        console.log(index);
        console.log(JSON.stringify(card));

        if (index < 0) {
            this.sessionCardsToAdd.push(card);

        } else {
            if (!card.active)
                this.sessionCardsToAdd.splice(index, 1);
        }
    }

    public onAddCardsClick() {
        for (let sessionCard of this.sessionCardsToAdd) {

        }
    }

    public navigateUp() {
        this._router.navigate(["/TopicDetail", {topicId: this.topic.topicId}])
    }

}