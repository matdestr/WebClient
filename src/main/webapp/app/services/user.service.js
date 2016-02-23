System.register(['angular2/core', 'angular2/http', '../libraries/angular2-jwt', 'rxjs/Rx', "../entities/authenticatie/credentials", "./token.service"], function(exports_1) {
    var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
        var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
        if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
        else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
        return c > 3 && r && Object.defineProperty(target, key, r), r;
    };
    var __metadata = (this && this.__metadata) || function (k, v) {
        if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
    };
    var core_1, http_1, angular2_jwt_1, credentials_1, token_service_1;
    var UserService;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (http_1_1) {
                http_1 = http_1_1;
            },
            function (angular2_jwt_1_1) {
                angular2_jwt_1 = angular2_jwt_1_1;
            },
            function (_1) {},
            function (credentials_1_1) {
                credentials_1 = credentials_1_1;
            },
            function (token_service_1_1) {
                token_service_1 = token_service_1_1;
            }],
        execute: function() {
            UserService = (function () {
                function UserService(_http, _authHttp, _tokenService) {
                    this._http = _http;
                    this._authHttp = _authHttp;
                    this._tokenService = _tokenService;
                }
                UserService.prototype.getUser = function (username) {
                    return this._http.get("api/users/" + username).map(function (res) {
                        console.log(res.json());
                        return res.json();
                    });
                };
                UserService.prototype.saveUser = function (user) {
                    return this._authHttp.put("api/users/" + user.userId, JSON.stringify(user));
                };
                UserService.prototype.signUp = function (registerModel) {
                    console.log(UserService.endpoint);
                    var headers = new http_1.Headers();
                    headers.append('Content-Type', 'application/json');
                    return this._http
                        .post(UserService.endpoint, JSON.stringify(registerModel), {
                        headers: headers
                    });
                };
                UserService.prototype.signIn = function (username, password) {
                    var credentials = new credentials_1.CredentialsModel();
                    credentials.username = username;
                    credentials.password = password;
                    return this._tokenService.authenticate(credentials);
                };
                UserService.prototype.updateUser = function (updateUser) {
                    var headers = new http_1.Headers();
                    headers.append('Content-Type', 'application/json');
                    return this._authHttp.put(UserService.endpoint, JSON.stringify(updateUser), ({
                        headers: headers
                    }));
                };
                UserService.endpoint = "./api/users";
                UserService = __decorate([
                    core_1.Injectable(), 
                    __metadata('design:paramtypes', [http_1.Http, angular2_jwt_1.AuthHttp, token_service_1.TokenService])
                ], UserService);
                return UserService;
            })();
            exports_1("UserService", UserService);
        }
    }
});
//# sourceMappingURL=user.service.js.map