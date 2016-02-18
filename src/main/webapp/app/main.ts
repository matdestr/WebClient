import {bootstrap}    from 'angular2/platform/browser'
import {AppComponent} from './components/app.component'
import {ROUTER_PROVIDERS, APP_BASE_HREF, LocationStrategy, HashLocationStrategy} from "angular2/router";
import {Http, HTTP_PROVIDERS} from "angular2/http";
import {provide} from "angular2/core";
import {JwtHelper, AuthHttp, AuthConfig} from "./libraries/angular2-jwt";

import {SignInService} from "./services/sign-in.service";
import {TokenService} from "./services/token.service";
import {UserService} from "./services/user.service";

bootstrap(AppComponent, [
    ROUTER_PROVIDERS,
    HTTP_PROVIDERS,
    SignInService, TokenService, UserService,
    JwtHelper,
    provide(AuthHttp, {
        useFactory: (http) => {
            return new AuthHttp(new AuthConfig({tokenName : 'token'}), http);
        },
        deps: [Http]
    }),
    AuthHttp,
    provide('App.BackEndPath', {useValue: "http://localhost:3000/"}),
    provide(APP_BASE_HREF, {useValue: '/'}),
    provide(LocationStrategy, {useClass: HashLocationStrategy})
]);