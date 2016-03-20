import {Component, Input, OnInit, Inject} from "angular2/core";
import {CardDetails} from "../../entities/category/card-details";
import {CardDetailsService} from "../../services/card-details.service";
import {ToolbarComponent} from "../widget/toolbar.component";
import {CardDetailComponent} from "../cards/card-detail.component";
import {TopicService} from "../../services/topic.service";
import {Session} from "../../entities/session/session";
import {CreateCardModel} from "../../entities/category/dto/create-card-model";
import {Http, Headers, Response} from 'angular2/http';
import {HttpStatus} from "../../util/http/http-status";
import {SessionGameService} from "../../services/session-game.service";
import {getUsername} from "../../libraries/angular2-jwt";

@Component({
    selector: 'session-add-cards',
    templateUrl: 'html/session/session-add-cards.html',
    directives: [ToolbarComponent, CardDetailComponent]
})
export class SessionAddCardsComponent implements OnInit {
    @Input()
    public session : Session;
    
    private form : CreateCardModel = CreateCardModel.createEmptyCreateCardModel();
    private errors : Array<string> = [];
    private statusMessage : string;
    private buttonsDisabled = false;

    constructor(private _sessionGameService: SessionGameService,
                @Inject('App.TokenName') private tokenName : string) { }
    
    ngOnInit() : any {
        let token = localStorage.getItem(this.tokenName);
        
        if (this.session.participantInfo.filter(p => p.participant.username == getUsername(token))[0].addedCardsCompleted) {
            this.statusMessage = 'Waiting for other participants ...';
            this.buttonsDisabled = true;
        }
    }

    public handleErrors(error:Response):void {
        let json = error.json();

        switch (error.status) {
            case HttpStatus.UNPROCESSABLE_ENTITY:
                json.fieldErrors.forEach(e => this.errors.push(e.message));
                break;
            case HttpStatus.BAD_REQUEST:
                this.errors.push(json.message);
                break;
            default:
                console.log(error);
                this.errors.push("Oops. Something went wrong!");
        }
    }

    public confirmCards():void{
        this.statusMessage = '';
        
        this._sessionGameService.confirmAddedCards(this.session.sessionId)
            .subscribe(
                data => {
                    this.errors = [];
                    this.form = CreateCardModel.createEmptyCreateCardModel();
                    this.statusMessage = 'Waiting for other participants ...';
                    this.buttonsDisabled = true;
                }
            );
        //this._router.navigate(["/SessionReviewCards", {sessionId:this.sessionId}])
    }

    public addThisCard():void{
        this._sessionGameService.addCardToSession(this.session.sessionId, this.form)
            .subscribe(
                () => {
                    console.log('Added card to session');
                    this.form = CreateCardModel.createEmptyCreateCardModel();
                    this.statusMessage = 'Card added';
                },
                error => this.handleErrors(error)
            );
    }
}
