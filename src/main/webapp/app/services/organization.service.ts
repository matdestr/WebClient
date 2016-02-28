import {Injectable, Inject} from 'angular2/core';
import {Http, Headers, Response} from 'angular2/http';
import {Observable} from "rxjs/Observable";
import 'rxjs/Rx';
import {AuthHttp} from '../libraries/angular2-jwt';

import {Organization} from "../entities/organization";
import {RequestOptionsArgs} from "angular2/http";
import {RequestOptions} from "angular2/http";
import {URLSearchParams} from "angular2/http";

@Injectable()
export class OrganizationService {

    
    constructor (private _http : Http, private _authHttp: AuthHttp) {
    }
    
    public saveOrganization(organization : Organization) : Observable<Response> {
        var headers : Headers = new Headers();
        headers.append('Content-Type', 'application/json');
        
        return this._authHttp.post('organizations/', JSON.stringify(organization), {headers: headers}).retry(2);
    }
    
    public getOrganizations(organizationId : number) : Observable<Response> {
        var headers : Headers = new Headers();
        headers.append('Content-Type', 'application/json');

        return this._authHttp.get('api/organizations/' + organizationId);
    }

    public getOrganizationsByOwner(username:string) : Observable<Response> {
        var searchParams: URLSearchParams = new URLSearchParams();
        searchParams.append("user", username);
        searchParams.append("owner", "" + true);


        var options: RequestOptions = new RequestOptions();
        options.search = searchParams;

        return this._authHttp.get("api/organizations", options);
    }

    public getOrganizationsByUser(username:string) : Observable<Response> {
        var searchParams: URLSearchParams = new URLSearchParams();
        searchParams.append("user", username);

        var options: RequestOptions = new RequestOptions();
        options.search = searchParams;

        return this._http.get("api/organizations", options);
    }

}
