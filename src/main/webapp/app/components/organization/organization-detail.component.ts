import {Component, OnInit, Input} from "angular2/core";
import {OrganizationService} from "../../services/organization.service";
import {Organization} from "../../entities/organization";
import {ToolbarComponent} from "../widget/toolbar.component";
import {Router} from "angular2/router";
import {RouteParams} from "angular2/router";
import {User} from "../../entities/user/user";
import {Category} from "../../entities/category";


@Component({
    selector: 'organization-detail',
    templateUrl: 'html/organization-detail.html',
    directives: [ToolbarComponent]
})
export class OrganizationDetailComponent {
    @Input()

    private organizationService : OrganizationService;
    private organization:Organization;
    private members: User[]=[];
    private categories: Category[]=[];

    constructor(private _router:Router,
                private _routeArgs:RouteParams,
                private _organizationService:OrganizationService) {
        this.organization = new Organization();
        this.organizationService = _organizationService;
        var organizationId:number = +_routeArgs.get("organizationId");

        this._organizationService.getOrganizations(organizationId).subscribe(
            data => {
                this.organization = data.json();
                this.members = this.organization.members;
                this.categories = this.organization.categories;
            });

    }

    public toAddNewCategory(organizationId:number){
        this._router.navigate(["/CreateCategory"], {organisationId:organizationId})
    }


}

