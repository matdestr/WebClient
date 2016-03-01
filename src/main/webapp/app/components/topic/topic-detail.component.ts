import {Component, OnInit, Input} from "angular2/core";
import {ToolbarComponent} from "../widget/toolbar.component";
import {Router} from "angular2/router";
import {RouteParams} from "angular2/router";
import {User} from "../../entities/user/user";
import {Category} from "../../entities/category/category";
import {CategoryService} from "../../services/category.service";
import {Topic} from "../../entities/topic";
import {TopicService} from "../../services/topic.service";

@Component({
    selector: 'topic-detail',
    templateUrl: 'html/topic-detail.html',
    directives: [ToolbarComponent]
})
export class TopicDetailComponent {
    @Input()

    private topic:Topic;


    constructor(private _router:Router,
                private _routeArgs:RouteParams,
                private _topicService: TopicService) {
        this.topic = new Topic("","");
        var topicId:number = +_routeArgs.params["topicId"];
        this._topicService.getTopic(topicId).subscribe(
            data => {
                this.topic = data.json();
            }
        );
    }

}