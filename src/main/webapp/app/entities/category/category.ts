import {Serializable} from "../../util/serializable";
import {Topic} from "../../entities/topic/topic"
import {Tag} from "../tag";

/**
 * This class contains detailed information about a single category, including its
 * name, description and related topics.
 * */
export class Category implements Serializable<Category>{
    public categoryId: number;
    public name : string;
    public description: string;
    public topics: Topic[];
    public tags: Tag[];


    constructor(categoryId:number, name:string, description:string, topics:Topic[], tags:Tag[]) {
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.topics = topics;
        this.tags = tags;
    }

    public static createEmptyCategory():Category {
        return new Category(0, "","", [] , []);
    }

    deserialize(object:Category):Category {
        this.name = object.name;
        this.description = object.description;
        this.categoryId = object.categoryId;

        var deserializedTopics:Topic[] = [];
        var deserializedTags: Tag[] = [];

        for (let topic of object.topics)
            deserializedTopics.push(Topic.createEmptyTopic().deserialize(topic));

        for(let tag of object.tags)
            deserializedTags.push(Tag.createEmptyTag().deserialize(tag));

        return this;
    }
}