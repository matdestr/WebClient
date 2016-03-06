import {Injectable} from "angular2/core";
import {Http, Headers, Response, RequestOptionsArgs, RequestOptions, URLSearchParams} from 'angular2/http';
import {AuthHttp} from "../libraries/angular2-jwt";
import {Observable} from 'rxjs/Rx'
import {CreateCardModel} from "../entities/category/dto/create-card-model";
import {CardDetails} from "../entities/category/card-details";

@Injectable()
export class CardDetailsService {
    public static endpoint:string = './api/carddetails';

    constructor(private _authHttp:AuthHttp) {
    }

    public saveCard(card:CreateCardModel, categoryId:Number):Observable<Response> {
        var searchParams:URLSearchParams = new URLSearchParams();
        searchParams.append("categoryId", categoryId.toString());

        var headers:Headers = new Headers();
        headers.append('Content-Type', 'application/json');

        var options:RequestOptions = new RequestOptions();
        options.search = searchParams;
        options.headers = headers;

        return this._authHttp
            .post(CardDetailsService.endpoint.concat("/categories"), JSON.stringify(card), options)
            .retry(2);
    }

    public getCardDetailsOfCategory(categoryId:Number):Observable<Response> {
        return this._authHttp
            .get(CardDetailsService.endpoint.concat("/categories/") + categoryId);
    }


}