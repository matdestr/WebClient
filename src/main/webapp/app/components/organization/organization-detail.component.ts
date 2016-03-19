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
    private categoriesSubSet: Category[]=[];
    private usersToInvite : Email[] = [];
    private counterCatBegin:number=0;
    private counterCatEnd:number=4;
    private myLeftCatDisplay:string="block";
    private myRightCatDisplay:string="block";

    constructor(private _router:Router,
                private _routeArgs:RouteParams,
                private _organizationService:OrganizationService,
                private _categoryService: CategoryService) {

        this.usersToInvite.push(new Email());
        var organizationId:number = +_routeArgs.params["organizationId"];

        this._organizationService.getOrganization(organizationId).subscribe(
            data => {
                this.organization = data.json();
                console.log(data.json());
                this.members = this.organization.members;
                this.getCategories();
            });
    }

    public getCategories():void{
        this._categoryService.getCategoriesFromOrganization(this.organization.organizationId)
            .subscribe(data => {
                this.categories = data.json();
                this.categoriesSubSet = this.categories.slice(0,4);
                if(this.categories.length<=4) {
                    this.myLeftCatDisplay = "none";
                    this.myRightCatDisplay = "none";
                }else {
                    this.myLeftCatDisplay = "none";
                    this.myRightCatDisplay = "block";
                }
            });
    }

    public addUsers() : void {
        console.log("adding " + this.usersToInvite.length + " users");
        this._organizationService.addUsersToOrganization(this.organization.organizationId, this.usersToInvite).subscribe(
            (data) => {
                console.log(data);
            },
            (error) => {
                console.log(error);
            },
            () => {
                console.log("done");
            }
        );
    }

    public toAddNewCategory(organizationId:number){
        this._router.navigate(["/CreateCategory", {organizationId:organizationId}])
    }

    public toCategory(categoryId:number){
        this._router.navigate(["/CategoryDetail",{categoryId:categoryId}])
    }


    public addUserEntry() {
        this.usersToInvite.push(new Email());
    }

    public filterEmails() {
        return this.usersToInvite.filter(u => {return u && u.email.length > 0});
    }

    public removeUserFromUsersToInvite(index : number) {
        this.usersToInvite.splice(index, 1);
    }

    public onEditClick(organizationName:string){
        this._organizationService.setOrganizationName(this.organization.organizationId,organizationName).subscribe(
            () => console.log("Succeeeeeed"),
            error => console.log(error)
        );
    }


    public nextCatPage(){
        this.myLeftCatDisplay = "block";
        if(this.counterCatEnd >= this.categories.length-1){
            this.myRightCatDisplay="none";
        }
        if(this.counterCatEnd >= this.categories.length){
            return;
        }
        else{
            this.counterCatBegin++;
            this.counterCatEnd++;
            this.categoriesSubSet= this.categories.slice(this.counterCatBegin,this.counterCatEnd);
        }
    }

    public previousCatPage(){
        this.myRightCatDisplay = "block";
        if(this.counterCatBegin <= 1){
            this.myLeftCatDisplay="none";
        }
        if(this.counterCatBegin <= 0){
            return;
        }  else {
            this.counterCatBegin--;
            this.counterCatEnd--;
            this.categoriesSubSet = this.categories.slice(this.counterCatBegin, this.counterCatEnd);
        }
    }

}

