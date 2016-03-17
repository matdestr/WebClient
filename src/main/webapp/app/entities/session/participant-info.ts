import {Serializable} from "../../util/serializable";
import {User} from "../user/user";

export class ParticipantInfo implements Serializable<ParticipantInfo> {
    participant : User;
    joined : boolean;
    addedCardsCompleted : boolean;
    reviewingCardsCompleted : boolean;
    
    deserialize(object : ParticipantInfo) : ParticipantInfo {
        this.participant = User.createEmptyUser().deserialize(object.participant);
        this.joined = object.joined;
        this.addedCardsCompleted = object.addedCardsCompleted;
        this.reviewingCardsCompleted = object.reviewingCardsCompleted;
        
        return this;
    }
}
