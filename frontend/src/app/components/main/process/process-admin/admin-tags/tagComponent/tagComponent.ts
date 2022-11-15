import { Component, Input, Output, EventEmitter, ViewContainerRef, Directive } from '@angular/core';
import { Tag } from '../../../../../../model/tag';
import Swal from 'sweetalert2/dist/sweetalert2.js';
import { TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'tag',
    templateUrl: './tagComponent.html',
    styleUrls: ['./tagComponent.css']
})

export class tagComponent{
    
    @Input()
    tag : Tag;

    @Output()
    deleted = new EventEmitter<Tag>();
    
    @Output()
    updated = new EventEmitter<Tag>();
    
    @Output()
    created = new EventEmitter<Tag>();

    @Output()
    erased = new EventEmitter<Tag>();
    
    submitted : boolean = false;
    public color1: string = '#2889e9';

    constructor(private translate: TranslateService) {
    }

    public onEventLog(event: string, data: any): void {
        console.log(event, data);
    }

    deleteTag(tag : Tag) {
        Swal.fire({
            title: this.translate.instant('main.process.processAdmin.tags.tag.removeTag') + tag.name,
            text: this.translate.instant('main.process.processAdmin.tags.tag.removeWarning'),
            icon: 'warning',
            showCancelButton: true,
            confirmButtonText: this.translate.instant('main.process.processAdmin.tags.tag.removeConfirmButton'),
            cancelButtonText: this.translate.instant('main.process.processAdmin.tags.tag.removeCancelButton'),
            backdrop: `
                rgba(64, 69, 58,0.4)
                left top
                no-repeat`
        }).then((result) => {
            if (result.value) {
                this.deleted.emit(tag);
                this.submitted = true;
            }
        })
    }

    updateTag(tag : Tag){
        this.updated.emit(tag);
        console.log(this.color1);
        this.submitted = true;
    }

    createTag(tag : Tag){
        this.created.emit(tag);
        this.submitted = true;
    }

    deleteFromArray(tag : Tag){
        this.erased.emit(tag);
    }

    isNew(tag : Tag){
      return tag.tagId == 0;
  }




}