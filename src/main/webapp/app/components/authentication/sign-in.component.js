System.register(['angular2/core', "angular2/common", "angular2/router", "../../services/sign-in.service"], function(exports_1) {
    var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
        var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
        if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
        else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
        return c > 3 && r && Object.defineProperty(target, key, r), r;
    };
    var __metadata = (this && this.__metadata) || function (k, v) {
        if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
    };
    var core_1, common_1, router_1, sign_in_service_1;
    var SignInComponent;
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
            function (sign_in_service_1_1) {
                sign_in_service_1 = sign_in_service_1_1;
            }],
        execute: function() {
            SignInComponent = (function () {
                function SignInComponent(_signInService, _router) {
                    this._signInService = _signInService;
                    this._router = _router;
                    this.invalidCredentials = false;
                }
                SignInComponent.prototype.onSubmit = function () {
                    var _this = this;
                    if (!this.username || !this.password) {
                        // TODO : Display error
                        console.log('username and password are required');
                    }
                    this._signInService
                        .signIn(this.username, this.password)
                        .subscribe(function (token) {
                        localStorage.setItem('token', JSON.stringify(token.access_token));
                        console.log(token); // TODO : Remove debug info
                    }, function (error) { console.error(error); _this.invalidCredentials = true; }, function () { _this._router.navigate(['/Dashboard']); });
                };
                SignInComponent = __decorate([
                    core_1.Component({
                        selector: 'sign-in',
                        templateUrl: 'html/sign-in.html',
                        directives: [common_1.NgForm]
                    }), 
                    __metadata('design:paramtypes', [sign_in_service_1.SignInService, router_1.Router])
                ], SignInComponent);
                return SignInComponent;
            })();
            exports_1("SignInComponent", SignInComponent);
        }
    }
});
//# sourceMappingURL=sign-in.component.js.map