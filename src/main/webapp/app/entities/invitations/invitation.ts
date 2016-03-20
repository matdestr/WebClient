import {User} from "../../entities/user/user";
import {Organization} from "../../entities/organization/organization";

/**
 * This class represents an invitation for a user to join an organization.
 * */
export class Invitation {
    public invitedUser:User;
    public organization:Organization;
    public acceptId:string;
    public email:string;

    public accepted:boolean;
}
