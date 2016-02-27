System.register(['angular2/core', "angular2/common", "angular2/router", "../../entities/user/user", "../../services/organization.service", "../../entities/organization", "../widget/toolbar.component", "../widget/error-dialog.component"], function(exports_1) {
    var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
        var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
        if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
        else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
        return c > 3 && r && Object.defineProperty(target, key, r), r;
    };
    var __metadata = (this && this.__metadata) || function (k, v) {
        if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
    };
    var core_1, common_1, router_1, user_1, organization_service_1, organization_1, toolbar_component_1, error_dialog_component_1;
    var CreateOrganizationComponent;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (common_1_1) {
                common_1 = common_1_1;
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
            function (toolbar_component_1_1) {
                toolbar_component_1 = toolbar_component_1_1;
            },
            function (error_dialog_component_1_1) {
                error_dialog_component_1 = error_dialog_component_1_1;
            }],
        execute: function() {
            CreateOrganizationComponent = (function () {
                function CreateOrganizationComponent(_organizationService, _router) {
                    this._organizationService = _organizationService;
                    this._router = _router;
                }
                CreateOrganizationComponent.prototype.ngOnInit = function () {
                    this.organization = new organization_1.Organization();
                    this.usersToInvite = [];
                    for (var i = 0; i < 3; i++) {
                        this.usersToInvite.push(user_1.User.createEmptyUser());
                    }
                    this.organizationCreated = false;
                    this.showErrorOrganizationName = false;
                    this.isError = false;
                };
                CreateOrganizationComponent.prototype.onSubmit = function (form) {
                    var _this = this;
                    if (this.organization.name) {
                        this._organizationService.saveOrganization(this.organization)
                            .subscribe(null, function (error) {
                            _this.isError = true;
                            console.log(error);
                        }, function () {
                            _this.organizationCreated = true;
                            _this.isError = false;
                            _this.inviteUsers();
                        });
                        this.inviteUsers();
                        this._router.navigate(['/Dashboard']);
                    }
                };
                CreateOrganizationComponent.prototype.setShowErrorOrganizationName = function (show) {
                    this.showErrorOrganizationName = show;
                };
                CreateOrganizationComponent.prototype.addUserEntry = function () {
                    this.usersToInvite.push(user_1.User.createEmptyUser());
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
                        directives: [error_dialog_component_1.ErrorDialogComponent, common_1.NgFor, common_1.NgForm, common_1.FORM_DIRECTIVES, toolbar_component_1.ToolbarComponent]
                    }), 
                    __metadata('design:paramtypes', [organization_service_1.OrganizationService, (typeof (_a = typeof router_1.Router !== 'undefined' && router_1.Router) === 'function' && _a) || Object])
                ], CreateOrganizationComponent);
                return CreateOrganizationComponent;
                var _a;
            })();
            exports_1("CreateOrganizationComponent", CreateOrganizationComponent);
        }
    }
});
//# sourceMappingURL=create-organization.component.js.map