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
    var __param = (this && this.__param) || function (paramIndex, decorator) {
        return function (target, key) { decorator(target, key, paramIndex); }
    };
    var core_1, http_1, core_2;
    var TokenService;
    function isTokenExpired() {
        var expireDate = localStorage.getItem('token-expire-date');
        var token = localStorage.getItem('token');
        if (token) {
            console.log("token found");
            if (expireDate) {
                console.log("calculating expire date");
                return Date.parse(expireDate) < Date.now();
            }
        }
        else {
            console.log("no token found");
        }
        return true;
    }
    exports_1("isTokenExpired", isTokenExpired);
    function isTokenAvailable() {
        var token = localStorage.getItem(this._tokenName);
        if (!token)
            console.log('No token available');
        return !!token;
    }
    exports_1("isTokenAvailable", isTokenAvailable);
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
                core_2 = core_1_1;
            },
            function (http_1_1) {
                http_1 = http_1_1;
            },
            function (_1) {}],
        execute: function() {
            /**
             * Service for doing http calls for token endpoint
             */
            TokenService = (function () {
                function TokenService(_http, _tokenName) {
                    this._http = _http;
                    this._tokenName = _tokenName;
                }
                /*private getAuthorizationHeaderClientDetails() : string {
                    return 'Basic ' + btoa(TokenService.client_id + ':' + TokenService.client_secret);
                }
                
                public getAccessToken() : string {
                    return localStorage.getItem(this._tokenName);
                }
                
                public tokenValidCheckedServerSideObservable() : Observable<Response> {
                    var headers = new Headers();
                    headers.append('Authorization', this.getAuthorizationHeaderClientDetails());
                    headers.append('Content-Type', 'application/x-www-form-urlencoded');
                    
                    return this._http.post(TokenService.tokenCheckEndpoint,
                        'token=' + this.getAccessToken(),
                        {headers: headers});
                }*/
                TokenService.prototype.saveToken = function (token) {
                    //Save token for authHttp
                    localStorage.setItem('token', token.access_token);
                    localStorage.setItem('token-refresh-token', token.refresh_token);
                    localStorage.setItem('token-expire-date', "" + (token.expires_in * 1000 + Date.now()));
                    console.log(token);
                };
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
                TokenService.prototype.refreshToken = function () {
                    var _this = this;
                    var refreshToken = localStorage.getItem('token-refresh-token');
                    var headers = new http_1.Headers();
                    headers.append('Authorization', btoa(TokenService.client_id + ':' + TokenService.client_secret));
                    headers.append('Content-Type', 'application/x-www-form-urlencoded');
                    var refreshContent = 'grant_type=refresh_token&refresh_token=' + refreshToken;
                    this._http.post(TokenService.tokenEndpoint, refreshContent, { headers: headers })
                        .subscribe(function (token) {
                        _this.saveToken(token);
                        console.log('Token refreshed');
                    }, function (error) { return console.error(error); });
                };
                TokenService.tokenEndpoint = "./oauth/token";
                TokenService.tokenCheckEndpoint = './oauth/check_token';
                TokenService.client_id = "webapp";
                TokenService.client_secret = "secret";
                TokenService = __decorate([
                    core_1.Injectable(),
                    __param(1, core_2.Inject('App.TokenName')), 
                    __metadata('design:paramtypes', [http_1.Http, String])
                ], TokenService);
                return TokenService;
            })();
            exports_1("TokenService", TokenService);
        }
    }
});
//# sourceMappingURL=token.service.js.map