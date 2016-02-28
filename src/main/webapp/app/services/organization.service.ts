import {Injectable, Inject} from 'angular2/core';
import {Http, Headers, Response} from 'angular2/http';
import {Observable} from "rxjs/Observable";
import 'rxjs/Rx';
import {AuthHttp} from '../libraries/angular2-jwt';

import {Organization} from "../entities/organization";

@Injectable()
export class OrganizationService {
    //private http : Http;
    private authHttp : AuthHttp;
    
    constructor (/*http : Http,*/ authHttp: AuthHttp) {
        //this.http = http;
        this.authHttp = authHttp;
    }
    
    public saveOrganization(organization : Organization) : Observable<Response> {
        var headers : Headers = new Headers();
        headers.append('Content-Type', 'application/json');
        
        return this.authHttp.post('organizations/', JSON.stringify(organization), {headers: headers}).retry(2);
    }
    
    public getOrganizations(organizationId : number) : Observable<Response> {
        var headers : Headers = new Headers();
        headers.append('Content-Type', 'application/json');

        return this.authHttp.get('api/organizations/' + organizationId);
    }

    public getOrganizationsByOwner(username:string) : Observable<Response> {
        var headers : Headers = new Headers();
        headers.append('Content-Type', 'application/json');

        return this.authHttp.get("api/organizations/owner/" + username);
    }

    public getOrganizationsByUser(username:string) : Observable<Response> {
        var headers : Headers = new Headers();
        headers.append('Content-Type', 'application/json');

        return this.authHttp.get("api/organizations/user/" + username);
    }
}
