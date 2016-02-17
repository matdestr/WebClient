System.register(['angular2/core', 'angular2/http', 'rxjs/Rx', "./token.service", "../entities/authenticatie/credentials"], function(exports_1) {
    var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
        var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
        if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
        else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
        return c > 3 && r && Object.defineProperty(target, key, r), r;
    };
    var __metadata = (this && this.__metadata) || function (k, v) {
        if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
    };
    var core_1, http_1, token_service_1, credentials_1;
    var SignInService;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (http_1_1) {
                http_1 = http_1_1;
            },
            function (_1) {},
            function (token_service_1_1) {
                token_service_1 = token_service_1_1;
            },
            function (credentials_1_1) {
                credentials_1 = credentials_1_1;
            }],
        execute: function() {
            SignInService = (function () {
                function SignInService(_http, tokenService) {
                    this._http = _http;
                    this.tokenService = tokenService;
                }
                SignInService.prototype.signIn = function (username, password) {
                    var credentials = new credentials_1.CredentialsModel();
                    credentials.username = username;
                    credentials.password = password;
                    return this.tokenService.authenticate(credentials);
                };
                SignInService = __decorate([
                    core_1.Injectable(), 
                    __metadata('design:paramtypes', [http_1.Http, token_service_1.TokenService])
                ], SignInService);
                return SignInService;
            })();
            exports_1("SignInService", SignInService);
        }
    }
});
//# sourceMappingURL=sign-in.service.js.map