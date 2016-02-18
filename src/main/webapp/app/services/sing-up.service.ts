import {Component} from 'angular2/core'
import {Injectable, Inject} from 'angular2/core';
import {Http, Headers, Response} from 'angular2/http';
import {Observable} from 'rxjs/Observable';
import 'rxjs/Rx';

import {TokenService} from "./token.service";
import {CredentialsModel} from "../entities/authenticatie/credentials";
import {Token} from "../entities/authenticatie/token";
import {RegisterModel} from "../entities/register/register";
import {SignInService} from "./sign-in.service";


@Injectable()
export class SignUpService {
    private static registerEndpoint:string = "./api/users"

    private

    constructor(private _http:Http) {
    }

    public signUp(registerModel:RegisterModel): Observable<Response> {

        console.log(SignUpService.registerEndpoint);

        var headers = new Headers();
        headers.append('Content-Type', 'application/json');

        return this._http
            .post(SignUpService.registerEndpoint, JSON.stringify(registerModel), {
                headers: headers
            });
    }
}
