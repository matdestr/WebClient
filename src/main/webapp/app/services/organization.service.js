System.register(['angular2/core', 'angular2/http', 'rxjs/Rx', '../libraries/angular2-jwt'], function(exports_1) {
    var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
        var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
        if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
        else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
        return c > 3 && r && Object.defineProperty(target, key, r), r;
    };
    var __metadata = (this && this.__metadata) || function (k, v) {
        if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
    };
    var core_1, http_1, angular2_jwt_1;
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
            }],
        execute: function() {
            OrganizationService = (function () {
                function OrganizationService(/*http : Http,*/ authHttp) {
                    //this.http = http;
                    this.authHttp = authHttp;
                }
                OrganizationService.prototype.saveOrganization = function (organization) {
                    var headers = new http_1.Headers();
                    headers.append('Content-Type', 'application/json');
                    return this.authHttp.post('api/organizations/', JSON.stringify(organization), { headers: headers }).retry(2);
                };
                OrganizationService.prototype.getOrganizations = function (userId) {
                    var headers = new http_1.Headers();
                    headers.append('Content-Type', 'application/json');
                    return this.authHttp.get('api/organizations/' + userId).retry(2);
                };
                OrganizationService = __decorate([
                    core_1.Injectable(), 
                    __metadata('design:paramtypes', [angular2_jwt_1.AuthHttp])
                ], OrganizationService);
                return OrganizationService;
            })();
            exports_1("OrganizationService", OrganizationService);
        }
    }
});
//# sourceMappingURL=organization.service.js.map