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


@Injectable()
export class SessionService {
    public static endPoint: string = "./api/sessions";

    constructor(private _authHttp: AuthHttp){

    }

    public saveSession(session: CreateSessionModel) : Observable<Response> {
        var searchParams: URLSearchParams = new URLSearchParams();

        var headers: Headers = new Headers();
        headers.append('Content-Type', 'application/json');

        var options: RequestOptions = new RequestOptions();
        options.search = searchParams;
        options.headers = headers;

        console.log(JSON.stringify(session));
        session.topicId = 0;

        return this._authHttp
            .post(SessionService.endPoint, JSON.stringify(session), {headers:headers})
            .retry(2);
    }

}