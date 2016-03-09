import {Component} from "angular2/core";
import {Input} from "angular2/core";
import {CardPosition} from "../../entities/session/card-position";

@Component({
    selector: 'game-circle',
    template: `
        <circle [cx]="xCoordCenter" 
                [cy]="yCoordCenter" 
                [r]="radius"
                [class.svg-circle]="!isInnerCircle"
                [class.inner-circle]="isInnerCircle"></circle>
    `
})
export class CircleComponent {
    @Input()
    public containerWidth : number;
    
    @Input()
    public containerHeight : number;
    
    @Input()
    public xCoordCenter : number;
    
    @Input()
    public yCoordCenter : number;
    
    @Input()
    public radius : number;
    
    @Input()
    public isInnerCircle : boolean;
    
    @Input()
    public cardPositions : CardPosition[];
    
    constructor() {
        this.cardPositions = [];
    }
    
    public updateCardPositions() : void {
        var angle = 0;
        
        this.cardPositions.forEach(function(cardPosition : CardPosition) {
            var step = (2 * Math.PI) / this.cardPositions.length;
            
            var x = Math.round(this.containerWidth / 2 + this.radius * Math.cos(angle) - cardPosition.cardWidth / 2);
            var y = Math.round(this.containerHeight / 2 + this.radius * Math.sin(angle) - cardPosition.cardHeight / 2);
            
            angle += step;
        });
    }
}
