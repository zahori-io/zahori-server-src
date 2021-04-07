export class BannerOptions {
    title: string;
    subtitle: string;
    color: string;
    closable: boolean = false;



    constructor(title? : string, subtitle? : string, color? : string, closable? : boolean){
        this.title = title;
        this.subtitle = subtitle;
        this.color = color;
        this.closable = closable;
    }
  }

 
 