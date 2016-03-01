import {Component} from "angular2/core";
import {ToolbarComponent} from "../widget/toolbar.component";
import {Router, RouteParams} from "angular2/router";
import {Http, Headers, Response} from 'angular2/http';
import {TopicService} from "../../services/topic.service"

import {Topic} from "../../entities/topic";
import {CategoryService} from "../../services/category.service";
import {Category} from "../../entities/category/category";

@Component({
    selector: 'add-tag',
    templateUrl: 'html/add-tags.html',
    directives: [ToolbarComponent],
    bindings: [TopicService]
})
export class AddTagComponent {
    private categoryId:number;
    private category: Category=new Category("","");

    constructor(private _router:Router,
                private _routeArgs:RouteParams,
                private _categoryService:CategoryService) {

        this.categoryId= +this._routeArgs.params["categoryId"];
        this._categoryService.getCategory(this.categoryId).subscribe(
            data => {
                this.category = data.json();
            }
        );
    }
}
