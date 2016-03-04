import {Component, OnInit, Input} from "angular2/core";
import {ToolbarComponent} from "../widget/toolbar.component";
import {Router} from "angular2/router";
import {RouteParams} from "angular2/router";
import {User} from "../../entities/user/user";
import {Category} from "../../entities/category/category";
import {CategoryService} from "../../services/category.service";
import {Topic} from "../../entities/topic/topic";
import {TopicService} from "../../services/topic.service";
import {Tag} from "../../entities/tag";
import {TagService} from "../../services/tag.service";

@Component({
    selector: 'category-detail',
    templateUrl: 'html/category-detail.html',
    directives: [ToolbarComponent]
})
export class CategoryDetailComponent {
    @Input()

    private category:Category;
    private topics:Topic[]=[];
    private tags:Tag[] = [];


    constructor(private _router:Router,
                private _routeArgs:RouteParams,
                private _categoryService: CategoryService,
                private _topicService:TopicService,
                private _tagService:TagService) {
        this.category = new Category("","");
        var categoryId:number = +_routeArgs.params["categoryId"];
        this._categoryService.getCategory(categoryId).subscribe(
            data => {
                this.category = data.json();
            }
        );
        this._topicService.getTopicsFromCategory(categoryId)
            .subscribe(
                data => this.topics = data.json(),
                error => console.log(error),
                () => console.log("Topics fetched")
            );

        this._tagService.getTags().subscribe(data => {
            let tags: Array<Tag> = data.json();
            for(let tag of tags )
                this.tags.push(Tag.createEmptyTag().deserialize(tag));



        });
    }

    public toAddNewTopic(categoryId:number){
        this._router.navigate(["/CreateTopic", {categoryId:categoryId}])
    }

    public toTopic(topicId:number){
        this._router.navigate(["/TopicDetail",{topicId:topicId}])
    }

    public toAddNewCard(categoryId:number){
        this._router.navigate(["/CreateCard",{categoryId:categoryId}])
    }

}