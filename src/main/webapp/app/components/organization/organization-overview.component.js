System.register(["angular2/core", "../../services/organization.service"], function(exports_1) {
    var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
        var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
        if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
        else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
        return c > 3 && r && Object.defineProperty(target, key, r), r;
    };
    var __metadata = (this && this.__metadata) || function (k, v) {
        if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
    };
    var core_1, organization_service_1;
    var OrganizationOverviewComponent;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (organization_service_1_1) {
                organization_service_1 = organization_service_1_1;
            }],
        execute: function() {
            OrganizationOverviewComponent = (function () {
                function OrganizationOverviewComponent(organizationService) {
                    this.organizationService = organizationService;
                    this.organizations = [];
                }
                OrganizationOverviewComponent.prototype.ngOnInit = function () {
                    var _this = this;
                    this.organizationService.getOrganizations(this.userId)
                        .subscribe(function (organizations) {
                        _this.organizations = organizations;
                    }, function (error) {
                        // TODO
                    });
                };
                __decorate([
                    core_1.Input(), 
                    __metadata('design:type', Object)
                ], OrganizationOverviewComponent.prototype, "userId", void 0);
                OrganizationOverviewComponent = __decorate([
                    core_1.Component({
                        selector: 'my-organizations',
                        templateUrl: 'html/organization-overview.html'
                    }), 
                    __metadata('design:paramtypes', [organization_service_1.OrganizationService])
                ], OrganizationOverviewComponent);
                return OrganizationOverviewComponent;
            })();
            exports_1("OrganizationOverviewComponent", OrganizationOverviewComponent);
        }
    }
});
//# sourceMappingURL=organization-overview.component.js.map