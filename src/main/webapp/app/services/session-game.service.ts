import {Injectable} from "angular2/core";
import {AuthHttp} from "../libraries/angular2-jwt";
import {Response, Headers} from "angular2/http";
import {Observable} from "rxjs/Observable";

import {CardPosition} from "../entities/session/card-position";
import {CreateCardModel} from "../entities/category/dto/create-card-model";
import {CreateReviewModel} from "../entities/category/dto/create-review-model";

@Injectable()
export class SessionGameService {
    public static endpoint : string = './api/sessions';
    
    constructor (private _authHttp : AuthHttp) { }
    
    public getChatMessages(sessionId : number) : Observable<Response> {
        return this._authHttp
            .get(SessionGameService.endpoint + '/' + sessionId + '/chat')
            .retry(2);
    }
    
    public getCardPositions(sessionId : number) : Observable<Response> {
        return this._authHttp
            .get(SessionGameService.endpoint + '/' + sessionId + '/positions')
            .retry(2);
    }
    
    public updateCardPosition(sessionId : number, cardPosition : CardPosition) : Observable<Response> {
        let cardDetailsId : number = cardPosition.cardDetails.cardDetailsId;
        let url = SessionGameService.endpoint + '/' + sessionId + '/positions?cardDetailsId=' 
            + cardPosition.cardDetails.cardDetailsId;
        
        return this._authHttp
            .put(url, null)
            .retry(2);
    }
    
    public joinSession(sessionId : number) : Observable<Response> {
        return this._authHttp
            .post(SessionGameService.endpoint + '/' + sessionId + '/join', null)
            .retry(2);
    }
    
    public addCardToSession(sessionId : number, card : CreateCardModel) : Observable<Response> {
        let headers : Headers = new Headers();
        headers.append('Content-Type', 'application/json');
        
        return this._authHttp
            .post(SessionGameService.endpoint + '/' + sessionId + '/all-cards', JSON.stringify(card), {headers: headers})
            .retry(1);
    }
    
    public confirmAddedCards(sessionId : number) : Observable<Response> {
        return this._authHttp
            .post(SessionGameService.endpoint + '/' + sessionId + '/all-cards/confirm', null)
            .retry(1);
    }
    
    public reviewCard(sessionId : number, createReviewModel : CreateReviewModel) : Observable<Response> {
        let headers : Headers = new Headers();
        headers.append('Content-Type', 'application/json');
        
        return this._authHttp
            .post(SessionGameService.endpoint + '/' + sessionId + '/reviews', JSON.stringify(createReviewModel), {headers: headers})
            .retry(1);
    }
    
    public confirmReviews(sessionId : number) : Observable<Response> {
        return this._authHttp
            .post(SessionGameService.endpoint + '/' + sessionId + '/reviews/confirm', null)
            .retry(1);
    }
    
    /*public chooseCards(cardDetailsIds : number[]) : Observable<Response> {
        var headers : Headers = new Headers();
        headers.append('Content-Type', 'application/json');
        
        // TODO
    }*/
    
    public startSession(sessionId : number) : Observable<Response> {
        return this._authHttp
            .post(SessionGameService.endpoint + '/' + sessionId + '/start', null)
            .retry(1);
    }
    
    public endSession(sessionId : number) : Observable<Response> {
        return this._authHttp
            .post(SessionGameService.endpoint + '/' + sessionId + '/end', null);
    }
    
    public getWinningCards(sessionId : number) : Observable<Response> {
        return this._authHttp
            .get(SessionGameService.endpoint + '/' + sessionId + '/winning-cards');
    }
}
