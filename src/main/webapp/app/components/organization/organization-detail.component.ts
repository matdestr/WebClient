import {Component, OnInit, Input} from "angular2/core";
import {OrganizationService} from "../../services/organization.service";
import {Organization} from "../../entities/organization";
import {ToolbarComponent} from "../widget/toolbar.component";
import {Router} from "angular2/router";
import {RouteParams} from "angular2/router";
import {User} from "../../entities/user/user";
import {Category} from "../../entities/category";
import {CategoryService} from "../../services/category.service";


@Component({
    selector: 'organization-detail',
    templateUrl: 'html/organization-detail.html',
    directives: [ToolbarComponent]
})
export class OrganizationDetailComponent {
    @Input()

    private organization:Organization = new Organization();
    private members: User[]=[];
    private categories: Category[]=[];

    constructor(private _router:Router,
                private _routeArgs:RouteParams,
                private _organizationService:OrganizationService,
                private _categoryService: CategoryService) {
        var organizationId:number = +_routeArgs.params["organizationId"];

        this._organizationService.getOrganizations(organizationId).subscribe(
            data => {
                this.organization = data.json();
                this.members = this.organization.members;
            });
        this._categoryService.getCategoriesFromOrganization(organizationId)
            .subscribe(
                data => this.categories = data.json(),
                error => console.log(error),
                () => console.log("Categories fetched")
            );

    }

    public toAddNewCategory(organizationId:number){
        this._router.navigate(["/CreateCategory", {organizationId:organizationId}])
    }


}

