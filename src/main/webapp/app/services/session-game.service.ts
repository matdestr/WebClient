import {Injectable} from "angular2/core";
import {AuthHttp} from "../libraries/angular2-jwt";
import {Observable} from "rxjs/Observable";
import {Response} from "angular2/http";
import {Headers} from "angular2/http";
import {CardPosition} from "../entities/session/card-position";

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
    
    public chooseCards(cardDetailsIds : number[]) : Observable<Response> {
        var headers : Headers = new Headers();
        headers.append('Content-Type', 'application/json');
        
        // TODO
    }
}
