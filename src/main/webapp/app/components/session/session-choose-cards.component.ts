import {Component} from "angular2/core";
import {Router, RouteParams} from "angular2/router";
import {CardDetails} from "../../entities/category/card-details";
import {CardDetailsService} from "../../services/card-details.service";
import {ToolbarComponent} from "../widget/toolbar.component";
import {CardDetailComponent} from "../cards/card-detail.component";
import {TopicService} from "../../services/topic.service";
import {SessionService} from "../../services/session.service";
import {Session} from "../../entities/session/session";
import {CreateReviewModel} from "../../entities/category/dto/create-review-model";

@Component({
    selector: 'session-choose-cards',
    templateUrl: 'html/session-choose-cards.html',
    directives: [ToolbarComponent, CardDetailComponent]
})
export class SessionChooseCardsComponent{
    private session: Session = Session.createEmptySession();
    private sessionCards:CardDetails[] = [];
    private sessionCardsToAdd:CardDetails[] = [];
    private sessionId: number;
    private cardDetailIds:number[]=[];
    private cardReviews:string[]=[];

    constructor(private _router:Router,
                private _routeArgs:RouteParams,
                private _sessionService: SessionService) {

    }

    ngOnInit():any {
         this.sessionId= +this._routeArgs.params["sessionId"];

        this._sessionService.getCardDetailsOfSession(this.sessionId).subscribe(
            data => {
                for (let card of data.json()) {
                    this.sessionCards.push(CardDetails.createEmptyCard().deserialize(card));
                }
            }
        );

        for (let card of this.sessionCards){
            for (let comment of card.comments){
                this.cardReviews.push(comment.message);
            }
        }

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


    public onContinueClick() {
        for (let catCard of this.sessionCardsToAdd) {
           this.cardDetailIds.push(catCard.cardDetailsId);
        }
        this._sessionService.chooseCardsForSession(this.sessionId,this.cardDetailIds);
        this.navigateUp();
    }

    public navigateUp() {
        this._router.navigate(["/ActiveSession", {sessionId: this.sessionId}])
    }

}