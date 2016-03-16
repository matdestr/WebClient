System.register(['angular2/core', 'angular2/http', 'rxjs/Rx', '../libraries/angular2-jwt', "angular2/http"], function(exports_1) {
    var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
        var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
        if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
        else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
        return c > 3 && r && Object.defineProperty(target, key, r), r;
    };
    var __metadata = (this && this.__metadata) || function (k, v) {
        if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
    };
    var core_1, http_1, angular2_jwt_1, http_2, http_3;
    var OrganizationService;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (http_1_1) {
                http_1 = http_1_1;
            },
            function (_1) {},
            function (angular2_jwt_1_1) {
                angular2_jwt_1 = angular2_jwt_1_1;
            },
            function (http_2_1) {
                http_2 = http_2_1;
                http_3 = http_2_1;
            }],
        execute: function() {
            OrganizationService = (function () {
                function OrganizationService(_http, _authHttp) {
                    this._http = _http;
                    this._authHttp = _authHttp;
                }
                OrganizationService.prototype.saveOrganization = function (organization) {
                    var headers = new http_1.Headers();
                    headers.append('Content-Type', 'application/json');
                    return this._authHttp.put(OrganizationService.endPoint, JSON.stringify(organization), { headers: headers });
                };
                OrganizationService.prototype.createOrganization = function (organization) {
                    var headers = new http_1.Headers();
                    headers.append('Content-Type', 'application/json');
                    return this._authHttp.post(OrganizationService.endPoint, JSON.stringify(organization), { headers: headers });
                };
                OrganizationService.prototype.addUsersToOrganization = function (organizationId, emails) {
                    var headers = new http_1.Headers();
                    headers.append('Content-Type', 'application/json');
                    return this._authHttp.post(OrganizationService.endPoint + "add/" + organizationId, JSON.stringify(emails), { headers: headers });
                };
                OrganizationService.prototype.getOrganization = function (organizationId) {
                    return this._authHttp.get(OrganizationService.endPoint + organizationId);
                };
                OrganizationService.prototype.getOrganizationsByOwner = function (username) {
                    var searchParams = new http_3.URLSearchParams();
                    searchParams.append("owner", "" + true);
                    var options = new http_2.RequestOptions();
                    options.search = searchParams;
                    return this._authHttp.get(OrganizationService.endPoint + "user/" + username, options);
                };
                OrganizationService.prototype.getOrganizationsByUser = function (username) {
                    return this._http.get(OrganizationService.endPoint + "user/" + username);
                };
                OrganizationService.prototype.setOrganizationName = function (organizationId, organizationName) {
                    var searchParams = new http_3.URLSearchParams();
                    searchParams.append("organizationName", organizationName);
                    var options = new http_2.RequestOptions();
                    options.search = searchParams;
                    return this._authHttp.post(OrganizationService.endPoint + "edit/" + organizationId, "", options);
                };
                OrganizationService.endPoint = "./api/organizations/";
                OrganizationService = __decorate([
                    core_1.Injectable(), 
                    __metadata('design:paramtypes', [http_1.Http, angular2_jwt_1.AuthHttp])
                ], OrganizationService);
                return OrganizationService;
            })();
            exports_1("OrganizationService", OrganizationService);
        }
    }
});
//# sourceMappingURL=organization.service.js.map