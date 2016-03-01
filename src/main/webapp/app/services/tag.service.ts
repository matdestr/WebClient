import {Injectable, Inject} from 'angular2/core';
import {Http, Headers, Response} from 'angular2/http';
import {Observable} from "rxjs/Observable";
import 'rxjs/Rx';
import {AuthHttp} from '../libraries/angular2-jwt';

import {RequestOptionsArgs} from "angular2/http";
import {RequestOptions} from "angular2/http";
import {URLSearchParams} from "angular2/http";


@Injectable()
export class TagService {
    public static endPoint: string = "./api/tags/"

    constructor(private _authHttp: AuthHttp){

    }

    public getTags(): Observable<Response> {
        return this._authHttp.get(TagService.endPoint);
    }
}