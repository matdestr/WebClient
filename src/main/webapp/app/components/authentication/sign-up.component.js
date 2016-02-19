System.register(['angular2/core', "angular2/router", "../../entities/register/register", "../../services/sing-up.service", "../../services/sign-in.service"], function(exports_1) {
    var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
        var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
        if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
        else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
        return c > 3 && r && Object.defineProperty(target, key, r), r;
    };
    var __metadata = (this && this.__metadata) || function (k, v) {
        if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
    };
    var core_1, router_1, register_1, sing_up_service_1, sign_in_service_1;
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
            function (sing_up_service_1_1) {
                sing_up_service_1 = sing_up_service_1_1;
            },
            function (sign_in_service_1_1) {
                sign_in_service_1 = sign_in_service_1_1;
            }],
        execute: function() {
            SignUpComponent = (function () {
                function SignUpComponent(_signUpService, _signInService, _router) {
                    this._signUpService = _signUpService;
                    this._signInService = _signInService;
                    this._router = _router;
                    this.form = new register_1.RegisterModel;
                    this.errors = new Array();
                }
                SignUpComponent.prototype.onSubmit = function () {
                    var _this = this;
                    this._signUpService.signUp(this.form).subscribe(function (data) { return _this.handleData(data); }, function (error) { return _this.handleErrors(error); }, function () { return _this._router.navigate(['/Authentication']); });
                };
                SignUpComponent.prototype.handleData = function (data) {
                    var _this = this;
                    if (data.status == 201) {
                        this._signInService
                            .signIn(this.form.username, this.form.password)
                            .subscribe(function (token) {
                            localStorage.setItem('token', JSON.stringify(token.access_token));
                            console.log(token); // TODO : Remove debug info
                        }, function (error) { console.log(error); }, function () { _this._router.navigate(['/Dashboard']); });
                        this.resetForm();
                    }
                };
                SignUpComponent.prototype.handleErrors = function (error) {
                    var _this = this;
                    this.resetForm();
                    if (error.status == 422) {
                        var json = error.json();
                        json.fieldErrors.forEach(function (e) { return _this.errors.push(e.message); });
                    }
                    else {
                        //todo better handling
                        console.log(error);
                    }
                };
                SignUpComponent.prototype.resetForm = function () {
                    this.errors = new Array();
                    this.form.password = "";
                    this.form.verifyPassword = "";
                };
                SignUpComponent = __decorate([
                    core_1.Component({
                        selector: 'sign-up',
                        templateUrl: 'html/sign-up.html'
                    }), 
                    __metadata('design:paramtypes', [sing_up_service_1.SignUpService, sign_in_service_1.SignInService, router_1.Router])
                ], SignUpComponent);
                return SignUpComponent;
            })();
            exports_1("SignUpComponent", SignUpComponent);
        }
    }
});
//# sourceMappingURL=sign-up.component.js.map