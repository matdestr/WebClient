import {Injectable} from "angular2/core";
import {AuthHttp} from "../libraries/angular2-jwt";

@Injectable()
export class CardDetailsService {
    public static endpoint : string = './api/carddetails';
    
    constructor(private _authHttp : AuthHttp) { }
    
    
}