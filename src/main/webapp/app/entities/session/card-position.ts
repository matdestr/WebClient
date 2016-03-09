export class CardPosition {
    /*public cardDetailsId : number;
    public text : string;
    public imageUrl : string;*/
    
    public cardWidth : number;
    public cardHeight : number;
    public positionLeft : number;
    public positionRight : number;
    
    constructor(//cardDetailsId : number, text : string, imageUrl : string,
                cardWidth : number, cardHeight : number, positionLeft : number, positionRight : number) {
        /*this.cardDetailsId = cardDetailsId;
        this.text = text;
        this.imageUrl = imageUrl;*/
        this.cardWidth = cardWidth;
        this.cardHeight = cardHeight;
        this.positionLeft = positionLeft;
        this.positionRight = positionRight;
    }
}
