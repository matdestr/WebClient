/**
 * This class is used to create a new card and is sent to the server.
 * */
export class CreateCardModel {
    text: string;
    imageUrl: string;

    constructor(text:string, imageUrl:string) {
        this.text = text;
        this.imageUrl = imageUrl;
    }

    public static createEmptyCreateCardModel(): CreateCardModel{
        return new CreateCardModel("", "");
    }
}
