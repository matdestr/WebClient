import {User} from "../user/user";

/**
 * Entity that represents a ChatMessage JSON object 
 */

export class ChatMessage {
    public constructor(public content : string, public user : User, public dateTime : Date){
    }
}