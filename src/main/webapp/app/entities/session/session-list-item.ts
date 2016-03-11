import {Serializable} from "../../util/serializable";
import {SessionStatus} from "./session-status";

export class SessionListItem implements Serializable<SessionListItem>{
    public  sessionId:number;
    public  organizationTitle:string;
    public  categoryTitle:string;
    public  topicTitle:string
    public  participantAmount:number
    public  sessionStatus:SessionStatus;


    constructor(sessionId:number, organizationTitle:string, categoryTitle:string, topicTitle:string, participantAmount:number, sessionStatus:SessionStatus) {
        this.sessionId = sessionId;
        this.organizationTitle = organizationTitle;
        this.categoryTitle = categoryTitle;
        this.topicTitle = topicTitle;
        this.participantAmount = participantAmount;
        this.sessionStatus = sessionStatus;
    }

    public static createEmptySessionListItem():SessionListItem {
        return new SessionListItem(0,"","","",0,0);
    }

    deserialize(object:SessionListItem):SessionListItem {
        this.sessionId = object.sessionId;
        this.organizationTitle =  object.organizationTitle;
        this.categoryTitle =  object.categoryTitle;
        this.topicTitle =  object.topicTitle;
        this.participantAmount =  object.participantAmount;
        this.sessionStatus =  object.sessionStatus;

        return this;
    }

}