System.register(['angular2/core', 'angular2/router', "../authentication/sign-out.component", "../../services/user.service", "../../libraries/angular2-jwt", "../../entities/user/user", "../../services/organization.service"], function(exports_1, context_1) {
    "use strict";
    var __moduleName = context_1 && context_1.id;
    var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
        var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
        if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
        else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
        return c > 3 && r && Object.defineProperty(target, key, r), r;
    };
    var __metadata = (this && this.__metadata) || function (k, v) {
        if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
    };
    var core_1, router_1, sign_out_component_1, user_service_1, angular2_jwt_1, user_1, organization_service_1, router_2;
    var ToolbarComponent;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (router_1_1) {
                router_1 = router_1_1;
                router_2 = router_1_1;
            },
            function (sign_out_component_1_1) {
                sign_out_component_1 = sign_out_component_1_1;
            },
            function (user_service_1_1) {
                user_service_1 = user_service_1_1;
            },
            function (angular2_jwt_1_1) {
                angular2_jwt_1 = angular2_jwt_1_1;
            },
            function (user_1_1) {
                user_1 = user_1_1;
            },
            function (organization_service_1_1) {
                organization_service_1 = organization_service_1_1;
            }],
        execute: function() {
            ToolbarComponent = (function () {
                function ToolbarComponent(_userService, _router, _organizationService) {
                    var _this = this;
                    this._userService = _userService;
                    this._router = _router;
                    this._organizationService = _organizationService;
                    this.organizations = [];
                    this.user = user_1.User.createEmptyUser();
                    var token = localStorage.getItem('token');
                    if (token == null)
                        return; // TODO : Show error page
                    this._userService.getUser(angular2_jwt_1.getUsername(token)).subscribe(function (user) {
                        _this.user = _this.user.deserialize(user);
                        _this.getOrganizations();
                    });
                }
                ToolbarComponent.prototype.ngOnInit = function () {
                    var token = localStorage.getItem('token');
                };
                ToolbarComponent.prototype.getOrganizations = function () {
                    var _this = this;
                    this._organizationService.getOrganizationsByOwner(this.user.username).subscribe(function (data) {
                        _this.organizations = data.json();
                    }, function (error) { console.log(error); _this.organizations = []; });
                };
                ToolbarComponent.prototype.toProfile = function () {
                    console.log("Routing to profile");
                    this._router.navigate(["/Profile", { username: this.user.username }]);
                };
                ToolbarComponent.prototype.toOrganization = function (organizationId) {
                    console.log("Routing to Organization");
                    this._router.navigate(["/OrganizationDetail", { organizationId: organizationId }]);
                };
                ToolbarComponent = __decorate([
                    core_1.Component({
                        selector: 'toolbar',
                        templateUrl: 'html/toolbar.html',
                        directives: [sign_out_component_1.SignOutComponent, router_1.ROUTER_DIRECTIVES]
                    }), 
                    __metadata('design:paramtypes', [user_service_1.UserService, router_2.Router, organization_service_1.OrganizationService])
                ], ToolbarComponent);
                return ToolbarComponent;
            }());
            exports_1("ToolbarComponent", ToolbarComponent);
        }
    }
});
//# sourceMappingURL=toolbar.component.js.map