import {User} from "../user/user";

export class ChatMessage {
    public constructor(public content : string, public user : User, public dateTime : Date){
    }
}