import {Serializable} from "../../util/serializable";
import {User} from "../user/user";

export class Comment implements Serializable<Comment> {
    public user:User;
    public message:string;

    constructor(user:User,message:string) {
         this.user=user;
        this.message=message;
    }

    public static createEmptyComment():Comment {
        return new Comment(null,"");
    }

    deserialize(object:Comment):Comment {
        this.user=object.user;
        this.message=object.message;
        return this;
    }
}
