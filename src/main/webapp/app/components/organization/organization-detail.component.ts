import {Component, OnInit, Input} from "angular2/core";
import {OrganizationService} from "../../services/organization.service";
import {Organization} from "../../entities/organization/organization";
import {ToolbarComponent} from "../widget/toolbar.component";
import {Router} from "angular2/router";
import {RouteParams} from "angular2/router";
import {User} from "../../entities/user/user";
import {Category} from "../../entities/category/category";
import {CategoryService} from "../../services/category.service";
import {Email} from "../../entities/user/email";
import {CreateCategoryModel} from "../../entities/category/dto/create-category-model";


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
    private usersToInvite : Email[] = [];

    constructor(private _router:Router,
                private _routeArgs:RouteParams,
                private _organizationService:OrganizationService,
                private _categoryService: CategoryService) {

        this.usersToInvite.push(new Email());
        var organizationId:number = +_routeArgs.params["organizationId"];

        this._organizationService.getOrganization(organizationId).subscribe(
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

    private addUsers() : void {
        console.log("adding " + this.usersToInvite.length + " users");
        this._organizationService.addUsersToOrganization(this.organization.organizationId, this.usersToInvite).subscribe(
            (data) => {
                console.log(data);
            },
            (error) => {
                console.log(error);
            },
            () => {
                console.log("XD");
            }
        );
    }

    public toAddNewCategory(organizationId:number){
        this._router.navigate(["/CreateCategory", {organizationId:organizationId}])
    }

    public toCategory(categoryId:number){
        this._router.navigate(["/CategoryDetail",{categoryId:categoryId}])
    }

    private addUserEntry() {
        this.usersToInvite.push(new Email());
    }

    private filterEmails() {
        return this.usersToInvite.filter(u => {return u && u.email.length > 0});
    }

    private removeUserFromUsersToInvite(index : number) {
        this.usersToInvite.splice(index, 1);
    }


    public toSession():void{
        this._router.navigate(["/ActiveSession"])
    }

}

