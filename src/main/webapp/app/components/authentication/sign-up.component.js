System.register(['angular2/core', "angular2/router", "../../entities/user/register", "../../services/user.service", "../../services/token.service", "../widget/error-dialog.component"], function(exports_1) {
    var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
        var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
        if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
        else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
        return c > 3 && r && Object.defineProperty(target, key, r), r;
    };
    var __metadata = (this && this.__metadata) || function (k, v) {
        if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
    };
    var core_1, router_1, register_1, user_service_1, token_service_1, error_dialog_component_1;
    var SignUpComponent;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (router_1_1) {
                router_1 = router_1_1;
            },
            function (register_1_1) {
                register_1 = register_1_1;
            },
            function (user_service_1_1) {
                user_service_1 = user_service_1_1;
            },
            function (token_service_1_1) {
                token_service_1 = token_service_1_1;
            },
            function (error_dialog_component_1_1) {
                error_dialog_component_1 = error_dialog_component_1_1;
            }],
        execute: function() {
            /**
             * This component is responsible for all the functionality of the sign up page
             */
            SignUpComponent = (function () {
                function SignUpComponent(_userService, _tokenService, _router) {
                    this._userService = _userService;
                    this._tokenService = _tokenService;
                    this._router = _router;
                    this.errorMessages = [];
                    this.form = new register_1.RegisterModel;
                }
                SignUpComponent.prototype.onSubmit = function () {
                    var _this = this;
                    this.onError(null);
                    this._userService.signUp(this.form).subscribe(function (data) { return _this.handleData(data); }, function (error) {
                        _this.handleErrors(error);
                    }, function () { return _this._router.navigate(['/Authentication']); });
                };
                SignUpComponent.prototype.handleData = function (data) {
                    var _this = this;
                    if (data.status == 201) {
                        this._userService
                            .signIn(this.form.username, this.form.password)
                            .subscribe(function (token) {
                            _this._tokenService.saveToken(token);
                        }, function (error) { console.log(error); }, function () { _this._router.navigate(['/Dashboard']); });
                        this.resetForm();
                    }
                };
                SignUpComponent.prototype.handleErrors = function (error) {
                    var _this = this;
                    var obj = JSON.parse(error.text());
                    console.log(obj);
                    if (obj.fieldErrors) {
                        if (obj.fieldErrors.length > 4)
                            this.onError("Please fill in all fields.");
                        else
                            obj.fieldErrors.forEach(function (e) { return _this.onError(e.message); });
                    }
                    else {
                        this.onError(obj.message);
                    }
                    this.resetForm();
                };
                SignUpComponent.prototype.resetForm = function () {
                    this.form.password = "";
                    this.form.verifyPassword = "";
                };
                SignUpComponent.prototype.onError = function (message) {
                    var self = this;
                    if (message) {
                        this.errorMessages.push(message);
                    }
                    else {
                        this.errorMessages = [];
                    }
                };
                SignUpComponent = __decorate([
                    core_1.Component({
                        selector: 'sign-up',
                        templateUrl: 'html/sign-up.html',
                        directives: [error_dialog_component_1.ErrorDialogComponent]
                    }), 
                    __metadata('design:paramtypes', [user_service_1.UserService, token_service_1.TokenService, router_1.Router])
                ], SignUpComponent);
                return SignUpComponent;
            })();
            exports_1("SignUpComponent", SignUpComponent);
        }
    }
});
//# sourceMappingURL=sign-up.component.js.map