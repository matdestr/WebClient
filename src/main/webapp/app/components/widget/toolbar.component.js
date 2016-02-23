System.register(['angular2/core', 'angular2/router', "../authentication/sign-out.component", "../../services/user.service", "../../libraries/angular2-jwt", "../../entities/user/user", "angular2/router"], function(exports_1) {
    var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
        var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
        if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
        else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
        return c > 3 && r && Object.defineProperty(target, key, r), r;
    };
    var __metadata = (this && this.__metadata) || function (k, v) {
        if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
    };
    var core_1, router_1, sign_out_component_1, user_service_1, angular2_jwt_1, user_1, router_2;
    var ToolbarComponent;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (router_1_1) {
                router_1 = router_1_1;
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
            function (router_2_1) {
                router_2 = router_2_1;
            }],
        execute: function() {
            ToolbarComponent = (function () {
                function ToolbarComponent(_userService, _router) {
                    var _this = this;
                    this._userService = _userService;
                    this._router = _router;
                    this.user = user_1.User.createEmptyUser();
                    var token = localStorage.getItem('token');
                    if (token == null)
                        return; // TODO : Show error page
                    this._userService.getUser(angular2_jwt_1.getUsername(token)).subscribe(function (user) {
                        _this.user = _this.user.deserialize(user);
                    });
                }
                ToolbarComponent.prototype.ngOnInit = function () {
                    var _this = this;
                    var token = localStorage.getItem('token');
                    this._userService.getUser(angular2_jwt_1.getUsername(token)).subscribe(function (user) {
                        _this.user = _this.user.deserialize(user);
                    });
                    return null;
                };
                ToolbarComponent.prototype.toProfile = function () {
                    console.log("Routing to profile");
                    this._router.navigate(["/Profile", { username: this.user.username }]);
                };
                ToolbarComponent = __decorate([
                    core_1.Component({
                        selector: 'toolbar',
                        templateUrl: 'html/toolbar.html',
                        directives: [sign_out_component_1.SignOutComponent, router_1.ROUTER_DIRECTIVES]
                    }), 
                    __metadata('design:paramtypes', [user_service_1.UserService, router_2.Router])
                ], ToolbarComponent);
                return ToolbarComponent;
            })();
            exports_1("ToolbarComponent", ToolbarComponent);
        }
    }
});
//# sourceMappingURL=toolbar.component.js.map