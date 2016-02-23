import {Injectable, Inject} from 'angular2/core';
import {Http, Response} from 'angular2/http';
import {AuthHttp} from '../libraries/angular2-jwt';
import {Observable} from 'rxjs/Observable';
import 'rxjs/Rx';
import {User} from '../entities/user';

@Injectable()
export class UserService {

    constructor(private _http : Http, private _authHttp:AuthHttp) {}

    public getUser(username:string): Observable<User>{
        return this._http.get("api/users/" + username).map((res:Response) => {
            console.log(res.json());
            return res.json()
        });
    }

    public saveUser(user:User): Observable<Response> {
        return this._authHttp.put("api/users/" + user.userId, JSON.stringify(user));
    }
}
