System.register(['angular2/core', "./sign-in.component", "./sign-up.component", "../../libraries/angular2-jwt", "angular2/router"], function(exports_1) {
    var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
        var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
        if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
        else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
        return c > 3 && r && Object.defineProperty(target, key, r), r;
    };
    var __metadata = (this && this.__metadata) || function (k, v) {
        if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
    };
    var __param = (this && this.__param) || function (paramIndex, decorator) {
        return function (target, key) { decorator(target, key, paramIndex); }
    };
    var core_1, sign_in_component_1, sign_up_component_1, angular2_jwt_1, router_1;
    var AuthenticationComponent;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (sign_in_component_1_1) {
                sign_in_component_1 = sign_in_component_1_1;
            },
            function (sign_up_component_1_1) {
                sign_up_component_1 = sign_up_component_1_1;
            },
            function (angular2_jwt_1_1) {
                angular2_jwt_1 = angular2_jwt_1_1;
            },
            function (router_1_1) {
                router_1 = router_1_1;
            }],
        execute: function() {
            AuthenticationComponent = (function () {
                function AuthenticationComponent(tokenName, router) {
                    if (angular2_jwt_1.tokenNotExpired(tokenName)) {
                        router.navigate(['/Dashboard']);
                    }
                    else {
                        console.log("Token is expired...");
                    }
                }
                AuthenticationComponent.prototype.ngOnInit = function () {
                    return null;
                };
                AuthenticationComponent = __decorate([
                    core_1.Component({
                        selector: 'authentication',
                        templateUrl: 'html/authentication.html',
                        directives: [sign_in_component_1.SignInComponent, sign_up_component_1.SignUpComponent]
                    }),
                    __param(0, core_1.Inject('App.TokenName')), 
                    __metadata('design:paramtypes', [String, router_1.Router])
                ], AuthenticationComponent);
                return AuthenticationComponent;
            })();
            exports_1("AuthenticationComponent", AuthenticationComponent);
        }
    }
});
//# sourceMappingURL=authentication.component.js.map