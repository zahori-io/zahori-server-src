import { Component, Input, Output, EventEmitter, ViewContainerRef, Directive } from '@angular/core';
import { Tag } from '../../../../../../model/tag';
import Swal from 'sweetalert2/dist/sweetalert2.js';
import { Direct } from 'protractor/built/driverProviders';

@Component({
    selector: 'tag',
    templateUrl: './tagComponent.html'
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

    public onEventLog(event: string, data: any): void {
        console.log(event, data);
    }

    deleteTag(tag : Tag) {
        Swal.fire({
            title: 'Borrar etiqueta ' + tag.name,
            text: 'Esta acción no puede deshacerse',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonText: 'Ok',
            cancelButtonText: 'No',
            backdrop: `
                rgba(64, 69, 58,0.4)
                left top
                no-repeat`
        }).then((result) => {
            if (result.value) {
                this.deleted.emit(tag);
                console.log("click on borrar");
                this.submitted = true;
            }
        })
    }

    updateTag(tag : Tag){
        this.updated.emit(tag);
        console.log("click on update");
        console.log(this.color1);
        this.submitted = true;
    }

    createTag(tag : Tag){
        this.created.emit(tag);
        console.log("click on create");
        this.submitted = true;
    }

    deleteFromArray(tag : Tag){
        this.erased.emit(tag);
        console.log("click on erase");
    }

    isNew(tag : Tag){
      return tag.tagId == 0;
  }




}