import { Component, OnInit } from '@angular/core';
import { Tag } from '../../../../../model/tag';
import { DataService } from '../../../../../services/data.service'
import { BannerOptions } from '../../../../../utils/banner/banner'

const SUCCESS : string = "Operación realizada con éxito";
const ERROR: string = "Algo ha ido mal";
const SUCCESS_COLOR : string = "alert alert-success";
const ERROR_COLOR : string = "alert alert-danger";

@Component({
  selector: 'app-admin-tags',
  templateUrl: './admin-tags.component.html',
  styleUrls: ['./admin-tags.component.css']
})
export class AdminTagsComponent implements OnInit {

  tags : Tag[] = [];
  myTag : Tag;
  banner: BannerOptions;
  showrow : boolean = false;

  ngOnInit() {
    this.refresh();
    this.myTag = new Tag();
    this.banner = new BannerOptions();
  }  
  constructor(public dataService: DataService) {
  }

  refresh(){
    this.dataService.getTags(String(this.dataService.processSelected.processId)).subscribe(
      (res : any) => {
        this.tags = res;
      });
  }

  deleteTag(tag: Tag){
    tag.active = false;
    let tagArray: Tag[] = [tag];
    this.sendPostPetition(tagArray, new BannerOptions(SUCCESS, "El entorno " + tag.name + " ha sido eliminado", SUCCESS_COLOR , true ));

    }

  updateTag(tag : Tag){
    if (tag.name.length == 0 ){
      this.banner = new BannerOptions(ERROR, "Todos los campos son obligatorios", ERROR_COLOR , true )
    }
    else{
      let tagArray: Tag[] = [tag];
      this.sendPostPetition(tagArray, new BannerOptions(SUCCESS, "Se ha modificado la etiqueta " + tag.name, SUCCESS_COLOR , true ));
    }
    
  }
  
  
  createTag(tag : Tag){    
    if (tag.name.length == 0){
      this.banner = new BannerOptions(ERROR, "Todos los campos son obligatorios", ERROR_COLOR , true );
    }
    else{
      let tagArray: Tag[] = [tag];
      this.sendPostPetition(tagArray, new BannerOptions(SUCCESS, "Se ha creado la etiqueta " + tag.name, SUCCESS_COLOR , true ));
    }
  }
  
  sendPostPetition(tag : Tag[], banner : BannerOptions){
    this.dataService.setTags(tag, String(this.dataService.processSelected.processId)).subscribe(
      (res : any) => {
        this.refresh();
        this.banner = banner
        this.showrow = false;
      });
      error => {
        console.log("Error en la petición:" + error);
        this.banner = new BannerOptions(ERROR, error, ERROR_COLOR , true )
      }  }

  closeBanner(){
    this.banner = new BannerOptions;
  }
  
  newTag(){
    if (!this.showrow){
      this.myTag = new Tag();
    }
      this.showrow = !this.showrow;
  
  }
}