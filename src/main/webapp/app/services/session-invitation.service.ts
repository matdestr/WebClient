import {Injectable} from 'angular2/core';
import {Http, Headers, Response} from 'angular2/http';
import {Observable} from "rxjs/Observable";
import 'rxjs/Rx';
import {AuthHttp} from '../libraries/angular2-jwt';

import {RequestOptionsArgs} from "angular2/http";
import {RequestOptions} from "angular2/http";
import {URLSearchParams} from "angular2/http";


@Injectable()
export class SessionInvitationService{
    public static endPoint: string = "./api/sessions";

    constructor(private _authHttp: AuthHttp){

    }

    public inviteUsersToSession(sessionId: number,email: string): Observable<Response> {
        var searchParams:URLSearchParams = new URLSearchParams();
        var requestOptions:RequestOptions = new RequestOptions();
        searchParams.append("email", "" + email);

        requestOptions.search = searchParams;

        return this._authHttp.post(SessionInvitationService.endPoint+"/"+sessionId+"/invite","",requestOptions);
    }
}