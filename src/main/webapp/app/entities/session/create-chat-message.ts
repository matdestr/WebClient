import {User} from "../user/user";

export class ChatMessage {
    public constructor(public message:string, public user:User, public date:Date){
    }
}