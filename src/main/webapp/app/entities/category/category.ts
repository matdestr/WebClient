import {Serializable} from "../../util/serializable";
import {Topic} from "../../entities/topic/topic"

export class Category implements Serializable<Category>{
    public categoryId: number;
    public name : string;
    public description: string;
    public topics: Topic[];

    constructor(name:string, description:string) {
        this.name = name;
        this.description = description;
    }


    public static createEmptyCategory():Category {
        return new Category("","");
    }

    deserialize(object:Category):Category {
        this.name = object.name;
        this.description = object.description;
        this.categoryId = object.categoryId;
        this.topics = object.topics;
        return this;
    }
}