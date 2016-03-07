import {Component} from "angular2/core";
import {OrganizationService} from "../../services/organization.service";
import {Organization} from "../../entities/organization/organization";
import {ToolbarComponent} from "../widget/toolbar.component";
import {Router, RouteParams} from "angular2/router";
import {Http, Headers, Response} from 'angular2/http';

import {User} from "../../entities/user/user";
import {CategoryService} from "../../services/category.service"
import {Category} from "../../entities/category/category"
import {Tag} from "../../entities/tag";
import {TagService} from "../../services/tag.service";
import {HttpStatus} from "../../util/http/http-status";
import {CreateSessionModel} from "../../entities/session/dto/create-session-model";
import {SessionService} from "../../services/session.service";

@Component({
    selector: 'create-session',
    templateUrl: 'html/create-session.html',
    directives: [ToolbarComponent]
})
export class CreateSessionComponent {
    private categoryId: number;
    private model:CreateSessionModel = CreateSessionModel.createEmptyCreateSession();
    private errors:Array<string> = [];


    constructor(private _router:Router,
                private _routeArgs:RouteParams,
                private _sessionService:SessionService) {

        this.categoryId = +this._routeArgs.params["categoryId"];

    }


    public onSubmit():void {
        //TODO: @mathisse : create session service
        this.model.categoryId =  this.categoryId;
        this._sessionService.saveSession(this.model).subscribe(
            data => this.handleData(data),
            error => this.handleErrors(error)
        )
    }

    public handleData(data:Response):void {
        if (data.status == 201){
            console.log("session created");
            this._router.navigate(["/CategoryDetail", {categoryId:this.categoryId }])
        }
    }


    public handleErrors(error:Response):void {
        this.resetForm();
        /*let json = error.json();

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
        }  */

        console.log(error);
    }

    public resetForm():void {
        this.errors = [];
        this.model = CreateSessionModel.createEmptyCreateSession()
    }
}