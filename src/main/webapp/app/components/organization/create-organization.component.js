System.register(['angular2/core', "angular2/common", "angular2/router", "../../entities/user", "../../services/organization.service", "../../entities/organization", "../../libraries/angular2-jwt"], function(exports_1) {
    var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
        var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
        if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
        else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
        return c > 3 && r && Object.defineProperty(target, key, r), r;
    };
    var __metadata = (this && this.__metadata) || function (k, v) {
        if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
    };
    var core_1, common_1, common_2, common_3, router_1, user_1, organization_service_1, organization_1, angular2_jwt_1;
    var CreateOrganizationComponent;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (common_1_1) {
                common_1 = common_1_1;
                common_2 = common_1_1;
                common_3 = common_1_1;
            },
            function (router_1_1) {
                router_1 = router_1_1;
            },
            function (user_1_1) {
                user_1 = user_1_1;
            },
            function (organization_service_1_1) {
                organization_service_1 = organization_service_1_1;
            },
            function (organization_1_1) {
                organization_1 = organization_1_1;
            },
            function (angular2_jwt_1_1) {
                angular2_jwt_1 = angular2_jwt_1_1;
            }],
        execute: function() {
            //import {ErrorDialogComponent} from "../error-dialog.component";
            CreateOrganizationComponent = (function () {
                function CreateOrganizationComponent(organizationService) {
                    this.organizationService = organizationService;
                    this.organization = new organization_1.Organization();
                    this.usersToInvite = [];
                    this.usersToInvite.push(new user_1.User());
                    this.organizationCreated = false;
                    this.isError = false;
                }
                CreateOrganizationComponent.prototype.onSubmit = function () {
                    var _this = this;
                    if (this.organization.name) {
                        this.organizationService.saveOrganization(this.organization)
                            .subscribe(null, function (error) {
                            _this.isError = true;
                        }, function () {
                            _this.organizationCreated = true;
                            _this.isError = false;
                            _this.inviteUsers();
                        });
                        this.inviteUsers();
                    }
                };
                CreateOrganizationComponent.prototype.addUserEntry = function () {
                    this.usersToInvite.push(new user_1.User());
                };
                CreateOrganizationComponent.prototype.inviteUsers = function () {
                    var nonEmptyUsers = this.usersToInvite.filter(function (u) { return u.email && u.email.length > 0; });
                    console.log(nonEmptyUsers);
                };
                CreateOrganizationComponent.prototype.removeUserFromUsersToInvite = function (index) {
                    this.usersToInvite.splice(index, 1);
                };
                CreateOrganizationComponent = __decorate([
                    core_1.Component({
                        selector: 'create-organization',
                        templateUrl: 'html/create-organization.html',
                        directives: [common_1.NgFor, common_3.NgForm, common_2.FORM_DIRECTIVES]
                    }),
                    router_1.CanActivate(function () { return angular2_jwt_1.tokenNotExpired(); }), 
                    __metadata('design:paramtypes', [organization_service_1.OrganizationService])
                ], CreateOrganizationComponent);
                return CreateOrganizationComponent;
            })();
            exports_1("CreateOrganizationComponent", CreateOrganizationComponent);
        }
    }
});
//# sourceMappingURL=create-organization.component.js.map