System.register(['angular2/platform/browser', './components/app.component', "angular2/router", "angular2/http", "angular2/core", "./libraries/angular2-jwt", "./services/sign-in.service", "./services/token.service", "./services/user.service"], function(exports_1) {
    var browser_1, app_component_1, router_1, http_1, core_1, angular2_jwt_1, sign_in_service_1, token_service_1, user_service_1;
    return {
        setters:[
            function (browser_1_1) {
                browser_1 = browser_1_1;
            },
            function (app_component_1_1) {
                app_component_1 = app_component_1_1;
            },
            function (router_1_1) {
                router_1 = router_1_1;
            },
            function (http_1_1) {
                http_1 = http_1_1;
            },
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (angular2_jwt_1_1) {
                angular2_jwt_1 = angular2_jwt_1_1;
            },
            function (sign_in_service_1_1) {
                sign_in_service_1 = sign_in_service_1_1;
            },
            function (token_service_1_1) {
                token_service_1 = token_service_1_1;
            },
            function (user_service_1_1) {
                user_service_1 = user_service_1_1;
            }],
        execute: function() {
            browser_1.bootstrap(app_component_1.AppComponent, [
                router_1.ROUTER_PROVIDERS,
                http_1.HTTP_PROVIDERS,
                sign_in_service_1.SignInService, token_service_1.TokenService, user_service_1.UserService,
                angular2_jwt_1.JwtHelper,
                core_1.provide(angular2_jwt_1.AuthHttp, {
                    useFactory: function (http) {
                        return new angular2_jwt_1.AuthHttp(new angular2_jwt_1.AuthConfig({ tokenName: 'token' }), http);
                    },
                    deps: [http_1.Http]
                }),
                angular2_jwt_1.AuthHttp,
                core_1.provide('App.BackEndPath', { useValue: "http://localhost:3000/" }),
                core_1.provide(router_1.APP_BASE_HREF, { useValue: '/' }),
                core_1.provide(router_1.LocationStrategy, { useClass: router_1.HashLocationStrategy })
            ]);
        }
    }
});
//# sourceMappingURL=main.js.map