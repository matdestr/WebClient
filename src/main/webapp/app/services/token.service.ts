import {Injectable} from "angular2/core";
import {Http, Headers} from "angular2/http";
import {Observable} from "rxjs/Observable";

import {CredentialsModel} from "../entities/authenticatie/credentials";
import {Token} from "../entities/authenticatie/token";

import 'rxjs/add/operator/map';
import {Response} from "angular2/http";
import {JwtHelper} from "../libraries/angular2-jwt";

@Injectable()
export class TokenService {
    public static tokenEndpoint: string = "./oauth/token";
    private static client_id: string = "webapp";
    private static client_secret: string = "secret";

    constructor(private _http: Http){
    }

    public saveToken(token: Token): void {
        //Save token for authHttp
        localStorage.setItem('token', token.access_token);
        localStorage.setItem('token-expire-date', "" +  (token.expires_in*1000 + Date.now()))
        console.log(token);
    }

    public authenticate(credentials: CredentialsModel): Observable<Response> {
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

export function isTokenExpired(): boolean {
    let expireDate = localStorage.getItem('token-expire-date');
    let token = localStorage.getItem('token');
    if (token){
        console.log("token found");
    } else {
        console.log("no token found")
    }
    if (expireDate){
        console.log("calculating expire date");
        return Date.parse(expireDate) < Date.now();
    }
    return true;
}
