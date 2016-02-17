import {Component} from 'angular2/core'
import {Injectable, Inject} from 'angular2/core';
import {Http, Headers} from 'angular2/http';
import {Observable} from 'rxjs/Observable';
import 'rxjs/Rx';

import {TokenService} from "./token.service";
import {CredentialsModel} from "../entities/authenticatie/credentials";
import {Token} from "../entities/authenticatie/token";

@Injectable()
export class SignInService {
    constructor(private _http : Http, private tokenService : TokenService) {}
    
    public signIn(username : string, password : string) : Observable<Token> {
        var credentials = new CredentialsModel();
        credentials.username = username;
        credentials.password = password;
        
        return this.tokenService.authenticate(credentials);
    }
}
