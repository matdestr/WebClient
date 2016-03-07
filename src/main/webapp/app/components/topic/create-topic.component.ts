import {Component} from "angular2/core";
import {OrganizationService} from "../../services/organization.service";
import {Organization} from "../../entities/organization/organization";
import {ToolbarComponent} from "../widget/toolbar.component";
import {Router, RouteParams} from "angular2/router";
import {Http, Headers, Response} from 'angular2/http';

import {User} from "../../entities/user/user";
import {Topic} from "../../entities/topic/topic";
import {CategoryService} from "../../services/category.service"
import {Category} from "../../entities/category/category"
//import {CreateCategoryModel} from "../../entities/category/category";
import {CreateTopicModel} from "../../entities/topic/create-topic-model";
import {TopicService} from "../../services/topic.service";

@Component({
    selector: 'create-topic',
    templateUrl: 'html/create-topic.html',
    directives: [ToolbarComponent]
})
export class CreateTopicComponent {
    private categoryId: number;
    private form: CreateTopicModel = new CreateTopicModel();
    private errors: Array<string> = new Array();

    constructor(private _router:Router,
                private _routeArgs:RouteParams,
                private _topicService: TopicService) {

        this.categoryId = +this._routeArgs.params["categoryId"];
    }

    public onSubmit(): void {
        this.form.categoryId = this.categoryId;
        this._topicService.saveTopic(this.form)
            .subscribe(
                data => this.handleData(data),
                error => this.handleErrors(error)
            )
    }

    public handleData(data: Response): void {
        if (data.status == 201){;
            console.log("topic created");
            this._router.navigate(["/CategoryDetail", {categoryId:this.categoryId }])
        }
    }


    public handleErrors(error: Response): void {
        this.resetForm();
        console.log(error);
        /*let json = error.json();
        if (error.status == 422) {
            json.fieldErrors.forEach(e => this.errors.push(e.message));
        } else if(error.status == 400) {
            this.errors.push(json.message);
        } else {
            console.log(error);
            this.errors.push("Oops. Something went wrong!");
        }    */
    }

    public resetForm(): void {
        this.errors = [];
        this.form = new CreateTopicModel();
    }

}