import {Component, OnInit, Input} from "angular2/core";
import {OrganizationService} from "../../services/organization.service";
import {Organization} from "../../entities/organization";
import {ToolbarComponent} from "../widget/toolbar.component";
import {Router} from "angular2/router";
import {RouteParams} from "angular2/router";
import {User} from "../../entities/user/user";
import {Topic} from "../../entities/topic";
import {Category} from "../../entities/category"

@Component({
    selector: 'organization-detail',
    templateUrl: 'html/create-category.html',
    directives: [ToolbarComponent]
})
export class CreateCategoryComponent {
    @Input()

    private organizationService : OrganizationService;
    private category: Category;

    constructor(private _router:Router,
                private _routeArgs:RouteParams,
                private _organizationService:OrganizationService) {
        this.category = Category.createEmptyCategory();
        this.organizationService = _organizationService;
    }

    //TODO Angular pipeline voor tags search box

}