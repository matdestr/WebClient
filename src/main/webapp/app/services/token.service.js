System.register(["angular2/core", "angular2/http", 'rxjs/add/operator/map'], function(exports_1) {
    var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
        var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
        if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
        else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
        return c > 3 && r && Object.defineProperty(target, key, r), r;
    };
    var __metadata = (this && this.__metadata) || function (k, v) {
        if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
    };
    var core_1, http_1;
    var TokenService;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (http_1_1) {
                http_1 = http_1_1;
            },
            function (_1) {}],
        execute: function() {
            TokenService = (function () {
                function TokenService(_http) {
                    this._http = _http;
                }
                TokenService.prototype.authenticate = function (credentials) {
                    var username = credentials.username;
                    var password = credentials.password;
                    var creds = "username=" + username + "&password=" + password + "&grant_type=password";
                    var headers = new http_1.Headers();
                    headers.append('Authorization', 'Basic ' + btoa(TokenService.client_id + ":" + TokenService.client_secret));
                    headers.append('Content-Type', 'application/x-www-form-urlencoded');
                    return this._http.post(TokenService.tokenEndpoint, creds, {
                        headers: headers
                    })
                        .map(function (res) { return res.json(); });
                };
                TokenService.tokenEndpoint = "./oauth/token";
                TokenService.client_id = "webapp";
                TokenService.client_secret = "secret";
                TokenService = __decorate([
                    core_1.Injectable(), 
                    __metadata('design:paramtypes', [(typeof (_a = typeof http_1.Http !== 'undefined' && http_1.Http) === 'function' && _a) || Object])
                ], TokenService);
                return TokenService;
                var _a;
            })();
            exports_1("TokenService", TokenService);
        }
    }
});
//# sourceMappingURL=token.service.js.map