import {Component, OnInit} from "angular2/core";
import {Http, Headers, Response} from 'angular2/http';
import {Router, RouteParams} from "angular2/router";

import {OrganizationService} from "../../services/organization.service";
import {CategoryService} from "../../services/category.service";
import {CardDetailsService} from "../../services/card-details.service";

import {Category} from "../../entities/category/category";
import {CreateCardModel} from "../../entities/category/dto/create-card-model";
import {Organization} from "../../entities/organization/organization";

import {ToolbarComponent} from "../widget/toolbar.component";
import {HttpStatus} from "../../util/http/http-status";

/**
 * This component is responsible for all the functionality of the create new card page
 */
@Component({
    selector: 'create-card-details',
    templateUrl: 'html/create-card.html',
    directives: [ToolbarComponent]
})
export class CreateCardComponent implements OnInit {
    private categoryId:number;
    private form:CreateCardModel = CreateCardModel.createEmptyCreateCardModel();
    private errors:Array<string> = [];

    constructor(private _router:Router,
                private _routeArgs:RouteParams,
                private _cardDetailsService:CardDetailsService) {
    }

    ngOnInit():any {
        this.categoryId = +this._routeArgs.params["categoryId"];
    }

    public onSubmit():void {
        this._cardDetailsService.saveCard(this.form, this.categoryId)
            .subscribe(
                data => this.handleData(data),
                error => this.handleErrors(error)
            );
    }

    public handleData(data:Response):void {
        if (data.status == HttpStatus.CREATED) {
            console.log("card created");
            this._router.navigate(["/CategoryDetail", {categoryId: this.categoryId}])
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
        this.form = CreateCardModel.createEmptyCreateCardModel();
    }
}