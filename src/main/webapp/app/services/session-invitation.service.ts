import {Injectable} from 'angular2/core';
import {Http, Headers, Response} from 'angular2/http';
import {Observable} from "rxjs/Observable";
//import 'rxjs/Rx';
import {AuthHttp} from '../libraries/angular2-jwt';

import {RequestOptionsArgs} from "angular2/http";
import {RequestOptions} from "angular2/http";
import {URLSearchParams} from "angular2/http";


@Injectable()
export class SessionInvitationService{
    public static endPoint: string = "./api/sessions";

    constructor(private _authHttp: AuthHttp){ }

    /*public inviteUsersToSession(sessionId: number,email: string): Observable<Response> {
        var searchParams: URLSearchParams = new URLSearchParams();
        searchParams.append("email", "" + email);

        var options: RequestOptions = new RequestOptions();
        options.search = searchParams;

        return this._authHttp.get(SessionInvitationService.endPoint+"/"+sessionId+"/invite",options);
    }*/
    
    public inviteUsersToSession(sessionId : number, email : string) : Observable<Response> {
        let searchParams : URLSearchParams = new URLSearchParams();
        searchParams.append('email', email + '');
        
        return this._authHttp
            .post(SessionInvitationService.endPoint + '/' + sessionId + '/invite', null, {search: searchParams})
            .retry(2);
    }
    
    public confirmInvitedUsers(sessionId : number) : Observable<Response> {
        return this._authHttp
            .post(SessionInvitationService.endPoint + '/' + sessionId + '/invite/confirm', null)
            .retry(2);
    }
}
