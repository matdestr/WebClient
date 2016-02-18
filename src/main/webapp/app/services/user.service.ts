import {Injectable, Inject} from 'angular2/core';
import {Http, Response} from 'angular2/http';
import {Observable} from 'rxjs/Observable';
import 'rxjs/Rx';
import {User} from '../entities/user';

@Injectable()
export class UserService {

    constructor(private _http : Http) {}

    public getUser(username:string): Observable<User>{
        return this._http.get("api/users/" + username).map((res:Response) => {
            console.log(res.json());
            return res.json()
        });
    }
}
