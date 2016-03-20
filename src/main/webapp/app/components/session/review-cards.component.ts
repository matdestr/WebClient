import {Component, Input, Inject} from "angular2/core";
import {Router, RouteParams} from "angular2/router";
import {CardDetails} from "../../entities/category/card-details";
import {CardDetailsService} from "../../services/card-details.service";
import {ToolbarComponent} from "../widget/toolbar.component";
import {CardDetailComponent} from "../cards/card-detail.component";
import {TopicService} from "../../services/topic.service";
import {Session} from "../../entities/session/session";
import {CreateCardModel} from "../../entities/category/dto/create-card-model";
import {Http, Headers, Response} from 'angular2/http';
import {HttpStatus} from "../../util/http/http-status";
import {Comment} from "../../entities/category/comment"
import {UserService} from "../../services/user.service";
import {User} from "../../entities/user/user";
import {getUsername} from "../../libraries/angular2-jwt";
import {CreateReviewModel} from "../../entities/category/dto/create-review-model";
import {SessionGameService} from "../../services/session-game.service";
import {SessionService} from "../../services/session.service";

/**
 * This component is responsible for the functionality of reviewing cards before the start of a session.
 * Using this component, users can write comments about the cards which will be available to choose from for a session.
 * */
@Component({
    selector: 'session-review-cards',
    templateUrl: 'html/session/session-review-cards.html',
    directives: [ToolbarComponent, CardDetailComponent]
})
export class SessionReviewCardsComponent{
    @Input()
    public session : Session;
    
    private sessionCards : CardDetails[] = [];
    private createReviewModels : CreateReviewModel[] = [];
    public user : User = User.createEmptyUser();
    
    private buttonDisabled : boolean = false;
    private statusMessage : string = '';

    constructor(@Inject('App.TokenName') private tokenName : string,
                private _sessionService : SessionService,
                private _sessionGameService : SessionGameService) { }

    ngOnInit() : any {
        let token = localStorage.getItem(this.tokenName);
        
        if (this.session.participantInfo.filter(p => p.participant.username == getUsername(token))[0].reviewingCardsCompleted) {
            this.statusMessage = 'Waiting for other participants ...';
            this.buttonDisabled = true;
        }
        
        this._sessionService.getCardDetailsOfSession(this.session.sessionId).subscribe(
            data => {
                for (let card of data.json()) {
                    this.sessionCards.push(CardDetails.createEmptyCard().deserialize(card));
                    this.createReviewModels.push(new CreateReviewModel('', card.cardDetailsId))
                }
            }
        );
    }

    public getCreateReviewModel(cardDetailsId : number) : CreateReviewModel{
        for (let reviewModel of this.createReviewModels)    {
            if (reviewModel.cardDetailsId == cardDetailsId)
                return reviewModel;
        }

        return null;
    }

    public onContinueClick() : void {
        this.buttonDisabled = true;
        
        let amountOfReviewsSucceeded : number = 0;
        let amountOfReviewsFailed : number = 0;
        
        let filteredReviewModels = this.createReviewModels.filter(
            r => r && r.message.length > 0
        );
        
        if (filteredReviewModels.length > 0) {
            for(let sessionCard of this.sessionCards){
                for (let reviewModel of filteredReviewModels){
                    if (sessionCard.cardDetailsId == reviewModel.cardDetailsId){
                        sessionCard.comments.push(new Comment(this.user, reviewModel.message));
                    }
                }
            }

            for (let reviewModel of filteredReviewModels) {
                if (!reviewModel.message)
                    continue;

                this._sessionGameService.reviewCard(this.session.sessionId, reviewModel)
                    .subscribe(
                        data => {
                            console.log('Added review to card');
                            amountOfReviewsSucceeded += 1;
                            this.confirmCardReviews(filteredReviewModels.length, amountOfReviewsSucceeded, amountOfReviewsFailed);
                        },
                        error => {
                            console.log('Could not add review to card');
                            amountOfReviewsFailed += 1;
                            this.confirmCardReviews(filteredReviewModels.length, amountOfReviewsSucceeded, amountOfReviewsFailed);
                        }
                    );
            }
        } else {
            this._sessionGameService.confirmReviews(this.session.sessionId)
                .subscribe(
                    () => {
                        console.log('Confirmed reviews without adding any review');
                        this.statusMessage = 'Waiting for other participants ...';
                    },
                    error => {
                        console.log('Failed to confirm reviews: ' +  error);
                        this.buttonDisabled = false;
                    }
                );
        }
    }
    
    private confirmCardReviews(totalAmountOfReviews : number, amountOfReviewsSucceeded : number, amountOfReviewsFailed : number) : void {
        if (totalAmountOfReviews > amountOfReviewsSucceeded + amountOfReviewsFailed)
            return;
        
        if (amountOfReviewsFailed >= amountOfReviewsSucceeded + amountOfReviewsFailed) {
            this.buttonDisabled = false;
            return;
        }

        this.statusMessage = 'Confirming reviews ...';
        
        this._sessionGameService.confirmReviews(this.session.sessionId)
            .subscribe(
                () => {
                    console.log('Confirmed reviews');
                    this.statusMessage = 'Waiting for other participants ...';
                },
                error => {
                    console.log('Failed to confirm reviews: ' +  error);
                    this.buttonDisabled = false;
                }
            );
    }
}
