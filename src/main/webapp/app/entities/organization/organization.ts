import {User} from "../../entities/user/user";
import {Category} from "../../entities/category/category"

import {Email} from "../../entities/user/email";

/**
 * This class represents an organization, owned by a user and containing zero, one or more members.
 * */
export class Organization {
    public organizationId : number;
    public name : string;
    public owner : User;
    public members: User[];
    public categories: Category[];

    public static createEmptyOrganization() : Organization {
        var organization : Organization = new Organization();
        organization.organizationId = -1;
        organization.name = "";
        organization.owner = User.createEmptyUser();
        organization.members = [];
        organization.categories = [];

        return organization;
    }
}


export class CreateOrganization {
    public name:string;
    public emails: Email[];
}