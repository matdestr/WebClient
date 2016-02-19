System.register([], function(exports_1) {
    var User;
    return {
        setters:[],
        execute: function() {
            User = (function () {
                function User(username, name, surname, email) {
                    this.username = username;
                    this.name = name;
                    this.surname = surname;
                    this.email = email;
                }
                Object.defineProperty(User.prototype, "fullName", {
                    get: function () {
                        return this.name + " " + this.surname;
                    },
                    enumerable: true,
                    configurable: true
                });
                User.createEmptyUser = function () {
                    return new User("", "", "", "");
                };
                User.prototype.deserialize = function (object) {
                    this.userId = object.userId;
                    this.username = object.username;
                    this.name = object.name;
                    this.surname = object.surname;
                    this.email = object.email;
                    return this;
                };
                return User;
            })();
            exports_1("User", User);
        }
    }
});
//# sourceMappingURL=user.js.map