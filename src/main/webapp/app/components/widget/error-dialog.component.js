System.register(["angular2/core"], function(exports_1) {
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
    var ErrorDialogComponent;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            }],
        execute: function() {
            /**
             * This component is responsible for showing a dialog when an error occurs.
             * */
            ErrorDialogComponent = (function () {
                function ErrorDialogComponent() {
                    this.messages = new Array();
                }
                __decorate([
                    core_1.Input(), 
                    __metadata('design:type', Array)
                ], ErrorDialogComponent.prototype, "messages", void 0);
                ErrorDialogComponent = __decorate([
                    core_1.Component({
                        selector: 'error-dialog',
                        template: "\n    <div *ngIf=\"messages.length > 0\" class=\"alert alert-danger\">\n        <ul class=\"error-list\" *ngFor=\"#message of messages; #i = index\">\n            <li><span class=\"error-icon glyphicon glyphicon-remove-circle\"></span>{{ messages[i] }}</li>\n        </ul>\n    </div>\n    "
                    }), 
                    __metadata('design:paramtypes', [])
                ], ErrorDialogComponent);
                return ErrorDialogComponent;
            })();
            exports_1("ErrorDialogComponent", ErrorDialogComponent);
        }
    }
});
//# sourceMappingURL=error-dialog.component.js.map