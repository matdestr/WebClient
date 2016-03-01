import {Component} from "angular2/core";
import {OrganizationService} from "../../services/organization.service";
import {Organization} from "../../entities/organization";
import {ToolbarComponent} from "../widget/toolbar.component";
import {Router, RouteParams} from "angular2/router";
import {Http, Headers, Response} from 'angular2/http';

import {User} from "../../entities/user/user";
import {Topic} from "../../entities/topic";
import {CategoryService} from "../../services/category.service"
import {Category} from "../../entities/category/category"
import {CreateCategoryModel} from "../../entities/category/createCategoryForm";

@Component({
    selector: 'create-topic',
    templateUrl: 'html/create-topic.html',
    directives: [ToolbarComponent]
})
export class CreateTopicComponent {
    private categoryId: number;
    private form: CreateCategoryModel = new CreateCategoryModel();
    private errors: Array<string> = new Array();

    constructor(private _router:Router,
                private _routeArgs:RouteParams,
                private _categoryService: CategoryService) {

        this.categoryId = +this._routeArgs.params["categoryId"];
    }

}