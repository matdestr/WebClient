import {Component} from "angular2/core";
import {OrganizationService} from "../../services/organization.service";
import {Organization} from "../../entities/organization/organization";
import {ToolbarComponent} from "../widget/toolbar.component";
import {Router, RouteParams} from "angular2/router";
import {Http, Headers, Response} from 'angular2/http';

import {User} from "../../entities/user/user";
import {CategoryService} from "../../services/category.service"
import {Category} from "../../entities/category/category"
import {CreateCategoryModel} from "../../entities/category/dto/create-category-model";
import {Tag} from "../../entities/tag";
import {TagService} from "../../services/tag.service";
import {HttpStatus} from "../../util/http/http-status";

@Component({
    selector: 'create-category',
    templateUrl: 'html/create-category.html',
    directives: [ToolbarComponent]
})
export class CreateCategoryComponent {
    private organizationId:number;
    private form:CreateCategoryModel = CreateCategoryModel.createEmptyCreateCategory();
    private errors:Array<string> = new Array();
    private tags:Tag[] = [];


    constructor(private _router:Router,
                private _routeArgs:RouteParams,
                private _categoryService:CategoryService, private _tagService:TagService) {

        this.organizationId = +this._routeArgs.params["organizationId"];
        this._tagService.getTags().subscribe(data => {
            let tags:Array<Tag> = data.json();
            for (let tag of tags)
                this.tags.push(Tag.createEmptyTag().deserialize(tag));


        });
    }

    //TODO Angular pipeline voor tags search box

    public onSubmit():void {
        this._categoryService.saveCategory(this.form, this.organizationId)
            .subscribe(
                data => this.handleData(data),
                error => this.handleErrors(error)
            )
    }

    public handleData(data:Response):void {
        if (data.status == HttpStatus.CREATED) {
            console.log("category created");
            this._router.navigate(["/OrganizationDetail", {organizationId: this.organizationId}])
        }
    }


    public handleErrors(error:Response):void {
        this.resetForm();
        let json = error.json();

        switch (error.status) {
            case HttpStatus.UNPROCESSABLE_ENTITY:
                json.fieldErrors.forEach(e => this.errors.push(e.message));
                break;
            case HttpStatus.BAD_REQUEST:
                this.errors.push(json.message);
                break;
            default:
                console.log(error);
                this.errors.push("Oops. Something went wrong!");
        }
    }

    public resetForm():void {
        this.errors = [];
        this.form = CreateCategoryModel.createEmptyCreateCategory();
    }
}