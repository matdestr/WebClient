System.register(["angular2/core", "angular2/router", "../widget/toolbar.component", "../../services/organization.service", "../../entities/user/user", "../../services/user.service", "../../libraries/angular2-jwt", "../../entities/session/session-list-item", "../../services/session.service", "../../entities/session/session-status"], function(exports_1) {
    var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
        var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
        if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
        else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
        return c > 3 && r && Object.defineProperty(target, key, r), r;
    };
    var __metadata = (this && this.__metadata) || function (k, v) {
        if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
    };
    var core_1, router_1, toolbar_component_1, organization_service_1, user_1, user_service_1, angular2_jwt_1, session_list_item_1, session_service_1, session_status_1;
    var DashboardComponent;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (router_1_1) {
                router_1 = router_1_1;
            },
            function (toolbar_component_1_1) {
                toolbar_component_1 = toolbar_component_1_1;
            },
            function (organization_service_1_1) {
                organization_service_1 = organization_service_1_1;
            },
            function (user_1_1) {
                user_1 = user_1_1;
            },
            function (user_service_1_1) {
                user_service_1 = user_service_1_1;
            },
            function (angular2_jwt_1_1) {
                angular2_jwt_1 = angular2_jwt_1_1;
            },
            function (session_list_item_1_1) {
                session_list_item_1 = session_list_item_1_1;
            },
            function (session_service_1_1) {
                session_service_1 = session_service_1_1;
            },
            function (session_status_1_1) {
                session_status_1 = session_status_1_1;
            }],
        execute: function() {
            DashboardComponent = (function () {
                function DashboardComponent(_router, _organizationService, _userService, _sessionService) {
                    this._router = _router;
                    this._organizationService = _organizationService;
                    this._userService = _userService;
                    this._sessionService = _sessionService;
                    this.user = user_1.User.createEmptyUser();
                    this.organizations = [];
                    this.organizationsSubSet = [];
                    this.counterBegin = 0;
                    this.counterEnd = 4;
                    this.myLeftDisplay = "block";
                    this.myRightDisplay = "block";
                    this.sessions = [];
                    this.activeSessions = [];
                    this.futureSessions = [];
                    this.previousSessions = [];
                }
                DashboardComponent.prototype.ngOnInit = function () {
                    var _this = this;
                    var token = localStorage.getItem('token');
                    this._userService.getUser(angular2_jwt_1.getUsername(token)).subscribe(function (user) {
                        _this.user = _this.user.deserialize(user);
                        _this.getOrganizations();
                        _this.getSessions();
                    });
                    if (this.organizations.length <= 4) {
                        this.myLeftDisplay = "none";
                        this.myRightDisplay = "none";
                    }
                    else {
                        this.myLeftDisplay = "block";
                        this.myRightDisplay = "block";
                    }
                };
                DashboardComponent.prototype.getOrganizations = function () {
                    var _this = this;
                    this._organizationService.getOrganizationsByUser(this.user.username).subscribe(function (data) {
                        _this.organizations = data.json();
                        _this.updateSubSet();
                    }, function (error) { console.log(error); _this.organizations = []; });
                };
                DashboardComponent.prototype.getSessions = function () {
                    var _this = this;
                    this._sessionService.getSessions().subscribe(function (data) {
                        for (var _i = 0, _a = data.json(); _i < _a.length; _i++) {
                            var sessionObject = _a[_i];
                            var session = session_list_item_1.SessionListItem.createEmptySessionListItem().deserialize(sessionObject);
                            _this.sessions.push(session);
                            switch (session.sessionStatus) {
                                case session_status_1.SessionStatus.CREATED:
                                    _this.futureSessions.push(session);
                                    break;
                                case session_status_1.SessionStatus.FINISHED:
                                    _this.previousSessions.push(session);
                                    break;
                                case session_status_1.SessionStatus.IN_PROGRESS:
                                    _this.activeSessions.push(session);
                                    break;
                                default:
                                    _this.futureSessions.push(session);
                                    break;
                            }
                        }
                    }, function (error) { console.log(error); });
                };
                DashboardComponent.prototype.updateSubSet = function () {
                    this.organizationsSubSet = this.organizations.slice(0, 4);
                    if (this.organizations.length > 4) {
                        this.myRightDisplay = "block";
                    }
                };
                DashboardComponent.prototype.toOrganization = function (organizationId) {
                    this._router.navigate(["/OrganizationDetail", { organizationId: organizationId }]);
                };
                DashboardComponent.prototype.nextOrgPage = function () {
                    this.myLeftDisplay = "block";
                    if (this.counterEnd >= this.organizations.length - 1) {
                        this.myRightDisplay = "none";
                    }
                    if (this.counterEnd >= this.organizations.length) {
                        return;
                    }
                    else {
                        this.counterBegin++;
                        this.counterEnd++;
                        this.organizationsSubSet = this.organizations.slice(this.counterBegin, this.counterEnd);
                    }
                };
                DashboardComponent.prototype.previousOrgPage = function () {
                    this.myRightDisplay = "block";
                    if (this.counterBegin <= 1) {
                        this.myLeftDisplay = "none";
                    }
                    if (this.counterBegin <= 0) {
                        return;
                    }
                    else {
                        this.counterBegin--;
                        this.counterEnd--;
                        this.organizationsSubSet = this.organizations.slice(this.counterBegin, this.counterEnd);
                    }
                };
                DashboardComponent.prototype.nextActiveSesPage = function () {
                };
                DashboardComponent.prototype.previousActiveSesPage = function () {
                };
                DashboardComponent = __decorate([
                    core_1.Component({
                        selector: 'dashboard',
                        templateUrl: 'html/dashboard.html',
                        directives: [toolbar_component_1.ToolbarComponent]
                    }), 
                    __metadata('design:paramtypes', [router_1.Router, organization_service_1.OrganizationService, user_service_1.UserService, session_service_1.SessionService])
                ], DashboardComponent);
                return DashboardComponent;
            })();
            exports_1("DashboardComponent", DashboardComponent);
        }
    }
});
//# sourceMappingURL=dashboard.component.js.map