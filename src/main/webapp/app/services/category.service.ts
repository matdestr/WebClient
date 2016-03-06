import {Injectable, Inject} from 'angular2/core';
import {Http, Headers, Response, RequestOptionsArgs, RequestOptions, URLSearchParams} from 'angular2/http';
import {Observable} from 'rxjs/Rx'
import 'rxjs/Rx';
import {AuthHttp} from '../libraries/angular2-jwt';

import {CreateCategoryModel} from "../entities/category/dto/create-category-model";
import {Category} from "../entities/category/category";

@Injectable()
export class CategoryService {
    public static endPoint:string = "./api/categories/";

    constructor(private _authHttp:AuthHttp) {
    }

    public saveCategory(category:CreateCategoryModel, organizationId:number):Observable<Response> {
        var searchParams:URLSearchParams = new URLSearchParams();
        searchParams.append("organizationId", organizationId.toString());

        var headers:Headers = new Headers();
        headers.append('Content-Type', 'application/json');

        var options:RequestOptions = new RequestOptions();
        options.search = searchParams;
        options.headers = headers;

        return this._authHttp
            .post(CategoryService.endPoint, JSON.stringify(category), options)
            .retry(2);
    }


    public getCategoriesFromOrganization(organizationId:number):Observable<Response> {
        var searchParams:URLSearchParams = new URLSearchParams();
        searchParams.append("organizationId", organizationId.toString());

        var options:RequestOptions = new RequestOptions();
        options.search = searchParams;

        return this._authHttp.get(CategoryService.endPoint, options);
    }

    public getCategory(categoryId:number):Observable<Response> {
        return this._authHttp.get(CategoryService.endPoint + categoryId);
    }
}