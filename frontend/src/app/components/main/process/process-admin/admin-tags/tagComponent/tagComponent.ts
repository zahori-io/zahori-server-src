import { Component, Input, Output, EventEmitter, ViewContainerRef } from '@angular/core';
import { Tag } from '../../../../../../model/tag';
import Swal from 'sweetalert2/dist/sweetalert2.js';

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
                Swal.fire({
                    backdrop: `
                        rgba(64, 69, 58,0.4)
                        left top
                        no-repeat`,                    
                    title: 'Eliminada la etiqueta ' + tag.name,
                    text : 'La etiqueta ha sido eliminada',
                    icon : 'success'
                    }
                )

                this.deleted.emit(tag);
                console.log("click on borrar");
                this.submitted = true;
            } else if (result.dismiss === Swal.DismissReason.cancel) {
                Swal.fire({
                    backdrop: `
                        rgba(64, 69, 58,0.4)
                        left top
                        no-repeat`,                    
                    title : 'Acción cancelada',
                    }
                )
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

    isNew(tag : Tag){
      return tag.tagId == 0;
  }



}