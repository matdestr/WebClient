import {Component, Input, Inject} from "angular2/core";
import {Response} from "angular2/http";
import {CardDetails} from "../../entities/category/card-details";
import {CardDetailsService} from "../../services/card-details.service";
import {ToolbarComponent} from "../widget/toolbar.component";
import {CardDetailComponent} from "../cards/card-detail.component";
import {TopicService} from "../../services/topic.service";
import {SessionService} from "../../services/session.service";
import {Session} from "../../entities/session/session";
import {CreateReviewModel} from "../../entities/category/dto/create-review-model";
import {SessionGameService} from "../../services/session-game.service";

@Component({
    selector: 'session-choose-cards',
    templateUrl: 'html/session/session-choose-cards.html',
    directives: [ToolbarComponent, CardDetailComponent]
})
export class SessionChooseCardsComponent{
    @Input()
    public session : Session;
    
    private sessionCards : CardDetails[] = [];
    private sessionCardsToAdd : CardDetails[] = [];
    private cardDetailIds : number[] = [];
    
    private statusMessage : string;
    private errorMessage : string;

    constructor(private _sessionService : SessionService,
                private _sessionGameService : SessionGameService,
                @Inject('App.TokenName') private tokenName : string) { }

    ngOnInit() : any {
        this._sessionService.getCardDetailsOfSession(this.session.sessionId).subscribe(
            data => {
                for (let card of data.json()) {
                    this.sessionCards.push(CardDetails.createEmptyCard().deserialize(card));
                }
            }
        );
    }

    /*public onSessionCardClick(card : CardDetails) : void {
        let index = this.sessionCardsToAdd.indexOf(card);
        
        console.log(index);
        console.log(JSON.stringify(card));

        if (index < 0) {
            this.sessionCardsToAdd.push(card);
        } else {
            if (!card.active)
                this.sessionCardsToAdd.splice(index, 1);
        }
    }*/

    public onSessionCardClick(card : CardDetails) : void {
        let index = this.sessionCardsToAdd.indexOf(card);

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
        this.errorMessage = '';
        this.cardDetailIds = [];
        
        for (let card of this.sessionCardsToAdd) {
           this.cardDetailIds.push(card.cardDetailsId);
        }
        
        this._sessionService.chooseCardsForSession(this.session.sessionId, this.cardDetailIds)
            .subscribe(
                data => {
                    console.log('Chose ' + this.sessionCardsToAdd.length + ' cards');
                    this.statusMessage = 'Waiting for other participants ...';
                },
                (error) => {
                    console.log('Failed to choose cards');
                    this.errorMessage = JSON.parse(error._body).message;
                }
            );
    }
}
