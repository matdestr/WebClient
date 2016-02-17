import {Injectable} from "angular2/core";
import {Http, Headers} from "angular2/http";
import {Observable} from "rxjs/Observable";

import {CredentialsModel} from "../entities/authenticatie/credentials";
import {Token} from "../entities/authenticatie/token";

import 'rxjs/add/operator/map';

@Injectable()
export class TokenService {
    public static tokenEndpoint: string = "./oauth/token";
    private static client_id: string = "webapp";
    private static client_secret: string = "secret";

    constructor(private _http: Http){
    }

    public authenticate(credentials: CredentialsModel): Observable<Token> {
        var username = credentials.username;
        var password = credentials.password;

        var creds = "username=" + username + "&password=" + password + "&grant_type=password";

        var headers = new Headers();
        headers.append('Authorization', 'Basic ' + btoa(TokenService.client_id + ":" + TokenService.client_secret));
        headers.append('Content-Type', 'application/x-www-form-urlencoded');

        return this._http.post(TokenService.tokenEndpoint, creds, {
                headers: headers
            })
            .map(res => res.json());
    }
}
