System.register(['angular2/core'], function(exports_1) {
    var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
        var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
        if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
        else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
        return c > 3 && r && Object.defineProperty(target, key, r), r;
    };
    var __metadata = (this && this.__metadata) || function (k, v) {
        if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
    };
    var core_1;
    var SignInComponent;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            }],
        execute: function() {
            SignInComponent = (function () {
                function SignInComponent() {
                }
                SignInComponent = __decorate([
                    core_1.Component({
                        selector: 'sign-in',
                        template: "\n        <form name=\"sign-in\">\n            <label for=\"username\">username</label>\n            <input id=\"username\" type=\"text\" class=\"span2\" placeholder=\"username\">\n            <label for=\"password\">password</label>\n            <input id=\"password\" type=\"password\" class=\"span2\" placeholder=\"password\">\n            <input type=\"submit\" value=\"Sign in\" class=\"btn\"/>\n        </form>\n    "
                    }), 
                    __metadata('design:paramtypes', [])
                ], SignInComponent);
                return SignInComponent;
            })();
            exports_1("SignInComponent", SignInComponent);
        }
    }
});
//# sourceMappingURL=sign-in.component.js.map