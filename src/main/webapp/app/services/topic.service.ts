import {Injectable, Inject} from 'angular2/core';
import {Http, Headers, Response} from 'angular2/http';
import {Observable} from "rxjs/Observable";
import 'rxjs/Rx';
import {AuthHttp} from '../libraries/angular2-jwt';

import {RequestOptionsArgs} from "angular2/http";
import {RequestOptions} from "angular2/http";
import {URLSearchParams} from "angular2/http";


import {CreateTopicModel} from "../entities/topic/create-topic-model";


@Injectable()
export class TopicService {
    public static endPoint: string = "./api/topics/";

    constructor(private _authHttp: AuthHttp){

    }

    public saveTopic(topic: CreateTopicModel) : Observable<Response> {
        var searchParams: URLSearchParams = new URLSearchParams();
        searchParams.append("categoryId", "" + topic.categoryId);

        var headers: Headers = new Headers();
        headers.append('Content-Type', 'application/json');

        var options: RequestOptions = new RequestOptions();
        options.search = searchParams;
        options.headers = headers;

        return this._authHttp
            .post(TopicService.endPoint, JSON.stringify(topic), options)
            .retry(2);
    }

    public getTopicsFromCategory(categoryId: number): Observable<Response>{
        var searchParams: URLSearchParams = new URLSearchParams();
        searchParams.append("categoryId", "" + categoryId);

        var options: RequestOptions = new RequestOptions();
        options.search = searchParams;

        return this._authHttp
            .get(TopicService.endPoint, options);
    }

    public getTopic(topicId : number) : Observable<Response> {
        return this._authHttp
            .get(TopicService.endPoint + topicId);
    }

    public getSessionsFromTopic(topicId: number): Observable<Response>{
        return this._authHttp
            .get(TopicService.endPoint + topicId +"/sessions");
    }

}