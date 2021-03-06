import {Injectable} from "angular2/core";
import {AuthHttp} from '../libraries/angular2-jwt';
import {Observable} from "rxjs/Observable";
import 'rxjs/Rx';
import {Headers, Response, RequestOptions} from 'angular2/http';
import {URLSearchParams} from "angular2/http";

/**
 * Service for doing http calls for Invitation endpoint
 */

@Injectable()
export class InvitationService {
    private static endPoint:string = "./api/invitations";

    public constructor(private _authHttp:AuthHttp){

    }

    public getInvitationsForUser(email:string) : Observable<Response> {
        var searchParams:URLSearchParams = new URLSearchParams();
        var requestOptions:RequestOptions = new RequestOptions();
        searchParams.append("email", "" + email);

        requestOptions.search = searchParams;

        return this._authHttp.get(InvitationService.endPoint + "/open", requestOptions);
    }

    public acceptInvitation(link:string, organizationId:number) : Observable<Response> {
        var searchParams:URLSearchParams = new URLSearchParams();
        var requestOptions:RequestOptions = new RequestOptions();
        searchParams.append("acceptId", link);
        searchParams.append("organizationId", "" + organizationId);

        requestOptions.search = searchParams;

        return this._authHttp.get(InvitationService.endPoint, requestOptions);
    }

    public declineInvitation(link:string, organizationId:number) : Observable<Response> {
        var searchParams:URLSearchParams = new URLSearchParams();
        var requestOptions:RequestOptions = new RequestOptions();
        searchParams.append("acceptId", link);
        searchParams.append("organizationId", "" + organizationId);

        requestOptions.search = searchParams;

        return this._authHttp.get(InvitationService.endPoint + "/decline", requestOptions);
    }
}
