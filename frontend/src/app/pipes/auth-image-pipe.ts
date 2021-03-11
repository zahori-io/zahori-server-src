import { Pipe, PipeTransform } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { DomSanitizer } from '@angular/platform-browser';

@Pipe({
    name: 'authImage'
})
export class AuthImagePipe implements PipeTransform {

    constructor(
        private http: HttpClient,
        private domSanitizer: DomSanitizer
    ) { }

    async transform(src: string): Promise<string> {
        try {
            const imageBlob = await this.http.get(src, { responseType: 'blob' }).toPromise();
            const reader = new FileReader();
            return new Promise((resolve, reject) => {
                reader.onloadend = (e) => {
                    const base64Image: string = this.domSanitizer.bypassSecurityTrustUrl(reader.result as string) as string;
                    resolve(base64Image);
                }
                reader.readAsDataURL(imageBlob);
            });
        } catch {
            return 'assets/image-broken.jpeg';
        }
    }

}