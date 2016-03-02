import {Injectable} from "angular2/core";
import {AuthHttp} from '../libraries/angular2-jwt';
import {Observable} from "rxjs/Observable";
import 'rxjs/Rx';
import {Headers, Response, RequestOptions} from 'angular2/http';
import {URLSearchParams} from "angular2/http";

@Injectable()
export class InvitationService {
    private static endPoint:string = "./api/invitations";

    public constructor(private _authHttp:AuthHttp){

    }

    public acceptInvitation(link:string) : Observable<Response> {
        var searchParams:URLSearchParams = new URLSearchParams();
        var requestOptions:RequestOptions = new RequestOptions();
        searchParams.append("acceptId", link);

        requestOptions.search = searchParams;

        return this._authHttp.get(InvitationService.endPoint, requestOptions);
    }
}
