import {Component} from "angular2/core";
import {Router, RouteParams} from "angular2/router";
import {CardDetails} from "../../entities/category/card-details";
import {CardDetailsService} from "../../services/card-details.service";
import {ToolbarComponent} from "../widget/toolbar.component";
import {CardDetailComponent} from "../cards/card-detail.component";
import {TopicService} from "../../services/topic.service";
import {SessionService} from "../../services/session.service";
import {Session} from "../../entities/session/session";
import {CreateCardModel} from "../../entities/category/dto/create-card-model";
import {Http, Headers, Response} from 'angular2/http';
import {HttpStatus} from "../../util/http/http-status";
import {Comment} from "../../entities/category/comment"
import {UserService} from "../../services/user.service";
import {User} from "../../entities/user/user";
import {getUsername} from "../../libraries/angular2-jwt";
import {CreateReviewModel} from "../../entities/category/dto/create-review-model";

@Component({
    selector: 'session-review-cards',
    templateUrl: 'html/session-review-cards.html',
    directives: [ToolbarComponent, CardDetailComponent]
})
export class SessionReviewCardsComponent{
    private sessionId: number;
    private sessionCards:CardDetails[] = [];
    private createReviewModels: CreateReviewModel[]=[];
    public user: User = User.createEmptyUser();

    constructor(private _router:Router,
                private _routeArgs:RouteParams,
                private _sessionService: SessionService,
                private _userService: UserService) {

    }

    ngOnInit():any {
        this.sessionId = +this._routeArgs.params["sessionId"];
        var token = localStorage.getItem('token');

        this._sessionService.getCardDetailsOfSession(this.sessionId).subscribe(
            data => {
                for (let card of data.json()) {
                    this.sessionCards.push(CardDetails.createEmptyCard().deserialize(card));
                    this.createReviewModels.push(new CreateReviewModel('', card.cardDetailsId))
                }
            }
        );
        this._userService.getUser(getUsername(token)).subscribe((user:User) => {
            this.user = this.user.deserialize(user);
        });
    }

    public onContinueClick():void{
        for(let sessionCard of this.sessionCards){
            for (let reviewModel of this.createReviewModels){
               if (sessionCard.cardDetailsId==reviewModel.cardId){
                   sessionCard.comments.push(new Comment(this.user,reviewModel.message));
               }
            }
        }
        this._router.navigate(["/SessionChooseCards", {sessionId: this.sessionId}])
    }

    public getCreateReviewModel(cardDetailsId : number):CreateReviewModel{
        for (let reviewModel of this.createReviewModels)    {
            if (reviewModel.cardId == cardDetailsId)
                return reviewModel;
        }
        return null;
    }
}