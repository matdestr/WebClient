import {Injectable, Inject} from 'angular2/core';
import {Http, Headers, Response} from 'angular2/http';
import {Observable} from "rxjs/Observable";
import 'rxjs/Rx';
import {AuthHttp} from '../libraries/angular2-jwt';

import {Email} from "../entities/user/email";
import {Organization} from "../entities/organization/organization";
import {CreateOrganization} from "../entities/organization/organization";
import {RequestOptionsArgs} from "angular2/http";
import {RequestOptions} from "angular2/http";
import {URLSearchParams} from "angular2/http";

@Injectable()
export class OrganizationService {
    public static endPoint:string = "./api/organizations/";
    
    constructor (private _http : Http, private _authHttp: AuthHttp) {
    }
    
    public saveOrganization(organization : Organization) : Observable<Response> {
        var headers : Headers = new Headers();
        headers.append('Content-Type', 'application/json');
        return this._authHttp.put(OrganizationService.endPoint, JSON.stringify(organization), {headers: headers});
    }

    public createOrganization(organization : CreateOrganization) : Observable<Response> {
        var headers : Headers = new Headers();
        headers.append('Content-Type', 'application/json');
        return this._authHttp.post(OrganizationService.endPoint, JSON.stringify(organization), {headers: headers});
    }

    public addUsersToOrganization(organizationId:number, emails:Email[]) : Observable<Response> {
        var headers : Headers = new Headers();
        headers.append('Content-Type', 'application/json');

        return this._authHttp.post(OrganizationService.endPoint + "add/" + organizationId, JSON.stringify(emails), {headers: headers});
    }
    
    public getOrganization(organizationId : number) : Observable<Response> {

        return this._authHttp.get(OrganizationService.endPoint + organizationId);
    }

    public getOrganizationsByOwner(username:string) : Observable<Response> {
        var searchParams: URLSearchParams = new URLSearchParams();
        searchParams.append("owner", "" + true);

        var options: RequestOptions = new RequestOptions();
        options.search = searchParams;

        return this._authHttp.get(OrganizationService.endPoint + "user/" + username, options);
    }

    public getOrganizationsByUser(username:string) : Observable<Response> {
        return this._http.get(OrganizationService.endPoint + "user/" + username);
    }

    public setOrganizationName(organizationId:number,organizationName:string):Observable<Response>{
        var searchParams: URLSearchParams = new URLSearchParams();
        searchParams.append("organizationName", organizationName);

        var options: RequestOptions = new RequestOptions();
        options.search = searchParams;

        return this._authHttp.post(OrganizationService.endPoint+"edit/"+organizationId,"",options);
    }

}
