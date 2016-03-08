System.register(['angular2/core', "angular2/common", "angular2/router", "../../services/user.service", "../../services/token.service", "../widget/error-dialog.component", "angular2/core"], function(exports_1) {
    var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
        var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
        if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
        else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
        return c > 3 && r && Object.defineProperty(target, key, r), r;
    };
    var __metadata = (this && this.__metadata) || function (k, v) {
        if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
    };
    var core_1, common_1, router_1, user_service_1, token_service_1, error_dialog_component_1, core_2;
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
            function (user_service_1_1) {
                user_service_1 = user_service_1_1;
            },
            function (token_service_1_1) {
                token_service_1 = token_service_1_1;
            },
            function (error_dialog_component_1_1) {
                error_dialog_component_1 = error_dialog_component_1_1;
            },
            function (core_2_1) {
                core_2 = core_2_1;
            }],
        execute: function() {
            SignInComponent = (function () {
                function SignInComponent(_signInService, _tokenService, _router) {
                    this._signInService = _signInService;
                    this._tokenService = _tokenService;
                    this._router = _router;
                    this.errorMessages = [];
                }
                SignInComponent.prototype.onSubmit = function () {
                    var _this = this;
                    this.onError(null);
                    if (!this.username || !this.password) {
                        this.onError(this.messageToJson("Username and password are required."));
                    }
                    else {
                        this._signInService
                            .signIn(this.username, this.password)
                            .subscribe(function (token) {
                            _this._tokenService.saveToken(token);
                        }, function (error) {
                            if (error.status == 400) {
                                _this.onError(_this.messageToJson("Invalid username or password."));
                            }
                        }, function () {
                            _this._router.navigate(['/Dashboard']);
                        });
                    }
                };
                SignInComponent.prototype.onError = function (message) {
                    var self = this;
                    if (message) {
                        var obj = JSON.parse(message);
                        if (obj) {
                            this.errorMessages.push(obj.message);
                        }
                    }
                    else
                        this.errorMessages = [];
                };
                SignInComponent.prototype.messageToJson = function (message) {
                    if (typeof (message) !== "string")
                        return null;
                    return "{\"message\":\"" + message + "\"}";
                };
                SignInComponent = __decorate([
                    core_1.Component({
                        selector: 'sign-in',
                        templateUrl: 'html/sign-in.html',
                        directives: [common_1.NgForm, error_dialog_component_1.ErrorDialogComponent],
                        styleUrls: ['css/home.css'],
                        encapsulation: core_2.ViewEncapsulation.None
                    }), 
                    __metadata('design:paramtypes', [user_service_1.UserService, token_service_1.TokenService, router_1.Router])
                ], SignInComponent);
                return SignInComponent;
            })();
            exports_1("SignInComponent", SignInComponent);
        }
    }
});
//# sourceMappingURL=sign-in.component.js.map