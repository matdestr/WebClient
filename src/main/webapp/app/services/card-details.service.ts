import {Injectable} from "angular2/core";
import {Response} from "angular2/http";
import {AuthHttp} from "../libraries/angular2-jwt";
import {Observable} from "rxjs/Observable";

@Injectable()
export class CardDetailsService {
    public static endpoint : string = './api/carddetails';
    
    constructor(private _authHttp : AuthHttp) { }
    
    public getCardDetailsOfCategory(categoryId : number) : Observable<Response> {
        // TODO : Implement
    }
}