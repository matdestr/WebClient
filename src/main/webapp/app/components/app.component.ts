import {Component, OnInit} from "angular2/core";
import {RouteConfig, ROUTER_DIRECTIVES} from "angular2/router";
import {WelcomeComponent} from "./authentication/welcome.component";
import {DashboardComponent} from "./dashboard/dashboard.component";
import {CreateOrganizationComponent} from "./organization/create-organization.component";
import {InvitationAcceptComponent} from "./invitations/invitation-accept.component";
import {UserProfileComponent} from "./profile/userprofile.component";
import {UserProfileEditComponent} from "./profile/userprofile-edit.component";
import {OrganizationDetailComponent} from "./organization/organization-detail.component";
import {CreateCategoryComponent} from "./categories/create-category.component";
import {TokenService} from "../services/token.service";
import {Router} from "angular2/router";
import {AuthenticatedRouterOutlet} from "../util/authenticated-router-outlet";
import {CategoryDetailComponent} from "./categories/category-detail.component";
import {CreateTopicComponent} from "./topic/create-topic.component";
import {AddTagComponent} from "./topic/add-tags.component";
import {TopicDetailComponent} from "./topic/topic-detail.component";
import {CreateCardComponent} from "./cards/create-card-details.component";
import {ActiveSessionComponent} from "./session/active-session.component";
import {CreateSessionComponent} from  "./session/create-session.component"
import {TopicCardChooserComponent} from "./topic/topic-card-chooser.component";
import {SessionAddCardsComponent} from "./session/session-add-cards.component";
import {SessionChooseCardsComponent} from "./session/session-choose-cards.component";
import {SessionReviewCardsComponent} from "./session/review-cards.component";
import {SessionComponent} from "./session/session.component";

@Component({
    selector: 'my-app',
    template: `
        <router-outlet></router-outlet>
    `,
    //directives: [ROUTER_DIRECTIVES]
    directives: [AuthenticatedRouterOutlet]
})
@RouteConfig([
    {path: "/",                                 name: "Authentication",         component: WelcomeComponent},
    {path: "/dashboard",                        name: "Dashboard",              component: DashboardComponent},
    {path: "/profile",                          name: "Profile",                component: UserProfileComponent},
    {path: "/profile/edit",                     name: "EditProfile",            component: UserProfileEditComponent},
    {path: '/organization/create',              name: 'NewOrganization',        component: CreateOrganizationComponent},
    {path: '/organization/:organizationId/detail',  name:'OrganizationDetail',  component: OrganizationDetailComponent},
    {path: '/organization/:organizationId/category/create',  name: 'CreateCategory', component: CreateCategoryComponent},
    {path: '/organization/accept',              name: 'AcceptInvitation',       component: InvitationAcceptComponent},
    {path: '/categories/:categoryId/detail',    name:'CategoryDetail',          component:CategoryDetailComponent},
    {path: '/topic/create',                     name:'CreateTopic',             component:CreateTopicComponent},
    {path: '/categories/:categoryId/addtags',   name:'AddTag',                  component:AddTagComponent} ,
    {path: '/topic/:topicId/detail',            name:'TopicDetail',             component:TopicDetailComponent} ,
    {path: '/topic/:topicId/cardChooser',       name:'TopicCardChooser',        component:TopicCardChooserComponent} ,
    //{path: '/session/:sessionId',               name:'ActiveSession',           component:ActiveSessionComponent},
    {path: '/categories/:categoryId/createCard',name:'CreateCard',              component:CreateCardComponent},
    {path: '/categories/:categoryId/createSession', name:'CreateSession',       component:CreateSessionComponent},
    /*{path: '/session/:sessionId/all-cards',     name:'SessionChooseCards',         component:SessionChooseCardsComponent},
    {path:'/session/:sessionId/invite-users',   name:'InviteUsers',             component:InviteUsersComponent},
    {path:'/session/:sessionId/add-cards',      name:'SessionAddCards',         component: SessionAddCardsComponent},
    {path:'/session/:sessionId/review-cards',   name:'SessionReviewCards',       component:SessionReviewCardsComponent}*/
    {path: '/session/:sessionId',               name: 'Session',                component: SessionComponent},
    {path: '/active-session/:sessionId',        name: 'ActiveSession',          component: ActiveSessionComponent}
])
export class AppComponent {
}
