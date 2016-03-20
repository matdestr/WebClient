import {Injectable, Inject} from 'angular2/core';
import {Http, Headers, Response} from 'angular2/http';
import {Observable} from "rxjs/Observable";
import 'rxjs/Rx';
import {AuthHttp} from '../libraries/angular2-jwt';

import {RequestOptionsArgs} from "angular2/http";
import {RequestOptions} from "angular2/http";
import {URLSearchParams} from "angular2/http";

import {CreateTopicModel} from "../entities/topic/create-topic-model";
import {CreateSessionModel} from "../entities/session/dto/create-session-model";
import {CreateCardModel} from "../entities/category/dto/create-card-model";

/**
 * Service for doing http calls for Session endpoint
 */

@Injectable()
export class SessionService {
    public static endPoint: string = "./api/sessions";

    constructor (private _authHttp: AuthHttp) { }
    
    public getSession(sessionId : number) : Observable<Response> {
        return this._authHttp
            .get(SessionService.endPoint + '/' + sessionId)
            .retry(2);
    }

    public saveSession(session: CreateSessionModel) : Observable<Response> {
        var searchParams: URLSearchParams = new URLSearchParams();

        var headers: Headers = new Headers();
        headers.append('Content-Type', 'application/json');

        var options: RequestOptions = new RequestOptions();
        options.search = searchParams;
        options.headers = headers;

        console.log(JSON.stringify(session));

        return this._authHttp
            .post(SessionService.endPoint, JSON.stringify(session), {headers:headers})
            .retry(2);
    }

    public getSessions() : Observable<Response>{
        return this._authHttp.get(SessionService.endPoint);
    }

    public getCardDetailsOfSession(sessionId:number):Observable<Response>{
        return this._authHttp.get(SessionService.endPoint+"/"+sessionId+"/all-cards");
    }

    /*public addCardToSession(sessionId:number,card:CreateCardModel):Observable<Response>{
        var searchParams:URLSearchParams = new URLSearchParams();
        searchParams.append("sessionId", sessionId.toString());

        var headers:Headers = new Headers();
        headers.append('Content-Type', 'application/json');

        var options:RequestOptions = new RequestOptions();
        options.search = searchParams;
        options.headers = headers;

        return this._authHttp.post(SessionService.endPoint+"/"+sessionId+"/all-cards", JSON.stringify(card), options).retry(2);

    }

    public confirmAddedCards(sessionId:number){
        return this._authHttp
            .post(SessionService.endPoint + "/" + sessionId + "/all-cards/confirm", null)
            .retry(1);
    }*/

    public chooseCardsForSession(sessionId:number,cardDetailsIds:number[]){
        var searchParams:URLSearchParams = new URLSearchParams();
        searchParams.append("sessionId", sessionId.toString());
        searchParams.append("cardDetailsId", cardDetailsIds.toString());

        var headers:Headers = new Headers();
        headers.append('Content-Type', 'application/json');

        var options:RequestOptions = new RequestOptions();
        options.search = searchParams;
        options.headers = headers;

        return this._authHttp.post(SessionService.endPoint+"/"+sessionId+"/chosen-cards", "",options);

    }
}
