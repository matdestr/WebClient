import {Component, OnInit, Input} from "angular2/core";
import {Router} from "angular2/router";
import {RouteParams} from "angular2/router";
import {ToolbarComponent} from "../widget/toolbar.component";
import {CardDetailComponent} from "../cards/card-detail.component";

@Component({
    selector: 'active-session',
    templateUrl: 'html/active-session.html',
    directives: [ToolbarComponent, CardDetailComponent]
})
export class ActiveSessionComponent {
    private stompClient:any = null;

    constructor(private _router:Router,
                private _routeArgs:RouteParams) {

        var self:any = this;
        var socket = new SockJS('/kandoe/ws?token=' + localStorage.getItem("token"));
        this.stompClient = Stomp.over(socket);
        this.stompClient.connect({}, function(){
            self.stompClient.subscribe('/topic/values', function (data) {
                console.log(data);
                //console.log(JSON.parse(data.body).content);
            });
        });
    }

    public send() : void {
        if (this.stompClient){
            this.stompClient.send('/test', {} , null);
            //this.stompClient.send("/messages", null, JSON.stringify({'message': 'el grande matador'}));
        }

    }
}
