import {Component} from "angular2/core";
import {OrganizationService} from "../../services/organization.service";
import {Organization} from "../../entities/organization/organization";
import {ToolbarComponent} from "../widget/toolbar.component";
import {Router, RouteParams} from "angular2/router";
import {Http, Headers, Response} from 'angular2/http';
import {Category} from "../../entities/category/category";
import {CategoryService} from "../../services/category.service";
import {CreateCardModel} from "../../entities/category/CreateCardForm";


@Component({
    selector: 'create-card',
    templateUrl: 'html/create-card.html',
    directives: [ToolbarComponent]
})
export class CreateCardComponent {
    private category:Category;
    private form:CreateCardModel = new CreateCardModel();
    private errors:Array<string> = new Array();
    private myImage:String="";


    constructor(private _router:Router,
                private _routeArgs:RouteParams,
                private _categoryService:CategoryService) {

        this.category = new Category("","");
        var categoryId:number = +_routeArgs.params["categoryId"];
        this._categoryService.getCategory(categoryId).subscribe(
            data => {
                this.category = data.json();
            }
        );
    }

    public onSubmit():void {
        this.form.categoryId = this.category.categoryId;
        this._categoryService.saveCard(this.form,this.category)
            .subscribe(
                data => this.handleData(data),
                error => this.handleErrors(error)
            )
    }

    public handleData(data:Response):void {
        if (data.status == 201) {
            ;
            console.log("card created");
            this._router.navigate(["/CategoryDetail", {categoryId: this.category.categoryId}])
        }
    }

    public handleErrors(error:Response):void {
        this.resetForm();
        let json = error.json();
        if (error.status == 422) {
            json.fieldErrors.forEach(e => this.errors.push(e.message));
        } else if (error.status == 400) {
            this.errors.push(json.message);
        } else {
            console.log(error);
            this.errors.push("Oops. Something went wrong!");
        }
    }

    public resetForm():void {
        this.errors = [];
        this.form = new CreateCardModel();
    }

}