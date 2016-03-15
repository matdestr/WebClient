import {bootstrap}    from 'angular2/platform/browser';
import {AppComponent} from './components/app.component';
import {ROUTER_PROVIDERS, APP_BASE_HREF, LocationStrategy, HashLocationStrategy} from "angular2/router";
import {Http, HTTP_PROVIDERS} from "angular2/http";
import {provide} from "angular2/core";
import {JwtHelper, AuthHttp, AuthConfig} from "./libraries/angular2-jwt";

import {TokenService} from "./services/token.service";
import {UserService} from "./services/user.service";
import {CategoryService} from "./services/category.service"
import {OrganizationService} from "./services/organization.service";
import {ToolbarComponent} from "./components/widget/toolbar.component";
import {TagService} from "./services/tag.service";
import {TopicService} from "./services/topic.service";
import {InvitationService} from "./services/invitation.service";
import {CardDetailsService} from "./services/card-details.service";
import {SessionService} from  "./services/session.service";
import {SessionInvitationService} from "./services/session-invitation.service";
import {SessionGameService} from "./services/session-game.service";

bootstrap(AppComponent, [
    ROUTER_PROVIDERS,
    HTTP_PROVIDERS,
    TokenService, UserService, OrganizationService, CategoryService,
    TagService, TopicService, InvitationService, CardDetailsService,SessionService, SessionGameService, SessionInvitationService,
    ToolbarComponent,
    provide(AuthHttp, {
        useFactory: (http) => {
            return new AuthHttp(new AuthConfig({tokenName: 'token'}), http);
        },
        deps: [Http]
    }),
    provide('App.TokenName', {useValue: 'token'}),
    provide(APP_BASE_HREF, {useValue: '/'}),
    provide(LocationStrategy, {useClass: HashLocationStrategy})
]);