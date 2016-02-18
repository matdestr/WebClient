System.register([], function(exports_1) {
    var RegisterModel;
    return {
        setters:[],
        execute: function() {
            RegisterModel = (function () {
                function RegisterModel() {
                    this.username = "";
                    this.name = "";
                    this.surname = "";
                    this.email = "";
                    this.password = "";
                    this.verifyPassword = "";
                }
                return RegisterModel;
            })();
            exports_1("RegisterModel", RegisterModel);
        }
    }
});
//# sourceMappingURL=register.js.map