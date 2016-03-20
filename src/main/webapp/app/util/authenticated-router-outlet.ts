import {Directive, Attribute, ElementRef, DynamicComponentLoader} from "angular2/core";
import {Router, RouterOutlet, ComponentInstruction} from "angular2/router";
import {TokenService, isTokenExpired, isTokenAvailable} from "../services/token.service";

/**
 * Router outlet that navigates back to the login page when authentication is not valid 
 */

@Directive({
    selector: 'router-outlet'
})
export class AuthenticatedRouterOutlet extends RouterOutlet {
    private static redirectUrl : string = '/Authentication';

    private parentRouter : Router;
    private publicRoutes : string[];
    private tokenService : TokenService;

    constructor(elementRef : ElementRef, loader : DynamicComponentLoader,
                parentRouter : Router, @Attribute('name') nameAttr : string,
                tokenService : TokenService) {
        super(elementRef, loader, parentRouter, nameAttr);

        this.parentRouter = parentRouter;
        this.publicRoutes = ['/'];
        this.tokenService = tokenService;
    }

    activate(nextInstruction : ComponentInstruction) : Promise<any> {
        var url = this.parentRouter.lastNavigationAttempt;

        if (this.publicRoutes.indexOf(url) < 0 && isTokenExpired()) {
            if (isTokenAvailable()) {
                this.tokenService.refreshToken();

                // Tried to refresh token, but is still not valid
                if (isTokenExpired()) {
                    this.parentRouter.navigate([AuthenticatedRouterOutlet.redirectUrl]);
                }
            } else {
                this.parentRouter.navigate([AuthenticatedRouterOutlet.redirectUrl]);
            }

        }

        return super.activate(nextInstruction);
    }
}
