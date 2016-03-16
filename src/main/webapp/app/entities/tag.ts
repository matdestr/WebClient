import {Serializable} from "../util/serializable";

export class Tag implements Serializable<Tag>{
    public name : string;
    public tagId : number;

    constructor(name:string, tagId:number) {
        this.name = name;
        this.tagId = tagId;
    }

    deserialize(object:Tag):Tag {
        this.name = object.name;
        this.tagId = object.tagId;

        return this;
    }

     static createEmptyTag():Tag{
        return new Tag("",null);
    }
}