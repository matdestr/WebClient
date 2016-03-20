import {Injectable} from "angular2/core";
import {Http, Headers, Response} from "angular2/http";
import {Observable} from "rxjs/Observable";

import {CredentialsModel} from "../entities/authenticatie/credentials";
import {Token} from "../entities/authenticatie/token";

import 'rxjs/add/operator/map';
import {JwtHelper} from "../libraries/angular2-jwt";
import {Inject} from "angular2/core";

/**
 * Service for doing http calls for token endpoint
 */

@Injectable()
export class TokenService {
    public static tokenEndpoint:string = "./oauth/token";
    public static tokenCheckEndpoint : string = './oauth/check_token';
    
    private static client_id:string = "webapp";
    private static client_secret:string = "secret";

    constructor(private _http : Http, @Inject('App.TokenName') private _tokenName:string) {
    }
    
    /*private getAuthorizationHeaderClientDetails() : string {
        return 'Basic ' + btoa(TokenService.client_id + ':' + TokenService.client_secret);
    }
    
    public getAccessToken() : string {
        return localStorage.getItem(this._tokenName);
    }
    
    public tokenValidCheckedServerSideObservable() : Observable<Response> {
        var headers = new Headers();
        headers.append('Authorization', this.getAuthorizationHeaderClientDetails());
        headers.append('Content-Type', 'application/x-www-form-urlencoded');
        
        return this._http.post(TokenService.tokenCheckEndpoint, 
            'token=' + this.getAccessToken(),
            {headers: headers});
    }*/

    public saveToken(token:Token):void {
        //Save token for authHttp
        localStorage.setItem('token', token.access_token);
        localStorage.setItem('token-refresh-token', token.refresh_token);
        localStorage.setItem('token-expire-date', "" + (token.expires_in * 1000 + Date.now()))
        console.log(token);
    }
    
    public authenticate(credentials:CredentialsModel):Observable<Response> {
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

    public refreshToken():void {
        var refreshToken = localStorage.getItem('token-refresh-token');

        var headers = new Headers();
        headers.append('Authorization', btoa(TokenService.client_id + ':' + TokenService.client_secret));
        headers.append('Content-Type', 'application/x-www-form-urlencoded');

        var refreshContent = 'grant_type=refresh_token&refresh_token=' + refreshToken;

        this._http.post(TokenService.tokenEndpoint, refreshContent, {headers: headers})
            .subscribe(
                (token:Token) => {
                    this.saveToken(token);
                    console.log('Token refreshed');
                },
                error => console.error(error)
            )
    }
}

export function isTokenExpired():boolean {
    let expireDate = localStorage.getItem('token-expire-date');
    let token = localStorage.getItem('token');

    if (token) {
        console.log("token found");

        if (expireDate) {
            console.log("calculating expire date");
            return Date.parse(expireDate) < Date.now();
        }
    } else {
        console.log("no token found")
    }

    return true;
}

export function isTokenAvailable():boolean {
    let token = localStorage.getItem(this._tokenName);
    
    if (!token)
        console.log('No token available');

    return !!token;
}
