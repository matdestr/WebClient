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


@Component({
    selector: 'session-add-cards',
    templateUrl: 'html/session-add-cards.html',
    directives: [ToolbarComponent, CardDetailComponent]
})
export class SessionAddCardsComponent{
    private sessionId: number;
    private form:CreateCardModel = CreateCardModel.createEmptyCreateCardModel();
    private errors:Array<string> = [];

    constructor(private _router:Router,
                private _routeArgs:RouteParams,
                private _sessionService: SessionService) {

    }

    ngOnInit():any {
        this.sessionId = +this._routeArgs.params["sessionId"];
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

    public addAnotherCard():void{
        this._sessionService.addCardToSession(this.sessionId,this.form)
            .subscribe(
                data => console.log('hoera'),
                error => this.handleErrors(error)
            );
        this._router.navigate(["/SessionAddCards", {sessionId:this.sessionId}])
    }

    public addThisCard():void{
        this._sessionService.addCardToSession(this.sessionId,this.form)
            .subscribe(
                error => this.handleErrors(error)
            );
        this._router.navigate(["/SessionReviewCards",{sessionId:this.sessionId}])
    }
}