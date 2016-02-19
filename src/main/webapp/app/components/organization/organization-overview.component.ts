import {Component, OnInit, Input} from "angular2/core";
import {OrganizationService} from "../../services/organization.service";
import {Organization} from "../../entities/organization";

@Component({
    selector: 'my-organizations',
    templateUrl: 'html/organization-overview.html'
})
export class OrganizationOverviewComponent implements OnInit {
    @Input()
    private userId;
    
    private organizationService : OrganizationService;
    private organizations : Array<Organization>;
    
    constructor(organizationService : OrganizationService) {
        this.organizationService = organizationService;
        this.organizations = [];
    }
    
    ngOnInit() {
        this.organizationService.getOrganizations(this.userId)
            .subscribe(
                (organizations : Array<Organization>) => {
                    this.organizations = organizations;
                },
                (error) => {
                    // TODO
                }
            );
    }
}
