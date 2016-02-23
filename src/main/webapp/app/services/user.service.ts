import {Injectable, Inject} from 'angular2/core';
import {Http, Response, Headers} from 'angular2/http';
import {AuthHttp} from '../libraries/angular2-jwt';
import {Observable} from 'rxjs/Observable';
import 'rxjs/Rx';

import {User} from '../entities/user/user';
import {RegisterModel} from '../entities/user/register'
import {CredentialsModel} from "../entities/authenticatie/credentials";
import {UpdateUserModel} from "../entities/user/edit";
import {TokenService} from "./token.service";

@Injectable()
export class UserService {
    public static endpoint: string = "./api/users";

    constructor(private _http : Http, private _authHttp: AuthHttp, private _tokenService: TokenService) {}

    public getUser(username:string): Observable<User>{
        return this._http.get("api/users/" + username).map((res:Response) => {
            console.log(res.json());
            return res.json()
        });
    }

    public saveUser(user:User): Observable<Response> {
        return this._authHttp.put("api/users/" + user.userId, JSON.stringify(user));
    }

    public signUp(registerModel:RegisterModel): Observable<Response> {

        console.log(UserService.endpoint);

        var headers = new Headers();
        headers.append('Content-Type', 'application/json');

        return this._http
            .post(UserService.endpoint, JSON.stringify(registerModel), {
                headers: headers
            });
    }

    public signIn(username : string, password : string) : Observable<Response> {
        var credentials = new CredentialsModel();
        credentials.username = username;
        credentials.password = password;

        return this._tokenService.authenticate(credentials);
    }

    public updateUser(updateUser: UpdateUserModel): Observable<Response> {
        var headers = new Headers();
        headers.append('Content-Type', 'application/json');
        return this._authHttp.put(UserService.endpoint, JSON.stringify(updateUser), ({
            headers: headers
        }))
    }
}
