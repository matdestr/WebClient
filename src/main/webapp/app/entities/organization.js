System.register(["../entities/user/user"], function(exports_1) {
    var user_1;
    var Organization, CreateOrganization;
    return {
        setters:[
            function (user_1_1) {
                user_1 = user_1_1;
            }],
        execute: function() {
            Organization = (function () {
                function Organization() {
                }
                Organization.createEmptyOrganization = function () {
                    var organization = new Organization();
                    organization.organizationId = -1;
                    organization.name = "";
                    organization.owner = user_1.User.createEmptyUser();
                    organization.members = new Array();
                    organization.categories = new Array();
                    return organization;
                };
                return Organization;
            })();
            exports_1("Organization", Organization);
            CreateOrganization = (function () {
                function CreateOrganization() {
                }
                return CreateOrganization;
            })();
            exports_1("CreateOrganization", CreateOrganization);
        }
    }
});
//# sourceMappingURL=organization.js.map