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
    private path : string;
    
    constructor (/*http : Http,*/ authHttp: AuthHttp, @Inject('backendPath') path : string) {
        //this.http = http;
        this.authHttp = authHttp;
        this.path = path;
    }
    
    public saveOrganization(organization : Organization) : Observable<Response> {
        var headers : Headers = new Headers();
        headers.append('Content-Type', 'application/json');
        
        return this.authHttp.post(this.path + 'organizations/', JSON.stringify(organization), {headers: headers}).retry(2);
    }
}
