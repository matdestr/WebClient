import {Component, OnInit, Input} from "angular2/core";
import {ToolbarComponent} from "../widget/toolbar.component";
import {Router} from "angular2/router";
import {RouteParams} from "angular2/router";
import {User} from "../../entities/user/user";
import {Category} from "../../entities/category/category";
import {CategoryService} from "../../services/category.service";
import {Topic} from "../../entities/topic";

@Component({
    selector: 'category-detail',
    templateUrl: 'html/category-detail.html',
    directives: [ToolbarComponent]
})
export class CategoryDetailComponent {
    @Input()

    private category:Category;
    private topics:Topic[]=[];

    constructor(private _router:Router,
                private _routeArgs:RouteParams,
                private _categoryService: CategoryService) {
        this.category = new Category("","");

        var categoryId:number = +_routeArgs.params["categoryId"];

        this._categoryService.getCategory(categoryId).subscribe(
            data => {
                this.category = data.json();
                this.topics = this.category.topics;
            }
        );
    }

    public toAddNewTopic(categoryId:number){
        this._router.navigate(["/CreateTopic", {categoryId:categoryId}])
    }

    public toTopic(topicId:number){
        this._router.navigate(["/TopicDetail",{topicId:topicId}])
    }
}