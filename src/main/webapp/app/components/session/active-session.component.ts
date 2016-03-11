import {Component, OnInit, Input} from "angular2/core";
import {ChatMessage} from "../../entities/session/create-chat-message";
import {User} from "../../entities/user/user";
import {Router} from "angular2/router";
import {RouteParams} from "angular2/router";
import {ToolbarComponent} from "../widget/toolbar.component";
import {CardDetailComponent} from "../cards/card-detail.component";
import {ElementRef} from "angular2/core";
import {ViewChild} from "angular2/core";

@Component({
    selector: 'active-session',
    templateUrl: 'html/active-session.html',
    directives: [ToolbarComponent, CardDetailComponent]
})
export class ActiveSessionComponent {
    @ViewChild("chatbox") private chatContainer:ElementRef;
    private currentMessage:string = "";
    private messages:ChatMessage[] = [];
    private stompClient:any = null;

    constructor(private _router:Router,
                private _routeArgs:RouteParams) {

        var self:any = this;
        var socket = new SockJS('/kandoe/ws?token=' + localStorage.getItem("token"));
        this.stompClient = Stomp.over(socket);
        this.stompClient.connect({}, function(){
            self.stompClient.subscribe('/topic/session/messages', function (data) {
                var message:ChatMessage = JSON.parse(data.body);
                self.messages.push(message);
                self.chatContainer.nativeElement.scrollTop = self.chatContainer.nativeElement.scrollHeight;
            });
        });
    }

    public send() : void {
        if (this.stompClient)
            if (this.currentMessage || this.currentMessage.length != 0) {
                console.log(this.currentMessage.length);
                this.stompClient.send("/messages", null, JSON.stringify({'message': this.currentMessage}));
                this.currentMessage = "";
            }
    }
}
